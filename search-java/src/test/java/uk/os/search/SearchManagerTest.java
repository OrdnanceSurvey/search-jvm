/*
 * Copyright (C) 2016 Ordnance Survey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.os.search;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import uk.os.search.android.providers.Provider;
import uk.os.search.android.providers.bng.GridReferenceProvider;
import uk.os.search.android.providers.latlon.LatLonProvider;
import uk.os.search.android.providers.opennames.OpennamesProvider;
import uk.os.search.android.providers.recents.RecentsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class SearchManagerTest {

    @Test
    public void shouldHaveLatLonAndGridReferenceDefaultProviders() {
        SearchManager searchManager = new SearchManager();
        assertNotNull(searchManager);
        assertNotNull(searchManager.query("51, 0").toBlocking().first());
        assertNotNull(searchManager.query("SU 37290 15512").toBlocking().first());
    }

    @Test
    public void shouldBuildWhenApiKeysAdded() {
        SearchManager searchManager = new SearchManager.Builder()
                .addOpenNames("openNamesApiKey")
                .addPlaces("placesApiKey")
                .build();
        assertNotNull(searchManager);
    }

    @Test
    public void shouldCreateSearchBundleWithErrorWhenRecentsManagerThrowsError() {
        final IllegalStateException error = new IllegalStateException("bad times for recents provider");
        Answer<Observable<List<SearchResult>>> errorAnswer = new Answer<Observable<List<SearchResult>>>() {
            @Override
            public Observable<List<SearchResult>> answer(InvocationOnMock invocation) throws Throwable {
                return Observable.error(error);
            }
        };

        RecentsManager recentsManager = Mockito.mock(RecentsManager.class);
        when(recentsManager.query(anyString())).then(errorAnswer);
        when(recentsManager.queryById(anyString())).then(errorAnswer);

        LatLonProvider latLonProvider = Mockito.mock(LatLonProvider.class);
        when(latLonProvider.query(Query.input)).then(Query.Database.HasData.response);

        List<Provider> providers = new ArrayList<>();
        providers.add(latLonProvider);
        SearchManager searchManager = new SearchManager.Builder().setRecentsManager(recentsManager).setProviders(providers).build();
        SearchBundle searchBundle = searchManager.query(Query.input).toBlocking().single();

        boolean isErrorCaptured = searchBundle.getErrors().contains(error);
        assertTrue(isErrorCaptured);

        boolean isRecentResultOk = searchBundle.getRecents().size() == 0;
        assertTrue(isRecentResultOk);

        boolean isProviderResultsOk =  searchBundle.getRemaining().size() == 1;
        assertTrue(isProviderResultsOk);
    }

    @Test
    public void shouldCreateSearchBundleWithErrorWhenProviderThrowsError() {
        final IllegalStateException error = new IllegalStateException("bad times for some reason");

        RecentsManager recentsManager = Mockito.mock(RecentsManager.class);
        when(recentsManager.query(anyString())).then(Query.Database.HasData.response);
        when(recentsManager.queryById(anyString())).then(Query.Database.HasData.response);

        LatLonProvider latLonProvider = Mockito.mock(LatLonProvider.class);
        when(latLonProvider.query(Query.input)).then(new Answer<Observable<List<SearchResult>>>() {
            @Override
            public Observable<List<SearchResult>> answer(InvocationOnMock invocation) throws Throwable {
                return Observable.error(error);
            }
        });

        List<Provider> providers = new ArrayList<>();
        providers.add(latLonProvider);
        SearchManager searchManager = new SearchManager.Builder().setRecentsManager(recentsManager).setProviders(providers).build();
        SearchBundle searchBundle = searchManager.query(Query.input).toBlocking().single();
        String expected = Query.Database.HasData.name;
        String actual = searchBundle.getRecents().get(0).getName();
        assertEquals(expected, actual);

        boolean isErrorCaptured = searchBundle.getErrors().contains(error);
        assertTrue(isErrorCaptured);

        boolean isRecentResultOk = searchBundle.getRecents().size() == 1 && searchBundle.getRemaining().size() == 0;
        assertTrue(isRecentResultOk);

    }

    @Test
    public void shouldFilterSearchResultsForRecents() {
        // We assume that the lat/lon entry obtained the coordinate and, apparently, London because it was a close match
        RecentsManager recentsManager = Mockito.mock(RecentsManager.class);
        when(recentsManager.query(Query.input)).then(Query.Database.HasData.response);

        when(recentsManager.queryById(Query.Database.HasData.id)).then(Query.Database.HasData.response);
        // querying recents returned only one result _but_ a provider (e.g. open names with fuzzy matching) returned
        // something special, e.g. lat/lon -> London.  London may still be in recents.
        when(recentsManager.queryById(Query.Database.HasData.id, Query2.Database.HasData.id))
                .then(new Answer<Observable<List<SearchResult>>>() {
            @Override
            public Observable<List<SearchResult>> answer(InvocationOnMock invocation) throws Throwable {
                List<SearchResult> results = new ArrayList<>();
                results.add(Query.Database.HasData.searchResult);
                results.add(Query2.Database.HasData.searchResult);
                return Observable.just(results);
            }
        });

        LatLonProvider latLonProvider = Mockito.mock(LatLonProvider.class);
        when(latLonProvider.query(Query.input)).then(Query.Database.HasData.response);

        // assume open-names returns London because the lat/lon is a close match
        OpennamesProvider opennamesProvider = Mockito.mock(OpennamesProvider.class);
        when(opennamesProvider.query(Query.input)).then(Query2.Database.HasData.response);

        List<Provider> providers = new ArrayList<>();
        providers.add(latLonProvider);
        providers.add(opennamesProvider);
        SearchManager searchManager = new SearchManager.Builder().setRecentsManager(recentsManager).setProviders(providers).build();
        SearchBundle searchBundle = searchManager.query(Query.input).toBlocking().single();
        String expected = Query.Database.HasData.name;
        String actual = searchBundle.getRecents().get(0).getName();
        assertEquals(expected, actual);

        assertTrue(searchBundle.getRecents().size() == 2);
        assertTrue(searchBundle.getRemaining().size() == 0);
    }

    @Test
    public void shouldFilterSearchResultsForRecentsWhenSingleProvider() {
        RecentsManager recentsManager = Mockito.mock(RecentsManager.class);
        when(recentsManager.query(anyString())).then(Query.Database.HasData.response);
        when(recentsManager.queryById(anyString())).then(Query.Database.HasData.response);

        LatLonProvider latLonProvider = Mockito.mock(LatLonProvider.class);
        when(latLonProvider.query(Query.input)).then(Query.Database.HasData.response);

        SearchManager searchManager = new SearchManager.Builder()
                .setRecentsManager(recentsManager)
                .setProviders(latLonProvider)
                .build();
        SearchBundle searchBundle = searchManager.query(Query.input).toBlocking().single();

        assertTrue(searchBundle.getRecents().size() == 1);
        assertTrue(searchBundle.getRemaining().size() == 0);
    }

    @Test
    public void shouldFilterSearchResultsForRecentsWhenSingleProviderViaList() {
        RecentsManager recentsManager = Mockito.mock(RecentsManager.class);
        when(recentsManager.query(anyString())).then(Query.Database.HasData.response);
        when(recentsManager.queryById(anyString())).then(Query.Database.HasData.response);

        LatLonProvider latLonProvider = Mockito.mock(LatLonProvider.class);
        when(latLonProvider.query(Query.input)).then(Query.Database.HasData.response);

        List<Provider> providers = new ArrayList<>();
        providers.add(latLonProvider);
        SearchManager searchManager = new SearchManager.Builder().setRecentsManager(recentsManager).setProviders(providers).build();
        SearchBundle searchBundle = searchManager.query(Query.input).toBlocking().single();
        String expected = Query.Database.HasData.name;
        String actual = searchBundle.getRecents().get(0).getName();
        assertEquals(expected, actual);

        assertTrue(searchBundle.getRecents().size() == 1);
        assertTrue(searchBundle.getRemaining().size() == 0);
    }

    @Test
    public void shouldQueryProvidersWhenNoRecentManager() {
        LatLonProvider latLonProvider = Mockito.mock(LatLonProvider.class);
        when(latLonProvider.query(Query.input)).then(Query.Database.HasData.response);

        SearchManager searchManager = new SearchManager.Builder().setProviders(latLonProvider).build();
        SearchBundle searchBundle = searchManager.query(Query.input).toBlocking().single();
        String expected = Query.Database.HasData.name;
        String actual = searchBundle.getRemaining().get(0).getName();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldProvideLatitudeLongitudeSearchResultForLatLonInputWhenMultipleProviders() throws Exception {
        RecentsManager recentsManager = Mockito.mock(RecentsManager.class);
        when(recentsManager.query(anyString())).then(Query.Database.NoData.response);
        when(recentsManager.queryById(anyString())).then(Query.Database.NoData.response);

        OpennamesProvider opennamesProvider = Mockito.mock(OpennamesProvider.class);
        when(opennamesProvider.query(anyString())).then(Query.Database.NoData.response);

        GridReferenceProvider gridReferenceProvider = Mockito.mock(GridReferenceProvider.class);
        when(gridReferenceProvider.query(anyString())).then(Query.Database.NoData.response);

        LatLonProvider latLonProvider = Mockito.mock(LatLonProvider.class);
        when(latLonProvider.query(Query.input)).then(Query.Database.HasData.response);

        SearchManager searchManager = new SearchManager.Builder()
                .setRecentsManager(recentsManager)
                .setProviders(gridReferenceProvider, opennamesProvider, latLonProvider)
                .build();
        SearchBundle searchBundle = searchManager.query(Query.input).toBlocking().single();
        String expected = Query.Database.HasData.name;
        String actual = searchBundle.getRemaining().get(0).getName();
        assertEquals(expected, actual);
    }

    private static class Query {
        final static String input = "51.50722, -0.1275";

        private static class Database {
            private static class HasData {
                final static String name = "51°30'26.0\"N 0°07'39.0\"W";
                final static String id = "123456789";
                final static SearchResult searchResult = new SearchResult(
                        id,
                        name,
                        "context",
                        new Point(51, 0),
                        new Envelope(),
                        SpatialReference.create(4326));
                final static Answer<Observable<List<SearchResult>>> response = new Answer<Observable<List<SearchResult>>>() {
                    @Override
                    public Observable<List<SearchResult>> answer(InvocationOnMock invocation) throws Throwable {
                        List<SearchResult> result = new ArrayList<>(Arrays.asList(searchResult));
                        return Observable.just(result);
                    }
                };
            }

            private static class NoData {
                final static Answer<Observable<List<SearchResult>>> response = new Answer<Observable<List<SearchResult>>>() {
                    @Override
                    public Observable<List<SearchResult>> answer(InvocationOnMock invocation) throws Throwable {
                        List<SearchResult> l = new ArrayList<>();
                        return Observable.just(l);
                    }
                };
            }
        }
    }

    private static class Query2 {
        final static String input = "London";

        private static class Database {
            private static class HasData {
                final static String name = "City of London";
                final static String id = "987654321";
                final static SearchResult searchResult = new SearchResult(
                        id,
                        name,
                        "context",
                        new Point(51, -0.1),
                        new Envelope(),
                        SpatialReference.create(4326));
                final static Answer<Observable<List<SearchResult>>> response = new Answer<Observable<List<SearchResult>>>() {
                    @Override
                    public Observable<List<SearchResult>> answer(InvocationOnMock invocation) throws Throwable {
                        List<SearchResult> result = new ArrayList<>(Arrays.asList(searchResult));
                        return Observable.just(result);
                    }
                };
            }

            private static class NoData {
                final static Answer<Observable<List<SearchResult>>> response = new Answer<Observable<List<SearchResult>>>() {
                    @Override
                    public Observable<List<SearchResult>> answer(InvocationOnMock invocation) throws Throwable {
                        List<SearchResult> l = new ArrayList<>();
                        return Observable.just(l);
                    }
                };
            }
        }
    }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.observers.TestSubscriber;
import uk.os.search.android.providers.bng.GridReferenceProvider;
import uk.os.search.android.providers.latlon.LatLonProvider;
import uk.os.search.android.providers.opennames.OpennamesProvider;
import uk.os.search.android.providers.opennames.service.SearchApi;
import uk.os.search.android.providers.opennames.service.model.Header;
import uk.os.search.android.providers.opennames.service.model.Result;
import uk.os.search.android.providers.opennames.service.model.ServerResponse;
import uk.os.search.android.providers.recents.RecentsManager;
import uk.os.search.android.providers.recents.RecentsManagerImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MergeSearchAndRecentsTest {

    @Test
    public void shouldReturnSingleRecentWhenAdded() throws Exception {
        String query = "High Street";

        RecentsManager recentsManager = new RecentsManagerImpl();
        SearchManager searchManager = getSearchManagerImpl(recentsManager);
        SearchBundle searchBundle = query(searchManager, query);
        int actual = searchBundle.getRecents().size();
        int expected = 0;
        assertEquals(expected, actual);

        SearchResult third = searchBundle.getRemaining().get(2);
        recentsManager.saveRecent(third).toBlocking().subscribe();

        searchBundle = query(searchManager, query);
        actual = searchBundle.getRecents().size();
        expected = 1;
        assertEquals(expected, actual);

        assertFalse(searchBundle.getRemaining().contains(third));
    }

    @Test
    public void shouldReturnRecentsWhenProviderErrors() {
        String query = "51 0";

        RecentsManager recentsManager = new RecentsManagerImpl();
        SearchManager searchManager = getBrokenSearchManagerImpl(recentsManager);

        SearchBundle searchBundle = query(searchManager, query);

        int actual = searchBundle.getRecents().size();
        int expected = 0;
        assertEquals(expected, actual);

        SearchResult sr = searchBundle.getRemaining().get(0);
        recentsManager.saveRecent(sr).toBlocking().subscribe();
        searchBundle = query(searchManager, query);
        actual = searchBundle.getRecents().size();
        expected = 1;
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnResultsWhenProviderIsOkAndThenFails() throws Exception {
        String query = "High Street";

        RecentsManager recentsManager = new RecentsManagerImpl();
        SearchManager searchManager = getSearchManagerImpl(recentsManager);
        SearchBundle searchBundle = query(searchManager, query);
        int actual = searchBundle.getRecents().size();
        int expected = 0;
        assertEquals(expected, actual);
        boolean hasErrors = searchBundle.getErrors().size() > 0;
        assertFalse(hasErrors);

        SearchResult third = searchBundle.getRemaining().get(2);
        recentsManager.saveRecent(third).toBlocking().subscribe();

        searchManager = getBrokenSearchManagerImpl(recentsManager);
        searchBundle = query(searchManager, query);
        actual = searchBundle.getRecents().size();
        expected = 1;
        assertEquals(expected, actual);
        hasErrors = searchBundle.getErrors().size() > 0;
        assertTrue(hasErrors);

        assertFalse(searchBundle.getRemaining().contains(third));
    }

    @Test
    public void shouldRemoveDuplicatesBasedOnId() throws Exception {
        String query = "applewood";

        String duplicateFeatureName = "Applewood - the duplicate";
        SearchResult theDuplicate = MockSupport
                .createCustomSearchResult("osgb4000000009438220", duplicateFeatureName, 1000);

        SearchManager searchManager = getSearchManagerWithRecents(MockSupport.createCustomSearchResult("a", "Applewood",
                1000), theDuplicate);

        TestSubscriber<SearchBundle> testSubscriber = new TestSubscriber<>();
        searchManager.query(query).subscribe(testSubscriber);

        verify(testSubscriber);

        SearchBundle result = testSubscriber.getOnNextEvents().get(0);

        // both results are returned because different IDs
        int expectedSize = 2;
        assertEquals(expectedSize, result.getRecents().size());

        // the duplicate has the same ID and therefore it should be filtered
        searchManager = getSearchManagerWithRecents(MockSupport.createCustomSearchResult("osgb4000000009438220",
                "Applewood Extra", 1000), theDuplicate);

        testSubscriber = new TestSubscriber<>();
        searchManager.query(query).subscribe(testSubscriber);

        verify(testSubscriber);

        result = testSubscriber.getOnNextEvents().get(0);

        // recent was found
        expectedSize = 1;
        assertEquals(expectedSize, result.getRecents().size());

        // TODO: this failure is caused by a preference to use the recent value (which will be updated as a side-effect
        // but isn't available now.  The search source should update the stream immediately with the latest value.
        //boolean isLatestItemPreferred = result.getRecents().get(0).getName().equals("Applewood");
        //assertTrue(isLatestItemPreferred);

        // Search results were filtered
        expectedSize = 2;
        int actualSize = result.getRemaining().size();
        assertEquals(expectedSize, actualSize);
    }

    private RecentsManager createRecentsManagerContaining(SearchResult... recents) {
        RecentsManager recentsManager = new RecentsManagerImpl();
        for (SearchResult searchResult : recents) {
            recentsManager.saveRecent(searchResult).toBlocking().subscribe();
        }
        return recentsManager;
    }

    private SearchManager getSearchManagerWithRecents(SearchResult... results) {
        RecentsManager recentsManager = createRecentsManagerContaining(results);
        return getSearchManagerImpl(recentsManager);
    }

    private SearchManager getSearchManagerImpl(RecentsManager recentsManager) {
        return new SearchManager.Builder()
                .setRecentsManager(recentsManager)
                .setProviders(
                        new GridReferenceProvider(),
                        new OpennamesProvider.Builder("working").setSearchApi(getSearchApi()).build(),
                        new LatLonProvider())
                .build();
    }

    private SearchManager getBrokenSearchManagerImpl(RecentsManager recentsManager) {
        return new SearchManager.Builder().setRecentsManager(recentsManager)
                .setProviders(
                        new GridReferenceProvider(),
                        new OpennamesProvider.Builder("broken").setSearchApi(getSearchApiBroken()).build(),
                        new LatLonProvider())
                .build();
    }

    private SearchApi getSearchApi() {
        SearchApi searchApi = Mockito.mock(SearchApi.class);
        when(searchApi.search(anyString(), anyString())).then(MockSupport.threeResults());
        return searchApi;
    }

    private SearchApi getSearchApiBroken() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost/broken/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(SearchApi.class);
    }

    private SearchBundle query(SearchManager searchManager, String query) {
        return searchManager.query(query).toBlocking().single();
    }

    private void verify(TestSubscriber<SearchBundle> testSubscriber) {
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        if (!testSubscriber.isUnsubscribed()) {
            // try and give it time to sync
            sleep(1000);
        }
        testSubscriber.assertUnsubscribed();
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class MockSupport {

        static SearchResult createCustomSearchResult(String id, String name, double position) {
            Envelope envelope = new Envelope(position - 500, position - 500, position + 500, position + 500);
            return new SearchResult(id,
                    name,
                    String.format("%s Town", name),
                    new Point(position, position),
                    envelope,
                    SpatialReference.create(27700));
        }

        static Answer<Observable<ServerResponse>> threeResults() {
            return new Answer<Observable<ServerResponse> >() {
                @Override
                public Observable<ServerResponse> answer(InvocationOnMock invocation) throws Throwable {
                    ServerResponse serverResponse = new ServerResponse() {
                        @Override
                        public Header getHeader() {
                            return null;
                        }

                        @Override
                        public List<Result> getResults() {
                            List<Result> results = new ArrayList<>();
                            results.add(getMockResult("osgb4000000009438220"));
                            results.add(getMockResult("2"));
                            results.add(getMockResult("3"));
                            return results;
                        }
                    };
                    return Observable.just(serverResponse);
                }
            };
        }

        static Result getMockResult(final String id) {
            final Result r = mock(Result.class);
            final Result.GazetteerEntry g = mock(Result.GazetteerEntry.class);
            when(r.getGazetteerEntry()).then(new Answer<Result.GazetteerEntry>() {
                @Override
                public Result.GazetteerEntry answer(InvocationOnMock invocation) throws Throwable {
                    return g;
                }
            });
            when(g.getID()).then(answer(id));
            when(g.getName()).then(answer("High Street " + id));
            when(g.getDISTRICT_BOROUGH()).then(answer("district borough" + id));
            when(g.getPOPULATED_PLACE()).then(answer("populated place" + id));
            when(g.getCOUNTY_UNITARY()).then(answer("county unitary" + id));
            when(g.getREGION()).then(answer("region" + id));
            when(g.getCOUNTRY()).then(answer("country" + id));
            when(g.getPOSTCODE_DISTRICT()).then(answer("postcode district" + id));
            when(g.getMBR_XMIN()).then(answer("51000"));
            when(g.getMBR_YMIN()).then(answer("51000"));
            when(g.getMBR_XMAX()).then(answer("56000"));
            when(g.getMBR_YMAX()).then(answer("56000"));
            when(g.getGEOMETRY_X()).then(answer("53500"));
            when(g.getGEOMETRY_Y()).then(answer("53500"));
            return r;
        }

        static Answer<String> answer(final String s) {
            return new Answer<String>() {
                @Override
                public String answer(InvocationOnMock invocation) throws Throwable {
                    return s;
                }
            };
        }

    }
}


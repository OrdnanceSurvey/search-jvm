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

package uk.os.elements.search;

import com.esri.core.geometry.Envelope;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import rx.Observable;
import rx.functions.Action1;
import uk.os.elements.search.android.providers.bng.GridReferenceProvider;
import uk.os.elements.search.android.providers.latlon.LatLonProvider;
import uk.os.elements.search.android.providers.recents.RecentsManager;
import uk.os.elements.search.android.providers.recents.RecentsManagerImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class MergeSearchAndRecentsTest {

    private static final Logger sLogger =
            Logger.getLogger(MergeSearchAndRecentsTest.class.getSimpleName());

    @Test
    public void testSingleRecent() throws Exception {
        String query = "51 0";

        RecentsManager recentsManager = new RecentsManagerImpl();
        SearchManager searchManager = getSearchManagerImpl(recentsManager);
        SearchBundle searchBundle = query(searchManager, query);
        int actual = searchBundle.getRecents().size();
        int expected = 0;
        assertEquals(expected, actual);

        SearchResult first = searchBundle.getRemaining().get(0);
        recentsManager.saveRecent(first);

        searchBundle = query(searchManager, query);
        actual = searchBundle.getRecents().size();
        expected = 1;
        assertEquals(expected, actual);

        assertFalse(searchBundle.getRemaining().contains(first));
    }

    private SearchManager getSearchManagerImpl(RecentsManager recentsManager) {
        return new SearchManager(recentsManager, new GridReferenceProvider(),
                new LatLonProvider());
    }

    private SearchManager getBrokenSearchManagerImpl(RecentsManager recentsManager) {
        return new SearchManager(recentsManager, new GridReferenceProvider(), new LatLonProvider());
    }

    @Test
    public void testBackendDown() {
        String query = "51 0";

        RecentsManager recentsManager = new RecentsManagerImpl();
        SearchManager searchManager = getBrokenSearchManagerImpl(recentsManager);

        SearchBundle searchBundle = query(searchManager, query);

        int actual = searchBundle.getRecents().size();
        int expected = 0;
        assertEquals(expected, actual);
    }

    private SearchBundle query(SearchManager searchManager, String query) {
        return searchManager.query(query).toBlocking().single();
    }


    @Test
    public void test1() throws Exception {
        String query = "applewood";

        SearchResult theDuplicate = new SearchResult("osgb4000000009438220", "Applewood - by Ollie", "Oldham, Chadderton, North West, England, OL9", new Point(389000.0, 405431.0), null, SpatialReference.create(27700));

        SearchManager searchManager = getSearchManagerWithRecents(createSearchResult("Applewood",
                1000), theDuplicate);

        final AtomicReference<Throwable> error = new AtomicReference<>();
        final AtomicReference<SearchBundle> result = new AtomicReference<>();

        final CountDownLatch latch = new CountDownLatch(1);

        searchManager.query(query).toBlocking().single();

        Observable<SearchBundle> actual = searchManager.query(query);
        actual.subscribe(new Action1<SearchBundle>() {
            @Override
            public void call(SearchBundle searchBundle) {
                result.set(searchBundle);
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);

        boolean hasError = error.get() != null;
        if (hasError) {
            sLogger.log(Level.SEVERE, "Failure", error.get());
            throw new Exception("borked", error.get());
        }
        boolean tooSlowOrBorked = result.get() == null;
        if (tooSlowOrBorked) {
            sLogger.warning(String.format("Too slow or broken - tried to execute: %s", query));
            // fail test
            assertNotNull(result.get());
        }

        int expectedSize = 2;
        assertEquals(expectedSize, result.get().getRecents().size());
    }

    @Test
    public void test2() throws Exception {
        final AtomicReference<Throwable> error = new AtomicReference<>();

        RecentsManager recentsManager = new RecentsManagerImpl();
        recentsManager.saveRecent(createSearchResult("Applewood", 10000));
        Thread.sleep(100);
        recentsManager.saveRecent(createSearchResult("Blackwood", 20000));
        Thread.sleep(100);
        recentsManager.saveRecent(createSearchResult("Cobwood", 30000));
        Thread.sleep(100);
        recentsManager.saveRecent(createSearchResult("Darkwood", 40000));
        Thread.sleep(100);

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<List<SearchResult>> result = new AtomicReference<>();

        Observable<List<SearchResult>> actual = recentsManager.last(10);
        actual.subscribe(new Action1<List<SearchResult>>() {
            @Override
            public void call(List<SearchResult> searchResults) {
                result.set(searchResults);
                latch.countDown();

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                error.set(throwable);
            }
        });
        latch.await(1, TimeUnit.SECONDS);

        int expectedSize = 4;
        assertEquals(expectedSize, result.get().size());

        SearchResult first = result.get().get(0);
        String expectedName = "Darkwood";
        String actualName = first.getName();
        assertEquals(expectedName, actualName);

        SearchResult last = result.get().get(result.get().size() - 1);
        expectedName = "Applewood";
        actualName = last.getName();
        assertEquals(expectedName, actualName);
    }

    private SearchManager getSearchManagerWithRecents(SearchResult... results) {
        RecentsManager recentsManager = new RecentsManagerImpl();
        for (SearchResult searchResult : results) {
            recentsManager.saveRecent(searchResult);
        }
        return getSearchManagerImpl(recentsManager);
    }

    private SearchResult createSearchResult(String name, double position) {
        Envelope envelope = new Envelope(position - 500, position - 500, position + 500, position + 500);
        return new SearchResult(name.toLowerCase().substring(0, 1),
                name,
                String.format("%s Town", name),
                new Point(position, position),
                envelope,
                SpatialReference.create(27700));
    }
}

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

package uk.os.search.android.providers.opennames;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Action1;
import uk.os.search.SearchResult;
import uk.os.search.android.providers.opennames.service.SearchApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.os.search.android.providers.Util.getEnvironmentalVariable;

/**
 * Verifies that OpenNames responses are provided.
 * > provides product feedback with regard to input, context and results in respect to relevance.
 */
public class IntegrationOpennamesProviderTestLive {

    private static final String ENV_KEY_DEFAULT = "undefined";
    private static final String API_KEY = getEnvironmentalVariable("OS_OPEN_NAMES_API_KEY", ENV_KEY_DEFAULT);

    private static final Logger sLogger =
            Logger.getLogger(IntegrationOpennamesProviderTestLive.class.getSimpleName());

    @Test
    public void bournemouth() throws Exception {
        queryRelevance(new QueryTest
                .Builder("Bournemouth")
                .expectedTophitName(new String[]{"Bournemouth"})
                .expectedTophitContext("")
                .build());
    }

    @Test
    public void london() throws Exception {
        queryRelevance(new QueryTest
                .Builder("London")
                .expectedTophitName(new String[]{"London"})
                .expectedTophitContext("")
                .build());
    }

    @Test
    public void oakleyroad() throws Exception {
        queryRelevance(new QueryTest
                .Builder("oakley road southampton")
                .expectedRelevant(1)
                .expectedTophitName(new String[]{"Oakley Rd", "Oakley Road"})
                .expectedTophitContext("Southampton SO16 4NR")
                .build());
    }

    @Test
    public void salisburyAvenue() throws Exception {
        queryRelevance(new QueryTest
                .Builder("salisbury avenue barking")
                .expectedRelevant(1)
                .expectedTophitName(new String[]{"Shaftesbury Ave", "Salisbury Avenue"})
                .expectedTophitContext("Barking, Greater London IG11")
                .build());
    }

    @Test
    public void salisburyAvenue2() throws Exception {
        queryRelevance(new QueryTest
                .Builder("salisbury avenue barking london")
                .expectedRelevant(1)
                .expectedTophitName(new String[]{"Shaftesbury Ave", "Salisbury Avenue"})
                .expectedTophitContext("Barking, Greater London IG11")
                .build());
    }

    @Test
    public void shaftesbury() throws Exception {
        queryRelevance(new QueryTest
                .Builder("shaftesbury avenue southampton")
                .expectedRelevant(1)
                .expectedTophitName(new String[]{"Shaftesbury Ave", "Shaftesbury Avenue"})
                .expectedTophitContext("Southampton SO17 1SD")
                .build());
    }

    @Test
    public void vigo() throws Exception {
        queryRelevance(new QueryTest
                .Builder("vigo road andover")
                .expectedRelevant(1)
                .expectedTophitName(new String[]{"Vigo Rd", "Vigo Road"})
                .expectedTophitContext("Andover, Hampshire SP10 1HW")
                .build());
    }

    @Test
    public void worwickSquareLondon() throws Exception {
        queryRelevance(new QueryTest
                .Builder("warwick square london")
                .expectedRelevant(1)
                .expectedTophitName("Warwick Square")
                .expectedTophitContext("London")
                .build());
    }

    private void queryRelevance(QueryTest queryTest) throws Exception {
        final int expectedRelevant = queryTest.expectedRelevant;
        final String query = queryTest.query;
        final String expectedTophitNames[] = queryTest.expectedTophitName;
        final String expectedTophitContext = queryTest.expectedTophitContext;

        boolean sizeUnderTest = expectedRelevant != -1;
        boolean tophitNameUnderTest = expectedTophitNames != null;
        boolean tophitContextUnderTest = expectedTophitContext != null;

        List<SearchResult> results = executeQuery(queryTest.query);

        if (sizeUnderTest) {
            int actual = results.size();
            if (expectedRelevant != actual) {
                int irrelevant = actual - expectedRelevant;
                print(irrelevant + " Irrelevant Results", query, String.valueOf(expectedRelevant),
                        String.valueOf(actual));
            }
        }

        SearchResult tophit = results.get(0);

        if (tophitNameUnderTest) {
            String actualTophitName = tophit != null ? tophit.getName() : null;
            boolean hasName = Arrays.asList(expectedTophitNames).contains(actualTophitName);
            if (!hasName) {
                print("Tophit Feature Name", query, separate(expectedTophitNames),
                        actualTophitName);
            }
        }

        if (tophitContextUnderTest) {
            String actualTophitContext = tophit != null ? tophit.getContext() : null;
            if (!actualTophitContext.equalsIgnoreCase(expectedTophitContext)) {
                print("Tophit Feature Context", query, expectedTophitContext, actualTophitContext);
            }
        }
    }

    private List<SearchResult> executeQuery(String query) throws Exception {
        OpennamesProvider SearchManager = getSearchManager();

        final AtomicReference<Throwable> error = new AtomicReference<>();
        final AtomicReference<List<SearchResult>> result = new AtomicReference<>();

        final CountDownLatch latch = new CountDownLatch(1);

        Observable<List<SearchResult>> actual = SearchManager.query(query);
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

        boolean hasError = error.get() != null;
        if (hasError) {
            sLogger.log(Level.SEVERE, "Failure", error.get());
            throw new Exception("problem executing query", error.get());
        }
        boolean tooSlowOrBorked = result.get() == null;
        if (tooSlowOrBorked) {
            sLogger.warning(String.format("Too slow or broken - tried to execute: %s", query));
            // fail test
            assertNotNull(result.get());
        }

        int expectedSize = 25;
        assertEquals(expectedSize, result.get().size());

        return result.get();
    }

    private String separate(String[] array) {
        String previousAcceptable = "";
        StringBuilder sb = new StringBuilder();
        for (String partial : array) {
            if (partial == null || previousAcceptable.equals(partial)) {
                continue;
            }
            previousAcceptable = partial;
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(partial);
        }
        return sb.toString();
    }

    private void print(String title, String query, String expected, String actual) {
        sLogger.warning(String.format(" ### %s ###\nQueried: %s\nExpected: %s\nReceived: %s",
                title, query, expected, actual));
    }

    private SearchApi getSearchApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.ordnancesurvey.co.uk/opennames/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(SearchApi.class);
    }

    private OpennamesProvider getSearchManager() {
        return new OpennamesProvider.Builder(API_KEY).setSearchApi(getSearchApi()).build();
    }

    public static class QueryTest {

        private final String query;
        private final int expectedRelevant;
        private final String[] expectedTophitName;
        private final String expectedTophitContext;

        public static class Builder {
            // Required parameters
            private final String query;

            // Optional parameters - initialized to default values
            private int expectedRelevant = -1;
            private String[] expectedTophitName = null;
            private String expectedTophitContext = null;

            public Builder(String query) {
                this.query = query;
            }

            public Builder expectedRelevant(int val) {
                expectedRelevant = val;
                return this;
            }

            public Builder expectedTophitName(String val) {
                expectedTophitName = new String[]{val};
                return this;
            }

            public Builder expectedTophitName(String[] val) {
                expectedTophitName = val;
                return this;
            }

            public Builder expectedTophitContext(String val) {
                expectedTophitContext = val;
                return this;
            }

            public QueryTest build() {
                return new QueryTest(this);
            }
        }

        private QueryTest(Builder builder) {
            query = builder.query;
            expectedRelevant = builder.expectedRelevant;
            expectedTophitName = builder.expectedTophitName;
            expectedTophitContext = builder.expectedTophitContext;
        }
    }
}


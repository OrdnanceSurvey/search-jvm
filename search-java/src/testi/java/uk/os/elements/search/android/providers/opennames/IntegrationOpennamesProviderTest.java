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

package uk.os.elements.search.android.providers.opennames;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.os.elements.search.SearchResult;
import uk.os.elements.search.android.providers.opennames.service.SearchApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static uk.os.elements.search.android.providers.Util.getStringResource;

public class IntegrationOpennamesProviderTest {

    private static String API_KEY = "nice_try!";

    @Test
    public void bournemouth() throws Exception {
        final String cannedResponse = getStringResource("opennames_canned.json");

        MockWebServer server = new MockWebServer();
        // Schedule response.
        server.enqueue(new MockResponse().setBody(cannedResponse).setResponseCode(200));
        server.start();

        // Ask the server for its URL
        HttpUrl baseUrl = server.url("/opennames/v1/");

        final OpennamesProvider opennamesProvider = new OpennamesProvider.Builder(API_KEY)
                .setSearchApi(getSearchApi(baseUrl.toString()))
                .build();

        final List<SearchResult> results = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                results.addAll(opennamesProvider.query("Bournemouth").toBlocking().first());
                latch.countDown();
            }
        }).start();

        RecordedRequest request = server.takeRequest();
        String actual = request.getPath();
        String expected = "/opennames/v1/find?maxresults=25&key=nice_try!&query=Bournemouth";
        assertEquals(expected, actual);

        latch.await(2, TimeUnit.SECONDS);

        int actualRequestCount = server.getRequestCount();
        int expectedRequestCount = 1;
        assertEquals(expectedRequestCount, actualRequestCount);
        server.shutdown();
    }

    /**
     * @param baseUrl from the mock server _but_ normally https://api.ordnancesurvey.co.uk/opennames/v1/
     * @return
     */
    private SearchApi getSearchApi(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(SearchApi.class);
    }
}

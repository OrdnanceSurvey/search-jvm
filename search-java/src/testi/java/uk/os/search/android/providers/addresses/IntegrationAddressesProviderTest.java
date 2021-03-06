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

package uk.os.search.android.providers.addresses;

import okhttp3.*;
import okhttp3.mockwebserver.*;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.os.search.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.Dispatcher;

import uk.os.search.android.providers.addresses.service.AddressApi;
import uk.os.search.android.providers.Util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationAddressesProviderTest {

    private static String API_KEY = "nice_try!";

    @Test
    public void shouldQueryBoundingBoxEndpointAndReceiveCannedData() throws IOException, InterruptedException {
        final String cannedResponse = Util.getStringResource("boundingbox_canned.json");

        // define a canned response
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (request.getPath().contains("/places/v1/addresses/bbox")){
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody(cannedResponse);
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        MockWebServer server = new MockWebServer();
        server.setDispatcher(dispatcher);
        server.start();

        HttpUrl baseUrl = server.url("/places/v1/");

        // configure
        final AddressesProvider addressesProvider = new AddressesProvider
                .Builder(API_KEY)
                .setAddressApi(getSearchApi(baseUrl.toString()))
                .queryBoundingBox(true)
                .queryFind(false)
                .queryNearest(false)
                .queryPostcode(false)
                .queryRadius(false)
                .queryUprn(false)
                .build();

        final double lat = 50.921709;
        final double lon = -1.401954;

        final List<SearchResult> results = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                results.addAll(addressesProvider.query("London", lat, lon).toBlocking().first());
                latch.countDown();
            }
        }).start();

        RecordedRequest request = server.takeRequest();
        String path = request.getPath();
        assertTrue(path.contains("/places/v1/addresses/bbox"));
        assertTrue(path.contains("bbox=50.920709,-1.4029539999999998,50.922709,-1.400954"));
        assertTrue(path.contains("srs=EPSG:4326"));
        assertTrue(path.contains("dataset=dpa"));
        assertTrue(path.contains("key=" + API_KEY));

        latch.await(5, TimeUnit.SECONDS);

        assertTrue(results.size() == 3);

        int actualRequestCount = server.getRequestCount();
        int expectedRequestedCount = 1;
        assertEquals(expectedRequestedCount, actualRequestCount);

        server.shutdown();
    }

    private AddressApi getSearchApi(String url) {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(AddressApi.class);
    }
}

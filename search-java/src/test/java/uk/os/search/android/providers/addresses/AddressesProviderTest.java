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

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import uk.os.search.android.providers.addresses.service.AddressApi;
import uk.os.search.android.providers.addresses.service.model.ServerResponse;

import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AddressesProviderTest {

    private static final int PARAM_MAX_RESULTS = 25;
    private static final int PARAM_RADIUS_METRES = 200;
    private static final String PARAM_SRS = "EPSG:4326";
    private static final String PARAM_OUTPUT_SRS = PARAM_SRS;

    private static final String API_KEY = "niceTry";
    private static final String BBOX_PARAM = "50.920709,-1.4029539999999998,50.922709,-1.400954";
    private static final String POINT = "50.921709,-1.401954";
    private static final String QUERY = "London";
    private static final String POSTCODE = "SO16 0AS";;
    private static final double USER_LAT = 50.921709;
    private static final double USER_LON = -1.401954;

    private static final Answer<Observable<ServerResponse>> SINGLE_ANSWER = getSingleAnswer();

    @Test
    public void queryBoundingBox() throws IOException, InterruptedException {
        AddressApi addressApi = Mockito.mock(AddressApi.class);
        when(addressApi.bbox(anyString(), anyString(), anyString(), anyString())).then(SINGLE_ANSWER);

        final AddressesProvider addressesProvider = new AddressesProvider
                .Builder(API_KEY)
                .setAddressApi(addressApi)
                .queryBoundingBox(true)
                .queryFind(false)
                .queryNearest(false)
                .queryPostcode(false)
                .queryRadius(false)
                .queryUprn(false)
                .build();

        addressesProvider.query(QUERY, USER_LAT, USER_LON).toBlocking().first();
        verify(addressApi, times(1)).bbox(API_KEY, BBOX_PARAM, PARAM_SRS, PARAM_OUTPUT_SRS);
        verify(addressApi, times(0)).find(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(0)).nearest(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(0)).postcode(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(0)).radius(anyString(), anyString(), anyString(), anyFloat(), anyString());
        verify(addressApi, times(0)).uprn(anyString(), anyString(), anyString());

        // query without lat/lon should not query bounding box API
        addressesProvider.query(QUERY).toBlocking().first();
        verify(addressApi, times(1)).bbox(API_KEY, BBOX_PARAM, PARAM_SRS, PARAM_OUTPUT_SRS);
    }

    @Test
    public void queryFind() throws IOException, InterruptedException {
        AddressApi addressApi = Mockito.mock(AddressApi.class);
        when(addressApi.find(anyString(), anyString(), anyInt(), anyString())).then(SINGLE_ANSWER);

        final AddressesProvider addressesProvider = new AddressesProvider
                .Builder(API_KEY)
                .setAddressApi(addressApi)
                .queryBoundingBox(false)
                .queryFind(true)
                .queryNearest(false)
                .queryPostcode(false)
                .queryRadius(false)
                .queryUprn(false)
                .build();

        addressesProvider.query(QUERY, USER_LAT, USER_LON).toBlocking().first();
        verify(addressApi, times(0)).bbox(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(1)).find(API_KEY, QUERY, PARAM_MAX_RESULTS, PARAM_OUTPUT_SRS);
        verify(addressApi, times(0)).nearest(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(0)).postcode(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(0)).radius(anyString(), anyString(), anyString(), anyFloat(), anyString());
        verify(addressApi, times(0)).uprn(anyString(), anyString(), anyString());

        addressesProvider.query(QUERY).toBlocking().first();
        verify(addressApi, times(2)).find(API_KEY, QUERY, PARAM_MAX_RESULTS, PARAM_OUTPUT_SRS);
    }

    @Test
    public void queryNearest() throws IOException, InterruptedException {
        AddressApi addressApi = Mockito.mock(AddressApi.class);
        when(addressApi.nearest(anyString(), anyString(), anyString(), anyString())).then(SINGLE_ANSWER);

        final AddressesProvider addressesProvider = new AddressesProvider
                .Builder(API_KEY)
                .setAddressApi(addressApi)
                .queryBoundingBox(false)
                .queryFind(false)
                .queryNearest(true)
                .queryPostcode(false)
                .queryRadius(false)
                .queryUprn(false)
                .build();

        addressesProvider.query(QUERY, USER_LAT, USER_LON).toBlocking().first();
        verify(addressApi, times(0)).bbox(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(0)).find(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(1)).nearest(API_KEY, POINT, PARAM_SRS, PARAM_OUTPUT_SRS);
        verify(addressApi, times(0)).postcode(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(0)).radius(anyString(), anyString(), anyString(), anyFloat(), anyString());
        verify(addressApi, times(0)).uprn(anyString(), anyString(), anyString());

        // nearest query without a lat/lon should not call backend
        addressesProvider.query(QUERY).toBlocking().first();
        verify(addressApi, times(1)).nearest(API_KEY, POINT, PARAM_SRS, PARAM_OUTPUT_SRS);
    }

    @Test
    public void queryPostcode() throws IOException, InterruptedException {
        AddressApi addressApi = Mockito.mock(AddressApi.class);
        when(addressApi.postcode(anyString(), anyString(), anyInt(), anyString())).then(SINGLE_ANSWER);

        final AddressesProvider addressesProvider = new AddressesProvider
                .Builder(API_KEY)
                .setAddressApi(addressApi)
                .queryBoundingBox(false)
                .queryFind(false)
                .queryNearest(false)
                .queryPostcode(true)
                .queryRadius(false)
                .queryUprn(false)
                .build();

        addressesProvider.query(POSTCODE, USER_LAT, USER_LON).toBlocking().first();
        verify(addressApi, times(0)).bbox(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(0)).find(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(0)).nearest(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(1)).postcode(API_KEY, POSTCODE, PARAM_MAX_RESULTS, PARAM_OUTPUT_SRS);
        verify(addressApi, times(0)).radius(anyString(), anyString(), anyString(), anyFloat(), anyString());
        verify(addressApi, times(0)).uprn(anyString(), anyString(), anyString());

        addressesProvider.query(POSTCODE).toBlocking().first();
        verify(addressApi, times(2)).postcode(API_KEY, POSTCODE, PARAM_MAX_RESULTS, PARAM_OUTPUT_SRS);
    }

    @Test
    public void queryRadius() throws IOException, InterruptedException {
        AddressApi addressApi = Mockito.mock(AddressApi.class);
        when(addressApi.radius(anyString(), anyString(), anyString(), anyFloat(), anyString())).then(SINGLE_ANSWER);

        final AddressesProvider addressesProvider = new AddressesProvider
                .Builder(API_KEY)
                .setAddressApi(addressApi)
                .queryBoundingBox(false)
                .queryFind(false)
                .queryNearest(false)
                .queryPostcode(false)
                .queryRadius(true)
                .queryUprn(false)
                .build();

        addressesProvider.query(QUERY, USER_LAT, USER_LON).toBlocking().first();

        verify(addressApi, times(0)).bbox(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(0)).find(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(0)).nearest(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(0)).postcode(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(1)).radius(API_KEY, POINT, PARAM_SRS, PARAM_RADIUS_METRES, PARAM_OUTPUT_SRS);
        verify(addressApi, times(0)).uprn(anyString(), anyString(), anyString());

        // a query without lat/lon should not trigger a radius call
        addressesProvider.query(QUERY).toBlocking().first();
        verify(addressApi, times(1)).radius(API_KEY, POINT, PARAM_SRS, PARAM_RADIUS_METRES, PARAM_OUTPUT_SRS);
    }

    @Test
    public void queryUprn() throws IOException, InterruptedException {
        String query = "123456789";

        AddressApi addressApi = Mockito.mock(AddressApi.class);
        when(addressApi.uprn(anyString(), anyString(), anyString())).then(SINGLE_ANSWER);

        final AddressesProvider addressesProvider = new AddressesProvider
                .Builder(API_KEY)
                .setAddressApi(addressApi)
                .queryBoundingBox(false)
                .queryFind(false)
                .queryNearest(false)
                .queryPostcode(false)
                .queryRadius(false)
                .queryUprn(true)
                .build();

        addressesProvider.query(query, USER_LAT, USER_LON).toBlocking().first();

        verify(addressApi, times(0)).bbox(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(0)).find(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(0)).nearest(anyString(), anyString(), anyString(), anyString());
        verify(addressApi, times(0)).postcode(anyString(), anyString(), anyInt(), anyString());
        verify(addressApi, times(0)).radius(anyString(), anyString(), anyString(), anyFloat(), anyString());
        verify(addressApi, times(1)).uprn(API_KEY, query, PARAM_OUTPUT_SRS);

        // second query without lat/lon context
        addressesProvider.query(query).toBlocking().first();
        verify(addressApi, times(2)).uprn(API_KEY, query, PARAM_OUTPUT_SRS);
    }

    private static Answer<Observable<ServerResponse>> getSingleAnswer() {
        return new Answer<Observable<ServerResponse>>() {
            @Override
            public Observable<ServerResponse> answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(Util.getServerResponse());
            }
        };
    }
}


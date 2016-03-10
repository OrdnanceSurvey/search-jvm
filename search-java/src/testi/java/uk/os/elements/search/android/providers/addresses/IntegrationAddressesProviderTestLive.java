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

package uk.os.elements.search.android.providers.addresses;

import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.os.elements.search.SearchResult;
import uk.os.elements.search.android.providers.addresses.service.AddressApi;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static uk.os.elements.search.android.providers.Util.getEnvironmentalVariable;

public class IntegrationAddressesProviderTestLive {

    private static final String ENV_KEY_DEFAULT = "undefined";
    private static final String API_KEY = getEnvironmentalVariable("OS_PLACES_API_KEY", ENV_KEY_DEFAULT);

    @Test
    public void queryDowningStreet() {
        AddressesProvider provider = new AddressesProvider.Builder(API_KEY).setAddressApi(provideSearchApi()).build();
        List<SearchResult> result = provider.query("10 Downing Street, London").toBlocking().first();
        assertTrue(result.size() > 0);
    }

    private static AddressApi provideSearchApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.ordnancesurvey.co.uk/places/v1/addresses/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(AddressApi.class);
    }
}

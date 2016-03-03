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

import rx.Observable;
import rx.functions.Func1;
import uk.os.elements.search.SearchResult;
import uk.os.elements.search.android.providers.Provider;
import uk.os.elements.search.android.providers.addresses.service.AddressApi;
import uk.os.elements.search.android.providers.addresses.service.ParamFormatting;
import uk.os.elements.search.android.providers.addresses.service.model.ServerResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddressesProvider implements Provider {

    public static class Builder {

        private String mKey;
        private AddressApi mAddressApi;
        private boolean mQueryBoundingBox = false;
        private boolean mQueryFind = true;
        private boolean mQueryPostcode = true;
        private boolean mQueryRadius = false;
        private boolean mQueryNearest = false;
        private boolean mQueryUprn = true;

        public Builder(String sKeyOpenNames, AddressApi addressApi) {
            mKey = sKeyOpenNames;
            mAddressApi = addressApi;
        }

        public Builder queryPostcode(boolean value) {
            mQueryPostcode = value;
            return this;
        }

        public Builder queryUprn(boolean value) {
            mQueryUprn = value;
            return this;
        }

        public Builder queryFind(boolean value) {
            mQueryFind = value;
            return this;
        }

        public Builder queryBoundingBox(boolean value) {
            mQueryBoundingBox = value;
            return this;
        }

        public Builder queryRadius(boolean value) {
            mQueryRadius = value;
            return this;
        }

        public Builder queryNearest(boolean value) {
            mQueryNearest = value;
            return this;
        }

        public AddressesProvider build() {
            return new AddressesProvider(mKey, mAddressApi, mQueryBoundingBox, mQueryFind, mQueryNearest,
                    mQueryPostcode, mQueryRadius, mQueryUprn);
        }
    }

    private final AddressApi mAddressApi;
    private final String mKey;
    private final boolean mQueryPostcode;
    private final boolean mQueryUprn;
    private final boolean mQueryFind;
    private final boolean mQueryBoundingBox;
    private final boolean mQueryRadius;
    private final boolean mQueryNearest;

    private AddressesProvider(String sKeyOpenNames, AddressApi addressApi, boolean queryBoundingBox, boolean queryFind,
                             boolean queryNearest, boolean queryPostcode, boolean queryRadius, boolean queryUprn) {
        mKey = sKeyOpenNames;
        mAddressApi = addressApi;

        mQueryBoundingBox = queryBoundingBox;
        mQueryFind = queryFind;
        mQueryNearest = queryNearest;
        mQueryPostcode = queryPostcode;
        mQueryRadius = queryRadius;
        mQueryUprn = queryUprn;
    }

    public Observable<List<SearchResult>> query(String searchTerm) {
        List<Observable<ServerResponse>> responses = queryProviders(searchTerm);
        return Transform.toSearchResults(responses).map(deduplicate()).defaultIfEmpty(new ArrayList<SearchResult>());
    }

    public Observable<List<SearchResult>> query(String searchTerm, double lat, double lon) {
        List<Observable<ServerResponse>> responses = queryProviders(searchTerm);
        responses.addAll(queryProviders(lat, lon));

        return Transform.toSearchResults(responses).map(deduplicate()).defaultIfEmpty(new ArrayList<SearchResult>());
    }

    private List<Observable<ServerResponse>> queryProviders(String searchTerm) {
        List<Observable<ServerResponse>> list = new ArrayList<>();

        if (mQueryFind) {
            list.add(mAddressApi.find(mKey, searchTerm));
        }
        if (mQueryPostcode && GeoPattern.isPostcodeCandidate(searchTerm)) {
            list.add(mAddressApi.postcode(mKey, searchTerm));
        }
        if (mQueryUprn && GeoPattern.isUprnCandidate(searchTerm)) {
            list.add(mAddressApi.uprn(mKey, searchTerm));
        }
        return list;
    }

    private List<Observable<ServerResponse>> queryProviders(double lat, double lon) {
        List<Observable<ServerResponse>> list = new ArrayList<>();

        if (mQueryNearest) {
            list.add(mAddressApi.nearest(mKey, ParamFormatting.point(lat, lon)));
        }

        if (mQueryBoundingBox) {
            list.add(mAddressApi.bbox(mKey, ParamFormatting.bbox(lat - 0.001, lon - 0.001, lat + 0.001, lon + 0.001)));
        }

        if (mQueryRadius) {
            list.add(mAddressApi.radius(mKey, ParamFormatting.point(lat, lon)));
        }

        return list;
    }

    private Func1<List<SearchResult>, List<SearchResult>> deduplicate() {
        return new Func1<List<SearchResult>, List<SearchResult>>() {
            @Override
            public List<SearchResult> call(List<SearchResult> results) {
                Set<String> set = new HashSet<>();
                List<SearchResult> betterResult = new ArrayList<>();

                for (SearchResult result : results) {
                    boolean isDuplicate = set.contains(result.getId());
                    if (!isDuplicate) {
                        betterResult.add(result);
                        set.add(result.getId());
                    }
                }
                return betterResult;
            }
        };
    }
}

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

import com.esri.core.geometry.Envelope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;
import uk.os.search.SearchResult;
import uk.os.search.android.providers.Provider;
import uk.os.search.android.providers.opennames.service.SearchApi;
import uk.os.search.android.providers.opennames.service.model.Result;
import uk.os.search.android.providers.opennames.service.model.ServerResponse;

public class OpennamesProvider implements Provider {

    public static class Builder {

        private String mKey;
        private SearchApi mSearchApi;

        public Builder(String key) {
            mKey = key;
        }

        public Builder setSearchApi(SearchApi searchApi) {
            mSearchApi = searchApi;
            return this;
        }

        public OpennamesProvider build() {
            return new OpennamesProvider(mKey, mSearchApi == null ? provideSearchApi() : mSearchApi);
        }
    }

    private final String mKey;
    private final SearchApi mSearchApi;

    private OpennamesProvider(String key, SearchApi searchApi) {
        mKey = key;
        mSearchApi = searchApi;
    }

    public Observable<List<SearchResult>> query(String searchTerm) {
        return mSearchApi.search(mKey, searchTerm).map(new Func1<ServerResponse, List<SearchResult>>() {
            @Override
            public List<SearchResult> call(ServerResponse serverResponse) {
                int capacity = serverResponse.getResults().size();
                List<SearchResult> list = new ArrayList<>(capacity);

                List<Result> results = serverResponse.getResults();
                for (Result result : results) {
                    SearchResult searchResult = from(result);
                    list.add(searchResult);
                }

                // TODO: consider - this should probably be the last operation as we probably
                // TODO: do not want the recent value duplicated.  This is closer to formatting.
                return markDuplicates(list);
            }
        });
    }

    private SearchResult from(Result result) {
        Result.GazetteerEntry entry = result.getGazetteerEntry();

        String id = entry.getID();
        String name = entry.getName();
        String context = getContext(entry);

        Envelope envelope = null;

        if (entry.hasBoundingBox()) {
            double minx = Double.parseDouble(entry.getMBR_XMIN());
            double miny = Double.parseDouble(entry.getMBR_YMIN());
            double maxx = Double.parseDouble(entry.getMBR_XMAX());
            double maxy = Double.parseDouble(entry.getMBR_YMAX());
            envelope = new Envelope(minx, miny, maxx, maxy);
        }

        double x = Double.parseDouble(entry.getGEOMETRY_X());
        double y = Double.parseDouble(entry.getGEOMETRY_Y());
        return new SearchResult(id, name, context, new Point(x, y), envelope,
                SpatialReference.create(27700));
    }

    private String getContext(Result.GazetteerEntry entry) {
        List<String> contextArray = new ArrayList<>();

        String districtBorough = entry.getDISTRICT_BOROUGH();
        if (districtBorough != null) {
            districtBorough = districtBorough.replaceFirst("City and County of the City of London", "London");
            contextArray.add(districtBorough);
        }

        String populatedPlace = entry.getPOPULATED_PLACE();
        if (populatedPlace != null) {
            populatedPlace = populatedPlace.replaceFirst("City of", "");
            contextArray.add(populatedPlace);
        }

        String countyUnitary = entry.getCOUNTY_UNITARY();
        if (countyUnitary != null) {
            countyUnitary = countyUnitary.replaceFirst("Greater London", "London");
            countyUnitary = countyUnitary.replaceFirst("City of ","");
            contextArray.add(countyUnitary);
        }

        contextArray.add(entry.getREGION());
        contextArray.add(entry.getCOUNTRY());
        contextArray.add(entry.getPOSTCODE_DISTRICT());

        String previousAcceptable = "";
        StringBuilder sb = new StringBuilder();
        for (String partial : contextArray) {
            if (partial == null || previousAcceptable.equals(partial)) {
                continue;
            }
            previousAcceptable = partial;
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(partial);
        }

        return sb.toString();
    }

    private List<SearchResult> markDuplicates(List<SearchResult> results) {
        Map<String, SearchResult> map = new HashMap<>();
        Map<String, Integer> counter = new HashMap<>();

        List<SearchResult> betterList = new ArrayList<SearchResult>();
        for (SearchResult result : results) {
            String key = result.getName() + ":" + result.getContext();

            boolean hasKey = map.containsKey(key);
            if (!hasKey) {
                map.put(result.getName() + ":" + result.getContext(), result);
                betterList.add(result);
            } else {
                int duplicateNo = 1;
                if (counter.containsKey(key)) {
                    duplicateNo = counter.get(key) + 1;
                }
                counter.put(key, duplicateNo);

                SearchResult newSearchResult = new SearchResult(result.getId(),
                        result.getName() + " (" + duplicateNo + ")", result.getContext(), result.getPoint(),
                        result.getEnvelope(), result.getSpatialReference());
                betterList.add(newSearchResult);
            }
        }
        return betterList;
    }

    private static SearchApi provideSearchApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.ordnancesurvey.co.uk/opennames/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(SearchApi.class);
    }
}

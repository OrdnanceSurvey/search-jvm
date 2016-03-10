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

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.FuncN;
import uk.os.elements.search.SearchResult;
import uk.os.elements.search.android.providers.addresses.service.model.Result;
import uk.os.elements.search.android.providers.addresses.service.model.ServerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Transform {

    protected static Observable<List<SearchResult>> toSearchResults(List<Observable<ServerResponse>> results) {

        // Transform list of ServerResponses to a list of SearchResults
        List<Observable<List<SearchResult>>> transformedResults = new ArrayList<>();
        for (Observable<ServerResponse> result : results) {
            Observable<List<SearchResult>> transformed = result.map(searchResultsFromServerResponse());
            transformedResults.add(transformed);
        }

        // Zip SearchResult lists
        Observable<List<SearchResult>> zipped = Observable.zip(transformedResults, new FuncN<List<SearchResult>>() {

            @SuppressWarnings("unchecked")
            @Override
            public List<SearchResult> call(Object... args) {
                List<SearchResult> result = new ArrayList<>();
                for (Object obj : args) {
                    result.addAll((List<SearchResult>) obj);
                }
                return result;
            }
        });

        return zipped;
    }

    private static SearchResult from(final SpatialReference spatialReference, final Result result) {
        if (result.getGazetteerEntry() == null) {
            throw new IllegalArgumentException("no gazetteer data");
        }
        final String addressLine = result.getGazetteerEntry().getADDRESS();

        String name;
        String context;
        Pattern pattern = Pattern.compile("((?:\\d*,[^,]*)|[^,]*),(.*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result.getGazetteerEntry().getADDRESS());
        if (matcher.matches()) {
            name = matcher.group(1).replaceAll(",", "");
            context = matcher.group(2).trim();
        } else {
            // log weirdness?
            final int indexTerminate = addressLine.length() - 1;
            name = addressLine.substring(0, Math.min(10, indexTerminate)) + "...";
            context = addressLine.substring(Math.min(10, indexTerminate), indexTerminate);
        }

        Result.Dpa entry = result.getGazetteerEntry();
        String id = entry.getUPRN();
        double x = Double.parseDouble(entry.getX_COORDINATE());
        double y = Double.parseDouble(entry.getY_COORDINATE());

        return new SearchResult(id, name, context, new Point(x, y), new Envelope(), spatialReference);
    }

    private static SpatialReference parseSpatialReference(String input) {
        try {
            String wkid = input.toLowerCase().replace("epsg:", "");
            return SpatialReference.create(Integer.parseInt(wkid));
        } catch (Exception e) {
            throw new IllegalArgumentException("unsupported SRID");
        }
    }

    private static Func1<ServerResponse, List<SearchResult>> searchResultsFromServerResponse() {
        return new Func1<ServerResponse, List<SearchResult>>() {
            @Override
            public List<SearchResult> call(ServerResponse serverResponse) {
                int capacity = serverResponse.getResults().size();
                List<SearchResult> list = new ArrayList<>(capacity);

                String srs = serverResponse.getHeader().getOutput_srs();
                SpatialReference spatialReference = parseSpatialReference(srs);

                List<Result> results = serverResponse.getResults();
                for (Result result : results) {
                    SearchResult searchResult = from(spatialReference, result);
                    list.add(searchResult);
                }
                return list;
            }
        };
    }
}

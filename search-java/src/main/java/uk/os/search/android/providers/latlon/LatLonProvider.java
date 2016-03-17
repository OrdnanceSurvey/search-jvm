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

package uk.os.search.android.providers.latlon;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import uk.os.search.SearchResult;
import uk.os.search.android.providers.Provider;

public class LatLonProvider implements Provider{
    @Override
    public Observable<List<SearchResult>> query(final String searchTerm) {
        return Observable.create(new Observable.OnSubscribe<List<SearchResult>>() {
            @Override
            public void call(Subscriber<? super List<SearchResult>> subscriber) {
                try {
                    List<SearchResult> result = new ArrayList<>();

                    try {
                        Point ref = GeoPattern.parseLatLon(searchTerm);
                        String id = String.format("lat: %s lon: %s", ref.getY(), ref.getX());
                        String name = DmsConverter.getLatitude(ref.getY(), DmsConverter.FORMAT_SECONDS) + " " +
                                DmsConverter.getLongitude(ref.getX(), DmsConverter.FORMAT_SECONDS);
                        String context = String.format("%.6f", ref.getY()) + ", " + String.format("%.6f", ref.getX());
                        result.add(new LatLonResult(id, name, context, new Point(ref.getX(), ref.getY()),
                                new Envelope(), SpatialReference.create(4326)));
                    } catch (IllegalArgumentException ignore) {};

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(result);
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }
}

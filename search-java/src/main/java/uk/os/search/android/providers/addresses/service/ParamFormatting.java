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

package uk.os.search.android.providers.addresses.service;

public class ParamFormatting {

    private ParamFormatting() { }

    public static String bbox(double ll_lat, double ll_lon,
                       double ur_lat, double ur_lon) {
        return ll_lat + "," + ll_lon + "," + ur_lat + "," + ur_lon;
    }

    public static String point(double lat, double lon) {
        return lat + "," + lon;
    }
}

/**
 * TODO consider types
 *
 * Gson gson = new GsonBuilder()
 * .register(Point.class, new MyPointTypeAdapter())
 * .enableComplexMapKeySerialization()
 * .create();
 *
 * Map<Point, String> original = new LinkedHashMap<Point, String>();
 * original.put(new Point(5, 6), "a");
 * original.put(new Point(8, 8), "b");
 * System.out.println(gson.toJson(original, type));
 *
 */
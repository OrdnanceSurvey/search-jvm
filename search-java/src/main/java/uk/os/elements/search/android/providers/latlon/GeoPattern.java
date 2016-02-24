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

package uk.os.elements.search.android.providers.latlon;

import com.esri.core.geometry.Point;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoPattern {

    private GeoPattern() {}

    public static Point parseLatLon(String searchTerm) {
        if (searchTerm == null) {
            throw new IllegalArgumentException("null search term!");
        }
        // a fairly lazy initial regex to have a punt - input might be decimal or DMS (using degrees symbol or colon)
        Pattern pattern = Pattern.compile("^([°( ?)|\"( ?)|'( ?)|\\w|.|:|-]*)[, ]+([°( ?)|\"( ?)|'( ?)|\\w|.|:|-]*)$");
        Matcher matcher = pattern.matcher(searchTerm.trim());
        if (matcher.matches()) {
            String possiblelatitude = matcher.group(1);
            String possibleLongitude = matcher.group(2);
            try {
                double latitude = DmsConverter.getLatitude(possiblelatitude);
                double longitude = DmsConverter.getLongitude(possibleLongitude);
                return new Point(longitude, latitude);
            } catch (Exception garbageInput) {
            }
        }
        throw new IllegalArgumentException("unsupported latitude / longitude format:" + searchTerm);
    }
}

/**
 * Developer notes:
 * 1) Initially approached using the following.  That, however, was not flexible enough once other standard DMS formats
 *    were introduced as demonstrated in the tests.
 *
 *    // have a first punt to see if decimal degrees
 *    // http://stackoverflow.com/questions/3518504/regular-expression-for-matching-latitude-longitude-coordinates
 *    Pattern pattern = Pattern.compile("^([-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)),?\\s*([-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?))$");
 *    Matcher matcher = pattern.matcher(searchTerm);
 *    if (matcher.find()) {
 *          double latitude = Double.parseDouble(matcher.group(1));
 *          double longitude = Double.parseDouble(matcher.group(5));
 *          return new Point(longitude, latitude);
 *    }
 */

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

import com.esri.core.geometry.Point;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class GeoPatternTest {

    @Test
    public void garbage() {
        garbageCheck("51 181");
        garbageCheck("51°30'26.0\"N, 180°07'39.0\"W");
        garbageCheck("51°30'26.0\"N, 181°07'39.0\"W");
        garbageCheck("51°30.433'N, 181°7.65'W");
        garbageCheck("51.50722°N, 181.1275°W");

        garbageCheck("-91 110");
        garbageCheck("91°30'26.0\"S, 110°07'39.0\"W");
        garbageCheck("91°30'26.0\"S, 110°07'39.0\"W");
        garbageCheck("91°30.433'S, 110°7.65'W");
        garbageCheck("91.50722°S, 110.1275°W");
    }

    @Test
    public void testLondon() {
        check(51.50722, -0.1275, "51°30'26.0\"N, 0°07'39.0\"W");
        check(51.50722, -0.1275, "51° 30' 26.0\"N, 0° 07' 39.0\"W");
        check(51.50722, -0.1275, "51:30:26:0, -0:07:39:0");
        check(51.50722, -0.1275, "51°30.433'N, 0°7.65'W");
        check(51.50722, -0.1275, "51.50722°N, 0.1275°W");

        checkCommaSpace(51.50722, -0.1275, "51.50722, -0.1275");
        checkCommaDoubleSpace(51.50722, -0.1275, "51.50722,  -0.1275");
        checkComma(51.50722, -0.1275, "51.50722,-0.1275");
        checkSpace(51.50722, -0.1275, "51.50722 -0.1275");
    }

    @Test
    public void testLondonCoarse() {
        checkCommaSpace(51, 0, "51.0, 0.0");
        checkCommaDoubleSpace(51, 0, "51.0,  0.0");
        checkComma(51, 0, "51.0,0.0");
        checkSpace(51, 0, "51.0 0.0");
    }

    @Test
    public void testSanFrancisco() {
        check(37.793953, -122.398715, "37°47'38.2\"N, 122°23'55.4\"W");
        check(37.793953, -122.398715, "37°47.637'N 122°23.923'W");
        check(37.793953, -122.398715, "37.79395°N 122.39871°W");

        checkCommaSpace(37.793953, -122.398715, "37.793953, -122.398715");
        checkCommaDoubleSpace(37.793953, -122.398715, "37.793953,  -122.398715");
        checkComma(37.793953, -122.398715, "37.793953,-122.398715");
        checkSpace(37.793953, -122.398715, "37.793953 -122.398715");
    }

    @Test
    public void testSydney() {
        check(-33.858306, 151.214944, "33°51'29.9\"S 151°12'53.8\"E");
        check(-33.858306, 151.214944, "33°51.498'S 151°12.897'E");
        check(-33.858306, 151.214944, "33.8583°S 151.21495°E");

        checkCommaSpace(-33.858306, 151.214944, "-33.858306, 151.214944");
        checkCommaDoubleSpace(-33.858306, 151.214944, "-33.858306,  151.214944");
        checkComma(-33.858306, 151.214944, "-33.858306,151.214944");
        checkSpace(-33.858306, 151.214944, "-33.858306 151.214944");
    }

    @Test
    public void testTianjin() {
        check(39.105435, 117.219939, "39°06'19.6\"N 117°13'11.8\"E");
        check(39.105435, 117.219939, "39°6.326'N 117°13.196'E");
        check(39.105435, 117.219939, "39.10543°N 117.21994°E");

        checkCommaSpace(39.105435, 117.219939, "39.105435, 117.219939");
        checkCommaDoubleSpace(39.105435, 117.219939, "39.105435,  117.219939");
        checkComma(39.105435, 117.219939, "39.105435,117.219939");
        checkSpace(39.105435, 117.219939, "39.105435 117.219939");
    }

    @Test
    public void testNull() {
        try {
            GeoPattern.parseLatLon(null);
            fail("null should throw an exception for the moment");
        } catch (IllegalArgumentException ignore){}
        catch (Exception failure){
            fail("null should throw an exception for the moment");
        }
    }

    @Test
    public void testTrim() {
        check(51.50722, -0.1275, " 51°30'26.0\"N, 0°07'39.0\"W ");
        check(51.50722, -0.1275, " 51° 30' 26.0\"N, 0° 07' 39.0\"W ");
        check(51.50722, -0.1275, "51:30:26:0, -0:07:39:0 ");
        check(51.50722, -0.1275, " 51°30.433'N, 0°7.65'W");
        check(51.50722, -0.1275, " 51.50722°N, 0.1275°W ");
    }

    private void check(double latitude, double longitude, String query) {
        Point result = GeoPattern.parseLatLon(query);
        assertNotNull(result);
        assertEquals(latitude, result.getY(), 0.0001);
        assertEquals(longitude, result.getX(), 0.0001);
    }

    private void checkComma(double latitude, double longitude, String query) {
        String input = latitude + "," + longitude;
        assertEquals(query, input);

        Point result = GeoPattern.parseLatLon(query);
        assertEquals(latitude, result.getY(), 0.0001);
        assertEquals(longitude, result.getX(), 0.0001);
    }

    private void checkCommaSpace(double latitude, double longitude, String query) {
        String input = latitude + ", " + longitude;
        assertEquals(query, input);

        Point result = GeoPattern.parseLatLon(query);
        assertEquals(latitude, result.getY(), 0.0001);
        assertEquals(longitude, result.getX(), 0.0001);
    }

    private void checkCommaDoubleSpace(double latitude, double longitude, String query) {
        String input = latitude + ",  " + longitude;
        assertEquals(query, input);

        Point result = GeoPattern.parseLatLon(query);
        assertEquals(latitude, result.getY(), 0.0001);
        assertEquals(longitude, result.getX(), 0.0001);
    }

    private void checkSpace(double latitude, double longitude, String query) {
        String input = latitude + " " + longitude;
        assertEquals(query, input);

        Point result = GeoPattern.parseLatLon(query);
        assertEquals(latitude, result.getY(), 0.0001);
        assertEquals(longitude, result.getX(), 0.0001);
    }

    private void garbageCheck(String query) {
        try {
            GeoPattern.parseLatLon(query);
            fail("garbage should throw an exception for the moment");
        } catch (IllegalArgumentException ignore){}
    }
}


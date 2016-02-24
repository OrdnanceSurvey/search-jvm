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

import org.junit.Test;

import java.util.List;

import uk.os.elements.search.SearchResult;

import static org.junit.Assert.assertEquals;

public class LatLonProviderTest {

    private static final double DELTA = 0.00001;

    @Test
    public void london() {
        checkit("51.50722, -0.1275", "51°30'26.0\"N 0°07'39.0\"W", "51.507220, -0.127500");
        parseIt("51°30'26.0\"N 0°07'39.0\"W", 51.50722, -0.1275);
    }

    @Test
    public void sydney() {
        checkit("-33.858297, 151.214949", "33°51'29.9\"S 151°12'53.8\"E");
        parseIt("33°51'29.9\"S 151°12'53.8\"E", -33.858297, 151.214949);
    }

    @Test
    public void sanFrancisco() {
        checkit("37.793953, -122.398715", "37°47'38.2\"N 122°23'55.4\"W");
        parseIt("37°47'38.2\"N 122°23'55.4\"W", 37.793953, -122.398715);
    }

    @Test
    public void tianjin() {
        checkit("39.105435, 117.219939", "39°06'19.6\"N 117°13'11.8\"E");
        parseIt("39°06'19.6\"N 117°13'11.8\"E", 39.105435, 117.219939);
    }

    @Test
    public void zero() {
        checkit("0, 0", "0°00'00.0\"N 0°00'00.0\"E", "0.000000, 0.000000");
        parseIt("0°00'00.0\"N 0°00'00.0\"E", 0, 0);
    }

    @Test
    public void max() {
        checkit("90, 180", "90°00'00.0\"N 180°00'00.0\"E", "90.000000, 180.000000");
        parseIt("90°00'00.0\"N 180°00'00.0\"E", 90, 180);
    }

    @Test
    public void maxOverLat() {
        checkEmpty("91, 180");
    }

    @Test
    public void maxOverLat2() {
        checkEmpty("91°00'00.0\"N 180°00'00.0\"E");
    }

    @Test
    public void maxOverLon() {
        checkEmpty("90, 181");
    }

    @Test
    public void maxOverLon2() {
        checkEmpty("90°00'00.0\"N 181°00'00.0\"E");
    }

    @Test
    public void min() {
        checkit("-90, -180", "90°00'00.0\"S 180°00'00.0\"W", "-90.000000, -180.000000");
        parseIt("90°00'00.0\"S 180°00'00.0\"W", -90, -180);
    }

    @Test
    public void minUnderLat() {
        checkEmpty("-91, 180");
    }

    @Test
    public void minUnderLat2() {
        checkEmpty("91°00'00.0\"S 180°00'00.0\"W");
    }

    @Test
    public void minUnderLon() {
        checkEmpty("90, -181");
    }

    @Test
    public void minUnderLon2() {
        checkEmpty("90°00'00.0\"S 181°00'00.0\"W");
    }

    @Test
    public void parseDegrees() {
        parseIt("90°N 180°E", 90, 180);
        parseIt("90°S 180°W", -90, -180);
        parseIt("0°N 0°E", 0, 0);
    }

    @Test
    public void parseDegreesOutOfRange() {
        checkEmpty("91°N 180°E");
        checkEmpty("91°S 180°E");
        checkEmpty("90°N 181°E");
        checkEmpty("90°N 181°W");
    }

    @Test
    public void parseDegreesMinutes() {
        parseIt("89°30'N 179°30'E", 89.5, 179.5);
        parseIt("89°30'S 179°30'W", -89.5, -179.5);
    }

    @Test
    public void parseDegreesMinutesGarbage() {
        // should not convert west to east
        checkEmpty("40°30'W 40°30'E");
        checkEmpty("40°30'E 40°30'W");
        checkEmpty("40°30'N 40°30'S");
        checkEmpty("40°30'S 40°30'N");
    }

    private void checkit(String query, String expected) {
        checkit(query, expected, "USE_QUERY");
    }

    private void checkit(String query, String expected, String expectedContext) {
        LatLonProvider latLonProvider = new LatLonProvider();
        List<SearchResult> a = latLonProvider.query(query).toBlocking().single();
        int expectedSize = 1;
        int actualSize = a.size();
        assertEquals(expectedSize, actualSize);

        SearchResult searchResult = a.get(0);
        String actual = searchResult.getName();
        assertEquals(expected, actual);

        if (expectedContext.equals("USE_QUERY")) {
            expectedContext = query;
        }

        String actualContext = searchResult.getContext();
        assertEquals(expectedContext, actualContext);
    }

    private void checkEmpty(String dmsQuery) {
        LatLonProvider latLonProvider = new LatLonProvider();
        List<SearchResult> a = latLonProvider.query(dmsQuery).toBlocking().single();
        int expectedSize = 0;
        int actualSize = a.size();
        assertEquals(expectedSize, actualSize);
    }

    private void parseIt(String dmsQuery, double expectedLatitude, double expectedLongitude) {
        LatLonProvider latLonProvider = new LatLonProvider();
        List<SearchResult> a = latLonProvider.query(dmsQuery).toBlocking().single();
        int expectedSize = 1;
        int actualSize = a.size();
        assertEquals(expectedSize, actualSize);

        SearchResult searchResult = a.get(0);
        double actualLongitude = searchResult.getPoint().getX();
        double actualLatitude = searchResult.getPoint().getY();

        assertEquals(expectedLongitude, actualLongitude, DELTA);
        assertEquals(expectedLatitude, actualLatitude, DELTA);
    }
}

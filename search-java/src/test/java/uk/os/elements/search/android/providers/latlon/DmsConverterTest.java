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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DmsConverterTest {

    private static final String GARBAGE_S = "GARBAGE";
    private static final double GARBAGE = Integer.MIN_VALUE;
    private static final double DELTA = 0.00001;

    // SPOT CHECKS

    @Test
    public void london() {
        toDms(51.50722, -0.1275, "51°30'26.0\"N", "0°07'39.0\"W");
        toDm(51.50722, -0.1275, "51°30.433'N", "0°7.65'W");
        toD(51.50722, -0.1275, "51.50722°N", "0.1275°W");

        fromDms("51°30'26.0\"N", "0°07'39.0\"W", 51.50722, -0.1275);
        fromDms("51°30'26.0\" N", "0°07'39.0\" W", 51.50722, -0.1275);
        fromDms("51° 30' 26.0\"N", "0° 07' 39.0\"W", 51.50722, -0.1275);
        fromDms("51:30:26.0", "-0:07:39.0", 51.50722, -0.1275);
        fromDm("51°30.433'N", "0°7.65'W", 51.50722, -0.1275);
        fromDm("51° 30.433'N", "0° 7.65'W", 51.50722, -0.1275);
        fromDm("51:30.433", "-0:7.65", 51.50722, -0.1275);
        fromD("51.50722°N", "0.1275°W", 51.50722, -0.1275);
        fromD("51.50722° N", "0.1275° W", 51.50722, -0.1275);
        fromD("51.50722", "-0.1275", 51.50722, -0.1275);
    }

    @Test
    public void sydney() {
        toDms(-33.858297, 151.214949, "33°51'29.9\"S", "151°12'53.8\"E");
        toDm(-33.858297, 151.214949, "33°51.498'S", "151°12.897'E");
        toD(-33.858297, 151.214949, "33.8583°S", "151.21495°E");

        fromDms("33°51'29.9\"S", "151°12'53.8\"E", -33.858297, 151.214949);
        fromDm("33°51.498'S", "151°12.897'E", -33.858297, 151.214949);
        fromD("33.8583°S", "151.21495°E", -33.858297, 151.214949);
    }

    @Test
    public void sanFrancisco() {
        toDms(37.793953, -122.398715, "37°47'38.2\"N", "122°23'55.4\"W");
        toDm(37.793953, -122.398715, "37°47.637'N", "122°23.923'W");
        toD(37.793953, -122.398715, "37.79395°N", "122.39871°W");

        fromDms("37°47'38.2\"N", "122°23'55.4\"W", 37.793953, -122.398715);
        fromDms("37° 47' 38.2\"N", "122° 23' 55.4\" W", 37.793953, -122.398715);
        fromDm("37°47.637' N", "122°23.923'W", 37.793953, -122.398715);
        fromDm("37° 47.637' N", "122° 23.923' W", 37.793953, -122.398715);
        fromD("37.79395°N", "122.39871°W", 37.793953, -122.398715);
        fromD("37.79395° N", "122.39871° W", 37.793953, -122.398715);
    }

    @Test
    public void tianjin() {
        toDms(39.105435, 117.219939, "39°06'19.6\"N", "117°13'11.8\"E");
        toDm(39.105435, 117.219939, "39°6.326'N", "117°13.196'E");
        toD(39.105435, 117.219939, "39.10543°N", "117.21994°E");

        fromDms("39°06'19.6\"N", "117°13'11.8\"E", 39.105435, 117.219939);
        fromDms("39° 06' 19.6\" N", "117° 13' 11.8\" E", 39.105435, 117.219939);
        fromDm("39°6.326'N", "117°13.196'E", 39.105435, 117.219939);
        fromDm("39° 6.326' N", "117° 13.196' E", 39.105435, 117.219939);
        fromD("39.10543°N", "117.21994°E", 39.105435, 117.219939);
        fromD("39.10543 °N", "117.21994 °E", 39.105435, 117.219939);
    }

    // EO SPOT CHECKS

    @Test
    public void toDegreesString() {
        double longitude = -122.61458;
        String longitudeExpected = "122.61458°W";
        double latitude = 32.30642;
        String latitudeExpected = "32.30642°N";
        toD(latitude, longitude, latitudeExpected, longitudeExpected);
    }

    @Test
    public void toDegreesMinutesString() {
        double longitude = -122.614583;
        String longitudeExpected = "122°36.875'W";
        double latitude = 32.306417;
        String latitudeExpected = "32°18.385'N";
        toDm(latitude, longitude, latitudeExpected, longitudeExpected);
    }

    @Test
    public void toDegreesMinutesSecondsString() {
        double latitude = 32.30642;
        String latitudeExpected = "32°18'23.1\"N";
        double longitude = -122.61458;
        String longitudeExpected = "122°36'52.5\"W";
        toDms(latitude, longitude, latitudeExpected, longitudeExpected);
    }

    @Test
    public void fromDegreesString() {
        String longitude = "122.61458°W";
        double longitudeExpected = -122.61458;
        String latitude = "32.30642°N";
        double latitudeExpected = 32.30642;
        fromD(latitude, longitude, latitudeExpected, longitudeExpected);
    }

    @Test
    public void fromDegreesMinutesString() {
        String longitude = "122°36.875'W";
        double longitudeExpected = -122.614583;
        String latitude = "32°18.385'N";
        double latitudeExpected = 32.306417;
        fromDm(latitude, longitude, latitudeExpected, longitudeExpected);
    }

    @Test
    public void fromDegreesMinutesSecondsString() {
        String latitude = "32°18'23.1\"N";
        double latitudeExpected = 32.30642;
        String longitude = "122°36'52.5\"W";
        double longitudeExpected = -122.61458;
        fromDms(latitude, longitude, latitudeExpected, longitudeExpected);
    }

    @Test
    public void max() {
        toD(90, 180, "90°N", "180°E");
        fromD("90°N", "180°E", 90, 180);
        toDm(90, 180, "90°0'N", "180°0'E");
        fromDm("90°0'N", "180°0'E", 90, 180);
        toDms(90, 180, "90°00'00.0\"N", "180°00'00.0\"E");
        fromDms("90°00'00.0\"N", "180°00'00.0\"E", 90, 180);
    }

    @Test
    public void maxOver() {
        toDShouldFail(91, 180, GARBAGE_S, "180°E");
        fromDShouldFail("91°N", "180°E", GARBAGE, 180);
        toDmShouldFail(91, 180, GARBAGE_S, "180°0'E");
        fromDmShouldFail("91°0'N", "180°0'E", GARBAGE, 180);
        toDmsShouldFail(91, 180, GARBAGE_S, "180°00'00.0\"E");
        fromDmsShouldFail("91°00'00.0\"N", "180°00'00.0\"E", GARBAGE, 180);

        toDShouldFail(90, 181, "90°N", GARBAGE_S);
        fromDShouldFail("90°N", "181°E", 90, GARBAGE);
        toDmShouldFail(90, 181, "90°0'N", GARBAGE_S);
        fromDmShouldFail("90°0'N", "181°0'E", 90, GARBAGE);
        toDmsShouldFail(90, 181, "90°00'00.0\"N", GARBAGE_S);
        fromDmsShouldFail("90°00'00.0\"N", "181°00'00.0\"E", 90, GARBAGE);
    }

    @Test
    public void minUnder() {
        toDShouldFail(-91, 180, GARBAGE_S, "180°E");
        fromDShouldFail("91°S", "180°E", GARBAGE, 180);
        toDmShouldFail(-91, 180, GARBAGE_S, "180°0'E");
        fromDmShouldFail("91°0'S", "180°0'E", GARBAGE, 180);
        toDmsShouldFail(-91, 180, GARBAGE_S, "180°00'00.0\"E");
        fromDmsShouldFail("91°00'00.0\"S", "180°00'00.0\"E", GARBAGE, 180);

        toDShouldFail(90, -181, "90°N", GARBAGE_S);
        fromDShouldFail("90°N", "181°W", 90, GARBAGE);
        toDmShouldFail(90, -181, "90°0'N", GARBAGE_S);
        fromDmShouldFail("90°0'N", "181°0'W", 90, GARBAGE);
        toDmsShouldFail(90, -181, "90°00'00.0\"N", GARBAGE_S);
        fromDmsShouldFail("90°00'00.0\"N", "181°00'00.0\"W", 90, GARBAGE);
    }

    @Test
    public void zero() {
        toD(0, 0, "0°N", "0°E");
        fromD("0°N", "0°E", 0, 0);
        toDm(0, 0, "0°0'N", "0°0'E");
        fromDm("0°0'N", "0°0'E", 0, 0);
        toDms(0, 0, "0°00'00.0\"N", "0°00'00.0\"E");
        fromDms("0°00'00.0\"N", "0°00'00.0\"E", 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleNorthAsInvalidLongitude() {
        DmsConverter.getLongitude("0°N");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleWestAsInvalidLatitude() {
        DmsConverter.getLatitude("0°W");
    }

    @Test
    public void invalidValid() {
        try {
            DmsConverter.getLatitude(Double.NaN, DmsConverter.FORMAT_DEGREES);
            fail("should not convert NaN!");
        } catch (IllegalArgumentException ignore) {}
        catch (Exception failure) {
            fail("wrong exception for NaN!");
        }
    }

    @Test
    public void invalidMode() {
        try {
            int invalidMode = 666;
            DmsConverter.getLatitude(0, invalidMode);
            fail("should not convert an invalid mode!");
        } catch (IllegalArgumentException ignore) {}
        catch (Exception failure) {
            fail("wrong exception for invalid mode!");
        }
    }

    @Test
    public void testNull() {
        try {
            DmsConverter.getLatitude(null);
            fail("should not convert a latitude null!");
        } catch (IllegalArgumentException ignore) {}
        catch (Exception failure) {
            fail("wrong exception for null");
        }

        try {
            DmsConverter.getLongitude(null);
            fail("should not convert a latitude null!");
        } catch (IllegalArgumentException ignore) {}
        catch (Exception failure) {
            fail("wrong exception");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleEmptyLatitude() {
        DmsConverter.getLatitude("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleEmptyLongitude() {
        DmsConverter.getLatitude("");
    }

    @Test
    public void testOutOfRangeMinutes() {
        try {
            DmsConverter.getLatitude("0°60'00.0\"N");
            fail("should not convert out of range minutes");
        } catch (IllegalArgumentException ignore) {}
        catch (Exception failure) {
            fail("wrong exception");
        }
    }

    @Test
    public void testOutOfRangeSeconds() {
        try {
            DmsConverter.getLatitude("0°00'60.0\"N");
            fail("should not convert out of range seconds");
        } catch (IllegalArgumentException ignore) {}
        catch (Exception failure) {
            fail("wrong exception");
        }
    }

    @Test
    public void testNastyInput() {
        try {
            DmsConverter.getLatitude("B°00'60.0\"N");
            fail("completely dodgy input");
        } catch (IllegalArgumentException ignore) {}
        catch (Exception failure) {
            fail("wrong exception");
        }
    }

    private void fromD(String latitude, String longitude, double latitudeExpected,
                       double longitudeExpected) {
        fromDms(latitude, longitude, latitudeExpected, longitudeExpected);
    }

    private void fromDShouldFail(String latitude, String longitude, double latitudeExpected,
                                 double longitudeExpected) {
        try {
            fromD(latitude, longitude, latitudeExpected, longitudeExpected);
            fail(String.format("Should not convert [%s, %s]", latitude, longitude));
        } catch (IllegalArgumentException ignore){}
    }

    private void fromDm(String latitude, String longitude, double latitudeExpected,
                        double longitudeExpected) {
        fromDms(latitude, longitude, latitudeExpected, longitudeExpected);
    }

    private void fromDmShouldFail(String latitude, String longitude, double latitudeExpected,
                                  double longitudeExpected) {
        try {
            fromDm(latitude, longitude, latitudeExpected, longitudeExpected);
            fail(String.format("Should not convert [%s, %s]", latitude, longitude));
        } catch (IllegalArgumentException ignore){}
    }

    private void fromDms(String latitude, String longitude, double latitudeExpected,
                         double longitudeExpected) {
        double latitudeActual = DmsConverter.getLatitude(latitude);
        assertEquals(latitudeExpected, latitudeActual, DELTA);
        double longitudeActual = DmsConverter.getLongitude(longitude);
        assertEquals(longitudeExpected, longitudeActual, DELTA);
    }

    private void fromDmsShouldFail(String latitude, String longitude, double latitudeExpected,
                                   double longitudeExpected) {
        try {
            fromDms(latitude, longitude, latitudeExpected, longitudeExpected);
            fail(String.format("Should not convert [%s, %s]", latitude, longitude));
        } catch (IllegalArgumentException ignore){}
    }

    private void toD(double latitude, double longitude, String latitudeExpected,
                     String longitudeExpected) {
        String longitudeActual = DmsConverter.getLongitude(longitude,
                DmsConverter.FORMAT_DEGREES);
        assertEquals(longitudeExpected, longitudeActual);
        String latitudeActual = DmsConverter.getLatitude(latitude,
                DmsConverter.FORMAT_DEGREES);
        assertEquals(latitudeExpected, latitudeActual);
    }

    private void toDShouldFail(double latitude, double longitude, String latitudeExpected,
                               String longitudeExpected) {
        try {
            toD(latitude, longitude, latitudeExpected, longitudeExpected);
            fail(String.format("Should not convert [%.5f, %.5f]", latitude, longitude));
        } catch (IllegalArgumentException ignore){}
    }

    private void toDm(double latitude, double longitude, String latitudeExpected,
                      String longitudeExpected) {
        String longitudeActual = DmsConverter.getLongitude(longitude,
                DmsConverter.FORMAT_MINUTES);
        assertEquals(longitudeExpected, longitudeActual);
        String latitudeActual = DmsConverter.getLatitude(latitude,
                DmsConverter.FORMAT_MINUTES);
        assertEquals(latitudeExpected, latitudeActual);
    }

    private void toDmShouldFail(double latitude, double longitude, String latitudeExpected,
                                String longitudeExpected) {
        try {
            toDm(latitude, longitude, latitudeExpected, longitudeExpected);
            fail(String.format("Should not convert [%.5f, %.5f]", latitude, longitude));
        } catch (IllegalArgumentException ignore){}
    }

    private void toDms(double latitude, double longitude, String latitudeExpected,
                       String longitudeExpected) {
        String longitudeActual = DmsConverter.getLongitude(longitude,
                DmsConverter.FORMAT_SECONDS);
        assertEquals(longitudeExpected, longitudeActual);
        String latitudeActual = DmsConverter.getLatitude(latitude,
                DmsConverter.FORMAT_SECONDS);
        assertEquals(latitudeExpected, latitudeActual);
    }

    private void toDmsShouldFail(double latitude, double longitude, String latitudeExpected,
                                 String longitudeExpected) {
        try {
            toDms(latitude, longitude, latitudeExpected, longitudeExpected);
            fail(String.format("Should not convert [%.5f, %.5f]", latitude, longitude));
        } catch (IllegalArgumentException ignore){}
    }
}

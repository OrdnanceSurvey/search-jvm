/*
 * Copyright (C) 2007 The Android Open Source Project
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/**
 * Adapted from:
 * https://raw.githubusercontent.com/android/platform_frameworks_base/master/location/java/android/location/Location.java
 */
public final class DmsConverter {

    private DmsConverter() {}

    /**
     * Constant used to specify formatting of a latitude or longitude
     * in the form DDD.DDDDD° where D indicates degrees.
     * This format is most commonly used with digital mapping systems.
     */
    public static final int FORMAT_DEGREES = 0;

    /**
     * Constant used to specify formatting of a latitude or longitude
     * in the form DDD°MM.MMM' where D indicates degrees and
     * M indicates minutes of arc (1 minute = 1/60th of a degree).
     * This format is most commonly used with navigation equipment.
     */
    public static final int FORMAT_MINUTES = 1;

    /**
     * Constant used to specify formatting of a latitude or longitude
     * in the form DDD°MM'SS.S" where D indicates degrees, M
     * indicates minutes of arc, and S indicates seconds of arc (1
     * minute = 1/60th of a degree, 1 second = 1/3600th of a degree).
     * This format is most commonly used with maps.
     */
    public static final int FORMAT_SECONDS = 2;

    /**
     * Return a latitude value specified in a range of common degrees minutes seconds formats.
     *
     * For example, the returned value 51.50722 may be obtained from:
     * <ul>
     *     <li>51° 30' 26.0"N</li>
     *     <li>51° 30' 26.0" N</li>
     *     <li>51:30:26.0</li>
     *     <li>51°30.433'N</li>
     *     <li>51° 30.433'N</li>
     *     <li>51:30.433</li>
     *     <li>51.50722°N</li>
     *     <li>51.50722° N</li>
     *     <li>51.50722</li>
     * </ul>
     * (and various permutations of spacing)
     *
     * @throws IllegalArgumentException if coordinate is less than -90.0, greater than 90.0, or is not a number.
     * @param coordinate a string representation of a latitude coordinate
     * @return decimal degree value representing the latitude input value
     */
    public final static double getLatitude(String coordinate) {
        throwIfEmptyOrNull(coordinate);
        if (coordinate.contains("°")) {
            String last = coordinate.substring(coordinate.length() - 1);
            if (!(last.equalsIgnoreCase("S") || last.equalsIgnoreCase("N"))) {
                throw new IllegalArgumentException("unsupported latitude format:" + coordinate);
            }
        }

        double result = convert(coordinate);
        if (!isValidLatitude(result)) {
            throw new IllegalArgumentException("out of range -90 to 90.  Input: " +
                    coordinate);
        }
        return result;
    }

    /**
     * @throws IllegalArgumentException if coordinate is less than
     * -90.0, greater than 90.0, or is not a number.
     * @throws IllegalArgumentException if outputType is not one of
     * FORMAT_DEGREES, FORMAT_MINUTES, or FORMAT_SECONDS.
     * @param latitude decimal degree latitude value
     * @param outputType the specified DMS output format to apply to the returned value
     * @return the formatted DMS latitude value
     */
    public final static String getLatitude(double latitude, int outputType) {
        if (!isValidLatitude(latitude)) {
            throw new IllegalArgumentException("out of range -180 to 180.  Input: " +
                    latitude);
        }
        char postfix = latitude >= 0 ? 'N' : 'S';
        return convert(Math.abs(latitude), outputType) + postfix;
    }

    /**
     * Return a longitude value specified in a range of common degrees minutes seconds formats.
     *
     * For example, the returned value -0.1275 may be obtained from:
     *
     * <ul>
     *     <li>0°07'39.0"W</li>
     *     <li>0°07'39.0" W</li>
     *     <li>0° 07' 39.0"W</li>
     *     <li>-0:07:39.0</li>
     *     <li>0°7.65'W</li>
     *     <li>-0:7.65</li>
     *     <li>0.1275°W</li>
     *     <li>0.1275° W</li>
     *     <li>-0.1275</li>
     * </ul>
     * (and various permutations of spacing)
     *
     * @throws IllegalArgumentException if coordinate is less than -180.0, greater than 180.0, or is not a number.
     * @param coordinate a string representation of a longitude coordinate
     * @return decimal degree value representing the longitude input value
     */
    public final static double getLongitude(String coordinate) {
        throwIfEmptyOrNull(coordinate);
        if (coordinate.contains("°")) {
            String last = coordinate.substring(coordinate.length() - 1);
            if (!(last.equalsIgnoreCase("W") || last.equalsIgnoreCase("E"))) {
                throw new IllegalArgumentException("unsupported longitude format:" + coordinate);
            }
        }

        double result = convert(coordinate);
        if (!isValidLongitude(result)) {
            throw new IllegalArgumentException("out of range -180 to 180.  Input: " +
                    coordinate);
        }
        return result;
    }

    /**
     * @throws IllegalArgumentException if coordinate is less than
     * -180.0, greater than 180.0, or is not a number.
     * @throws IllegalArgumentException if outputType is not one of
     * FORMAT_DEGREES, FORMAT_MINUTES, or FORMAT_SECONDS.
     * @param longitude decimal degree longitude value
     * @param outputType the specified DMS output format to apply to the returned value
     * @return the formatted DMS longitude value
     */
    public final static String getLongitude(double longitude, int outputType) {
        if (!isValidLongitude(longitude)) {
            throw new IllegalArgumentException("out of range -180 to 180.  Input: " +
                    longitude);
        }
        char postfix = longitude >= 0 ? 'E' : 'W';
        return convert(Math.abs(longitude), outputType) + postfix;
    }

    /**
     * Converts a coordinate to a String representation. The outputType
     * may be one of FORMAT_DEGREES, FORMAT_MINUTES, or FORMAT_SECONDS.
     * The coordinate must be a valid double between -180.0 and 180.0.
     *
     * @throws IllegalArgumentException if outputType is not one of
     * FORMAT_DEGREES, FORMAT_MINUTES, or FORMAT_SECONDS.
     * @param coordinate decimal degree coordinate value
     * @param outputType the specified DMS output format to apply to the returned value
     * @return the formatted DMS output
     */
    private static String convert(double coordinate, int outputType) {
        if ((outputType != FORMAT_DEGREES) &&
                (outputType != FORMAT_MINUTES) &&
                (outputType != FORMAT_SECONDS)) {
            throw new IllegalArgumentException("outputType=" + outputType);
        }

        StringBuilder sb = new StringBuilder();

        // print degrees
        if (outputType == FORMAT_DEGREES) {
            DecimalFormat dfm = new DecimalFormat("###.#####");
            sb.append(dfm.format(coordinate));
            sb.append('°');
        } else {
            // print degrees and minutes
            int degrees = (int) Math.floor(coordinate);
            sb.append(degrees);
            sb.append('°');
            coordinate -= degrees;
            coordinate *= 60;
            // print minutes
            if (outputType == FORMAT_MINUTES) {
                // print minutes
                DecimalFormat dfm = new DecimalFormat("###.###");
                sb.append(dfm.format(coordinate));
                sb.append('\'');
            } else {
                // print degrees, minutes and seconds
                int minutes = (int) Math.floor(coordinate);
                sb.append(String.format("%02d", minutes));
                sb.append('\'');
                coordinate -= minutes;
                coordinate *= 60.0;
                DecimalFormat dfs = new DecimalFormat("00.0");
                BigDecimal bigDecimal = BigDecimal.valueOf(coordinate).setScale(1,
                        BigDecimal.ROUND_HALF_UP);
                sb.append(dfs.format(bigDecimal));
                sb.append('"');
            }
        }
        return sb.toString();
    }

    /**
     * Converts a String in one of the formats described by
     * FORMAT_DEGREES, FORMAT_MINUTES, or FORMAT_SECONDS into a
     * double.
     *
     * @throws NullPointerException if coordinate is null
     * @throws IllegalArgumentException if the coordinate is not
     * in one of the valid formats.
     * @param coordinate string DMS value to parse
     * @return the decimal degree representation
     */
    private static double convert(String coordinate) {
        if (!coordinate.isEmpty()) {
            coordinate = coordinate.replaceAll(" ", "");
            String last = coordinate.substring(coordinate.length() - 1);
            String value = coordinate.replaceFirst("([°'\"][NESWnesw])$", "");
            coordinate = value.replaceAll("[°']", ":");
            if (last.equalsIgnoreCase("W") || last.equalsIgnoreCase("S")) {
                coordinate = "-" + coordinate;
            }
        }

        boolean negative = false;
        if (coordinate.charAt(0) == '-') {
            coordinate = coordinate.substring(1);
            negative = true;
        }

        StringTokenizer st = new StringTokenizer(coordinate, ":");
        // Note: if not empty cannot be <1
        int tokens = st.countTokens();
        try {
            String degrees = st.nextToken();
            double val;
            if (tokens == 1) {
                val = Double.parseDouble(degrees);
                return negative ? -val : val;
            }

            String minutes = st.nextToken();
            int deg = Integer.parseInt(degrees);
            double min;
            double sec = 0.0;

            if (st.hasMoreTokens()) {
                min = Integer.parseInt(minutes);
                String seconds = st.nextToken();
                sec = Double.parseDouble(seconds);
            } else {
                min = Double.parseDouble(minutes);
            }

            boolean isNegative180 = negative && (deg == 180) &&
                    (min == 0) && (sec == 0);

            // ORIGINAL: deg must be in [0, 179] except for the case of -180 degrees
            // if ((deg < 0.0) || (deg > 179 && !isNegative180)) {
            // NEW:
            if ((deg < 0.0) || (deg > 180 && !isNegative180)) {
                throw new IllegalArgumentException("coordinate=" + coordinate);
            }
            if (min < 0 || min > 59) {
                throw new IllegalArgumentException("coordinate=" +
                        coordinate);
            }
            if (sec < 0 || sec > 59) {
                throw new IllegalArgumentException("coordinate=" +
                        coordinate);
            }

            val = deg * 3600.0 + min * 60.0 + sec;
            val /= 3600.0;
            return negative ? -val : val;
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }
    }

    private static boolean isValidLatitude(double latitude) {
        return -90 <= latitude && latitude <= 90 ? true : false;
    }

    private static boolean isValidLongitude(double longitude) {
        return -180 <= longitude && longitude <= 180 ? true : false;
    }

    private static void throwIfEmptyOrNull(String coordinate) {
        if (coordinate == null) {
            throw new IllegalArgumentException("null is an invalid coordinate");
        }
        if (coordinate.isEmpty()) {
            throw new IllegalArgumentException("invalid coordinate: zero sized string");
        }
    }
}

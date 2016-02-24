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

package uk.os.elements.search.android.providers.bng;

import com.esri.core.geometry.Envelope;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoPattern {

    private GeoPattern() {}

    /**
     * For example, the following values can be parsed:
     * <ul>
     *     <li>SU</li>
     *     <li>SU 41</li>
     *     <li>SU 4315</li>
     *     <li>TL 032 386</li>
     *     <li>SU 4005 1003</li>
     *     <li>SU 40052 10037</li>
     * </ul>
     *
     * Spacing is ignored, thus "TL 032 386" and "TL032386" may be parsed.
     *
     * @param gridRefIn a user supplied British National Grid reference
     * @return an OS Grid Reference search result
     */
    public static OsGridReference parseGridReference(String gridRefIn) {
        Pattern pattern = Pattern.compile("^(\\w\\w)(\\d{0,10})$");
        Matcher matcher = pattern.matcher(gridRefIn.toUpperCase().replaceAll("\\s", ""));
        int iIndex = 7;

        boolean probableGridReference = matcher.matches();

        if (probableGridReference) {
            String characters = matcher.group(1);
            String numbers = matcher.group(2);

            // get numeric values of letter references, mapping A->0, B->1, C->2, etc:
            int l1 = Character.codePointAt(characters, 0) - Character.codePointAt("A", 0);
            int l2 = Character.codePointAt(characters, 1) - Character.codePointAt("A", 0);

            // shuffle down letters after 'I' since 'I' is not used in grid:
            if (l1 > iIndex) l1--;
            if (l2 > iIndex) l2--;

            // convert grid letters into 100km-square indexes from false origin (grid square SV):
            int es = ((l1 - 2) % 5) * 5 + (l2 % 5);
            int ns = (int) ((19 - Math.floor(l1 / 5) * 5) - Math.floor(l2 / 5));
            if (es < 0 || es > 6 || ns < 0 || ns > 12) return null;

            String e = String.valueOf(es);
            String n = String.valueOf(ns);

            // append numeric part of references to grid index:
            e += numbers.substring(0, numbers.length() / 2);
            n += numbers.substring(numbers.length() / 2);

            // normalise to 1m grid, rounding up to centre of grid square:
            int offset = 0;

            switch (String.valueOf(numbers).length()) {
                case 0:
                    offset = 100000;
                    break;
                case 2:
                    offset = 10000;
                    break;
                case 4:
                    offset = 1000;
                    break;
                case 6:
                    offset = 100;
                    break;
                case 8:
                    offset = 10;
                    break;
                case 10:
                    offset = 1;
                    break;
                default:
                    return null;
            }

            int easting = Integer.valueOf(e) * offset;
            int northing = Integer.valueOf(n) * offset;
            Envelope envelope = new Envelope(easting, northing, easting + offset, northing + offset);

            String beautifulName = beautifulFormat(toGridReference(easting, northing, numbers.length() / 2));

            return new OsGridReference(beautifulName, easting, northing, envelope);
        }
        return null;
    }

    /**
     * Convert British National Grid (EPSG:27700) cartesian coordinates for a point into a British National Grid
     * reference.  The returned condensed coordinate varies between ten and zero figures (e.g. SU 40052 10037 and SU).
     *
     * The National Grid provides a unique reference system, which can be applied to all Ordnance Survey maps of Great
     * Britain at all scales.
     *
     * The first two letters identify the 100 kilometre square of a location.  That square may be further broken down
     * using integer values  The value TL63 identifies a 10 kilometre square.  Further integers subdivide the square,
     * tending towards a point.  Thus, TL 623317 represents a 100 metre square.
     *
     * This method returns a condensed grid value by removing zeros.  In cases where zeros are removed, the box less
     * specific but the grid reference value is smaller and easier to record.
     *
     * For example, the 440050 (easting) and 110030 (northing) values will be converted to SU 4005 1003.
     *
     * Note: British National Grid is also referenced as "Ordnance Survey National Grid" or the "National Grid".
     *
     * @param easting the x cartesian coordinate in British National Grid
     * @param northing the y cartesian coordinate in British National Grid
     * @return a formatted grid reference that is as condensed as possible
     */
    public static String toGridReference(int easting, int northing) {
        int digits = 10;
        String verbose = toGridReference(easting, northing, digits);

        Pattern pattern = Pattern.compile("^(\\w\\w)(\\d{0,10})$");
        Matcher matcher = pattern.matcher(verbose.toUpperCase().replaceAll("\\s", ""));
        if (!matcher.matches()) {
            throw new IllegalStateException("Failed to parse " + verbose);
        }
        String numbers = matcher.group(2);
        String eNumber = numbers.substring(0, numbers.length() / 2);
        String nNumber = numbers.substring(numbers.length() / 2);

        int trimIndex = -1;
        int top = eNumber.length() - 1;
        for (int i = top; i >= 0; i--) {
            char echar = eNumber.charAt(i);
            char nchar = nNumber.charAt(i);
            if (echar == '0' && nchar == '0') {
                trimIndex = i;
            } else {
                break;
            }
        }
        boolean hasTrimIndex = trimIndex != -1;
        if (hasTrimIndex) {
            eNumber = eNumber.substring(0, trimIndex);
            nNumber = nNumber.substring(0, trimIndex);
        }

        String simplified;
        if (eNumber.length() > 2) {
            simplified = matcher.group(1)+ " " + eNumber + " " + nNumber;
        } else {
            simplified = matcher.group(1) + eNumber + nNumber;
        }
        return simplified.trim();
    }

    public static String toGridReference(int easting, int northing, int digits) {
        final String[] NATGRID_LETTERS = {"VWXYZ","QRSTU","LMNOP","FGHJK","ABCDE"};
        int e = easting;
        int n = northing;
        if (digits < 0) {
            return e + "," + n;
        }
        // We can actually handle negative E and N in the lettered case, but that's more effort.
        if (e < 0 || n < 0) { return null; }

        String ret = "";

        // 	The following code doesn't correctly handle e<0 or n<0 due to problems with / and %.
        int big = 500000;
        int small = big/5;
        int firstdig = small/10;

        int es = e/big;
        int ns = n/big;
        e = e % big;
        n = n % big;
        // move to the S square
        es += 2;
        ns += 1;
        if (es > 4 || ns > 4) { return null; }
        ret = ret + NATGRID_LETTERS[ns].charAt(es);

        es = e/small;
        ns = n/small;
        e = e % small;
        n = n % small;
        ret= ret + NATGRID_LETTERS[ns].charAt(es);

        // Only add spaces if there are digits too. This lets us have "zero-figure" grid references, e.g. "SK"
        if (digits > 0)
        {
            ret += ' ';

            for (int dig = firstdig, i = 0; dig != 0 && i < digits; i++, dig /= 10) {
                ret += (e/dig%10);
            }

            ret += ' ';

            for (int dig = firstdig, i = 0; dig != 0 && i < digits; i++, dig /= 10) {
                ret += (n/dig%10);
            }
        }

        return ret;
    }

    private static String beautifulFormat(String value) {
        Pattern pattern = Pattern.compile("^(\\w\\w)(\\d{0,10})$");
        Matcher matcher = pattern.matcher(value.toUpperCase().replaceAll("\\s", ""));
        if (!matcher.matches()) {
            throw new IllegalStateException("Failed to parse " + value);
        }
        String numbers = matcher.group(2);
        String eNumber = numbers.substring(0, numbers.length() / 2);
        String nNumber = numbers.substring(numbers.length() / 2);

        String simplified;
        if (eNumber.length() > 2) {
            simplified = matcher.group(1)+ " " + eNumber + " " + nNumber;
        } else {
            simplified = matcher.group(1) + eNumber + nNumber;
        }
        return simplified.trim();
    }
}

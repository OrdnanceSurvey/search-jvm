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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GeoPatternTest {

    @Test
    public void formatZero() throws Exception {
        double easting = 400000;
        double northing = 100000;
        String actual = GeoPattern.toGridReference((int) easting, (int) northing);
        String expected = "SU";
        assertEquals(expected, actual);
    }

    @Test
    public void formatTwo() throws Exception {
        double easting = 440000;
        double northing = 110000;
        String actual = GeoPattern.toGridReference((int)easting, (int)northing);
        String expected = "SU41";
        assertEquals(expected, actual);
    }

    @Test
    public void formatFour() throws Exception {
        double easting = 443000;
        double northing = 115000;
        String actual = GeoPattern.toGridReference((int)easting, (int)northing);

        String expected = "SU4315";
        assertEquals(expected, actual);
    }

    @Test
    public void formatSix() throws Exception {
        double easting = 503200;
        double northing = 238600;
        String actual = GeoPattern.toGridReference((int)easting, (int)northing);

        String expected = "TL 032 386";
        assertEquals(expected, actual);
    }

    @Test
    public void formatEight() throws Exception {
        double easting = 440050;
        double northing = 110030;
        String actual = GeoPattern.toGridReference((int)easting, (int)northing);

        String expected = "SU 4005 1003";
        assertEquals(expected, actual);
    }

    @Test
    public void formatTen() throws Exception {
        double easting = 440052;
        double northing = 110037;

        String actual = GeoPattern.toGridReference((int)easting, (int)northing);

        String expected = "SU 40052 10037";
        assertEquals(expected, actual);
    }

    @Test
    public void zeroFigure() throws Exception {
        // 1km square
        OsGridReference reference = GeoPattern.parseGridReference("SU");
        double expectedEasting = 400000;
        double expectedNorthing = 100000;
        double actualEasting = reference.getPoint().getX();
        double actualNorthing = reference.getPoint().getY();
        assertEquals(expectedEasting, actualEasting, 1);
        assertEquals(expectedNorthing, actualNorthing, 1);
    }

    @Test
    public void twoFigure() throws Exception {
        // 1km square
        OsGridReference reference = GeoPattern.parseGridReference("SU41");
        double expectedEasting = 440000;
        double expectedNorthing = 110000;
        double actualEasting = reference.getPoint().getX();
        double actualNorthing = reference.getPoint().getY();
        assertEquals(expectedEasting, actualEasting, 1);
        assertEquals(expectedNorthing, actualNorthing, 1);
    }

    @Test
    public void fourFigure() throws Exception {
        // 1km square
        OsGridReference reference = GeoPattern.parseGridReference("SU 4315");
        double expectedEasting = 443000;
        double expectedNorthing = 115000;

        double actualEasting = reference.getPoint().getX();
        double actualNorthing = reference.getPoint().getY();
        assertEquals(expectedEasting, actualEasting, 1);
        assertEquals(expectedNorthing, actualNorthing, 1);
    }

    @Test
    public void sixFigure() throws Exception {
        // 100m square
        OsGridReference reference = GeoPattern.parseGridReference("TL 032 386");
        double expectedEasting = 503200;
        double expectedNorthing = 238600;

        double actualEasting = reference.getPoint().getX();
        double actualNorthing = reference.getPoint().getY();
        assertEquals(expectedEasting, actualEasting, 1);
        assertEquals(expectedNorthing, actualNorthing, 1);
    }

    @Test
    public void sixFigureNoSpacing() throws Exception {
        // 100m square
        OsGridReference reference = GeoPattern.parseGridReference("TL032386");
        double expectedEasting = 503200;
        double expectedNorthing = 238600;

        double actualEasting = reference.getPoint().getX();
        double actualNorthing = reference.getPoint().getY();
        assertEquals(expectedEasting, actualEasting, 1);
        assertEquals(expectedNorthing, actualNorthing, 1);
    }

    @Test
    public void eightFigure() throws Exception {
        // 10m square
        OsGridReference reference = GeoPattern.parseGridReference("SU 4005 1003");
        double expectedEasting = 440050;
        double expectedNorthing = 110030;

        double actualEasting = reference.getPoint().getX();
        double actualNorthing = reference.getPoint().getY();
        assertEquals(expectedEasting, actualEasting, 1);
        assertEquals(expectedNorthing, actualNorthing, 1);
    }

    @Test
    public void tenFigure() throws Exception {
        // 10m square
        OsGridReference reference = GeoPattern.parseGridReference("SU 40052 10037");
        double expectedEasting = 440052;
        double expectedNorthing = 110037;

        double actualEasting = reference.getPoint().getX();
        double actualNorthing = reference.getPoint().getY();
        assertEquals(expectedEasting, actualEasting, 1);
        assertEquals(expectedNorthing, actualNorthing, 1);
    }

    @Test
    public void oneFailsFigure() throws Exception {
        OsGridReference reference = GeoPattern.parseGridReference("SU 4");
        assertNull(reference);
    }

    @Test
    public void threeFailsFigure() throws Exception {
        OsGridReference reference = GeoPattern.parseGridReference("SU 40 1");
        assertNull(reference);
    }

    @Test
    public void fiveFailsFigure() throws Exception {
        OsGridReference reference = GeoPattern.parseGridReference("SU 404 12");
        assertNull(reference);
    }

    @Test
    public void sevenFailsFigure() throws Exception {
        OsGridReference reference = GeoPattern.parseGridReference("SU 4005 100");
        assertNull(reference);
    }

    @Test
    public void nineFailsFigure() throws Exception {
        OsGridReference reference = GeoPattern.parseGridReference("SU 40055 1005");
        assertNull(reference);
    }

    @Test
    public void elevenFailsFigure() throws Exception {
        OsGridReference reference = GeoPattern.parseGridReference("SU 400551 10055");
        assertNull(reference);
    }

    @Test
    public void test1() throws Exception {
        OsGridReference reference = GeoPattern.parseGridReference("SU4325215300");
        double expectedEasting = 443252;
        double expectedNorthing = 115300;
        double actualEasting = reference.getPoint().getX();
        double actualNorthing = reference.getPoint().getY();
        assertEquals(expectedEasting, actualEasting, 1);
        assertEquals(expectedNorthing, actualNorthing, 1);

        reference = GeoPattern.parseGridReference("SU 43252 15300");
        expectedEasting = 443252;
        expectedNorthing = 115300;
        actualEasting = reference.getPoint().getX();
        actualNorthing = reference.getPoint().getY();
        assertEquals(expectedEasting, actualEasting, 1);
        assertEquals(expectedNorthing, actualNorthing, 1);
    }
}


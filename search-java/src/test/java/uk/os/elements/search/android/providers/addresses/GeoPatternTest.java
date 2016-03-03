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

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class GeoPatternTest {

    @Test
    public void matchesEndpointResult() {
        assertFalse(GeoPattern.isPostcodeCandidate("s"));
        assertTrue(GeoPattern.isPostcodeCandidate("s1"));
        assertFalse(GeoPattern.isPostcodeCandidate("ss"));
        assertTrue(GeoPattern.isPostcodeCandidate("ss1"));
        assertFalse(GeoPattern.isPostcodeCandidate("sss1"));
        assertTrue(GeoPattern.isPostcodeCandidate("ss11"));
        assertTrue(GeoPattern.isPostcodeCandidate("ss111"));
        assertTrue(GeoPattern.isPostcodeCandidate("so156rt"));
        assertFalse(GeoPattern.isPostcodeCandidate("so156rtx"));
        assertTrue(GeoPattern.isPostcodeCandidate("So156rt"));
        assertTrue(GeoPattern.isPostcodeCandidate("So15 6rt"));
        assertFalse(GeoPattern.isPostcodeCandidate("Random so15 6rt"));
        assertFalse(GeoPattern.isPostcodeCandidate("so15 6rt random"));
    }
}

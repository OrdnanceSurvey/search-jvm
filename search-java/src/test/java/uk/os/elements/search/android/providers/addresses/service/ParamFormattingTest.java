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

package uk.os.elements.search.android.providers.addresses.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParamFormattingTest {

    @Test
    public void bbox() {
        String expected = "-2.4457,51.2613,0.0507,52.3091";
        String actual = ParamFormatting.bbox(-2.4457,51.2613,0.0507,52.3091);
        assertEquals(expected, actual);
    }

    @Test
    public void point() {
        String expected = "-2.4457,51.2613";
        String actual = ParamFormatting.point(-2.4457,51.2613);
        assertEquals(expected, actual);
    }
}
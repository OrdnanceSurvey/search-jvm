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

package uk.os.elements.search;

import org.junit.Test;

import uk.os.elements.search.android.providers.bng.GridReferenceProvider;
import uk.os.elements.search.android.providers.latlon.LatLonProvider;
import uk.os.elements.search.android.providers.recents.RecentsManagerImpl;

import static org.junit.Assert.assertEquals;

public class SearchServiceImplTest {

    @Test
    public void latitudeLongitude() throws Exception {
        SearchManager searchManager = new SearchManager(new RecentsManagerImpl(), new GridReferenceProvider(),
                new LatLonProvider());
        SearchBundle searchBundle = searchManager.query("51.50722, -0.1275").toBlocking().single();
        String expected = "51°30'26.0\"N 0°07'39.0\"W";
        String actual = searchBundle.getRemaining().get(0).getName();
        assertEquals(expected, actual);
    }

    @Test
    public void SU4315() throws Exception {
        SearchManager searchManager = new SearchManager(new RecentsManagerImpl(),
                new GridReferenceProvider(), new LatLonProvider());
        SearchBundle searchBundle = searchManager.query("SU4315").toBlocking().single();
        String expected = "SU4315";
        String actual = searchBundle.getRemaining().get(0).getName();
        assertEquals(expected, actual);
    }

    @Test
    public void SH609543() throws Exception {
        SearchManager searchManager = new SearchManager(new RecentsManagerImpl(),
                new GridReferenceProvider(), new LatLonProvider());
        SearchBundle searchBundle = searchManager.query("SH609543").toBlocking().single();
        String expected = "SH 609 543";
        String actual = searchBundle.getRemaining().get(0).getName();
        assertEquals(expected, actual);
    }
}

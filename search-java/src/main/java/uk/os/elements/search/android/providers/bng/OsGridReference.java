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

import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import uk.os.elements.search.SearchResult;

public class OsGridReference extends SearchResult {

    public OsGridReference(String name, int easting, int northing, Envelope boundingBox) {
        super("GridRef: " + name, name, "Easting: " + easting + "  Northing: " +
                northing, new Point(easting, northing), boundingBox, SpatialReference.create(27700));
    }
}
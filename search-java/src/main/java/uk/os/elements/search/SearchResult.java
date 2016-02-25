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

import com.esri.core.geometry.Envelope;

import java.io.Serializable;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

import uk.os.elements.search.util.HashCodeUtil;

import static uk.os.elements.search.util.EqualsUtil.areEqual;
import static uk.os.elements.search.util.HashCodeUtil.hash;

public class SearchResult implements Serializable {
    private static final long serialVersionUID = -1477961267112066338L;

    private final String mId;
    private final String mName;
    private final String mContext;
    private final Envelope mEnvelope;
    private final Point mPoint;
    private final SpatialReference mSpatialReference;

    public SearchResult(String id, String name, String context, Point point, Envelope boundingBox,
                        SpatialReference spatialReference) {
        mId = id;
        mName = name;
        mContext = context;
        mPoint = point;
        mEnvelope = boundingBox;
        mSpatialReference = spatialReference;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getContext() {
        return mContext;
    }

    public Point getPoint() { return mPoint; }

    public Envelope getEnvelope() {
        return mEnvelope;
    }

    public SpatialReference getSpatialReference() {
        return mSpatialReference;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SearchResult)) return false;
        final SearchResult that = (SearchResult) other;

        return areEqual(getContext(), that.getContext()) &&
                areEqual(getEnvelope(), that.getEnvelope()) &&
                areEqual(getId(), that.getId()) &&
                areEqual(getName(), that.getName()) &&
                areEqual(getPoint(), that.getPoint()) &&
                areEqual(getSpatialReference(), that.getSpatialReference());
    }

    @Override
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        //collect the contributions of various fields
        result = hash(result, getContext());
        result = hash(result, getEnvelope());
        result = hash(result, getId());
        result = hash(result, getName());
        result = hash(result, getPoint());
        result = hash(result, getSpatialReference());
        return result;
    }

    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append(" ID: " + getId() + NEW_LINE);
        result.append(" Name: " + getName() + NEW_LINE);
        result.append(" Context: " + getContext() + NEW_LINE);
        if (getEnvelope() != null) {
            result.append(" Envelope: " + getEnvelope().toString() + NEW_LINE);
        }
        result.append(" Point: " + getPoint().toString() + NEW_LINE );
        result.append(" Spatial Reference: " + getSpatialReference().toString() + NEW_LINE );
        result.append("}");

        return result.toString();
    }
}

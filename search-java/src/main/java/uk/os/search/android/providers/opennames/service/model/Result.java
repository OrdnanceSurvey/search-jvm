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

package uk.os.search.android.providers.opennames.service.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * generated code
 */
public class Result
{
    @SerializedName("GAZETTEER_ENTRY")
    private GazetteerEntry GazetteerEntry;

    public GazetteerEntry getGazetteerEntry()
    {
        return GazetteerEntry;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [GAZETTEER_ENTRY = "+ GazetteerEntry +"]";
    }

    public static class GazetteerEntry implements Serializable {

        private static final long serialVersionUID = -1477961267112066338L;

        private String POPULATED_PLACE_TYPE;

        private String MBR_XMAX;

        private String MATCH;

        private String MBR_XMIN;

        private String POSTCODE_DISTRICT;

        private String ID;

        private String GAZETTEER_URI;

        private String MBR_YMAX;

        private String REGION;

        private String TYPE;

        private String LOCAL_TYPE;

        private String COUNTY_UNITARY_TYPE;

        private String REGION_URI;

        private String COUNTRY;

        private String COUNTY_UNITARY;

        private String POSTCODE_DISTRICT_URI;

        private String DISTRICT_BOROUGH;

        private String MBR_YMIN;

        private String MOST_DETAIL_VIEW_RES;

        private String NAME1;

        private String LEAST_DETAIL_VIEW_RES;

        private String POPULATED_PLACE_URI;

        private String COUNTRY_URI;

        private String GEOMETRY_Y;

        private String POPULATED_PLACE;

        private String COUNTY_UNITARY_URI;

        private String GEOMETRY_X;

        public String getPOPULATED_PLACE_TYPE() {
            return POPULATED_PLACE_TYPE;
        }

        public String getMBR_XMAX() {
            return MBR_XMAX;
        }

        public String getMATCH() {
            return MATCH;
        }

        public String getMBR_XMIN() {
            return MBR_XMIN;
        }

        public String getPOSTCODE_DISTRICT() {
            return POSTCODE_DISTRICT;
        }

        public String getDISTRICT_BOROUGH() {
            return DISTRICT_BOROUGH;
        }

        public String getID() {
            return ID;
        }

        public String getGAZETTEER_URI() {
            return GAZETTEER_URI;
        }

        public String getMBR_YMAX() {
            return MBR_YMAX;
        }

        public String getREGION() {
            return REGION;
        }

        public String getTYPE() {
            return TYPE;
        }

        public String getLOCAL_TYPE() {
            return LOCAL_TYPE;
        }

        public String getCOUNTY_UNITARY_TYPE() {
            return COUNTY_UNITARY_TYPE;
        }

        public String getREGION_URI() {
            return REGION_URI;
        }

        public String getCOUNTRY() {
            return COUNTRY;
        }

        public String getCOUNTY_UNITARY() {
            return COUNTY_UNITARY;
        }

        public String getPOSTCODE_DISTRICT_URI() {
            return POSTCODE_DISTRICT_URI;
        }

        public String getMBR_YMIN() {
            return MBR_YMIN;
        }

        public String getMOST_DETAIL_VIEW_RES() {
            return MOST_DETAIL_VIEW_RES;
        }

        public String getName() {
            return NAME1;
        }

        public String getLEAST_DETAIL_VIEW_RES() {
            return LEAST_DETAIL_VIEW_RES;
        }

        public String getPOPULATED_PLACE_URI() {
            return POPULATED_PLACE_URI;
        }

        public String getCOUNTRY_URI() {
            return COUNTRY_URI;
        }

        public String getGEOMETRY_Y() {
            return GEOMETRY_Y;
        }

        public String getPOPULATED_PLACE() {
            return POPULATED_PLACE;
        }

        public String getCOUNTY_UNITARY_URI() {
            return COUNTY_UNITARY_URI;
        }

        public String getGEOMETRY_X() {
            return GEOMETRY_X;
        }

        public boolean hasBoundingBox() {
            String[] values = new String[]{
                    getMBR_XMIN(), getMBR_XMAX(), getMBR_YMAX(), getMBR_YMIN()
            };

            for (String value : values) {
                if (!isValidDouble(value)) {
                    return false;
                }
            }

            return true;
        }

        private boolean isValidDouble(String s) {
            boolean isThere = s != null;
            boolean isDouble = false;
            if (isThere) {
                try {
                    Double.parseDouble(s);
                    isDouble = true;
                } catch (NumberFormatException nfe) { /* ignore */ }
            }

            return isThere && isDouble;
        }

        @Override
        public String toString() {
            return "ClassPojo [POPULATED_PLACE_TYPE = " + POPULATED_PLACE_TYPE + ", MBR_XMAX = " +
                    MBR_XMAX + ", MATCH = " + MATCH + ", MBR_XMIN = " + MBR_XMIN +
                    ", POSTCODE_DISTRICT = " + POSTCODE_DISTRICT + ", ID = " + ID +
                    ", GAZETTEER_URI = " + GAZETTEER_URI + ", MBR_YMAX = " + MBR_YMAX +
                    ", REGION = " + REGION + ", TYPE = " + TYPE + ", LOCAL_TYPE = " + LOCAL_TYPE +
                    ", COUNTY_UNITARY_TYPE = " + COUNTY_UNITARY_TYPE + ", REGION_URI = " +
                    REGION_URI + ", COUNTRY = " + COUNTRY + ", COUNTY_UNITARY = " +
                    COUNTY_UNITARY + ", POSTCODE_DISTRICT_URI = " + POSTCODE_DISTRICT_URI +
                    ", MBR_YMIN = " + MBR_YMIN + ", MOST_DETAIL_VIEW_RES = " +
                    MOST_DETAIL_VIEW_RES + ", NAME1 = " + NAME1 + ", LEAST_DETAIL_VIEW_RES = " +
                    LEAST_DETAIL_VIEW_RES + ", POPULATED_PLACE_URI = " + POPULATED_PLACE_URI +
                    ", COUNTRY_URI = " + COUNTRY_URI + ", GEOMETRY_Y = " + GEOMETRY_Y +
                    ", POPULATED_PLACE = " + POPULATED_PLACE + ", COUNTY_UNITARY_URI = " +
                    COUNTY_UNITARY_URI + ", GEOMETRY_X = " + GEOMETRY_X + "]";
        }
    }
}



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

package uk.os.elements.search.android.providers.addresses.service.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * generated code
 */
public class Result
{
    @SerializedName("DPA")
    private Dpa Dpa;

    public Dpa getGazetteerEntry()
    {
        return Dpa;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [DPA = "+ Dpa +"]";
    }


    public static class Dpa implements Serializable {

        private static final long serialVersionUID = -1477961267112066338L;

        private String CLASSIFICATION_CODE;

        private String BLPU_STATE_CODE_DESCRIPTION;

        private String POSTCODE;

        private String LOCAL_CUSTODIAN_CODE;

        private String POST_TOWN;

        private String STATUS;

        private String BLPU_STATE_CODE;

        private String MATCH;

        private String X_COORDINATE;

        private String ORGANISATION_NAME;

        private String POSTAL_ADDRESS_CODE_DESCRIPTION;

        private String UDPRN;

        private String Y_COORDINATE;

        private String UPRN;

        private String POSTAL_ADDRESS_CODE;

        private String LANGUAGE;

        private String LOCAL_CUSTODIAN_CODE_DESCRIPTION;

        private String BUILDING_NUMBER;

        private String ADDRESS;

        private String BLPU_STATE_DATE;

        private String THOROUGHFARE_NAME;

        private String MATCH_DESCRIPTION;

        private String CLASSIFICATION_CODE_DESCRIPTION;

        private String LAST_UPDATE_DATE;

        private String DEPENDENT_LOCALITY;

        private String TOPOGRAPHY_LAYER_TOID;

        private String RPC;

        private String ENTRY_DATE;

        private String LOGICAL_STATUS_CODE;

        public String getCLASSIFICATION_CODE ()
        {
            return CLASSIFICATION_CODE;
        }


        public String getBLPU_STATE_CODE_DESCRIPTION ()
        {
            return BLPU_STATE_CODE_DESCRIPTION;
        }


        public String getPOSTCODE ()
        {
            return POSTCODE;
        }

        public String getLOCAL_CUSTODIAN_CODE ()
        {
            return LOCAL_CUSTODIAN_CODE;
        }


        public String getPOST_TOWN ()
        {
            return POST_TOWN;
        }

        public String getSTATUS ()
        {
            return STATUS;
        }


        public String getBLPU_STATE_CODE ()
        {
            return BLPU_STATE_CODE;
        }

        public String getMATCH ()
        {
            return MATCH;
        }

        public String getX_COORDINATE ()
        {
            return X_COORDINATE;
        }

        public String getORGANISATION_NAME ()
        {
            return ORGANISATION_NAME;
        }


        public String getPOSTAL_ADDRESS_CODE_DESCRIPTION ()
        {
            return POSTAL_ADDRESS_CODE_DESCRIPTION;
        }

        public String getUDPRN ()
        {
            return UDPRN;
        }

        public String getY_COORDINATE ()
        {
            return Y_COORDINATE;
        }

        public String getUPRN ()
        {
            return UPRN;
        }

        public String getPOSTAL_ADDRESS_CODE ()
        {
            return POSTAL_ADDRESS_CODE;
        }

        public String getLANGUAGE ()
        {
            return LANGUAGE;
        }

        public String getLOCAL_CUSTODIAN_CODE_DESCRIPTION ()
        {
            return LOCAL_CUSTODIAN_CODE_DESCRIPTION;
        }

        public String getBUILDING_NUMBER ()
        {
            return BUILDING_NUMBER;
        }

        public String getADDRESS ()
        {
            return ADDRESS;
        }

        public String getBLPU_STATE_DATE ()
        {
            return BLPU_STATE_DATE;
        }

        public String getTHOROUGHFARE_NAME ()
        {
            return THOROUGHFARE_NAME;
        }

        public String getMATCH_DESCRIPTION ()
        {
            return MATCH_DESCRIPTION;
        }

        public String getCLASSIFICATION_CODE_DESCRIPTION ()
        {
            return CLASSIFICATION_CODE_DESCRIPTION;
        }

        public String getLAST_UPDATE_DATE ()
        {
            return LAST_UPDATE_DATE;
        }

        public String getDEPENDENT_LOCALITY ()
        {
            return DEPENDENT_LOCALITY;
        }

        public String getTOPOGRAPHY_LAYER_TOID ()
        {
            return TOPOGRAPHY_LAYER_TOID;
        }

        public String getRPC ()
        {
            return RPC;
        }

        public String getENTRY_DATE ()
        {
            return ENTRY_DATE;
        }

        public String getLOGICAL_STATUS_CODE ()
        {
            return LOGICAL_STATUS_CODE;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [CLASSIFICATION_CODE = "+CLASSIFICATION_CODE+", BLPU_STATE_CODE_DESCRIPTION = "+BLPU_STATE_CODE_DESCRIPTION+", POSTCODE = "+POSTCODE+", LOCAL_CUSTODIAN_CODE = "+LOCAL_CUSTODIAN_CODE+", POST_TOWN = "+POST_TOWN+", STATUS = "+STATUS+", BLPU_STATE_CODE = "+BLPU_STATE_CODE+", MATCH = "+MATCH+", X_COORDINATE = "+X_COORDINATE+", ORGANISATION_NAME = "+ORGANISATION_NAME+", POSTAL_ADDRESS_CODE_DESCRIPTION = "+POSTAL_ADDRESS_CODE_DESCRIPTION+", UDPRN = "+UDPRN+", Y_COORDINATE = "+Y_COORDINATE+", UPRN = "+UPRN+", POSTAL_ADDRESS_CODE = "+POSTAL_ADDRESS_CODE+", LANGUAGE = "+LANGUAGE+", LOCAL_CUSTODIAN_CODE_DESCRIPTION = "+LOCAL_CUSTODIAN_CODE_DESCRIPTION+", BUILDING_NUMBER = "+BUILDING_NUMBER+", ADDRESS = "+ADDRESS+", BLPU_STATE_DATE = "+BLPU_STATE_DATE+", THOROUGHFARE_NAME = "+THOROUGHFARE_NAME+", MATCH_DESCRIPTION = "+MATCH_DESCRIPTION+", CLASSIFICATION_CODE_DESCRIPTION = "+CLASSIFICATION_CODE_DESCRIPTION+", LAST_UPDATE_DATE = "+LAST_UPDATE_DATE+", DEPENDENT_LOCALITY = "+DEPENDENT_LOCALITY+", TOPOGRAPHY_LAYER_TOID = "+TOPOGRAPHY_LAYER_TOID+", RPC = "+RPC+", ENTRY_DATE = "+ENTRY_DATE+", LOGICAL_STATUS_CODE = "+LOGICAL_STATUS_CODE+"]";
        }
    }
}



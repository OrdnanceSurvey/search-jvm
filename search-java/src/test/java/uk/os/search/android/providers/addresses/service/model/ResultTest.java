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

package uk.os.search.android.providers.addresses.service.model;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ResultTest {

    @Test
    public void methodsExist() {
        Result r = new Result();
        r.getGazetteerEntry();
        assertNotNull(r.toString());
    }

    @Test
    public void dpaMethodsExist() {
        Result.Dpa dpa = new Result.Dpa();
        dpa.getCLASSIFICATION_CODE();
        dpa.getBLPU_STATE_CODE_DESCRIPTION();
        dpa.getPOSTCODE();
        dpa.getLOCAL_CUSTODIAN_CODE();
        dpa.getPOST_TOWN();
        dpa.getSTATUS();
        dpa.getBLPU_STATE_CODE();
        dpa.getMATCH();
        dpa.getX_COORDINATE();
        dpa.getORGANISATION_NAME();
        dpa.getPOSTAL_ADDRESS_CODE_DESCRIPTION();
        dpa.getUDPRN();
        dpa.getY_COORDINATE();
        dpa.getUPRN();
        dpa.getPOSTAL_ADDRESS_CODE();
        dpa.getLANGUAGE();
        dpa.getLOCAL_CUSTODIAN_CODE_DESCRIPTION();
        dpa.getBUILDING_NUMBER();
        dpa.getADDRESS();
        dpa.getBLPU_STATE_DATE();
        dpa.getTHOROUGHFARE_NAME();
        dpa.getMATCH_DESCRIPTION();
        dpa.getCLASSIFICATION_CODE_DESCRIPTION();
        dpa.getLAST_UPDATE_DATE();
        dpa.getDEPENDENT_LOCALITY();
        dpa.getTOPOGRAPHY_LAYER_TOID();
        dpa.getRPC();
        dpa.getENTRY_DATE();
        dpa.getLOGICAL_STATUS_CODE();
        assertNotNull(dpa.toString());
    }
}

/*
 * Copyright 2017 Sam Hume.
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
package tracexml;

/**
 * Phase class contains the constants for the life-cycle phase text
 * @version 0.1
*/
public class Phase {
    /** constant String for the EHR phase of the life-cycle */
    public static final String EHR_PHASE = "EHR";
    /** constant String for the Data Collection phase of the life-cycle */
    public static final String DATA_COLLECTION_PHASE = "DATA_COLLECTION";
    /** constant String for the standardized tabulation phase of the life-cycle */
    public static final String TABULATION_PHASE = "TABULATION";
    /** constant String for the analysis phase of the life-cycle */
    public static final String ANALYSIS_PHASE = "ANALYSIS";
    /** constant String for the analysis results phase of the life-cycle */
    public static final String ANALYSIS_RESULTS_PHASE = "ANALYSIS_RESULTS";
    // description of each lifecycle phase
    /** constant String for a description of the EHR phase of the life-cycle */
    public static final String EHR_PHASE_DESC = "HL7 EHR data as eSource";
    /** constant String for a description of the Data Collection phase of the life-cycle */
    public static final String DATA_COLLECTION_PHASE_DESC = "Data collection in ODM-XML";
    /** constant String for a description of the standardized tabulation phase of the life-cycle */
    public static final String TABULATION_PHASE_DESC = "SDTM data tabulation in Define-XML";
    /** constant String for a description of the analysis phase of the life-cycle */
    public static final String ANALYSIS_PHASE_DESC = "ADaM data analysis in Define-XML";
    /** constant String for a description of the analysis results phase of the life-cycle */
    public static final String ANALYSIS_RESULTS_PHASE_DESC = "Analysis results in the Define-XML ARM extension";
    // lifecycle phase
    /** constant String for the first in the order of the phases in the life-cycle */
    public static final String EHR_PHASE_ORDER = "phase1";
    /** constant String for the second in the order of the phases in the life-cycle */
    public static final String DATA_COLLECTION_PHASE_ORDER = "phase2";
    /** constant String for the third in the order of the phases in the life-cycle */
    public static final String TABULATION_PHASE_ORDER = "phase3";
    /** constant String for the fourth in the order of the phases in the life-cycle */
    public static final String ANALYSIS_PHASE_ORDER = "phase4";
    /** constant String for the fifth in the order of the phases in the life-cycle */
    public static final String ANALYSIS_RESULTS_PHASE_ORDER = "phase5";
    
    /** 
     * getStandardByPhase looks up the standard associated with each life-cycle phase
     * @param phase String life-cycle phase (e.g. DATA_COLLECTION, TABULATION, ANALYSIS)
     * @return a String containing the name of the CDISC standard associated with the life-cycle phase
     */
    public static String getStandardByPhase(String phase) {
        String standard = "";
        switch (phase) {
            case DATA_COLLECTION_PHASE:
                standard = "ODM";
                break;
            case TABULATION_PHASE:
                standard = "SDTM";
                break;
            case ANALYSIS_PHASE:
                standard = "ADaM";
                break;
            default:
                System.out.println("Warning: Lifecycle phase " + phase + " has not yet been implemented (Phase.getStandardByPhase).");
        }
        return standard;
    }
}

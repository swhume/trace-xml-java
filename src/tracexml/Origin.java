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
 * The Origin class contains constants for the origin types and the tests
 * determining whether or not a specific origin is traceable
 * @version 0.1
*/
public class Origin {
    /** Constant for a def:Origin of Derived */
    public static final String DERIVED_ORIGIN = "Derived";
    /** Constant for a def:Origin of CRF */
    public static final String CRF_ORIGIN = "CRF";
    /** Constant for a def:Origin of Predecessor */
    public static final String PREDECESSOR_ORIGIN = "Predecessor";
    /** Constant for a def:Origin of None - denote a missing origin */
    public static final String NO_ORIGIN = "None";
    /** Constant for a def:Origin of Assigned */
    public static final String ASSIGNED_ORIGIN = "Assigned";
    /** Constant for a def:Origin of Protocol */
    public static final String PROTOCOL_ORIGIN = "Protocol";
    /** Constant for an origin of Method - added for Trace-XML */
    public static final String METHOD_ORIGIN = "Method";          // added for Trace-XML
    /** Constant for an origin of Collection - added for Trace-XML */
    public static final String COLLECTION_ORIGIN = "Collection";  // added for Trace-XML
    /** Constant for an origin of Derived with no source arguments (no inputs) - added for Trace-XML */
    public static final String DERIVED_NO_SRC_ORIGIN = "Derived - no source arguments";
        
    /**
     * isOriginTraceable determines if an ItemDef should be traceable based on its origin
     * @param origin String containing the origin value
     * @return True if an ItemDef should be traceable based on its origin
     */
    public static Boolean isOriginTraceable(String origin) {
        Boolean isTraceable;
        if (origin.equalsIgnoreCase(DERIVED_ORIGIN) || origin.equalsIgnoreCase(CRF_ORIGIN) || 
                    origin.equalsIgnoreCase(PREDECESSOR_ORIGIN) || origin.equalsIgnoreCase(NO_ORIGIN)) {
            isTraceable = Boolean.TRUE;
        } else if (origin.equalsIgnoreCase(ASSIGNED_ORIGIN) || origin.equalsIgnoreCase(PROTOCOL_ORIGIN) ||
                origin.equalsIgnoreCase(METHOD_ORIGIN) || origin.equalsIgnoreCase(COLLECTION_ORIGIN) || 
                origin.equalsIgnoreCase(DERIVED_NO_SRC_ORIGIN)) {
            isTraceable = Boolean.FALSE;
        } else {
            isTraceable = Boolean.TRUE;
            System.out.println("Error: invalid origin type - " + origin);
        }
        return isTraceable;    
    }    

    /** 
     * isOriginSourceItems
     * @param origin String containing the origin value
     * @return True if an ItemDef should include source items using the Trace-XML extension
     */
    public static Boolean isOriginSourceItems(String origin) {
        Boolean isTraceable = Boolean.FALSE;
        if (origin.equalsIgnoreCase(DERIVED_ORIGIN) || origin.equalsIgnoreCase(CRF_ORIGIN) || 
                    origin.equalsIgnoreCase(PREDECESSOR_ORIGIN)) {
            isTraceable = Boolean.TRUE;
        }
        return isTraceable;    
    }
}

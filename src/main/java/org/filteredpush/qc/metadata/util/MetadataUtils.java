/**
 * MetadataUtils.java
 *
 * Copyright 2022 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */
package org.filteredpush.qc.metadata.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class MetadataUtils {

	private static final Log logger = LogFactory.getLog(MetadataUtils.class);

    /**
     * Does a string contain a non-blank value.
     * 
     * @param aString to check
     * @return true if the string is null, is an empty string, 
     *     or contains only whitespace.
     */
    public static boolean isEmpty(String aString)  {
    	boolean result = true;
    	if (aString != null && aString.trim().length()>0) { 
    		// TG2, do not consider string representations of NULL as null, consider as data.
    		//if (!aString.trim().toUpperCase().equals("NULL")) { 
    		   result = false;
    		//}
    	}
    	return result;
    }
	
}

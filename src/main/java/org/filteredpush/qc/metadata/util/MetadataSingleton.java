/**
 * MetadataSingleton.java
 *
 * Copyright 2024 President and Fellows of Harvard College
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.metadata.services.GbifService;

/**
 * @author mole
 *
 */
public class MetadataSingleton {

	private static final Log logger = LogFactory.getLog(MetadataSingleton.class);
	
	// TDOO: Cache values
	
	
	private static final MetadataSingleton instance = new MetadataSingleton();
	
	private Boolean loaded;
	private String loadError;
	
	private Map<String,List<String>> lifeStageTerms = new HashMap<String,List<String>>();
	private Map<String,String> lifeStageValues = new HashMap<String,String>();
	
	private Map<String,List<String>> pathwayTerms = new HashMap<String,List<String>>();
	private Map<String,String> pathwayValues = new HashMap<String,String>();
	
	private Map<String,List<String>> typeStatusTerms = new HashMap<String,List<String>>();
	private Map<String,String> typeStatusValues = new HashMap<String,String>();
	
	private Map<String,List<String>> sexTerms = new HashMap<String,List<String>>();
	private Map<String,String> sexValues = new HashMap<String,String>();
	
	private Map<String,List<String>> degreeOfEstablishmentTerms = new HashMap<String,List<String>>();
	private Map<String,String> degreeOfEstablishmentValues = new HashMap<String,String>();
		
	private MetadataSingleton() { 
		init();
	}
	
	/**
	 * Get the singleton instance of MetadataSingleton.
	 *
	 * @return the singleton {@link org.filteredpush.qc.metadata.util.MetadataSingleton} object instance.
	 */
	public static synchronized MetadataSingleton getInstance() {
		return instance;
	}
	
	private void init() {
		loaded = false; 
		
		try { 
			GbifService gbif = new GbifService();
			
			lifeStageTerms = gbif.loadVocabulary("LifeStage");
			Iterator<String> keys = lifeStageTerms.keySet().iterator();
			while (keys.hasNext()) { 
				String key = keys.next();
				List<String> values = lifeStageTerms.get(key);
				Iterator<String> i = values.iterator();
				while (i.hasNext()) { 
					lifeStageValues.put(i.next(), key);
				}
			}
			
			pathwayTerms = gbif.loadVocabulary("Pathway");
			keys = pathwayTerms.keySet().iterator();
			while (keys.hasNext()) { 
				String key = keys.next();
				List<String> values = pathwayTerms.get(key);
				Iterator<String> i = values.iterator();
				while (i.hasNext()) { 
					pathwayValues.put(i.next(), key);
				}
			}
			
			typeStatusTerms = gbif.loadVocabulary("TypeStatus");
			keys = typeStatusTerms.keySet().iterator();
			while (keys.hasNext()) { 
				String key = keys.next();
				List<String> values = typeStatusTerms.get(key);
				Iterator<String> i = values.iterator();
				while (i.hasNext()) { 
					typeStatusValues.put(i.next(), key);
				}
			}
			
			sexTerms = gbif.loadVocabulary("Sex");
			keys = sexTerms.keySet().iterator();
			while (keys.hasNext()) { 
				String key = keys.next();
				List<String> values = sexTerms.get(key);
				Iterator<String> i = values.iterator();
				while (i.hasNext()) { 
					sexValues.put(i.next(), key);
				}
			}
		
			degreeOfEstablishmentTerms = gbif.loadVocabulary("DegreeOfEstablishment");
			keys = degreeOfEstablishmentTerms.keySet().iterator();
			while (keys.hasNext()) { 
				String key = keys.next();
				List<String> values = degreeOfEstablishmentTerms.get(key);
				Iterator<String> i = values.iterator();
				while (i.hasNext()) { 
					degreeOfEstablishmentValues.put(i.next(), key);
				}
			}
		
			loaded = true;
			loadError = "";
		} catch (Exception e) { 
			loadError = e.getMessage();
		}
	}

	/**
	 * get the lifeStage value:key pairs
	 * for finding vocabulary values for alternative labels
	 *  
	 * @return the map of lifeStage values from the vocabulary
	 */
	public Map<String,String> getLifeStageValues() { 
		return lifeStageValues;
	}
	/**
	 * get the liifeStage key:list of value pairs 
	 * for finding values in the vocabulary
	 * 
	 * @return the map of lifeStage values from the vocabulary
	 */
	public Map<String, List<String>> getLifeStageTerms() { 
		return lifeStageTerms;
	}
	
	/**
	 * get the pathway value:key pairs
	 * for finding vocabulary values for alternative labels
	 * 
	 * @return the map of pathway values from the vocabulary
	 */
	public Map<String,String> getPathwayValues() { 
		return pathwayValues;
	}
	/**
	 * get the pathway key:list of value pairs 
	 * for finding values in the vocabulary
	 * 
	 * @return the map of pathway values from the vocabulary
	 */
	public Map<String, List<String>> getPathwayTerms() { 
		return pathwayTerms;
	}
	
	/**
	 * get the typeStatus value:key pairs
	 * 
	 * @return the map of typeStatus values from the vocabulary
	 */
	public Map<String,String> getTypeStatusValues() { 
		return typeStatusValues;
	}
	/**
	 * get the typeStatus key:list of value pairs 
	 * for finding values in the vocabulary
	 * 
	 * @return the map of typeStatus values from the vocabulary
	 */
	public Map<String, List<String>> getTypeStatusTerms() { 
		return typeStatusTerms;
	}
	
	/**
	 * get the sex value:key pairs
	 * for finding vocabulary values for alternative labels
	 * 
	 * @return the map of sex values from the vocabulary
	 */
	public Map<String,String> getSexValues() { 
		return sexValues;
	}
	
	/**
	 * get the sex key:list of value pairs 
	 * for finding values in the vocabulary
	 * 
	 * @return the map of sex values from the vocabulary
	 */
	public Map<String, List<String>> getSexTerms() { 
		return sexTerms;
	}
	
	/**
	 * get the degreeOfEstablishment value:key pairs
	 * for finding vocabulary values for alternative labels
	 * 
	 * @return the map of degreeOfEstablishment values from the vocabulary
	 */
	public Map<String,String> getDegreeOfEstablishmentValues() { 
		return degreeOfEstablishmentValues;
	}
	
	/**
	 * get the degreeOfEstablishment key:list of value pairs 
	 * for finding values in the vocabulary
	 * 
	 * @return the map of degreeOfEstablishment values from the vocabulary
	 */
	public Map<String, List<String>> getDegreeOfEstablishmentTerms() { 
		return degreeOfEstablishmentTerms;
	}
	
	/**
	 * @return true if vocabularies have been loaded
	 */
	public Boolean isLoaded() { 
		return loaded;
	}
	
	/**
	 * @return any load error message
	 */
	public String getLoadError() { 
		return loadError;
	}
	
}

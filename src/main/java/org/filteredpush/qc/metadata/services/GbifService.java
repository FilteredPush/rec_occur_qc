/**
 * GbifService.java
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
package org.filteredpush.qc.metadata.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gbif.dwc.terms.Vocabulary;
import org.json.simple.JSONArray;
// import org.gbif.ws.client.ClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * @author mole
 *
 */
public class GbifService {

	private static final Log logger = LogFactory.getLog(GbifService.class);
	
	private static final String gbifApiEndpoint = "https://api.gbif.org/v1/";

	/**
	 * default constructor
	 */
	public GbifService() {
		init();
	}
	/** 
	 * set up class instance
	 */
	private void init() { 
		//ClientBuilder clientBuilder = new ClientBuilder().withUrl(gbifApiEndpoint);
		
		//Vocabulary vocabularyClient = clientBuilder.build(Vocabulary.class);
		
	}
	
	/**
	 * Load a GBIF vocabulary
	 * 
	 * @param vocabulary the name of the vocabulary to load
	 * @return a  Map String, List String where the keys are vocabulary terms and the values are lists of 
	 * possible alternative names for the term.
	 */
	public Map<String,List<String>> loadVocabulary(String vocabulary) { 
		HashMap<String,List<String>> result = new HashMap();
		
		String lookup = gbifApiEndpoint + "vocabularies/" + vocabulary + "/concepts";
		logger.debug(lookup);
		URI lookupURI = URI.create(lookup);
		
		HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
		HttpRequest request = HttpRequest.newBuilder().uri(lookupURI).GET().build();
		
		HttpResponse<String> response;
		
		try {
			response = client.send(request,HttpResponse.BodyHandlers.ofString());
			JSONObject responseJson = (JSONObject) JSONValue.parse(response.body());
			boolean loaded = true;
			
			logger.debug(response);
			logger.debug(response.body());
			
			boolean endOfRecords = false;
			JSONArray resultList = (JSONArray) responseJson.get("results");
			logger.debug(responseJson.get("count"));
			if (resultList!=null) { 
				int limit = Integer.parseInt(responseJson.get("limit").toString());
				int offset = 0;
				while (endOfRecords==false) { 
					if (!loaded) { 
						lookup = gbifApiEndpoint + "vocabularies/" + vocabulary + "/concepts?offset=" + offset;
						logger.debug(lookup);
						lookupURI = URI.create(lookup);
						client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
						request = HttpRequest.newBuilder().uri(lookupURI).GET().build();
						response = client.send(request,HttpResponse.BodyHandlers.ofString());
						responseJson = (JSONObject) JSONValue.parse(response.body());
						resultList = (JSONArray) responseJson.get("results");
					}
					for (int i=0; i<resultList.size(); i++) { 
						JSONObject item = (JSONObject) resultList.get(i);
						String name = item.get("name").toString();
						ArrayList<String> list = new ArrayList<String>();
						list.add(name);
						logger.debug(name);
						// label[0].value
						JSONArray labels = (JSONArray) item.get("label");
						for (int j=0; j<labels.size(); j++) { 
							String label = ((JSONObject)labels.get(j)).get("value").toString();
							list.add(label);
						}
						// externalDefinitions[0]
						JSONArray terms = (JSONArray) item.get("externalDefinitions");
						for (int j=0; j<terms.size(); j++) { 
							String externalDefinition = terms.get(j).toString();
							list.add(externalDefinition);
						}
						result.put(name,list);
					}
					String endOfRecordsString = responseJson.get("endOfRecords").toString();
					if (endOfRecordsString.toLowerCase().equals("true")) { 
						endOfRecords = true;
					} else { 
						endOfRecords = false;
					}
					offset = offset + limit;
					loaded = false;
				}	
			}
			logger.debug(result.size());
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	/**
	 * Main class for testing during development
	 * @param args arguments, none expected
	 */
	public static void main( String[] args ) { 
		GbifService test = new GbifService();
		Map<String,List<String>> pathway = test.loadVocabulary("Pathway");
		
		Iterator<String> keys = pathway.keySet().iterator();
		while (keys.hasNext()) { 
			String key = keys.next();
			List<String> values = pathway.get(key);
			Iterator<String> i = values.iterator();
			while (i.hasNext()) { 
				System.out.println(key + ":" + i.next());
			}
		}
		
	}
}

package org.filteredpush.qc.metadata.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Read-only access to a W3C Web Annotation annotation system, for ISSUE_ANNOTATION_NOTEMPTY:
 * the targets of the annotations it holds.
 *
 * The annotation system is identified by an http(s) IRI that returns Web Annotation JSON-LD
 * (https://www.w3.org/TR/annotation-model/, https://www.w3.org/TR/annotation-protocol/): an
 * AnnotationCollection (a Web Annotation Protocol container, or a static collection), an AnnotationPage, or a single Annotation. The pages of a
 * collection ("first", then each page's "next") are followed, whether embedded or referenced by IRI.
 *
 * @author mole
 */
public class AnnotationSystem {

	/** Maximum number of pages read from one annotation system. */
	public static final int MAX_PAGES = 100;
	private static final int TIMEOUT_MILLIS = 15000;
	private static final String ACCEPT = "application/ld+json; profile=\"http://www.w3.org/ns/anno.jsonld\", application/ld+json, application/json";

	/**
	 * Is a value of bdqval:annotationSystem an annotation system that can be queried: an http(s) IRI?
	 * The default, "W3C Web Annotation", names the data model, not a system that can be queried.
	 *
	 * @param annotationSystem the value of bdqval:annotationSystem
	 * @return true if it is an http or https IRI
	 */
	public static boolean isQueryable(String annotationSystem) {
		if (annotationSystem == null) { return false; }
		String s = annotationSystem.trim();
		return s.startsWith("http://") || s.startsWith("https://");
	}

	/**
	 * The targets of all annotations in the annotation system.
	 *
	 * @param annotationSystem an http(s) IRI of an AnnotationCollection, AnnotationPage, or Annotation
	 * @return the IRIs given as the target (or the source of a specific resource target) of each annotation
	 * @throws IOException if the annotation system cannot be read or does not return Web Annotation JSON
	 */
	public static Set<String> targets(String annotationSystem) throws IOException {
		Set<String> result = new HashSet<String>();
		Set<String> visited = new HashSet<String>();
		Object current = fetch(annotationSystem.trim());
		visited.add(annotationSystem.trim());
		int pages = 0;
		while (current != null && pages < MAX_PAGES) {
			pages++;
			Object next = collectTargets(current, result);
			current = null;
			if (next instanceof Map) {
				current = next;
			} else if (next instanceof String && !visited.contains(next)) {
				visited.add((String) next);
				current = fetch((String) next);
			}
		}
		return result;
	}

	/**
	 * Add the targets of the annotations in a parsed JSON-LD document to targets, and return the next
	 * page to read: an embedded page (a Map), the IRI of a page (a String), or null.
	 *
	 * @param document a parsed AnnotationCollection, AnnotationPage, or Annotation
	 * @param targets the set to which the targets are added
	 * @return the next page, or null
	 */
	@SuppressWarnings("rawtypes")
	public static Object collectTargets(Object document, Set<String> targets) {
		if (!(document instanceof Map)) { return null; }
		Map map = (Map) document;
		String type = typeOf(map);
		if ("Annotation".equals(type)) {
			addTargets(map.get("target"), targets);
			return null;
		}
		if ("AnnotationCollection".equals(type) || map.containsKey("first")) {
			return map.get("first");
		}
		Object items = map.get("items");
		if (items instanceof List) {
			for (Object item : (List) items) {
				if (item instanceof Map) {
					addTargets(((Map) item).get("target"), targets);
				}
			}
		}
		return map.get("next");
	}

	/**
	 * Parse Web Annotation JSON-LD text.
	 *
	 * @param json the text
	 * @return the parsed document
	 * @throws IOException if the text is not JSON
	 */
	public static Object parse(String json) throws IOException {
		try {
			return new JSONParser().parse(json);
		} catch (ParseException e) {
			throw new IOException("Not JSON: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	private static String typeOf(Map map) {
		Object type = map.containsKey("type") ? map.get("type") : map.get("@type");
		if (type instanceof List && !((List) type).isEmpty()) { type = ((List) type).get(0); }
		if (type == null) { return null; }
		String t = type.toString();
		return t.substring(Math.max(t.lastIndexOf('#'), t.lastIndexOf('/')) + 1).replace("oa:", "").replace("as:", "");
	}

	@SuppressWarnings("rawtypes")
	private static void addTargets(Object target, Set<String> targets) {
		if (target == null) { return; }
		if (target instanceof String) {
			targets.add(((String) target).trim());
		} else if (target instanceof List) {
			for (Object t : (List) target) { addTargets(t, targets); }
		} else if (target instanceof Map) {
			Map m = (Map) target;
			// A specific resource names its source; an external web resource its id.
			Object source = m.get("source");
			if (source != null) {
				addTargets(source, targets);
			} else {
				Object id = m.containsKey("id") ? m.get("id") : m.get("@id");
				if (id != null) { targets.add(id.toString().trim()); }
			}
		}
	}

	private static Object fetch(String iri) throws IOException {
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URI(iri).toURL().openConnection();
		} catch (Exception e) {
			throw new IOException("Not an IRI that can be read: " + iri, e);
		}
		connection.setRequestProperty("Accept", ACCEPT);
		connection.setConnectTimeout(TIMEOUT_MILLIS);
		connection.setReadTimeout(TIMEOUT_MILLIS);
		connection.setInstanceFollowRedirects(true);
		int status = connection.getResponseCode();
		if (status != 200) {
			throw new IOException("HTTP " + status + " from " + iri);
		}
		try (InputStream in = connection.getInputStream()) {
			java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int n;
			while ((n = in.read(buffer)) != -1) { out.write(buffer, 0, n); }
			return parse(new String(out.toByteArray(), StandardCharsets.UTF_8));
		}
	}
}

package org.filteredpush.qc.metadata.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AnnoSys, the annotation system of the Botanic Garden and Botanical Museum Berlin (BGBM), integrated
 * with GBIF occurrence pages (https://annosys.bgbm.fu-berlin.de/), as a bdqval:annotationSystem for
 * ISSUE_ANNOTATION_NOTEMPTY.
 *
 * AnnoSys identifies a record by its Darwin Core triplet (institution code, collection code, catalogue
 * number), and its RESTful services answer whether it holds annotations of the record identified by a
 * triplet: GET {@value #SERVICES}/records/&lt;institution&gt;/&lt;collection&gt;/&lt;catalogue number&gt;/annotations
 * (each URL-encoded) returns JSON with hasAnnotation and size (AnnoSys Technical Documentation, Section
 * 11.3, https://wiki.bgbm.org/annosys/index.php?title=TechnicalDocumentation). The Test gives the record as
 * an IRI (oa:hasTarget), so the triplet is obtained from GBIF: the occurrences whose dwc:occurrenceID is
 * the IRI (GBIF occurrence search), or the occurrence itself when the IRI is a GBIF occurrence IRI.
 *
 * @author mole
 */
public class AnnoSys {

	/** The value of bdqval:annotationSystem that selects AnnoSys. */
	public static final String IDENTIFIER = "AnnoSys";
	/** The AnnoSys service base IRI (also accepted as a value of bdqval:annotationSystem). */
	public static final String BASE = "https://annosys.bgbm.fu-berlin.de/AnnoSys";
	/** AnnoSys RESTful services. */
	public static final String SERVICES = BASE + "/services";
	/** GBIF API. */
	public static final String GBIF_API = "https://api.gbif.org/v1";

	private static final int TIMEOUT_MILLIS = 30000;
	private static final Pattern GBIF_OCCURRENCE = Pattern.compile(
			"^https?://(?:www\\.gbif\\.org|gbif\\.org|api\\.gbif\\.org/v1)/occurrence/(\\d+)/?$");

	/**
	 * Does a value of bdqval:annotationSystem select AnnoSys?
	 *
	 * @param annotationSystem the value of bdqval:annotationSystem
	 * @return true for {@value #IDENTIFIER} (in any case), or for the service base IRI {@value #BASE}
	 *   (with or without a trailing slash, either scheme)
	 */
	public static boolean isAnnoSys(String annotationSystem) {
		if (annotationSystem == null) { return false; }
		String s = annotationSystem.trim().replaceAll("/+$", "").replaceFirst("^http://", "https://");
		return s.equalsIgnoreCase(IDENTIFIER) || s.equalsIgnoreCase(BASE);
	}

	/**
	 * Is AnnoSys available: does its record service answer?
	 *
	 * @throws IOException if not
	 */
	public static void checkAvailable() throws IOException {
		recordAnnotations(new Triplet("-", "-", "-"));
	}

	/** A Darwin Core triplet: dwc:institutionCode, dwc:collectionCode, dwc:catalogNumber. */
	public static class Triplet {
		public final String institutionCode;
		public final String collectionCode;
		public final String catalogNumber;
		public Triplet(String institutionCode, String collectionCode, String catalogNumber) {
			this.institutionCode = institutionCode;
			this.collectionCode = collectionCode;
			this.catalogNumber = catalogNumber;
		}
		@Override public String toString() { return institutionCode + ":" + collectionCode + ":" + catalogNumber; }
		@Override public boolean equals(Object o) { return o instanceof Triplet && toString().equals(o.toString()); }
		@Override public int hashCode() { return toString().hashCode(); }
	}

	/** The annotations AnnoSys holds of one record. */
	public static class RecordAnnotations {
		public final boolean hasAnnotation;
		public final long size;
		public RecordAnnotations(boolean hasAnnotation, long size) {
			this.hasAnnotation = hasAnnotation;
			this.size = size;
		}
	}

	/**
	 * The Darwin Core triplets of the GBIF occurrences identified by an IRI: the occurrence itself for a
	 * GBIF occurrence IRI (e.g. https://www.gbif.org/occurrence/214542049), otherwise the occurrences
	 * whose dwc:occurrenceID is the IRI. Occurrences lacking any of the three values are skipped.
	 *
	 * @param iri the IRI of the record (oa:hasTarget)
	 * @return the distinct triplets, possibly none
	 * @throws IOException if GBIF cannot be queried
	 */
	public static List<Triplet> gbifTriplets(String iri) throws IOException {
		String target = iri.trim();
		Matcher m = GBIF_OCCURRENCE.matcher(target);
		if (m.matches()) {
			Object occurrence;
			try {
				occurrence = getJson(GBIF_API + "/occurrence/" + m.group(1));
			} catch (NotFound e) {
				return new ArrayList<Triplet>();
			}
			return triplets(java.util.Collections.singletonList(occurrence));
		}
		Object result = getJson(GBIF_API + "/occurrence/search?limit=50&occurrenceId=" + encode(target));
		Object results = result instanceof Map ? ((Map<?, ?>) result).get("results") : null;
		return triplets(results instanceof List ? (List<?>) results : new ArrayList<Object>());
	}

	/**
	 * The distinct, complete Darwin Core triplets of parsed GBIF occurrences.
	 *
	 * @param occurrences parsed GBIF occurrence JSON objects
	 * @return the triplets
	 */
	public static List<Triplet> triplets(List<?> occurrences) {
		Set<Triplet> result = new LinkedHashSet<Triplet>();
		for (Object o : occurrences) {
			if (o instanceof Map) {
				Map<?, ?> occurrence = (Map<?, ?>) o;
				String i = text(occurrence.get("institutionCode"));
				String c = text(occurrence.get("collectionCode"));
				String n = text(occurrence.get("catalogNumber"));
				if (!i.isEmpty() && !c.isEmpty() && !n.isEmpty()) {
					result.add(new Triplet(i, c, n));
				}
			}
		}
		return new ArrayList<Triplet>(result);
	}

	/**
	 * Ask AnnoSys for the annotations of the record with the given triplet.
	 *
	 * @param triplet the record's Darwin Core triplet
	 * @return whether AnnoSys holds annotations of the record, and how many
	 * @throws IOException if AnnoSys cannot be queried
	 */
	public static RecordAnnotations recordAnnotations(Triplet triplet) throws IOException {
		Object response = getJson(SERVICES + "/records/" + encode(triplet.institutionCode) + "/"
				+ encode(triplet.collectionCode) + "/" + encode(triplet.catalogNumber) + "/annotations");
		return recordAnnotations(response);
	}

	/**
	 * Read a parsed AnnoSys record annotations response.
	 *
	 * @param response the parsed JSON
	 * @return whether AnnoSys holds annotations of the record, and how many
	 * @throws IOException if the response is not an AnnoSys record annotations response
	 */
	public static RecordAnnotations recordAnnotations(Object response) throws IOException {
		if (!(response instanceof Map) || !((Map<?, ?>) response).containsKey("hasAnnotation")) {
			throw new IOException("Not an AnnoSys record annotations response");
		}
		Map<?, ?> map = (Map<?, ?>) response;
		boolean has = Boolean.TRUE.equals(map.get("hasAnnotation"));
		Object size = map.get("size");
		return new RecordAnnotations(has, size instanceof Number ? ((Number) size).longValue() : (has ? 1 : 0));
	}

	private static String text(Object value) {
		return value == null ? "" : value.toString().trim();
	}

	/** Encode one path segment or query value (spaces as %20). */
	private static String encode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	private static class NotFound extends IOException {
		private static final long serialVersionUID = 1L;
		NotFound(String message) { super(message); }
	}

	private static Object getJson(String url) throws IOException {
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URI(url).toURL().openConnection();
		} catch (Exception e) {
			throw new IOException("Not a URL that can be read: " + url, e);
		}
		connection.setRequestProperty("Accept", "application/json");
		connection.setConnectTimeout(TIMEOUT_MILLIS);
		connection.setReadTimeout(TIMEOUT_MILLIS);
		int status = connection.getResponseCode();
		if (status == 404) {
			throw new NotFound("HTTP 404 from " + url);
		}
		if (status != 200) {
			throw new IOException("HTTP " + status + " from " + url);
		}
		try (InputStream in = connection.getInputStream()) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int n;
			while ((n = in.read(buffer)) != -1) { out.write(buffer, 0, n); }
			return AnnotationSystem.parse(new String(out.toByteArray(), StandardCharsets.UTF_8));
		}
	}
}

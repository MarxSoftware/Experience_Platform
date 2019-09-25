package com.thorstenmarx.webtools.referer;

/*-
 * #%L
 * webtools-incubator
 * %%
 * Copyright (C) 2016 - 2018 Thorsten Marx
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class RefererParser {

	private static final String REFERERS_YAML_PATH = "referers.yml";
	private Map<String, RefererLookup> referers;

	/**
	 * Construct our Parser object using the bundled referers.yml
	 * @throws java.io.IOException
	 */
	public RefererParser() throws IOException {
		this(RefererParser.class.getResourceAsStream(REFERERS_YAML_PATH));
	}

	/**
	 * Construct our Parser object using a InputStream (in YAML format)
	 *
	 * @param referersStream
	 * 
	 */
	public RefererParser(InputStream referersStream)  {
		referers = loadReferers(referersStream);
	}

	public Referer parse(String refererUri, URI pageUri) throws URISyntaxException {
		return parse(refererUri, pageUri.getHost());
	}

	public Referer parse(String refererUri, String pageHost) throws URISyntaxException {
		if (refererUri == null || refererUri == "") {
			return null;
		}
		final URI uri = new URI(refererUri);
		return parse(uri, pageHost);
	}

	public Referer parse(URI refererUri, String pageHost) {
		return parse(refererUri, pageHost, Collections.<String>emptyList());
	}

	public Referer parse(URI refererUri, String pageHost, List<String> internalDomains) {
		if (refererUri == null) {
			return null;
		}
		return parse(refererUri.getScheme(), refererUri.getHost(), refererUri.getPath(), refererUri.getRawQuery(), pageHost, internalDomains);
	}

	public Referer parse(URL refererUrl, String pageHost) {
		if (refererUrl == null) {
			return null;
		}
		return parse(refererUrl.getProtocol(), refererUrl.getHost(), refererUrl.getPath(), refererUrl.getQuery(), pageHost);
	}

	private Referer parse(String scheme, String host, String path, String query, String pageHost) {
		return parse(scheme, host, path, query, pageHost, Collections.<String>emptyList());
	}

	private Referer parse(String scheme, String host, String path, String query, String pageHost, List<String> internalDomains) {

		if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
			return Referer.UNKNOWN;
		}

		// Internal link if hosts match exactly
		// TODO: would also be nice to:
		// 1. Support a list of other hosts which count as internal
		// 2. Have an algo for stripping subdomains before checking match
		if (host == null) {
			return null; // Not a valid URL
		}
		if (host.equals(pageHost)) {
			return Referer.INTERNAL;
		}
		for (String s : internalDomains) {
			if (s.trim().equals(host)) {
				return Referer.INTERNAL;
			}
		}

		// Try to lookup our referer. First check with paths, then without.
		// This is the safest way of handling lookups
//		RefererLookup referer = lookupReferer(host, path, true);
//		if (referer == null) {
			RefererLookup referer = lookupReferer(host, path, false);
//		}

		if (referer == null) {
			return Referer.UNKNOWN; // Unknown referer, nothing more to do
		} else {
			// Potentially add a search term
			return new Referer(referer.medium, referer.source);
		}
	}

	/**
	 * Recursive function to lookup a host (or partial host) in our referers
	 * map.
	 *
	 * First check the host, then the host+full path, then the host+ one-level
	 * path.
	 *
	 * If not found, remove one subdomain-level off the front of the host and
	 * try again.
	 *
	 * @param pageHost The host of the current page
	 * @param pagePath The path to the current page
	 * @param includePath Whether to include the path in the lookup
	 *
	 * @return a RefererLookup object populated with the given referer, or null
	 * if not found
	 */
	private RefererLookup lookupReferer(String refererHost, String refererPath, Boolean includePath) {

		// Check if domain+full path matches, e.g. for apollo.lv/portal/search/ 
		RefererLookup referer = (includePath) ? referers.get(refererHost + refererPath) : referers.get(refererHost);

		// Check if domain+one-level path matches, e.g. for orange.fr/webmail/fr_FR/read.html (in our YAML it's orange.fr/webmail)
		if (includePath && referer == null) {
			final String[] pathElements = refererPath.split("/");
			if (pathElements.length > 1) {
				referer = referers.get(refererHost + "/" + pathElements[1]);
			}
		}

		if (referer == null) {
			final int idx = refererHost.indexOf('.');
			if (idx == -1) {
				return null; // No "."? Let's quit.
			} else {
				return lookupReferer(refererHost.substring(idx + 1), refererPath, includePath); // Recurse
			}
		} else {
			return referer;
		}
	}

	/**
	 * Builds the map of hosts to referers from the input YAML file.
	 *
	 * @param referersYaml An InputStream containing the referers database in
	 * YAML format.
	 *
	 * @return a Map where the key is the hostname of each referer and the value
	 * (RefererLookup) contains all known info about this referer
	 */
	private Map<String, RefererLookup> loadReferers(InputStream referersYaml) {

		Yaml yaml = new Yaml(new SafeConstructor());
		Map<String, Map<String, Map>> rawReferers = (Map<String, Map<String, Map>>) yaml.load(referersYaml);

		// This will store all of our referers
		Map<String, RefererLookup> referers = new HashMap<>();

		// Outer loop is all referers under a given medium
		for (Map.Entry<String, Map<String, Map>> mediumReferers : rawReferers.entrySet()) {

			Medium medium = Medium.fromString(mediumReferers.getKey());

			// Inner loop is individual referers
			for (Map.Entry<String, Map> referer : mediumReferers.getValue().entrySet()) {

				String sourceName = referer.getKey();
				Map<String, List<String>> refererMap = referer.getValue();

				List<String> domains = refererMap.get("domains");
				if (domains == null) {
					throw new IllegalStateException("No domains found for referer '" + sourceName + "'");
				}

				// Our hash needs referer domain as the
				// key, so let's expand
				for (String domain : domains) {
					if (referers.containsValue(domain)) {
						throw new IllegalStateException("Duplicate of domain '" + domain + "' found");
					}
					referers.put(domain, new RefererLookup(medium, sourceName));
				}
			}
		}

		return referers;
	}
}

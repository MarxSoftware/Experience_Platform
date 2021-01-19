package de.marx_software.webtools.tracking;

/*-
 * #%L
 * webtools-manager
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
import com.optimaize.webcrawlerverifier.DefaultKnownCrawlerDetector;
import com.optimaize.webcrawlerverifier.bots.BuiltInCrawlers;
import com.optimaize.webcrawlerverifier.bots.CrawlerData;
import com.optimaize.webcrawlerverifier.bots.KnownHostBotVerifier;
import com.optimaize.webcrawlerverifier.bots.KnownHostBotVerifierBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marx
 */
public class CrawlerUtil {

	DefaultKnownCrawlerDetector detector;

	public CrawlerUtil() {
		List<KnownHostBotVerifier> verifiers = new ArrayList<>();
		BuiltInCrawlers.get().stream().forEach((crawlerData) -> {
			verifiers.add(new KnownHostBotVerifierBuilder()
					.crawlerData(crawlerData)
					.dnsVerifierDefault()
					.dnsResultCacheDefault()
					.build());
		});
		this.detector = new DefaultKnownCrawlerDetector(verifiers);
	}
	
	public boolean isCrawler (final String userAgent, final String ip) {
		return detector.detect(userAgent, ip).isPresent();
	}
}

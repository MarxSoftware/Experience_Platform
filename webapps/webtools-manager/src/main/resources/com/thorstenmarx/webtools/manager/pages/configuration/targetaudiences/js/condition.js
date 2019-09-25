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

window.QueryBuilder = (function (exports, ko) {

	function CampaignCondition(initValue) {
		var self = this;

		self.templateName = 'campaign-condition-template';

		self.campaign = ko.observable();
		self.medium = ko.observable();
		self.source = ko.observable();

		if (typeof initValue !== "undefined") {
			self.campaign(initValue.campaign);
			self.medium(initValue.medium);
			self.source(initValue.source);
		}

		self.json = ko.computed(function () {
			return {
				type: "campaign",
				campaign: self.campaign(),
				medium: self.medium(),
				source: self.source(),
			};
		});
	}
	function ReferrerCondition(initValue) {
		var self = this;

		self.templateName = 'referrer-condition-template';

		self.medium = ko.observable();
		self.source = ko.observable();

		if (typeof initValue !== "undefined") {
			self.medium(initValue.medium);
			self.source(initValue.source);
		}

		self.json = ko.computed(function () {
			return {
				type: "referrer",
				medium: self.medium(),
				source: self.source(),
			};
		});
	}

	function PageViewCondition(initValue) {
		var self = this;

		self.templateName = 'pageview-condition-template';

		self.site = ko.observable();
		self.page = ko.observable();
		self.count = ko.observable(1);

		if (typeof initValue !== "undefined") {
			self.site(initValue.site);
			self.page(initValue.page);
			self.count(initValue.count);
		}

		self.json = ko.computed(function () {
			return {
				type: "pageview",
				site: self.site(),
				page: self.page(),
				count: self.count(),
			};
		});

	}
	
	function FirstVisitCondition(initValue) {
		var self = this;

		self.templateName = 'firstvisit-condition-template';

		self.site = ko.observable();

		if (typeof initValue !== "undefined") {
			self.site(initValue.site);
		}

		self.json = ko.computed(function () {
			return {
				type: "firstvisit",
				site: self.site(),
			};
		});

	}

	function ScoreCondition(initValue) {
		var self = this;

		self.templateName = 'score-condition-template';

		self.name = ko.observable();
		self.score = ko.observable();

		if (typeof initValue !== "undefined") {
			self.name(initValue.name);
			self.score(initValue.score);
		}

		self.json = ko.computed(function () {
			return {
				type: "score",
				name: self.name(),
				score: self.score()
			};
		});
	}

	function EventCondition(initValue) {
		var self = this;

		self.templateName = 'event-condition-template';

		self.site = ko.observable();
		self.event = ko.observable();
		self.count = ko.observable(1);

		if (typeof initValue !== "undefined") {
			self.site(initValue.site);
			self.event(initValue.event);
			self.count(initValue.count);
		}

		self.json = ko.computed(function () {
			return {
				type: "event",
				site: self.site(),
				event: self.event(),
				count: self.count()
			};
		});
	}
	
	function LocationCondition(initValue) {
		var self = this;

		self.templateName = 'location-condition-template';

		self.country = ko.observable();

		if (typeof initValue !== "undefined") {
			self.country(initValue.country);
		}

		self.json = ko.computed(function () {
			return {
				type: "location",
				country: self.country()
			};
		});
	}
	
	function BrowserCondition(initValue) {
		var self = this;

		self.templateName = 'browser-condition-template';

		self.value = ko.observable();

		if (typeof initValue !== "undefined") {
			self.value(initValue.value);
		}

		self.json = ko.computed(function () {
			return {
				type: "browser",
				value: self.value()
			};
		});
	}
	function OSCondition(initValue) {
		var self = this;

		self.templateName = 'os-condition-template';

		self.value = ko.observable();

		if (typeof initValue !== "undefined") {
			self.value(initValue.value);
		}

		self.json = ko.computed(function () {
			return {
				type: "os",
				value: self.value()
			};
		});
	}
	function DeviceCondition(initValue) {
		var self = this;

		self.templateName = 'device-condition-template';

		self.value = ko.observable();

		if (typeof initValue !== "undefined") {
			self.value(initValue.value);
		}

		self.json = ko.computed(function () {
			return {
				type: "device",
				value: self.value()
			};
		});
	}
	function KeyValueCondition(initValue) {
		var self = this;

		self.templateName = 'keyvalue-condition-template';

		self.values = ko.observable();
		self.name = ko.observable();
		self.operation = ko.observable();

		if (typeof initValue !== "undefined") {
			self.values(initValue.values);
			self.name(initValue.name);
			self.operation(initValue.operation);
		}

		self.json = ko.computed(function () {
			return {
				type: "keyvalue",
				name : self.name(),
				operation : self.operation(),
				values: self.values()
			};
		});
	}

	exports.PageViewCondition = PageViewCondition;
	exports.FirstVisitCondition = FirstVisitCondition;
	exports.ScoreCondition = ScoreCondition;
	exports.EventCondition = EventCondition;
	exports.LocationCondition = LocationCondition;
	exports.BrowserCondition = BrowserCondition;
	exports.OSCondition = OSCondition;
	exports.DeviceCondition = DeviceCondition;
	exports.CampaignCondition = CampaignCondition;
	exports.ReferrerCondition = ReferrerCondition;
	exports.KeyValueCondition = KeyValueCondition;
	return exports;

})(window.QueryBuilder || {}, window.ko);

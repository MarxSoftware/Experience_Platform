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
window.QueryBuilder = (function (exports, ko) {

	function PageViewCondition(initValue) {
		var self = this;

		self.templateName = 'pageview-condition-template';

		self.site = ko.observable();
		self.page = ko.observable();
		self.count = ko.observable(1);

		if (typeof initValue != "undefined") {
			self.site(initValue.site);
			self.page(initValue.page);
			self.count(initValue.count);
		}

		// the text() function is just an example to show output
		self.text = ko.computed(function () {
			return self.site() + "/" + self.page();
		});
		self.json = ko.computed(function () {
			return {
				type: "pageview",
				site: self.site(),
				page: self.page(),
				count: self.count(),
			};
		});

	}

	function ScoreCondition(initValue) {
		var self = this;

		self.templateName = 'score-condition-template';

		self.name = ko.observable();
		self.score = ko.observable();

		if (typeof initValue != "undefined") {
			self.name(initValue.name);
			self.score(initValue.score);
		}

		// the text() function is just an example to show output
		self.text = ko.computed(function () {
			return self.name() + "/" + self.score();
		});
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

		if (typeof initValue != "undefined") {
			self.site(initValue.site);
			self.event(initValue.event);
			self.count(initValue.count);
		}


		// the text() function is just an example to show output
		self.text = ko.computed(function () {
			return self.site() + "/" + self.event() + "/" + self.count();
		});
		self.json = ko.computed(function () {
			return {
				type: "event",
				site: self.site(),
				event: self.event(),
				count: self.count()
			};
		});
	}

	exports.PageViewCondition = PageViewCondition;
	exports.ScoreCondition = ScoreCondition;
	exports.EventCondition = EventCondition;
	return exports;

})(window.QueryBuilder || {}, window.ko);

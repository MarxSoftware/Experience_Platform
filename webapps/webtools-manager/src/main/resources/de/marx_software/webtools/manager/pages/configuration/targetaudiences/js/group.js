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

	var PageViewCondition = exports.PageViewCondition;
	var CampaignCondition = exports.CampaignCondition;
	var ReferrerCondition = exports.ReferrerCondition;
	var FirstVisitCondition = exports.FirstVisitCondition;
	var ScoreCondition = exports.ScoreCondition;
	var EventCondition = exports.EventCondition;
	var LocationCondition = exports.LocationCondition;
	var BrowserCondition = exports.BrowserCondition;
	var OSCondition = exports.OSCondition;
	var DeviceCondition = exports.DeviceCondition;
	var KeyValueCondition = exports.KeyValueCondition;

	function Group(initValue) {
		var self = this;

		self.templateName = 'group-template';
		self.children = ko.observableArray();
		self.logicalOperators = ko.observableArray(['AND', 'OR', 'NOT']);
		self.selectedLogicalOperator = ko.observable('AND');

		if (typeof initValue !== "undefined" && initValue !== ""){
			if (initValue.operator) {
				self.selectedLogicalOperator(initValue.operator);
			}
			
			if (typeof initValue.rules !== "undefined") {
				initValue.rules.forEach(function (rule) {
					if (rule.type === "group") {
						self.children.push(new Group(rule));
					} else if (rule.type === "pageview") {
						self.children.push(new PageViewCondition(rule));
					} else if (rule.type === "event") {
						self.children.push(new EventCondition(rule));
					} else if (rule.type === "score") {
						self.children.push(new ScoreCondition(rule));
					} else if (rule.type === "browser") {
						self.children.push(new BrowserCondition(rule));
					} else if (rule.type === "os") {
						self.children.push(new OSCondition(rule));
					} else if (rule.type === "device") {
						self.children.push(new DeviceCondition(rule));
					} else if (rule.type === "location") {
						self.children.push(new LocationCondition(rule));
					} else if (rule.type === "firstvisit") {
						self.children.push(new FirstVisitCondition(rule));
					} else if (rule.type === "campaign") {
						self.children.push(new CampaignCondition(rule));
					} else if (rule.type === "referrer") {
						self.children.push(new ReferrerCondition(rule));
					} else if (rule.type === "keyvalue") {
						self.children.push(new KeyValueCondition(rule));
					}
				});
				
			}
		}

		self.addReferrerCondition = function () {
			self.children.push(new ReferrerCondition());
		};
		self.addCampaignCondition = function () {
			self.children.push(new CampaignCondition());
		};
		self.addPageViewCondition = function () {
			self.children.push(new PageViewCondition());
		};
		self.addFirstVisitCondition = function () {
			self.children.push(new FirstVisitCondition());
		};
		self.addScoreCondition = function () {
			self.children.push(new ScoreCondition());
		};
		self.addEventCondition = function () {
			self.children.push(new EventCondition());
		};
		self.addBrowserCondition = function () {
			self.children.push(new BrowserCondition());
		};
		self.addOSCondition = function () {
			self.children.push(new OSCondition());
		};
		self.addDeviceCondition = function () {
			self.children.push(new DeviceCondition());
		};
		self.addLocationCondition = function () {
			self.children.push(new LocationCondition());
		};
		self.addKeyValueCondition = function () {
			self.children.push(new KeyValueCondition());
		};

		self.addGroup = function () {
			self.children.push(new Group());
		};

		self.removeChild = function (child) {
			self.children.remove(child);
		};

		// the text() function is just an example to show output
		self.json = ko.computed(function () {
			var result = {};
			result.rules = [];
			result.operator = self.selectedLogicalOperator();
			result.type = "group";
			for (var i = 0; i < self.children().length; i++) {
				var child = self.children()[i];
				result.rules.push(child.json());
			}
			return result;
		});
	}

	exports.Group = Group;
	return exports;

})(window.QueryBuilder || {}, window.ko);

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

	var PageViewCondition = exports.PageViewCondition;
	var ScoreCondition = exports.ScoreCondition;
	var EventCondition = exports.EventCondition;

	function Group(initValue) {
		var self = this;

		self.templateName = 'group-template';
		self.children = ko.observableArray();
		self.logicalOperators = ko.observableArray(['AND', 'OR', 'NOT']);
		self.selectedLogicalOperator = ko.observable('AND');

		if (typeof initValue !== "undefined"){
			self.selectedLogicalOperator(initValue.operator);
			
			if (typeof initValue.rules != "undefinded") {
				initValue.rules.forEach(function (rule) {
					if (rule.type === "group") {
						self.children.push(new Group(rule));
					} else if (rule.type === "pageview") {
						self.children.push(new PageViewCondition(rule));
					} else if (rule.type === "event") {
						self.children.push(new EventCondition(rule));
					} else if (rule.type === "score") {
						self.children.push(new ScoreCondition(rule));
					}
				});
				
			}
		}

		self.addPageViewCondition = function () {
			self.children.push(new PageViewCondition());
		};
		self.addScoreCondition = function () {
			self.children.push(new ScoreCondition());
		};
		self.addEventCondition = function () {
			self.children.push(new EventCondition());
		};

		self.addGroup = function () {
			self.children.push(new Group());
		};

		self.removeChild = function (child) {
			self.children.remove(child);
		};

		// the text() function is just an example to show output
		self.text = ko.computed(function () {
			var result = '(';
			var op = '';
			for (var i = 0; i < self.children().length; i++) {
				var child = self.children()[i];
				result += op + child.text();
				op = ' ' + self.selectedLogicalOperator() + ' ';
			}
			return result += ')';
		});
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

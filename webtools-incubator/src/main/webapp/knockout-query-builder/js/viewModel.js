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

	var Group = exports.Group;

	function ViewModel(initValue) {
		var self = this;
		self.group = ko.observable(new Group(initValue));

		// the text() function is just an example to show output
		self.text = ko.computed(function () {
			return self.group().text();
		});
		self.json = ko.computed(function () {
			return JSON.stringify(self.group().json());
		});
	}

	exports.ViewModel = ViewModel;
	return exports;

})(window.QueryBuilder || {}, window.ko);

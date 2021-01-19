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
/*
 * WebTools-Platform
 * Copyright (C) 2016-2018  ThorstenMarx (kontakt@thorstenmarx.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
$(document).ready(function () {


// Hide submenus
	$('#body-row .collapse').collapse('hide');

// Collapse/Expand icon
	$('#collapse-icon').addClass('fa-angle-double-left');

// Collapse click
	$('[data-toggle=sidebar-collapse]').click(function () {
		SidebarCollapse();
	});

	function SidebarCollapse() {
		$('.menu-collapsed').toggleClass('d-none');
		$('.sidebar-submenu').toggleClass('d-none');
		$('.submenu-icon').toggleClass('d-none');
		$('#sidebar-container').toggleClass('sidebar-expanded sidebar-collapsed');

		// Treating d-flex/d-none on separators with title
		var SeparatorTitle = $('.sidebar-separator-title');
		if (SeparatorTitle.hasClass('d-flex')) {
			SeparatorTitle.removeClass('d-flex');
		} else {
			SeparatorTitle.addClass('d-flex');
		}

		// Collapse/Expand icon
		$('#collapse-icon').toggleClass('fa-angle-double-left fa-angle-double-right');
	}

	$('[data-toggle="tooltip"]').tooltip();
});

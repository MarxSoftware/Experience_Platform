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
jQuery(document).ready(function ($) {

	var windowXArray = [],
			windowYArray = [];

	for (var i = 0; i < $(window).innerWidth(); i++) {
		windowXArray.push(i);
	}

	for (var i = 0; i < $(window).innerHeight(); i++) {
		windowYArray.push(i);
	}

	function randomPlacement(array) {
		var placement = array[Math.floor(Math.random() * array.length)];
		return placement;
	}


	var canvas = oCanvas.create({
		canvas: '#canvas',
		background: '#2c3e50',
		fps: 60
	});

	setInterval(function () {

		var rectangle = canvas.display.ellipse({
			x: randomPlacement(windowXArray),
			y: randomPlacement(windowYArray),
			origin: {x: 'center', y: 'center'},
			radius: 0,
			fill: '#27ae60',
			opacity: 1
		});

		canvas.addChild(rectangle);

		rectangle.animate({
			radius: 10,
			opacity: 0
		}, {
			duration: '1000',
			easing: 'linear',
			callback: function () {
				this.remove();
			}
		});

	}, 100);

	$(window).resize(function () {
		canvas.width = $(window).innerWidth();
		canvas.height = $(window).innerHeight();
	});

	$(window).resize();

});

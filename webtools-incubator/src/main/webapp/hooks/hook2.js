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
var hooks = {}; // store all hooks here

function on(name, fn) {
    name = name.split('.');
    if (!hooks[name[0]]) hooks[name[0]] = {};
    var i = name[1] || Object.keys(hooks[name[0]]).length;
    hooks[name[0]][i] = fn;
}

function off(name) {
    if (!name) {
        return hooks = {};
    }
    name = name.split('.');
    if (name[1]) {
        delete hooks[name[0]][name[1]];
    } else {
        delete hooks[name[0]];
    }
}

function trigger(name, param) {
    name = name.split('.');
    if (!hooks[name[0]]) hooks[name[0]] = {};
    if (name[1]) {
        if (hooks[name[0]][name[1]]) hooks[name[0]][name[1]].apply({}, param);
    } else {
        for (var i in hooks[name[0]]) {
            hooks[name[0]][i].apply({}, param);
        }
    }
}



/*

// add a `click` hook
on("click", function(x) {
    alert('test ' + x + ' ok!');
});

// add a `click` hook with `foo` namespace
on("click.foo", function(x) {
    alert('test ' + x + ' foo ok!');
});

// remove all `click` hooks
off("click");

// remove `click.foo` hook only
off("click.foo");

// trigger all `click` hooks
trigger("click", ['this will be the `x`', 'this will be the second function parameter…']);

// trigger `click.foo` hook only
trigger("click.foo", ['this will be the `x`', 'this will be the second function parameter…']);

*/

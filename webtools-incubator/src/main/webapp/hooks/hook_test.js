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
// source: Jon Hobbs @http://www.velvetcache.org/

function test () {
  el = document.getElementById( 'test-area' );
  // Set up the hooks
  Hook.register( 'one', function ( args ) { el.innerHTML = el.innerHTML + 'One<br/>'; return true; } );
  Hook.register( 'one', function ( args ) { el.innerHTML = el.innerHTML + 'Two<br/>'; return true; } );
  Hook.register( 'one', function ( args ) { el.innerHTML = el.innerHTML + args[0]; args[0] = 'Six<br/>'; return true; } );
  Hook.register( 'one', function ( args ) { el.innerHTML = el.innerHTML + 'Four<br/>'; return false; } );
  Hook.register( 'one', function ( args ) { el.innerHTML = el.innerHTML + 'Five<br/>'; return true; } );

  // Set up the arguments
  arguments = [ 'Three<br/>' ];
  // Call the hooks
  Hook.call( 'one', [ 'Three<br/>' ] );
  // Prove the arguments were changed
  el.innerHTML = el.innerHTML + arguments[0];
}

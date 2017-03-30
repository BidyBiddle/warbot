/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PircBotX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks.types;

import org.pircbotx.PircBotX;

/**
 * Base interface that all Generic event interfaces must extend from.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface GenericEvent<T extends PircBotX> {
	/**
	 * Send a response using the underlying event's respond() method
	 * @param response The response to send
	 */
	public void respond(String response);

	/**
	 * Returns the {@link PircBotX} instance that this event originally came from
	 * @return A {@link PircBotX} instance
	 */
	public T getBot();

	/**
	 * Returns the timestamp of when the event was created
	 * @return A timestamp as a long
	 */
	public long getTimestamp();
}

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

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 * Any event dealing with CTCP.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface GenericCTCPCommand<T extends PircBotX> extends GenericEvent<T> {
	/**
	 * Gets the user that sent the CTCP command
	 * @return The user that sent the CTCP command
	 */
	public User getUser();

	/**
	 * Gets the target channel of the CTCP command. If null, then the target was us.
	 * @return The target channel or null if the target was us
	 */
	public Channel getChannel();
}

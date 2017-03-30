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
package org.pircbotx.hooks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.*;

/**
 * Adapter that provides methods to capture each event separately, removing
 * the need to check, cast, and call your custom method for each event you want
 * to capture.
 * <p>
 * To use, simply override the method that has the event you want to capture.
 * <p>
 * <b>WARNING:</b> If you are going to be implementing {@link Listener}'s
 * {@link Listener#onEvent(org.pircbotx.hooks.Event) } method, you must call
 * <code>super.onEvent(event)</code>, otherwise none of the Adapter hook methods
 * will work!
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public abstract class ListenerAdapter<T extends PircBotX> implements Listener<T> {
	protected static final Map<Class<? extends Event>, Set<Method>> eventToMethod = new HashMap();

	static {
		updateEventMethodMapping(ListenerAdapter.class);
	}

	/**
	 * Adds custom event listeners created by the specified class to the internal
	 * event to method map so onEvent is aware of them. The methods must follow 
	 * the same naming and parameter convention as the {@Link ListenerAdapter ListenerAdapter class} 
	 * in order to guarantee they are added
	 * <p>
	 * This is needed because onEvent is only aware of methods that have been added
	 * to its internal map. It is only needed to be called once on a class that
	 * has the methods
	 * @param clazz A class that has event listener methods that conform to the
	 * ListenerAdapter class convention
	 */
	protected static void updateEventMethodMapping(Class<? extends ListenerAdapter> clazz) {
		//Map events to methods
		for (Method curMethod : clazz.getDeclaredMethods()) {
			//Filter out methods by basic criteria
			if (curMethod.getName().equals("onEvent") || curMethod.getParameterTypes().length != 1 || curMethod.isSynthetic())
				continue;
			Class<?> curClass = curMethod.getParameterTypes()[0];
			//Filter out methods that don't have the right param or are already added
			if (curClass.isAssignableFrom(Event.class) || curClass.isInterface()
					|| (eventToMethod.containsKey(curClass) && eventToMethod.get(curClass).contains(curMethod)))
				continue;
			Set methods = new HashSet();
			methods.add(curMethod);
			eventToMethod.put((Class<? extends Event>) curClass, methods);

		}
		//Now that we have all the events, start mapping interfaces
		for (Method curMethod : clazz.getDeclaredMethods()) {
			//Make sure this is an event method
			if (curMethod.getParameterTypes().length != 1 || curMethod.isSynthetic())
				continue;
			Class<?> curClass = curMethod.getParameterTypes()[0];
			if (!curClass.isInterface() || !GenericEvent.class.isAssignableFrom(curClass))
				continue;
			//Add this interface method to all events that implement it
			for (Class curEvent : eventToMethod.keySet())
				if (curClass.isAssignableFrom(curEvent) && !eventToMethod.get(curEvent).contains(curMethod))
					eventToMethod.get(curEvent).add(curMethod);
		}
	}

	public void onEvent(Event<T> event) throws Exception {
		try {
			if (eventToMethod.containsKey(event.getClass()))
				for (Method curMethod : eventToMethod.get(event.getClass()))
					curMethod.invoke(this, event);
		} catch (InvocationTargetException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof Exception)
				throw (Exception) ex.getCause();
			//Wrap in RuntimeException and throw it instead
			throw new RuntimeException("Error in executing ListenerAdapter", cause);
		}
	}

	public void onAction(ActionEvent<T> event) throws Exception {
	}

	public void onChannelInfo(ChannelInfoEvent<T> event) throws Exception {
	}

	public void onConnect(ConnectEvent<T> event) throws Exception {
	}

	public void onDisconnect(DisconnectEvent<T> event) throws Exception {
	}

	public void onFileTransferFinished(FileTransferFinishedEvent<T> event) throws Exception {
	}

	public void onFinger(FingerEvent<T> event) throws Exception {
	}

	public void onHalfOp(HalfOpEvent<T> event) throws Exception {
	}

	public void onIncomingChatRequest(IncomingChatRequestEvent<T> event) throws Exception {
	}

	public void onIncomingFileTransfer(IncomingFileTransferEvent<T> event) throws Exception {
	}

	public void onInvite(InviteEvent<T> event) throws Exception {
	}

	public void onJoin(JoinEvent<T> event) throws Exception {
	}

	public void onKick(KickEvent<T> event) throws Exception {
	}

	public void onMessage(MessageEvent<T> event) throws Exception {
	}

	public void onMode(ModeEvent<T> event) throws Exception {
	}

	public void onMotd(MotdEvent<T> event) throws Exception {
	}

	public void onNickChange(NickChangeEvent<T> event) throws Exception {
	}

	public void onNotice(NoticeEvent<T> event) throws Exception {
	}

	public void onOp(OpEvent<T> event) throws Exception {
	}

	public void onOwner(OwnerEvent<T> event) throws Exception {
	}

	public void onPart(PartEvent<T> event) throws Exception {
	}

	public void onPing(PingEvent<T> event) throws Exception {
	}

	public void onPrivateMessage(PrivateMessageEvent<T> event) throws Exception {
	}

	public void onQuit(QuitEvent<T> event) throws Exception {
	}

	public void onReconnect(ReconnectEvent<T> event) throws Exception {
	}

	public void onRemoveChannelBan(RemoveChannelBanEvent<T> event) throws Exception {
	}

	public void onRemoveChannelKey(RemoveChannelKeyEvent<T> event) throws Exception {
	}

	public void onRemoveChannelLimit(RemoveChannelLimitEvent<T> event) throws Exception {
	}

	public void onRemoveInviteOnly(RemoveInviteOnlyEvent<T> event) throws Exception {
	}

	public void onRemoveModerated(RemoveModeratedEvent<T> event) throws Exception {
	}

	public void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent<T> event) throws Exception {
	}

	public void onRemovePrivate(RemovePrivateEvent<T> event) throws Exception {
	}

	public void onRemoveSecret(RemoveSecretEvent<T> event) throws Exception {
	}

	public void onRemoveTopicProtection(RemoveTopicProtectionEvent<T> event) throws Exception {
	}

	public void onServerPing(ServerPingEvent<T> event) throws Exception {
	}

	public void onServerResponse(ServerResponseEvent<T> event) throws Exception {
	}

	public void onSetChannelBan(SetChannelBanEvent<T> event) throws Exception {
	}

	public void onSetChannelKey(SetChannelKeyEvent<T> event) throws Exception {
	}

	public void onSetChannelLimit(SetChannelLimitEvent<T> event) throws Exception {
	}

	public void onSetInviteOnly(SetInviteOnlyEvent<T> event) throws Exception {
	}

	public void onSetModerated(SetModeratedEvent<T> event) throws Exception {
	}

	public void onSetNoExternalMessages(SetNoExternalMessagesEvent<T> event) throws Exception {
	}

	public void onSetPrivate(SetPrivateEvent<T> event) throws Exception {
	}

	public void onSetSecret(SetSecretEvent<T> event) throws Exception {
	}

	public void onSetTopicProtection(SetTopicProtectionEvent<T> event) throws Exception {
	}

	public void onSocketConnect(SocketConnectEvent<T> event) throws Exception {
	}

	public void onSuperOp(SuperOpEvent<T> event) throws Exception {
	}

	public void onTime(TimeEvent<T> event) throws Exception {
	}

	public void onTopic(TopicEvent<T> event) throws Exception {
	}

	public void onUnknown(UnknownEvent<T> event) throws Exception {
	}

	public void onUserList(UserListEvent<T> event) throws Exception {
	}

	public void onUserMode(UserModeEvent<T> event) throws Exception {
	}

	public void onVersion(VersionEvent<T> event) throws Exception {
	}

	public void onVoice(VoiceEvent<T> event) throws Exception {
	}

	public void onWhois(WhoisEvent<T> event) throws Exception {
	}

	public void onGenericCTCPCommand(GenericCTCPCommand<T> event) throws Exception {
	}

	public void onGenericUserMode(GenericUserModeEvent<T> event) throws Exception {
	}

	public void onGenericChannelMode(GenericChannelModeEvent<T> event) throws Exception {
	}

	public void onGenericDCC(GenericDCCEvent<T> event) throws Exception {
	}

	public void onGenericMessage(GenericMessageEvent<T> event) throws Exception {
	}
}

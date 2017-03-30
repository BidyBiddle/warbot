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
package org.pircbotx;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import lombok.Synchronized;
import org.pircbotx.exception.DccException;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.IncomingFileTransferEvent;

/**
 * This class is used to process DCC events from the server.
 *
 * @since PircBot 1.2.0
 * @author Origionally by:
 * <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 * <p>Forked and Maintained by Leon Blakey <lord.quackstar at gmail.com> in <a href="http://pircbotx.googlecode.com">PircBotX</a>
 */
public class DccManager implements Closeable {
	protected PircBotX bot;
	protected List<DccFileTransfer> awaitingResume = Collections.synchronizedList(new ArrayList<DccFileTransfer>());
	protected List<DccFileTransfer> allFileTransfers = Collections.synchronizedList(new ArrayList<DccFileTransfer>());
	protected List<DccChat> allChats = Collections.synchronizedList(new ArrayList<DccChat>());

	/**
	 * Constructs a DccManager to look after all DCC SEND and CHAT events.
	 *
	 * @param bot The PircBotX whose DCC events this class will handle.
	 */
	protected DccManager(PircBotX bot) {
		this.bot = bot;
	}

	/**
	 * Processes a DCC request.
	 *
	 * @return True if the type of request was handled successfully.
	 */
	protected boolean processRequest(User source, String request) throws DccException, IOException {
		StringTokenizer tokenizer = new StringTokenizer(request);
		//Skip the DCC part of the line
		tokenizer.nextToken();
		String type = tokenizer.nextToken();

		//Parse filename, removing quotes
		String filename = tokenizer.nextToken();
		if (filename.startsWith("\""))
			filename = filename.substring(1);
		if (filename.endsWith("\""))
			filename = filename.substring(0, filename.length() - 1);

		if (type.equals("SEND")) {
			//Someone is trying to send a file to us
			//Example: DCC SEND <filename> <ip> <port> <file size> <passive(random,optional)> (note File size is optional)
			InetAddress address = integerToAddress(tokenizer.nextToken());
			int port = Integer.parseInt(tokenizer.nextToken());
			long size = -1;
			try {
				size = Long.parseLong(tokenizer.nextToken());
			} catch (Exception e) {
				// Stick with the old value.
			}

			bot.getListenerManager().dispatchEvent(new IncomingFileTransferEvent(bot, new DccFileTransfer(bot, source, type, filename, address, port, size)));
		} else if (type.equals("RESUME")) {
			//Someone is trying to resume sending a file to us
			//Example: DCC RESUME <filename> <port> <position>
			//Reply with: DCC ACCEPT <filename> <port> <position>
			int port = Integer.parseInt(tokenizer.nextToken());
			long progress = Long.parseLong(tokenizer.nextToken());

			DccFileTransfer transfer = removeAwaitingResume(source, port);
			if (transfer == null)
				throw new DccException("No Dcc File Transfer to resume recieving (filename: " + filename + ", source: " + source + ", port: " + port + ")");
			transfer.setProgress(progress);
			bot.sendCTCPCommand(source, "DCC ACCEPT file.ext " + port + " " + progress);
		} else if (type.equals("ACCEPT")) {
			//We are resuming sending a file to someone, this is them acknowledging
			//Example: DCC ACCEPT <filename> <port> <position>
			int port = Integer.parseInt(tokenizer.nextToken());
			long progress = Long.parseLong(tokenizer.nextToken());

			DccFileTransfer transfer = removeAwaitingResume(source, port);
			if (transfer == null)
				throw new DccException("No Dcc File Transfer to resume sending (filename: " + filename + ", source: " + source + ", port: " + port + ")");
			transfer.doReceive(true);
		} else if (type.equals("CHAT")) {
			//Someone is trying to chat with us
			//Example: DCC CHAT <protocol> <ip> <port> (protocol should be chat)
			InetAddress address = integerToAddress(tokenizer.nextToken());
			int port = Integer.parseInt(tokenizer.nextToken());

			final DccChat chat = new DccChat(bot, source, address, port);
			bot.getListenerManager().dispatchEvent(new IncomingChatRequestEvent(bot, chat));
		} else
			return false;

		return true;
	}

	@Synchronized("awaitingResume")
	protected DccFileTransfer removeAwaitingResume(User user, int port) {
		for (Iterator<DccFileTransfer> it = awaitingResume.iterator(); it.hasNext();) {
			DccFileTransfer transfer = it.next();
			if (transfer.getUser().equals(user) && transfer.getPort() == port) {
				it.remove();
				return transfer;
			}
		}
		return null;
	}

	protected DccFileTransfer addDccFileTransfer(DccFileTransfer transfer) {
		allFileTransfers.add(transfer);
		return transfer;
	}

	protected DccFileTransfer removeDccFileTransfer(DccFileTransfer transfer) {
		if (!allFileTransfers.remove(transfer))
			throw new RuntimeException("Attempted to forget about DccFileTransfer that doesn't exist");
		return transfer;
	}

	/**
	 * Get all DccFileTransfer's, regardless if they are awaiting resume or not.
	 * @return An unmodifiable list view of DccChats
	 */
	public List<DccFileTransfer> getAllFileTransfers() {
		return Collections.unmodifiableList(allFileTransfers);
	}

	protected DccChat addDccChat(DccChat chat) {
		allChats.add(chat);
		return chat;
	}

	protected DccChat removeDccChat(DccChat chat) {
		if (!allChats.remove(chat))
			throw new RuntimeException("Attempted to forget about DccChat that doesn't exist");
		return chat;
	}

	/**
	 * Get all open DccChats
	 * @return An unmodifiable list view of DccChats
	 */
	public List<DccChat> getAllChats() {
		return Collections.unmodifiableList(allChats);
	}

	protected boolean addAwaitingResume(DccFileTransfer transfer) {
		return awaitingResume.add(transfer);
	}

	public boolean removeAwaitingResume(DccFileTransfer transfer) {
		return awaitingResume.remove(transfer);
	}

	protected ServerSocket createServerSocket() throws IOException, DccException {
		ServerSocket ss = null;
		List<Integer> ports = bot.getDccPorts();
		if (ports.isEmpty())
			// Use any free port.
			ss = new ServerSocket(0);
		else {
			for (int currentPort : ports)
				try {
					ss = new ServerSocket(currentPort);
					// Found a port number we could use.
					break;
				} catch (Exception e) {
					// Do nothing; go round and try another port.
				}
			if (ss == null)
				// No ports could be used.
				throw new DccException("All ports returned by getDccPorts() " + ports.toString() + "are in use.");
		}
		return ss;
	}

	/**
	 * Closes all open DccFileTransfers and DccChats.
	 * @throws IOException
	 */
	public void close() throws IOException {
		List<Closeable> allDcc = new ArrayList(allChats);
		allDcc.addAll(allFileTransfers);
		for (Closeable curDcc : allDcc)
			curDcc.close();
	}

	public static String addressToInteger(InetAddress address) {
		return new BigInteger(1, address.getAddress()).toString();
	}

	public static InetAddress integerToAddress(String rawInteger) {
		//Convert the rawInteger into something usable
		BigInteger bigIp = new BigInteger(rawInteger);
		byte[] addressBytes = bigIp.toByteArray();

		//If there aren't enough bytes, pad with 0 byte
		if (addressBytes.length == 5)
			//Has signum, strip it
			addressBytes = Arrays.copyOfRange(addressBytes, 1, 5);
		else if (addressBytes.length < 4) {
			byte[] newAddressBytes = new byte[4];
			newAddressBytes[3] = addressBytes[0];
			newAddressBytes[2] = (addressBytes.length > 1) ? addressBytes[1] : (byte) 0;
			newAddressBytes[1] = (addressBytes.length > 2) ? addressBytes[2] : (byte) 0;
			newAddressBytes[0] = (addressBytes.length > 3) ? addressBytes[3] : (byte) 0;
			addressBytes = newAddressBytes;
		} else if (addressBytes.length == 17)
			//Has signum, strip it
			addressBytes = Arrays.copyOfRange(addressBytes, 1, 17);
		try {
			return InetAddress.getByAddress(addressBytes);
		} catch (UnknownHostException ex) {
			throw new RuntimeException("Can't get InetAdrress version of int IP address " + rawInteger + " (bytes: " + Arrays.toString(addressBytes) + ")", ex);
		}
	}

	/**
	 * A convenient method that accepts an IP address represented as a
	 * long and returns an integer array of size 4 representing the same
	 * IP address.
	 *
	 * @since PircBot 0.9.4
	 *
	 * @param address the long value representing the IP address.
	 *
	 * @return An int[] of size 4.
	 */
	public static int[] longToIp(long address) {
		int[] ip = new int[4];
		for (int i = 3; i >= 0; i--) {
			ip[i] = (int) (address % 256);
			address = address / 256;
		}
		return ip;
	}
}

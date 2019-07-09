/*
 * Copyright (C) 2003-2017, C. Ramakrishnan / Illposed Software.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.communication.osc;

/**
 * Allows to listen to incoming messages.
 * In OSC speak, this is a <i>Method</i>, and it listens to <i>Messages</i>.
 */
public interface OSCMessageListener {

	/**
	 * Process a matching, incoming OSC Message.
	 * @param event the message received event to be processed
	 */
	void acceptMessage(OSCMessageEvent event);
}

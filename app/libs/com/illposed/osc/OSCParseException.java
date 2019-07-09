/*
 * Copyright (C) 2015-2017, C. Ramakrishnan / Illposed Software.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.communication.osc;

public class OSCParseException extends Exception {

	public OSCParseException() {
		super();
	}

	public OSCParseException(final String message) {
		super(message);
	}

	public OSCParseException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public OSCParseException(final Throwable cause) {
		super(cause);
	}
}

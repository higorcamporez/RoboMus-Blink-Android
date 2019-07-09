/*
 * Copyright (C) 2014-2017, C. Ramakrishnan / Auracle.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.communication.osc.messageselector;

import com.communication.osc.MessageSelector;
import com.communication.osc.OSCMessageEvent;

import java.util.regex.Pattern;

/**
 * Checks whether an OSC <i>Address Pattern</i> matches a given Java regular expression.
 */
public class JavaRegexAddressMessageSelector implements MessageSelector {

	private final Pattern selector;

	// Public API
	@SuppressWarnings("WeakerAccess")
	public JavaRegexAddressMessageSelector(final Pattern selector) {
		this.selector = selector;
	}

	public JavaRegexAddressMessageSelector(final String selectorRegex) {
		this(Pattern.compile(selectorRegex));
	}

	@Override
	public boolean equals(final Object other) {

		boolean equal = false;
		if (other instanceof JavaRegexAddressMessageSelector) {
			final JavaRegexAddressMessageSelector otherSelector
					= (JavaRegexAddressMessageSelector) other;
			equal = this.selector.equals(otherSelector.selector);
		}

		return equal;
	}

	@Override
	public int hashCode() {
		return this.selector.hashCode();
	}

	@Override
	public boolean isInfoRequired() {
		return false;
	}

	@Override
	public boolean matches(final OSCMessageEvent messageEvent) {
		return selector.matcher(messageEvent.getMessage().getAddress()).matches();
	}
}

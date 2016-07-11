package io.github.lumue.getdown.core.common.util;

import reactor.bus.selector.PredicateSelector;

/**
 * Created by lm on 11.07.16.
 */
public class StartsWithSelector extends PredicateSelector<String> {

	public StartsWithSelector(String pattern) {
		super(s -> s.startsWith(pattern));
	}
}

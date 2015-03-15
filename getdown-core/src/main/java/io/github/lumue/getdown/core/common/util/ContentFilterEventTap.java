package io.github.lumue.getdown.core.common.util;

import java.util.function.Predicate;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.event.selector.Selector;
import reactor.function.Consumer;

/**
 * 
 * Listens to Events matching a given selector and matches them to a given
 * predicate
 * 
 * events are either ignored or repackaged and forwarded with a new selector key
 * 
 * @author lm
 *
 * @param <T>
 */
public class ContentFilterEventTap<T> implements Consumer<Event<T>> {

	private final Reactor reactor;

	private final String forwardSelectorKey;

	private final Predicate<T> predicate;

	public ContentFilterEventTap(Reactor reactor, String forwardSelectorKey, Selector selector, Predicate<T> predicate) {
		super();
		this.reactor = reactor;
		this.forwardSelectorKey = forwardSelectorKey;
		this.predicate = predicate;
		reactor.on(selector, this);
	}

	@Override
	public void accept(Event<T> t) {

		if (!predicate.test(t.getData()))
			return;

		reactor.notify(forwardSelectorKey, Event.wrap(t.getData()));
	}


}

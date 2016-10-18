package io.github.lumue.getdown.core.common.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.omg.CORBA.Object;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.bus.selector.Selector;

import javax.annotation.PostConstruct;

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

	private final EventBus eventbus;

	private final String forwardSelectorKey;

	private final Predicate<T> predicate;
	private final Selector<?> selector;

	public ContentFilterEventTap(EventBus eventbus, String forwardSelectorKey, Selector<?> selector, Predicate<T> predicate) {
		super();
		this.eventbus = eventbus;
		this.forwardSelectorKey = forwardSelectorKey;
		this.predicate = predicate;
		this.selector=selector;
	}

	/**
	 * start listening
	 */
	@PostConstruct
	public void start(){
		eventbus.on(selector, this);
	}

	@Override
	public void accept(Event<T> t) {

		if (!predicate.test(t.getData()))
			return;

		eventbus.notify(forwardSelectorKey, Event.wrap(Objects.requireNonNull(t.getData())));
	}


}

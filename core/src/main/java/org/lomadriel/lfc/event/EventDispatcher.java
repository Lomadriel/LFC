/*
 * LFC
 * Copyright (c) 2016 Jérôme BOULMIER
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * This program is free software; you can redistribute it and/or modify
 */

package org.lomadriel.lfc.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is used to link event senders and event listeners.
 *
 * @author Jérôme BOULMIER
 * @since 0.1
 */
public class EventDispatcher {
	private final Map<Class<? extends Event<?>>, List<? extends EventListener>> eventMap = new HashMap<>(10);
	private final Map<Class<? extends Event<?>>, List<? extends EventListener>> flushMap = new HashMap<>(10);

	private Boolean hasToFlushed = Boolean.FALSE;
	private final ReentrantLock lock = new ReentrantLock();

	EventDispatcher() {
	}

	private static class EventDispatcherHolder {
		static final EventDispatcher instance = new EventDispatcher();
	}

	public static EventDispatcher getInstance() {
		return EventDispatcherHolder.instance;
	}

	/**
	 * Adds a listener for the given event.
	 *
	 * @param eventClass event's class
	 * @param listener   listener to notify when the event is fired.
	 * @param <T>        listener's class, a listener must implement EventListener.
	 * @return true if the listener have been added, false otherwise.
	 */
	public <T extends EventListener> boolean addListener(Class<? extends Event<T>> eventClass, T listener) {
		List<T> listeners = getListeners(eventClass);

		boolean added = false;
		synchronized (this.eventMap) {
			if (!listeners.contains(listener)) {
				added = listeners.add(listener);
			}
		}

		return added;
	}

	/**
	 * Removes a listener for the given event.
	 *
	 * @param eventClass event's class
	 * @param listener   listener to remove from the notification cycle.
	 * @param <T>        listener's class, a listener must implement EventListener.
	 */
	public <T extends EventListener> void removeListener(Class<? extends Event<T>> eventClass, T listener) {
		assert (this.eventMap.containsKey(eventClass));

		if (this.lock.tryLock()) {
			synchronized (this.flushMap) {
				List<T> listeners = getFlushListeners(eventClass);
				listeners.add(listener);
				this.hasToFlushed = Boolean.TRUE;
			}
		} else {
			try {
				List<T> listeners = getListeners(eventClass);
				listeners.remove(listener);
			} finally {
				this.lock.unlock();
			}
		}
	}

	/**
	 * Notifies each listener which listen this event.
	 *
	 * @param event event to fire
	 * @param <T>   listener's class
	 */
	public <T extends EventListener> void fire(Event<T> event) {
		@SuppressWarnings("unchecked")
		Class<Event<T>> eventClass = (Class<Event<T>>) event.getClass();

		this.lock.lock();
		try {
			getListeners(eventClass).forEach(event::notify);

			if (this.hasToFlushed.booleanValue()) {
				flush();
			}
		} finally {
			this.lock.unlock();
		}
	}

	private <T extends EventListener> List<T> getListeners(Class<? extends Event<T>> eventClass) {
		@SuppressWarnings("unchecked")
		List<T> listeners = (List<T>) this.eventMap.get(eventClass);
		if (listeners == null) {
			listeners = new ArrayList<>();
			this.eventMap.put(eventClass, listeners);
		}

		return listeners;
	}

	private <T extends EventListener> List<T> getFlushListeners(Class<? extends Event<T>> eventClass) {
		@SuppressWarnings("unchecked")
		List<T> listeners = (List<T>) this.flushMap.get(eventClass);
		if (listeners == null) {
			listeners = new ArrayList<>();
			this.flushMap.put(eventClass, listeners);
		}

		return listeners;
	}

	private void flush() {
		for (Class<? extends Event<?>> aClass : this.flushMap.keySet()) {
			List<? extends EventListener> listeners = this.eventMap.get(aClass);
			if (listeners != null) {
				listeners.removeAll(this.flushMap.get(aClass));
			}
		}

		this.flushMap.clear();
		this.hasToFlushed = Boolean.FALSE;
	}

}

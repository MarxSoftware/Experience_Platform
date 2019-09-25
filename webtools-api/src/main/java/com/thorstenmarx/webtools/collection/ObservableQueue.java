package com.thorstenmarx.webtools.collection;

/*-
 * #%L
 * webtools-api
 * %%
 * Copyright (C) 2016 - 2018 Thorsten Marx
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author marx
 */
public class ObservableQueue<T> implements BlockingQueue<T>, AutoCloseable {

	private final Subject<T> subject = PublishSubject.<T>create().toSerialized();

	public Observable<T> observe() {
		return subject;
	}

	@Override
	public boolean add(T t) {
		return offer(t);
	}

	@Override
	public boolean offer(T t) {
		subject.onNext(t);
		return true;
	}

	@Override
	public void close() throws IOException {
		subject.onComplete();
	}

	@Override
	public T remove() {
		return noSuchElement();
	}

	@Override
	public T poll() {
		return null;
	}

	@Override
	public T element() {

		return noSuchElement();
	}

	private T noSuchElement() {
		throw new NoSuchElementException();
	}

	@Override
	public T peek() {
		return null;
	}

	@Override
	public void put(T t) throws InterruptedException {
		offer(t);
	}

	@Override
	public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
		return offer(t);
	}

	@Override
	public T take() throws InterruptedException {
		throw new UnsupportedOperationException("Use observe() instead");
	}

	@Override
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
		return null;
	}

	@Override
	public int remainingCapacity() {
		return 0;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		c.forEach(this::offer);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return a;
	}

	@Override
	public int drainTo(Collection<? super T> c) {
		return 0;
	}

	@Override
	public int drainTo(Collection<? super T> c, int maxElements) {
		return 0;
	}
}

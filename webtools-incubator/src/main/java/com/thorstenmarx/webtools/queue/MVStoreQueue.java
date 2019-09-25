/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.queue;

/*-
 * #%L
 * webtools-incubator
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

import com.google.common.base.Preconditions;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fast queue implementation on top of MVStore. This class is thread-safe.
 *
 * @author Valentin Popov
 * @author Jamie Band
 * Thanks for Martin Grotze for his original work on Persistent Queue
 */

class MVStoreQueue implements Comparable<MVStoreQueue> {

    private final String queueName;
    private MVMap<Integer, byte[]> mvMap;
    private MVStore store;
    private String queueDir;
    private final AtomicInteger tailKey = new AtomicInteger(0);

    /**
     * Creates instance of persistent filequeue.
     *
     * @param queueDir  filequeue database environment directory path
     * @param queueName descriptive filequeue name
     * @throws IOException thrown when the given queueEnvPath does not exist and cannot be created.
     */
    MVStoreQueue(final Path queueDir,
                        final String queueName) throws IOException {
        Files.createDirectories(queueDir);
        this.queueDir = queueDir.toAbsolutePath().toString();
        this.queueName = queueName;
        reopen();
    }

    /**
     * Creates instance of persistent filequeue in memory
     *
     * @param queueName descriptive filequeue name
     * @throws IOException thrown when the given queueEnvPath does not exist and cannot be created.
     */
    MVStoreQueue(final String queueName) throws IOException {
        this.queueDir = "nioMemFS:";
        this.queueName = queueName;
        reopen();
    }

    private String getDBName() {
        return Paths.get(queueDir, queueName).toString();
    }

    public synchronized void reopen() throws IllegalStateException {
        try {
            if (store != null && !store.isClosed()) store.close();
        } catch (Exception ignored) {
        }
        store = getOpenStore();
        mvMap = store.openMap(queueName);
        if (!mvMap.isEmpty())
            tailKey.set(mvMap.lastKey());
    }

    private MVStore getOpenStore() {
        return new MVStore.Builder().fileName(getDBName()).cacheSize(1).open();
    }

    public Path getQueueDir() {
        return Paths.get(queueDir);
    }

    /**
     * Retrieves and and removes element from the head of this filequeue.
     *
     * @return element from the tail of the filequeue or null if filequeue is empty
     * @throws IOException in case of disk IO failure
     */
    public synchronized byte[] poll() {
        if (mvMap.isEmpty()) {
            tailKey.set(0);
            return null;
        }
        return mvMap.remove(mvMap.firstKey());
    }

    /**
     * Pushes element to the tail of this filequeue.
     *
     * @param {@link Nonnull} element
     * @throws IOException in case of disk IO failure
     */
    public synchronized void push(final byte[] element) {
        Preconditions.checkNotNull(element, "cant insert null");
        mvMap.put(tailKey.incrementAndGet(), element);
    }

    public void clear() {
        mvMap.clear();
    }

    /**
     * Returns the size of this filequeue.
     *
     * @return the size of the filequeue
     */
    public long size() {
        return mvMap.size();
    }

    /**
     * Determines if this filequeue is empty (equivalent to <code>{@link #size()} == 0</code>).
     *
     * @return <code>true</code> if this filequeue is empty, otherwise <code>false</code>.
     */
    public boolean isEmpty() {
        return mvMap.isEmpty();
    }

    /**
     * Closes this filequeue and frees up all resources associated to it.
     */
    public synchronized void close() {
        store.sync();
        store.close();
    }

    @Override
    public int compareTo(@Nonnull MVStoreQueue o) {
        int result = (int) (this.size() - o.size());
        return Integer.compare(result, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MVStoreQueue)) return false;

        MVStoreQueue that = (MVStoreQueue) o;

        if (!queueName.equals(that.queueName)) return false;
        if (!store.equals(that.store)) return false;
        return getQueueDir().equals(that.getQueueDir());
    }

    @Override
    public int hashCode() {
        int result = queueName.hashCode();
        result = 31 * result + store.hashCode();
        result = 31 * result + getQueueDir().hashCode();
        return result;
    }
}

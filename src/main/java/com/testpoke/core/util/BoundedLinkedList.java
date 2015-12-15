package com.testpoke.core.util;

import java.util.Collection;
import java.util.LinkedList;


public class BoundedLinkedList<E> extends LinkedList<E> {

    private final int maxSize;

    public BoundedLinkedList(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(E object) {
        if (size() == maxSize) {
            removeFirst();
        }
        return super.add(object);
    }


    @Override
    public void add(int location, E object) {
        if (size() == maxSize) {
            removeFirst();
        }
        super.add(location, object);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        final int totalNeededSize = size() + collection.size();
        final int overflow = totalNeededSize - maxSize;
        if (overflow > 0) {
            removeRange(0, overflow);
        }
        return super.addAll(collection);
    }

    @Override
    public boolean addAll(int location, Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void addFirst(E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addLast(E object) {
        add(object);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (E object : this) {
            result.append(object.toString());
        }
        return result.toString();
    }
}

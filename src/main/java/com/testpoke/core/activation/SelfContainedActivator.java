package com.testpoke.core.activation;

import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * Created by Jansel Valentin on 5/6/14.
 */

final class SelfContainedActivator implements Iterable<Activator> {
    private Class<?>[] activators;

    private SelfContainedActivator() {
        activators = getKnownActivators();
    }

    private Class<?>[] getKnownActivators() {
        return new Class<?>[]{
                com.testpoke.core.crashes.Activator.class,
                com.testpoke.core.content.Activator.class
        };
    }

    public static SelfContainedActivator prepare() {
        return new SelfContainedActivator();
    }

    public void clear(){
        activators = null;
    }

    public Iterator<Activator> iterator() {
        return new LazyIterator(this);
    }

    private class LazyIterator implements Iterator<Activator> {
        SelfContainedActivator container;
        int next;

        private LazyIterator(SelfContainedActivator container) {
            this.container = container;
        }

        public boolean hasNext() {
            return null != container.activators && container.activators.length > next;
        }


        public Activator next() {
            if (!hasNext())
                throw new NoSuchElementException();

            Class<?> toLoad = container.activators[next++];
            Activator loaded;
            try {
                loaded = Activator.class.cast(toLoad.newInstance());
            } catch (IllegalAccessException ie) {
                throw new IllegalActivatorException(ie);
            } catch (InstantiationException ie) {
                throw new IllegalActivatorException(ie);
            }
            return loaded;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class IllegalActivatorException extends RuntimeException {
        private IllegalActivatorException(Throwable throwable) {
            super(throwable);
        }
    }
}

package org.soak.mocha.utils;

import java.util.function.Supplier;

public class Singleton<T> implements Supplier<T> {

    private final Supplier<T> getter;
    private T cache;

    public Singleton(Supplier<T> getter) {
        this.getter = getter;
    }

    @Override
    public T get() {
        if (this.cache == null) {
            this.cache = this.getter.get();
        }
        return this.cache;
    }
}

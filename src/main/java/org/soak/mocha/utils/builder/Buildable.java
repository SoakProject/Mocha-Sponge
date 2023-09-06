package org.soak.mocha.utils.builder;

public interface Buildable<B extends Builder<?, T>, T extends Buildable<B, ?>> {

    B toBuilder();
}

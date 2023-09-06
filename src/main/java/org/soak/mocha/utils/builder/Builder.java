package org.soak.mocha.utils.builder;

public interface Builder<B extends Builder<?, T>, T extends Buildable<B, ?>> {

    T build();

    B from(B builder);

    default B from(T buildable) {
        return from(buildable.toBuilder());
    }
}

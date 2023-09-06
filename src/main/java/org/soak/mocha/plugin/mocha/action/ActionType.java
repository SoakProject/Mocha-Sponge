package org.soak.mocha.plugin.mocha.action;

public interface ActionType {

    Class<?> spongeActionClass();

    ActionBuilder<?, ?> createActionBuilder();

    String name();
}

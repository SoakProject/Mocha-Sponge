package org.soak.mocha.plugin.mocha.action;

import org.soak.mocha.plugin.mocha.action.actions.lifecycle.construct.ConstructPluginActionBuilder;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;

import java.util.function.Supplier;

public enum ActionTypes implements ActionType {
    CONSTRUCT_PLUGIN(ConstructPluginEvent.class, ConstructPluginActionBuilder::new);

    private final Supplier<ActionBuilder<?, ?>> builderConstructor;
    private final Class<?> spongeActionClass;

    ActionTypes(Class<?> spongeActionClass, Supplier<ActionBuilder<?, ?>> supplier) {
        this.spongeActionClass = spongeActionClass;
        this.builderConstructor = supplier;
    }

    @Override
    public Class<?> spongeActionClass() {
        return this.spongeActionClass;
    }

    @Override
    public ActionBuilder<?, ?> createActionBuilder() {
        return this.builderConstructor.get();
    }

    public <AB extends ActionBuilder<?, ?>> AB actionBuilder() {
        return (AB) createActionBuilder();
    }
}

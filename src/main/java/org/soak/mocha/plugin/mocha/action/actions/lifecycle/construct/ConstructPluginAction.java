package org.soak.mocha.plugin.mocha.action.actions.lifecycle.construct;

import org.soak.mocha.plugin.mocha.action.ActionTypes;
import org.soak.mocha.plugin.mocha.action.actions.lifecycle.LifecycleAction;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.plugin.PluginContainer;

import java.util.Objects;
import java.util.function.Supplier;

public class ConstructPluginAction implements LifecycleAction<ConstructPluginActionBuilder, ConstructPluginAction> {

    private final Supplier<PluginContainer> plugin;
    private final Supplier<Cause> cause;

    public ConstructPluginAction(ConstructPluginActionBuilder builder) {
        this.cause = builder.causeSupplier();
        this.plugin = Objects.requireNonNull(builder.pluginSupplier());
    }

    public PluginContainer plugin() {
        return this.plugin.get();
    }

    @Override
    public ActionTypes type() {
        return ActionTypes.CONSTRUCT_PLUGIN;
    }

    @Override
    public Cause cause() {
        return this.cause.get();
    }

    @Override
    public ConstructPluginActionBuilder toBuilder() {
        return type().<ConstructPluginActionBuilder>actionBuilder().setPlugin(this.plugin).setCause(this.cause);
    }

    @Override
    public Game game() {
        return Sponge.game();
    }

    @Override
    public MochaConstructPluginEvent createSpongeEvent() {
        return new MochaConstructPluginEvent(this);
    }
}

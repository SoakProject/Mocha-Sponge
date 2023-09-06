package org.soak.mocha.plugin.mocha.action.actions.lifecycle.construct;

import org.jetbrains.annotations.NotNull;
import org.soak.mocha.plugin.mocha.action.ActionBuilder;
import org.spongepowered.api.event.Cause;
import org.spongepowered.plugin.PluginContainer;

import java.util.function.Supplier;

public class ConstructPluginActionBuilder implements ActionBuilder<ConstructPluginActionBuilder, ConstructPluginAction> {

    private Supplier<Cause> cause;
    private Supplier<PluginContainer> plugin;

    public Supplier<PluginContainer> pluginSupplier() {
        return plugin;
    }

    public ConstructPluginActionBuilder setPlugin(Supplier<PluginContainer> plugin) {
        this.plugin = plugin;
        return this;
    }

    @Override
    public ConstructPluginAction build() {
        return new ConstructPluginAction(this);
    }

    @Override
    public ConstructPluginActionBuilder from(ConstructPluginActionBuilder builder) {
        this.cause = builder.cause;
        this.plugin = builder.plugin;
        return this;
    }

    @Override
    public @NotNull Supplier<Cause> causeSupplier() {
        if (this.cause == null) {
            this.setExampleCause();
        }
        return this.cause;
    }

    @Override
    public ConstructPluginActionBuilder setExampleCause() {
        setCause(() -> Cause.builder().build()); //TODO
        return this;
    }

    @Override
    public ConstructPluginActionBuilder setCause(Supplier<Cause> cause) {
        this.cause = cause;
        return this;
    }
}

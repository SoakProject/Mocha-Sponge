package org.soak.mocha.plugin.mocha.action.actions.lifecycle.construct;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.plugin.PluginContainer;

public class MochaConstructPluginEvent implements ConstructPluginEvent {

    private final @NotNull ConstructPluginAction action;

    MochaConstructPluginEvent(@NotNull ConstructPluginAction action) {
        this.action = action;
    }

    @Override
    public PluginContainer plugin() {
        return this.action.plugin();
    }

    @Override
    public Game game() {
        return this.action.game();
    }

    @Override
    public Cause cause() {
        return this.action.cause();
    }
}

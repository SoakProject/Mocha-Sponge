package org.soak.mocha.plugin.mocha.action.actions.lifecycle;

import org.soak.mocha.plugin.mocha.action.Action;
import org.soak.mocha.plugin.mocha.action.ActionBuilder;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.lifecycle.LifecycleEvent;

public interface LifecycleAction<B extends ActionBuilder<?, Self>, Self extends Action.EventAction<B, ?>> extends Action.EventAction<B, Self> {

    Game game();

    @Override
    LifecycleEvent createSpongeEvent();
}

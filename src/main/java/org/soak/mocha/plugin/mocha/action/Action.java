package org.soak.mocha.plugin.mocha.action;

import org.soak.mocha.utils.builder.Buildable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventContext;

public interface Action<B extends ActionBuilder<?, Self>, Self extends Action<B, ?>> extends Buildable<B, Self> {

    ActionType type();

    Cause cause();

    default EventContext context() {
        return cause().context();
    }

    default Object source() {
        return cause().root();
    }

    interface CommandAction<B extends ActionBuilder<?, Self>, Self extends CommandAction<B, ?>> extends Action<B, Self> {

    }

    interface EventAction<B extends ActionBuilder<?, Self>, Self extends EventAction<B, ?>> extends Action<B, Self> {
        Event createSpongeEvent();
    }
}

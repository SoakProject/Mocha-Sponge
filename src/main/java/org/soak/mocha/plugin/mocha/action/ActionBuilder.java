package org.soak.mocha.plugin.mocha.action;

import org.jetbrains.annotations.NotNull;
import org.soak.mocha.utils.builder.Builder;
import org.spongepowered.api.event.Cause;

import java.util.function.Supplier;

public interface ActionBuilder<Self extends ActionBuilder<?, T>, T extends Action<Self, ?>> extends Builder<Self, T> {

    @NotNull Supplier<Cause> causeSupplier();
    default @NotNull Cause cause(){
        return causeSupplier().get();
    }

    Self setExampleCause();

    Self setCause(Supplier<Cause> cause);

    default Self setCause(@NotNull Cause cause) {
        return this.setCause(() -> cause);
    }

}

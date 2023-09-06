package org.soak.mocha.main;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.spongepowered.api.Sponge;

public class SpongeGlobalGameModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.requestStaticInjection(Sponge.class);
    }
}

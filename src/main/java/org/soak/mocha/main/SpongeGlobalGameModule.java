package org.soak.mocha.main;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.soak.mocha.plugin.mocha.environment.MochaEnvironment;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;

public class SpongeGlobalGameModule implements Module {

    private MochaEnvironment mochaEnvironment;

    public SpongeGlobalGameModule(MochaEnvironment mochaEnvironment){
        this.mochaEnvironment = mochaEnvironment;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(Game.class).toInstance(this.mochaEnvironment);
        binder.requestStaticInjection(Sponge.class);
    }
}

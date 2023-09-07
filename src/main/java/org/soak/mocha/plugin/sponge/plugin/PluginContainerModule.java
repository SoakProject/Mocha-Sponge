package org.soak.mocha.plugin.sponge.plugin;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.apache.logging.log4j.Logger;
import org.spongepowered.plugin.PluginContainer;

public class PluginContainerModule implements Module {

    private final MochaPluginContainer container;

    public PluginContainerModule(MochaPluginContainer container) {
        this.container = container;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(Logger.class).toInstance(this.container.logger());
        binder.bind(PluginContainer.class).toInstance(this.container);
    }
}

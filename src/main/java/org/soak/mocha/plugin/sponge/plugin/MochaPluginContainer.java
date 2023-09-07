package org.soak.mocha.plugin.sponge.plugin;

import com.google.inject.Guice;
import org.spongepowered.plugin.PluginCandidate;
import org.spongepowered.plugin.builtin.jvm.JVMPluginContainer;
import org.spongepowered.plugin.builtin.jvm.locator.JVMPluginResource;

public class MochaPluginContainer extends JVMPluginContainer {
    public MochaPluginContainer(PluginCandidate<JVMPluginResource> candidate) {
        super(candidate);
    }

    public Object initializeInstance(ClassLoader loader) throws ClassNotFoundException {
        var mainClassType = Class.forName(this.metadata().entrypoint(), true, loader);
        var mainClass = Guice.createInjector(new PluginContainerModule(this)).getInstance(mainClassType);
        this.initializeInstance(mainClass);
        return mainClass;
    }
}

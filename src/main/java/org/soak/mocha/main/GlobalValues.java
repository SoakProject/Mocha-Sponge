package org.soak.mocha.main;

import org.soak.mocha.plugin.mocha.environment.MochaEnvironment;
import org.soak.mocha.plugin.sponge.plugin.MochaPluginContainer;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.builtin.StandardPluginMetadata;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GlobalValues {

    private static MochaEnvironment ENVIRONMENT;
    private static Map<StandardPluginMetadata, File> REQUESTED_PLUGINS;
    private static List<MochaPluginContainer> LOADED_PLUGINS;

    GlobalValues() {

    }

    public static List<PluginContainer> loadedPlugins() {
        if (LOADED_PLUGINS == null) {
            System.err.println("Mocha plugin requested Sponge plugins before they have loaded");
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(LOADED_PLUGINS);
    }

    public static Map<StandardPluginMetadata, File> requestedPlugins() {
        if (REQUESTED_PLUGINS == null) {
            System.err.println("Mocha plugin requested Sponge plugins before they have loaded");
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(REQUESTED_PLUGINS);
    }

    public static Optional<MochaEnvironment> environment() {
        return Optional.ofNullable(ENVIRONMENT);
    }

    public void setEnvironment(MochaEnvironment environment) {
        ENVIRONMENT = environment;
    }

    public void setLoadedPlugins(List<MochaPluginContainer> containers) {
        LOADED_PLUGINS = containers;
    }

    public void setRequestedPlugins(Map<StandardPluginMetadata, File> plugins) {
        REQUESTED_PLUGINS = plugins;
    }
}

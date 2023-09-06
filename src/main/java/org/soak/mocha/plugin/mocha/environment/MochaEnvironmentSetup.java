package org.soak.mocha.plugin.mocha.environment;

import org.soak.mocha.plugin.mocha.MochaPlugin;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MochaEnvironmentSetup {

    private final List<String> pluginsToLoad = new ArrayList<>();
    private final MochaPlugin mochaPlugin;
    private Path spongePath = Path.of("example/Sponge");

    public MochaEnvironmentSetup(MochaPlugin mochaPlugin) {
        this.mochaPlugin = mochaPlugin;
    }

    public MochaPlugin mocha() {
        return this.mochaPlugin;
    }

    public Path path() {
        return this.spongePath;
    }

    public MochaEnvironmentSetup setPath(Path path) {
        this.spongePath = path;
        return this;
    }

    public List<String> pluginsToLoad() {
        return this.pluginsToLoad;
    }

    public void loadPlugin(String plugin) {
        this.loadPlugins(plugin);
    }

    public void loadPlugins(String... plugins) {
        this.pluginsToLoad.addAll(Arrays.asList(plugins));
    }
}

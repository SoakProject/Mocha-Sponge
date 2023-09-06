package org.soak.mocha.plugin.mocha.environment;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.*;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.network.channel.ChannelManager;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.registry.BuilderProvider;
import org.spongepowered.api.registry.FactoryProvider;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryType;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.service.ServiceProvider;
import org.spongepowered.api.sql.SqlManager;
import org.spongepowered.api.util.metric.MetricsConfigManager;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public class MochaEnvironment implements Game {

    private final @NotNull Path gameDirectory;

    public MochaEnvironment(MochaEnvironmentSetup setup){
        this.gameDirectory = setup.path();
    }
    @Override
    public Scheduler asyncScheduler() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Path gameDirectory() {
        return this.gameDirectory;
    }

    @Override
    public boolean isServerAvailable() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Server server() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public SystemSubject systemSubject() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Locale locale(@NonNull String locale) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Platform platform() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public BuilderProvider builderProvider() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public FactoryProvider factoryProvider() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public DataManager dataManager() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public PluginManager pluginManager() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public EventManager eventManager() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public ConfigManager configManager() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public ChannelManager channelManager() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public MetricsConfigManager metricsConfigManager() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public SqlManager sqlManager() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public ServiceProvider.GameScoped serviceProvider() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public <T> Registry<T> registry(RegistryType<T> type) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public <T> Optional<Registry<T>> findRegistry(RegistryType<T> type) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Stream<Registry<?>> streamRegistries(ResourceKey root) {
        throw new RuntimeException("Not implemented yet");
    }
}

package org.soak.mocha.main;

import com.google.inject.Guice;
import org.soak.mocha.plugin.mocha.MochaPlugin;
import org.soak.mocha.plugin.mocha.environment.MochaEnvironment;
import org.soak.mocha.plugin.mocha.environment.MochaEnvironmentSetup;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.StandardPluginCandidate;
import org.spongepowered.plugin.builtin.jvm.JVMPluginContainer;
import org.spongepowered.plugin.builtin.jvm.locator.JVMPluginResource;
import org.spongepowered.plugin.builtin.jvm.locator.ResourceType;
import org.spongepowered.plugin.metadata.builtin.StandardPluginMetadata;
import org.spongepowered.plugin.metadata.builtin.model.StandardPluginContributor;
import org.spongepowered.plugin.metadata.builtin.model.StandardPluginDependency;
import org.spongepowered.plugin.metadata.builtin.model.StandardPluginLinks;
import org.spongepowered.plugin.metadata.model.PluginDependency;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class MochaSponge {

    private static final String MOCHA_PLUGIN_NAME = "mochaPluginName";
    private static final String MOCHA_PLUGIN_CLASS_PATH = "mochaPluginClassPath";
    private static final String SPONGE_PLUGINS_FOLDER = "spongePluginsFolder";

    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String mochaName = "plugin.jar";
        String mainClass = null;
        File spongePluginsFolder = new File("plugins");

        for (String arg : args) {
            if (arg.toLowerCase().startsWith(MOCHA_PLUGIN_NAME.toLowerCase() + "=")) {
                mochaName = arg.substring(MOCHA_PLUGIN_NAME.length() + 1);
                continue;
            }
            if (arg.toLowerCase().startsWith(MOCHA_PLUGIN_CLASS_PATH.toLowerCase() + "=")) {
                mainClass = arg.substring(MOCHA_PLUGIN_CLASS_PATH.length() + 1);
            }
            if (arg.toLowerCase().startsWith(SPONGE_PLUGINS_FOLDER.toLowerCase() + "=")) {
                spongePluginsFolder = new File(arg.substring(SPONGE_PLUGINS_FOLDER.length() + 1));
            }
            System.err.println("Unknown launch argument of '" + arg + "'");
        }

        File mochaPluginFile = new File(mochaName);
        if (!mochaPluginFile.exists()) {
            System.err.println("No file at '" + mochaPluginFile.getAbsolutePath() + "'");
            System.exit(1);
            return;
        }

        var mochaMainClass = loadMochaPlugin(mochaPluginFile, mainClass);
        var environmentSetup = new MochaEnvironmentSetup(mochaMainClass);

        mochaMainClass.onSetup(environmentSetup);
        if (environmentSetup.pluginsToLoad().isEmpty()) {
            System.err.println("No Sponge plugins set to load");
            System.exit(1);
            return;
        }

        var spongePluginFiles = spongePluginsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (spongePluginFiles == null || spongePluginFiles.length == 0) {
            spongePluginsFolder.mkdirs();
            System.err.println("No Sponge plugins found in '" + spongePluginsFolder.getAbsolutePath() + "'");
            System.exit(1);
            return;
        }


        var pluginMetadata = createPluginMetadata(spongePluginFiles, environmentSetup.pluginsToLoad());
        System.out.println("Loading " + pluginMetadata.size() + " sponge plugin(s)");

        GlobalValues globalValues = new GlobalValues();
        globalValues.setRequestedPlugins(pluginMetadata);

        var pluginClassLoader = new URLClassLoader(pluginMetadata.values().stream().map(file -> {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).toArray(URL[]::new));

        var loadedPlugins = loadSpongePlugins(pluginMetadata, environmentSetup.pluginsToLoad());
        globalValues.setLoadedPlugins(loadedPlugins);

        MochaEnvironment environment = new MochaEnvironment(environmentSetup);
        globalValues.setEnvironment(environment);

        Guice.createInjector(new SpongeGlobalGameModule()).injectMembers(environment);
    }

    private static MochaPlugin loadMochaPlugin(File mochaPluginFile, String mainClass) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JarFile mochaPluginJar = new JarFile(mochaPluginFile);
        if (mainClass == null) {
            var entry = mochaPluginJar.getEntry("META-INF/mocha.txt");
            var entryStream = new BufferedReader(new InputStreamReader(mochaPluginJar.getInputStream(entry)));
            mainClass = entryStream.readLine();
            entryStream.close();
        }

        if (mainClass == null) {
            System.err.println("Missing main class in mocha.txt");
            mochaPluginJar.close();
            System.exit(1);
            return null;
        }

        URLClassLoader classLoader = new URLClassLoader(new URL[]{mochaPluginFile.toURI().toURL()});

        var loadedClasses = mochaPluginJar.stream()
                .filter(entry -> !entry.isDirectory())
                .filter(entry -> entry.getName().endsWith(".class"))
                .map(entry -> {
                    var className = entry.getName();
                    className = className.substring(0, className.length() - 6).replaceAll("/", ".");
                    try {
                        return classLoader.loadClass(className);
                    } catch (ClassNotFoundException e) {
                        System.err.println("Ignoring " + e.getLocalizedMessage());
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toSet());
        var finalMainClass = mainClass;
        var opMainClass = loadedClasses.stream().filter(clazz -> clazz.getName().equals(finalMainClass)).findAny();
        mochaPluginJar.close();
        if (opMainClass.isEmpty()) {
            System.err.println("Cannot find main class of '" + finalMainClass + "'");
            System.exit(1);
            return null;
        }
        var mainClassType = opMainClass.get();
        if (!MochaPlugin.class.isAssignableFrom(mainClassType)) {
            System.err.println(finalMainClass + " does not implement MochaPlugin");
            System.exit(1);
            return null;
        }

        return (MochaPlugin) mainClassType.getConstructor().newInstance();
    }

    private static Map<StandardPluginMetadata, File> createPluginMetadata(File[] spongePluginFiles, Collection<String> filterTo) {
        return Arrays.stream(spongePluginFiles)
                .flatMap(file -> {
                    try {
                        var jarFile = new JarFile(file);
                        var pluginMetadataEntry = jarFile.getEntry("META-INF/sponge_plugins.json");
                        var pluginMetadataIS = jarFile.getInputStream(pluginMetadataEntry);
                        var pluginMetadataJson = new BufferedReader(new InputStreamReader(pluginMetadataIS)).lines()
                                .collect(Collectors.joining("\n"));
                        var rootNode = JacksonConfigurationLoader.builder().buildAndLoadString(pluginMetadataJson);
                        jarFile.close();

                        var licence = rootNode.node("license").getString();
                        var globalVersion = rootNode.node("global", "version").getString();
                        var globalHomepage = rootNode.node("global", "links", "homepage").getString();
                        var globalSource = rootNode.node("global", "links", "source").getString();
                        var globalIssues = rootNode.node("global", "links", "issues").getString();
                        var globalContributors = rootNode.node("global", "contributors")
                                .childrenList()
                                .stream()
                                .map(node -> {
                                    var name = node.node("name").getString();
                                    if (name == null) {
                                        return null;
                                    }
                                    var description = node.node("description").getString();
                                    if (description == null) {
                                        return null;
                                    }
                                    return StandardPluginContributor.builder()
                                            .name(name)
                                            .description(description)
                                            .build();
                                })
                                .filter(Objects::nonNull)
                                .toList();
                        var globalDependencies = rootNode.node("global", "dependencies")
                                .childrenList()
                                .stream()
                                .map(node -> {
                                    var id = node.node("id").getString();
                                    if (id == null) {
                                        return null;
                                    }
                                    var version = node.node("version").getString();
                                    if (version == null) {
                                        return null;
                                    }
                                    var loadOrder = node.node("load-order").getString();
                                    var optional = node.node("optional").getBoolean();
                                    return StandardPluginDependency.builder()
                                            .id(id)
                                            .version(version)
                                            .loadOrder(loadOrder == null ? PluginDependency.LoadOrder.UNDEFINED : PluginDependency.LoadOrder.valueOf(
                                                    loadOrder))
                                            .optional(optional)
                                            .build();
                                })
                                .filter(Objects::nonNull)
                                .toList();

                        return rootNode.node("plugins").childrenList().stream()
                                .map(pluginNode -> {
                                    var id = pluginNode.node("id").getString();
                                    var name = pluginNode.node("name").getString();
                                    var entryPoint = pluginNode.node("entrypoint").getString();
                                    var description = pluginNode.node("description").getString();
                                    var version = pluginNode.node("version").getString();
                                    if (version == null) {
                                        version = globalVersion;
                                    }
                                    var homepage = pluginNode.node("links", "homepage").getString();
                                    if (homepage == null) {
                                        homepage = globalHomepage;
                                    }
                                    var source = pluginNode.node("links", "source").getString();
                                    if (source == null) {
                                        source = globalSource;
                                    }
                                    var issues = pluginNode.node("links", "issues").getString();
                                    if (issues == null) {
                                        issues = globalIssues;
                                    }
                                    StandardPluginLinks links = StandardPluginLinks.none();
                                    try {
                                        //if null then only that link should fail
                                        links = StandardPluginLinks.builder()
                                                .source(new URL(source))
                                                .issues(new URL(issues))
                                                .homepage(new URL(homepage))
                                                .build();
                                    } catch (MalformedURLException e) {
                                        e.getLocalizedMessage();
                                    }

                                    //contributors
                                    //dependencies

                                    return StandardPluginMetadata.builder()
                                            .id(id)
                                            .name(name)
                                            .entrypoint(entryPoint)
                                            .description(description)
                                            .version(version)
                                            .links(links)
                                            .property("license", licence)
                                            .contributors(globalContributors)
                                            .dependencies(globalDependencies)
                                            .build();
                                })
                                .map(v -> new AbstractMap.SimpleImmutableEntry<>(v, file));


                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(entry -> filterTo.contains(entry.getKey().id()))
                .collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey,
                        AbstractMap.SimpleImmutableEntry::getValue));
    }

    private static List<PluginContainer> loadSpongePlugins(Map<StandardPluginMetadata, File> metadatas, Collection<String> loadOrder) {
        return loadOrder.stream().map(pluginId -> {
            var metadataEntry = metadatas.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().id().equals(pluginId))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Cannot find plugin with the id of '" + pluginId + "'"));
            var pluginMetadataResource = new JVMPluginResource("",
                    ResourceType.JAR,
                    metadataEntry.getValue().toPath(),
                    null);
            var pluginCandidate = new StandardPluginCandidate<>(metadataEntry.getKey(), pluginMetadataResource);
            return new JVMPluginContainer(pluginCandidate);
        }).collect(Collectors.toList());
    }
}

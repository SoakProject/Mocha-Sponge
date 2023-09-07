package org.soak.mocha.plugin.sponge.event;

import org.spongepowered.api.event.*;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.*;
import org.spongepowered.plugin.PluginContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class MochaEventManager implements EventManager {

    private Map<Object, PluginContainer> eventListenerClass = new ConcurrentHashMap<>();

    @Override
    public <E extends Event> EventManager registerListener(EventListenerRegistration<E> registration) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public EventManager registerListeners(PluginContainer plugin, Object obj) {
        this.eventListenerClass.put(obj, plugin);
        return this;
    }

    @Override
    public EventManager unregisterListeners(Object obj) {
        this.eventListenerClass.remove(obj);
        return this;
    }

    @Override
    public boolean post(Event event) {
        var methods = new ArrayList<>(this.findMethods(event).toList());
        methods.sort(Comparator.comparing(entry -> entry.getKey().getAnnotation(Listener.class).order()));
        for (var entry : methods) {
            var method = entry.getKey();
            Object[] parameters = new Object[method.getParameterCount()];
            for (int i = 0; i < parameters.length; i++) {
                if (i == 0) {
                    parameters[0] = event;
                    continue;
                }
                throw new RuntimeException("Not implemented yet");
            }
            try {
                method.invoke(entry.getValue(), parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        if (event instanceof Cancellable cancellable) {
            return !cancellable.isCancelled();
        }
        return true;
    }

    private boolean hasGetter(Event event, Parameter parameter) {
        Getter getter = parameter.getAnnotation(Getter.class);
        try {
            Object type = event.getClass().getMethod(getter.value()).invoke(event);
            return parameter.getType().isInstance(type);
        } catch (NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            return false;
        }
    }

    private boolean hasAfter(Event event, Parameter parameter) {
        return hasCauseAnnotation(event, parameter, After.class, (cause, after) -> cause.after(after.value()));
    }

    private boolean hasBefore(Event event, Parameter parameter) {
        return hasCauseAnnotation(event, parameter, Before.class, (cause, before) -> cause.before(before.value()));
    }

    private boolean hasFirst(Event event, Parameter parameter) {
        return hasCauseAnnotation(event, parameter, First.class, (cause, first) -> cause.first(parameter.getType()));
    }

    private boolean hasLast(Event event, Parameter parameter) {
        return hasCauseAnnotation(event, parameter, Last.class, (cause, last) -> cause.last(parameter.getType()));
    }

    private boolean hasRoot(Event event, Parameter parameter) {
        return hasCauseAnnotation(event, parameter, Root.class, (cause, root) -> cause.root());
    }

    private boolean hasContextValue(Event event, Parameter parameter) {
        return hasCauseAnnotation(event, parameter, ContextValue.class, (cause, context) -> {
            return cause.context()
                    .asMap()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().key().value().equals(context.value()))
                    .findAny()
                    .map(Map.Entry::getValue)
                    .orElse(null);
        });
    }

    private <Anno extends Annotation> boolean hasCauseAnnotation(Event event, Parameter parameter, Class<Anno> annotationType, BiFunction<Cause, Anno, Object> getter) {
        Anno anno = parameter.getAnnotation(annotationType);
        Cause cause = event.cause();
        Object compare = getter.apply(cause, anno);
        if (compare == null) {
            return false;
        }
        if (!parameter.getType().isInstance(compare)) {
            return false;
        }
        try {
            boolean inverse = (boolean) annotationType.getDeclaredMethod("inverse").invoke(anno);
            Class<?>[] typeFilter = (Class<?>[]) annotationType.getDeclaredMethod("typeFilter").invoke(anno);
            if (typeFilter.length == 0) {
                return true;
            }
            if (inverse) {
                return Arrays.stream(typeFilter).noneMatch(t -> t.isInstance(compare));
            }
            return Arrays.stream(typeFilter).anyMatch(t -> t.isInstance(compare));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return false;
        }
    }

    private Stream<Map.Entry<Method, Object>> findMethods(Event event) {
        return this.eventListenerClass.keySet()
                .parallelStream()
                .flatMap(eventListenerClass -> Arrays.stream(eventListenerClass.getClass().getDeclaredMethods())
                        .filter(method -> method.getParameterCount() != 0)
                        .filter(method -> method.getParameterTypes()[0].isInstance(event))
                        .filter(method -> method.isAnnotationPresent(Listener.class))
                        .filter(method -> {
                            if (method.getParameterCount() == 1) {
                                return true;
                            }
                            var parameters = method.getParameters();
                            return Arrays.stream(parameters).allMatch(parameter -> {
                                boolean hasAnnotation = false;
                                if (parameter.isAnnotationPresent(Getter.class)) {
                                    if (!hasGetter(event, parameter)) {
                                        return false;
                                    }
                                    hasAnnotation = true;
                                }
                                if (parameter.isAnnotationPresent(Before.class)) {
                                    if (!hasBefore(event, parameter)) {
                                        return false;
                                    }
                                    hasAnnotation = true;
                                }
                                if (parameter.isAnnotationPresent(After.class)) {
                                    if (!hasAfter(event, parameter)) {
                                        return false;
                                    }
                                    hasAnnotation = true;
                                }
                                if (parameter.isAnnotationPresent(First.class)) {
                                    if (!hasFirst(event, parameter)) {
                                        return false;
                                    }
                                    hasAnnotation = true;
                                }
                                if (parameter.isAnnotationPresent(Last.class)) {
                                    if (!hasLast(event, parameter)) {
                                        return false;
                                    }
                                    hasAnnotation = true;
                                }
                                if (parameter.isAnnotationPresent(Root.class)) {
                                    if (!hasRoot(event, parameter)) {
                                        return false;
                                    }
                                    hasAnnotation = true;
                                }

                                return hasAnnotation;
                            });
                        }).map(method -> new AbstractMap.SimpleImmutableEntry<>(method, eventListenerClass))
                );

    }
}

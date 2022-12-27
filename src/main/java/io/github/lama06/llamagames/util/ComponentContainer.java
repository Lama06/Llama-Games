package io.github.lama06.llamagames.util;

import java.util.HashMap;
import java.util.Map;

public class ComponentContainer {
    private final Map<Class<?>, Object> components = new HashMap<>();

    public void addComponent(Object component) {
        components.put(component.getClass(), component);
    }

    public boolean hasComponent(Class<?> target) {
        for (Class<?> type : components.keySet()) {
            if (type.equals(target)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> type) {
        for (Map.Entry<Class<?>, Object> component : components.entrySet()) {
            if (component.getKey() == type) {
                return (T) component.getValue();
            }
        }

        return null;
    }

    public Map<Class<?>, Object> getComponents() {
        return components;
    }
}

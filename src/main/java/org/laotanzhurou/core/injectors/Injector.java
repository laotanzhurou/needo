package org.laotanzhurou.core.injectors;

import org.laotanzhurou.core.storage.ObjectStore;

public interface Injector {
    <T> void register(Class<T> registerClazz);
    <T> void prepare(Class<T> clazz);
    <T> T createInstance(Class<T> clazz);
}

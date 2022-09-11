package org.laotanzhurou.core.injectors;

import org.laotanzhurou.core.exceptions.ElementNotExistException;
import org.laotanzhurou.core.exceptions.UnimplementedException;
import org.laotanzhurou.core.processors.DependencyGraphProcessor;
import org.laotanzhurou.core.storage.ObjectStore;
import org.laotanzhurou.core.storage.StorageKey;

public class BasicInjector implements Injector{

    private ObjectStore objectStore = new ObjectStore();
    private DependencyGraphProcessor processor = new DependencyGraphProcessor();

    public <T> void prepare(Class<T> clazz) {
        try {
            processor.process(objectStore, clazz);
        } catch (Exception ex) {
            //TODO: throw a well-defined exception from here, e.g. PreparationException
            throw new RuntimeException(ex);
        }
    }

    public <T> void register(Class<T> registerClazz) {

    }

    public <T> T createInstance(Class<T> clazz) {
        return createInstance(clazz, StorageKey.DEFAULT);
    }

    public <T> T createInstance(Class<T> clazz, String name) {
        StorageKey key = new StorageKey(clazz, name);
        if(!objectStore.containsKey(key)) {
            throw new ElementNotExistException("Composite key of " + clazz.getSimpleName() + " and " + name + " doesn't exists!");
        }
        Object obj = objectStore.get(key);
        if(!clazz.isInstance(obj)) {
            throw new RuntimeException("Wrong type! Wanted " + clazz.getSimpleName() + " but had " + obj.getClass().getSimpleName());
        }
        return clazz.cast(obj);
    }
}

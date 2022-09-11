package org.laotanzhurou.core.processors;

import org.laotanzhurou.core.annotations.Provide;
import org.laotanzhurou.core.exceptions.UnimplementedException;
import org.laotanzhurou.core.register.Register;
import org.laotanzhurou.core.storage.ObjectStore;
import org.laotanzhurou.core.storage.StorageKey;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.reflections.ReflectionUtils.Methods;
import static org.reflections.ReflectionUtils.get;

//TODO: right now register processor only supports one-step provision, i.e. no arguments
public class RegisterProcessor implements Processor {

    public void process(ObjectStore store, Class entryClass) {
        // validate entry class is Register
        if (Arrays.stream(entryClass.getInterfaces()).noneMatch(i -> i == Register.class)) {
            //TODO: throw a more well-defined exception here
            throw new RuntimeException("Expecting a sub-class of Register, but got " + entryClass.getSimpleName());
        }
        // collect all Provide methods
        List<Method> providers = get(Methods.of(entryClass)).stream()
                .filter(m -> Arrays.stream(m.getDeclaredAnnotations()).anyMatch(ann -> ann instanceof Provide))
                .toList();

        // invoke them and put into store
        for (Method provider : providers) {
            if (provider.getParameterCount() > 0) {
                //TODO: to support this
                throw new UnimplementedException("Provider method will argument injection is not supported right now.");
            }
            Class clazz = provider.getReturnType();
            Provide annotation = (Provide) Arrays.stream(provider.getDeclaredAnnotations())
                    .filter(ann -> ann instanceof Provide)
                    .findFirst()
                    .get();
            String name = annotation.name();

            StorageKey key = new StorageKey(clazz, name);
            try {
                Object value = provider.invoke(entryClass);
                store.put(key, value);
            } catch (Exception ex) {
                //TODO: throw a well-defined exception
                throw new RuntimeException(String.format("Failed to invoke provider method %s from register class %s",
                        provider.getName(), entryClass.getSimpleName()));
            }
        }
    }

}

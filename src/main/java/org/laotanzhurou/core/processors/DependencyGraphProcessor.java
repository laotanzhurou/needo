package org.laotanzhurou.core.processors;

import org.laotanzhurou.core.annotations.Need;
import org.laotanzhurou.core.storage.ObjectStore;
import org.laotanzhurou.core.storage.StorageKey;
import org.reflections.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.reflections.ReflectionUtils.Constructors;
import static org.reflections.ReflectionUtils.get;

public class DependencyGraphProcessor implements Processor{

    //TODO: right now it handles only 1 case:
    // - all classes in the graph has only 1 constructor or the first constructor, in both case it needs to fully comply, i.e. all parameter types have annotations
    // - no primitive/built-in types as argument types allowed
    // - corollary: all leave classes have empty constructors
    // - no cyclic dependency at constructor level

    //TODO: better design
    // - try not to throw Exception at method signature
    // - create Exception types to replace RuntimeException

    Set<StorageKey> expanded = new HashSet<>();
    Set<StorageKey> processed = new HashSet<>();
    public void process(ObjectStore store, Class entryClass) {
        try {
            process(store, new StorageKey(entryClass, StorageKey.DEFAULT));
        } catch(Exception ex) {
            //TODO: throw a more well-defined exception, perhaps declare that from interface method
            throw new RuntimeException(ex);
        }
    }

    private void process(ObjectStore store, StorageKey rootKey) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // mark self as expanded
        expanded.add(rootKey);
        // expand all fields in constructor in DFS
        Class entryClass = rootKey.getClass();
        Set<Constructor> constructors = get(Constructors.of(entryClass));
        Constructor con = constructors.stream().findFirst().orElseThrow(() -> new RuntimeException("No constructor defined for " + entryClass.getSimpleName()));
        Class[] params = con.getParameterTypes();
        Annotation[][] annotations = con.getParameterAnnotations();
        StorageKey[] keys = new StorageKey[params.length];
        for(int i=0;i<params.length;i++) {
            Class param = params[i];
            List<Annotation> paramAnnotations = Arrays.asList(annotations[i]);
            Need needAnnotation = (Need) paramAnnotations.stream()
                    .filter(ann -> ann instanceof Need)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No @Need found in param type " + param.getSimpleName()
                            + " at constructor of " + entryClass.getSimpleName()));
            StorageKey key = new StorageKey(entryClass, needAnnotation.name());
            keys[i] = key;
            if(expanded.contains(key)) {
                if(!processed.contains(key)) {
                    throw new RuntimeException("Cyclic dependency is not allowed!");
                }
                continue;
            }
            // process child
            process(store, param);
        }
        // create instance
        if(params.length == 0) {
            store.put(rootKey, con.newInstance());
        } else {
            // TODO: check for null here
            Object[] requiredParams = IntStream.range(0, params.length)
                    .mapToObj(i -> store.get(keys[i]))
                    .toList().toArray();
            store.put(rootKey, con.newInstance(requiredParams));
        }
        // mark self as processed
        processed.add(rootKey);
    }
}

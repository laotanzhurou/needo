package org.laotanzhurou.core.storage;

public class StorageKey {
    //TODO: we'll need to have a check to ensure annotations can't have DEFAULT as name
    public static final String DEFAULT = "default";

    Class clazz;
    String name;

    public StorageKey(Class clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.clazz.getSimpleName() + "::" + this.name;
    }

    @Override
    public int hashCode() {
        //TODO: will this work?
        return this.clazz.hashCode() * this.name.hashCode();
    }
}

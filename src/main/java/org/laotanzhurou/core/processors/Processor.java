package org.laotanzhurou.core.processors;

import org.laotanzhurou.core.storage.ObjectStore;

public interface Processor {

    void process(ObjectStore store, Class entryClass);

}

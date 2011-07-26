/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AmeeObjectCacheImpl implements Serializable, AmeeObjectCache {

    private Map<String, AmeeObjectCacheEntry> cache = new HashMap<String, AmeeObjectCacheEntry>();

    public AmeeObjectCacheImpl() {
        super();
    }

    public void put(AmeeObjectCacheEntry objectCacheEntry) {
        cache.put(objectCacheEntry.getObjectReference().getUri(), objectCacheEntry);
    }

    public AmeeObjectCacheEntry get(String path) {
        return cache.get(path);
    }

    public boolean remove(String path) {
        return cache.remove(path) != null;
    }

    public void removeAll() {
        cache.clear();
    }
}

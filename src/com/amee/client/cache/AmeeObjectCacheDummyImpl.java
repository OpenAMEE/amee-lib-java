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

public class AmeeObjectCacheDummyImpl implements Serializable, AmeeObjectCache {

    public AmeeObjectCacheDummyImpl() {
        super();
    }

    public void put(AmeeObjectCacheEntry objectCacheEntry) {
        // do nothing
    }

    public AmeeObjectCacheEntry get(String path) {
        return null;
    }

    public boolean remove(String path) {
        return false;
    }

    public void removeAll() {
        // do nothing
    }
}
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

/**
 * Loosely based on net.sf.ehcache.Ehcache.
 */
public interface AmeeObjectCache extends Serializable {

    public void put(AmeeObjectCacheEntry objectCacheEntry);

    public AmeeObjectCacheEntry get(String path);

    public boolean remove(String path);

    public void removeAll();
}

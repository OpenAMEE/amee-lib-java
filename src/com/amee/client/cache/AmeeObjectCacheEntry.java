/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.cache;

import com.amee.client.model.base.AmeeObject;
import com.amee.client.model.base.AmeeObjectReference;

import java.io.Serializable;

public class AmeeObjectCacheEntry implements Serializable, Comparable {

    private AmeeObjectReference objectReference = null;
    private AmeeObject object = null;

    private AmeeObjectCacheEntry() {
        super();
    }

    public AmeeObjectCacheEntry(AmeeObjectReference ref, AmeeObject object) {
        this();
        setObjectReference(ref);
        setObject(object);
    }

    public boolean equals(Object o) {
        return getObjectReference().equals(o);
    }

    public int compareTo(Object o) {
        return getObjectReference().compareTo(o);
    }

    public int hashCode() {
        return getObjectReference().hashCode();
    }

    public String toString() {
        return getObjectReference().toString();
    }

    public AmeeObjectReference getObjectReference() {
        return objectReference;
    }

    public void setObjectReference(AmeeObjectReference ref) {
        if (ref != null) {
            this.objectReference = ref;
        }
    }

    public AmeeObject getObject() {
        return object;
    }

    public void setObject(AmeeObject object) {
        if (object != null) {
            this.object = object;
        }
    }
}
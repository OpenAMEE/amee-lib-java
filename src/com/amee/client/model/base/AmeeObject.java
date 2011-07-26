/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.model.base;

import com.amee.client.AmeeException;
import com.amee.client.service.AmeeObjectFactory;

import java.io.Serializable;

public abstract class AmeeObject implements Serializable {

    private String uid = null;
    private String name = null;
    private AmeeObjectReference objectReference = null;
    private boolean fetched = false;

    public AmeeObject() {
        super();
    }

    public AmeeObject(AmeeObjectReference ref) {
        this();
        setObjectReference(ref);
    }

    public AmeeObject(String path, AmeeObjectType objectType) {
        this(new AmeeObjectReference(path, objectType));
    }

    public void populate(AmeeObject copy) {
        copy.setUid(uid);
        copy.setName(name);
        copy.setObjectReference(objectReference);
        copy.setFetched(fetched);
    }

    public abstract AmeeObject getCopy();

    public void fetch() throws AmeeException {
        AmeeObjectFactory.getInstance().fetch(this);
    }

    public void save() throws AmeeException {
        AmeeObjectFactory.getInstance().save(this);
    }

    public void delete() throws AmeeException {
        AmeeObjectFactory.getInstance().delete(this);
    }

    public String getUri() {
        return getObjectReference().getUri();
    }

    public String getLocalPath() {
        return getObjectReference().getLocalPart();
    }

    public String getParentUri() {
        return getObjectReference().getParentUri();
    }

    public AmeeObjectType getObjectType() {
        return getObjectReference().getObjectType();
    }

    public AmeeObjectReference getObjectReference() {
        return objectReference;
    }

    public void setObjectReference(AmeeObjectReference ameeObjectReference) {
        if (ameeObjectReference != null) {
            this.objectReference = ameeObjectReference;
        }
    }

    public String getUid() throws AmeeException {
        if ((uid == null) && !isFetched()) {
            fetch();
        }
        return uid;
    }

    public void setUid(String uid) {
        if (uid != null) {
            this.uid = uid;
        }
    }

    public String getName() throws AmeeException {
        if ((name == null) && !isFetched()) {
            fetch();
        }
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public boolean isFetched() {
        return fetched;
    }

    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }
}
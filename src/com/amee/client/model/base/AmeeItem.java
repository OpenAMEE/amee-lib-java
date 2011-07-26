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
import com.amee.client.util.Choice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AmeeItem extends AmeeObject implements Serializable {

    private AmeeObjectReference parentRef = null;
    private String label = null;
    private List<AmeeObjectReference> valueRefs = new ArrayList<AmeeObjectReference>();

    public AmeeItem() {
        super();
    }

    public AmeeItem(AmeeObjectReference ref) {
        super(ref);
    }

    public AmeeItem(String path, AmeeObjectType objectType) {
        super(path, objectType);
    }

    public void populate(AmeeItem copy) {
        super.populate(copy);
        copy.setParentRef(parentRef);
        copy.setLabel(label);
        copy.setValueRefs(new ArrayList<AmeeObjectReference>(valueRefs));
    }

    public abstract void setParentRef();

    public List<AmeeValue> getValues() throws AmeeException {
        AmeeObjectFactory ameeObjectFactory = AmeeObjectFactory.getInstance();
        List<AmeeValue> values = new ArrayList<AmeeValue>();
        AmeeObject ameeObject;
        for (AmeeObjectReference ref : getValueRefs()) {
            ameeObject = ameeObjectFactory.getObject(ref);
            if (ameeObject != null) {
                values.add((AmeeValue) ameeObject);
            }
        }
        return values;
    }

    public void setValues(List<Choice> values) throws AmeeException {
        AmeeObjectFactory.getInstance().setItemValues(this, values);
    }

    public AmeeCategory getParent() throws AmeeException {
        AmeeCategory category = null;
        if (getParentRef() != null) {
            category = (AmeeCategory) AmeeObjectFactory.getInstance().getObject(getParentRef());
        }
        return category;
    }

    public AmeeObjectReference getParentRef() {
        if (parentRef == null) {
            setParentRef();
        }
        return parentRef;
    }

    public void setParentRef(AmeeObjectReference parentRef) {
        if (parentRef != null) {
            this.parentRef = parentRef;
        }
    }

    public AmeeValue getValue(String localPath) throws AmeeException {
        return (AmeeValue) AmeeObjectFactory.getInstance().getObject(getUri() + "/" + localPath, AmeeObjectType.VALUE);
    }

    public String getLabel() throws AmeeException {
        if (!isFetched()) {
            fetch();
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<AmeeObjectReference> getValueRefs() throws AmeeException {
        return getValueRefs(true);
    }

    public List<AmeeObjectReference> getValueRefs(boolean fetchIfNotFetched) throws AmeeException {
        if (fetchIfNotFetched && !isFetched()) {
            fetch();
        }
        return valueRefs;
    }

    public void addValueRef(AmeeObjectReference ref) {
        valueRefs.add(ref);
    }

    public void clearValueRefs() {
        valueRefs.clear();
    }

    public void setValueRefs(List<AmeeObjectReference> valueRefs) {
        if (valueRefs != null) {
            this.valueRefs = valueRefs;
        }
    }
}
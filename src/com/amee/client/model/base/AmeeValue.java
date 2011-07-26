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

public class AmeeValue extends AmeeObject implements Serializable {

    private AmeeObjectReference itemRef = null;
    private String value = "";
    private String unit = "";
    private String perUnit = "";
    
    public AmeeValue() {
        super();
    }

    public AmeeValue(AmeeObjectReference ref) {
        super(ref);
    }

    public AmeeValue(String path, AmeeObjectType objectType) {
        super(path, objectType);
    }

    public void populate(AmeeValue copy) {
        super.populate(copy);
        copy.setItemRef(itemRef);
        copy.setValue(value);
        copy.setUnit(unit);
        copy.setPerUnit(perUnit);
    }

    public AmeeObject getCopy() {
        AmeeValue copy = new AmeeValue();
        populate(copy);
        return copy;
    }

    public AmeeItem getItem() throws AmeeException {
        AmeeItem item = null;
        if (getItemRef() != null) {
            item = (AmeeItem) AmeeObjectFactory.getInstance().getObject(getItemRef());
        }
        return item;
    }

    public AmeeObjectReference getItemRef() {
        return itemRef;
    }

    public void setItemRef(AmeeObjectReference itemRef) {
        if (itemRef != null) {
            this.itemRef = itemRef;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        if (unit == null || unit.length() == 0)
            return;
        this.unit = unit;
    }

    public String getPerUnit() {
        return perUnit;
    }

    public void setPerUnit(String perUnit) {
        if (perUnit == null || perUnit.length() == 0)
            return;
        this.perUnit = perUnit;
    }

}
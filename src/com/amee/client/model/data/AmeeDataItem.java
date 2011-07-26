/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.model.data;

import com.amee.client.model.base.AmeeItem;
import com.amee.client.model.base.AmeeObject;
import com.amee.client.model.base.AmeeObjectReference;
import com.amee.client.model.base.AmeeObjectType;

import java.io.Serializable;

public class AmeeDataItem extends AmeeItem implements Serializable {

    public AmeeDataItem() {
        super();
    }

    public AmeeDataItem(AmeeObjectReference ref) {
        super(ref);
    }

    public AmeeDataItem(String path, AmeeObjectType objectType) {
        super(path, objectType);
    }

    public void populate(AmeeDataItem copy) {
        super.populate(copy);
    }

    public AmeeObject getCopy() {
        AmeeDataItem copy = new AmeeDataItem();
        populate(copy);
        return copy;
    }

    public void setParentRef() {
        setParentRef(new AmeeObjectReference(getObjectReference().getParentUri(), AmeeObjectType.DATA_CATEGORY));
    }
}

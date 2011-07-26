/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.model.data;

import com.amee.client.AmeeException;
import com.amee.client.model.base.AmeeCategory;
import com.amee.client.model.base.AmeeItem;
import com.amee.client.model.base.AmeeObject;
import com.amee.client.model.base.AmeeObjectReference;
import com.amee.client.model.base.AmeeObjectType;

import com.amee.client.service.AmeeObjectFactory;
import com.amee.client.util.Choice;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AmeeDataCategory extends AmeeCategory implements Serializable {

    public AmeeDataCategory() {
        super();
    }

    public AmeeDataCategory(AmeeObjectReference ref) {
        super(ref);
    }

    public AmeeDataCategory(String path, AmeeObjectType objectType) {
        super(path, objectType);
    }

    public void populate(AmeeDataCategory copy) {
        super.populate(copy);
    }

    public AmeeObject getCopy() {
        AmeeDataCategory copy = new AmeeDataCategory();
        populate(copy);
        return copy;
    }

    public void setParentRef() {
        if (getUri().equals("data")) {
            setParentRef(null);
        } else {
            setParentRef(new AmeeObjectReference(getObjectReference().getParentUri(), AmeeObjectType.DATA_CATEGORY));
        }
    }

    public AmeeCategory getNewChildCategory(AmeeObjectReference ref) {
        return new AmeeDataCategory(ref);
    }

    public AmeeObjectType getChildCategoryObjectType() {
        return AmeeObjectType.DATA_CATEGORY;
    }

    public AmeeItem getNewChildItem(AmeeObjectReference ref) {
        return new AmeeDataItem(ref);
    }

    public AmeeObjectType getChildItemObjectType() {
        return AmeeObjectType.DATA_ITEM;
    }

    public List<AmeeDataCategory> getDataCategories() throws AmeeException {
        List<AmeeDataCategory> dataCategories = new ArrayList<AmeeDataCategory>();
        for (AmeeCategory category : super.getCategories()) {
            dataCategories.add((AmeeDataCategory) category);
        }
        return dataCategories;
    }

    public List<AmeeDataItem> getDataItems() throws AmeeException {
        List<AmeeDataItem> dataItems = new ArrayList<AmeeDataItem>();
        for (AmeeItem item : super.getItems()) {
            dataItems.add((AmeeDataItem) item);
        }
        return dataItems;
    }

    public AmeeDataItem addDataItem(String dataItemDefinitionUid) throws AmeeException {
        return addDataItem(dataItemDefinitionUid, null);
    }

    public AmeeDataItem addDataItem(String dataItemDefinitionUid, List<Choice> values) throws AmeeException {
        return AmeeObjectFactory.getInstance().getDataItem(this, dataItemDefinitionUid, values);
    }

}
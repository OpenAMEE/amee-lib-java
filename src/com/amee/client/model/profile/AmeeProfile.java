/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.model.profile;

import com.amee.client.AmeeException;
import com.amee.client.model.base.AmeeObject;
import com.amee.client.model.base.AmeeObjectReference;
import com.amee.client.model.base.AmeeObjectType;
import com.amee.client.service.AmeeObjectFactory;
import com.amee.client.util.Choice;

import java.io.Serializable;
import java.util.List;

public class AmeeProfile extends AmeeProfileCategory implements Serializable {

    private String validFrom = "";

    public AmeeProfile() {
        super();
    }

    public AmeeProfile(AmeeObjectReference ref) {
        super(ref);
    }

    public AmeeProfile(String path, AmeeObjectType objectType) {
        super(path, objectType);
    }

    public void populate(AmeeProfile copy) {
        super.populate(copy);
        copy.setValidFrom(validFrom);
    }

    public AmeeObject getCopy() {
        AmeeProfile copy = new AmeeProfile();
        populate(copy);
        return copy;
    }

    public void setProfileRef() {
        setProfileRef(null);
    }

    public void setParentRef() {
        setParentRef(null);
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        if (validFrom != null) {
            this.validFrom = validFrom;
        }
    }

    public AmeeProfileItem addProfileItem(String categoryUri, String dataItemUid, List<Choice> values) throws AmeeException {
        return AmeeObjectFactory.getInstance().addProfileItem(this, categoryUri, dataItemUid, values);
    }

}
/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.model.base;

import com.amee.client.model.data.AmeeDataCategory;
import com.amee.client.model.data.AmeeDataItem;
import com.amee.client.model.data.AmeeDrillDown;
import com.amee.client.model.profile.AmeeProfile;
import com.amee.client.model.profile.AmeeProfileCategory;
import com.amee.client.model.profile.AmeeProfileItem;

import java.io.Serializable;

public enum AmeeObjectType implements Serializable {

    DATA_CATEGORY,
    DATA_ITEM,
    DRILL_DOWN,
    PROFILE,
    PROFILE_CATEGORY,
    PROFILE_ITEM,
    UNKNOWN,
    VALUE;

    private String[] names = {
            "DATA_CATEGORY",
            "DATA_ITEM",
            "DRILL_DOWN",
            "PROFILE",
            "PROFILE_CATEGORY",
            "PROFILE_ITEM",
            "UNKNOWN",
            "VALUE"};

    private String[] labels = {
            "Data Category",
            "Data Item",
            "Drill Down",
            "Profile",
            "Profile Category",
            "Profile Item",
            "Unknown",
            "Value"};

    private Class[] clazzes = {
            AmeeDataCategory.class,
            AmeeDataItem.class,
            AmeeDrillDown.class,
            AmeeProfile.class,
            AmeeProfileCategory.class,
            AmeeProfileItem.class,
            Object.class,
            AmeeValue.class};

    public String toString() {
        return getName();
    }

    public String getName() {
        return names[this.ordinal()];
    }

    public String getLabel() {
        return labels[this.ordinal()];
    }

    public Class getClazz() {
        return clazzes[this.ordinal()];
    }
}
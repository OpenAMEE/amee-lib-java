/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.examples;

import com.amee.client.AmeeException;
import com.amee.client.service.*;
import com.amee.client.model.base.*;
import com.amee.client.model.profile.*;

public class ViewProfileItem {

    public static void main(String[] args) throws AmeeException {
        
        // Set up AMEE connection
        AmeeContext.getInstance().setUsername("username-here");
        AmeeContext.getInstance().setPassword("password-here");
        AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");

        // Parameters
        String profileUID = "05ECD93B7752"; // Change this to one that exists for you.
        String profileCategory = "home/energy/quantity";
        String itemUID = "B358BC0CCC23"; // Change this to an item UID that exists in the above ProfileCategory.

        // Get profile object
        AmeeProfile profile = AmeeObjectFactory.getInstance().getProfile(profileUID);
        // Get category
        AmeeProfileCategory cat = AmeeObjectFactory.getInstance().getProfileCategory(profile, profileCategory);
        // Find the item we want
        for (AmeeProfileItem item : cat.getProfileItems()) {
            if (item.getUid().compareTo(itemUID) == 0) {
                // Print data
                System.out.print(" - name: ");
                System.out.println(item.getName());
                for (AmeeValue value : item.getValues()) {
                    System.out.printf(" - %s (%s): %s %s/%s\n", value.getName(), value.getUid(), value.getValue(), value.getUnit(), value.getPerUnit());
                }
                System.out.printf(" - CO2 total: %s %s\n", item.getAmount(), item.getAmountUnit());
            }
        }
    }
}

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

public class ViewProfileCategory {

    public static void main(String[] args) throws AmeeException {
        
        // Set up AMEE connection
        AmeeContext.getInstance().setUsername("username-here");
        AmeeContext.getInstance().setPassword("password-here");
        AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");
        AmeeContext.getInstance().setItemsPerPage(10);

        // Parameters
        String profileUID = "05ECD93B7752"; // Change this to one that exists for you.
        String profileCategory = "home/energy/quantity";

        // Get profile object
        AmeeProfile profile = AmeeObjectFactory.getInstance().getProfile(profileUID);
        // Get category
        AmeeProfileCategory cat = AmeeObjectFactory.getInstance().getProfileCategory(profile, profileCategory);

        // Print data
        System.out.println("---------------------");
        System.out.print("Category: ");
        System.out.println(cat.getName());
        System.out.print("Path: ");
        System.out.println(cat.getUri());
        System.out.print("UID: ");
        System.out.println(cat.getUid());
        System.out.println("Subcategories:");
        for (AmeeProfileCategory c : cat.getProfileCategories()) {
            System.out.print("  - ");
            System.out.print(c.getUri());
            System.out.print(" (");
            System.out.print(c.getName());
            System.out.println(")");
        }
        // Show items, using pager
        System.out.printf("Items: %d in total\n", cat.getItemsPager().getItems());
        while (true) {
            System.out.printf(" page %d\n", cat.getItemsPager().getCurrentPage());
            for (AmeeProfileItem item : cat.getProfileItems()) {
                // Print data
                System.out.printf("name: %s\n", item.getName());
                System.out.printf(" - startDate: %s\n", item.getStartDate());
                System.out.printf(" - endDate: %s\n", item.getEndDate());
                for (AmeeValue value : item.getValues()) {
                    System.out.printf(" - %s (%s): %s %s/%s\n", value.getName(), value.getUid(), value.getValue(), value.getUnit(), value.getPerUnit());
                }
                System.out.printf(" - CO2 total: %s %s\n", item.getAmount(), item.getAmountUnit());
            }
            int next = cat.getItemsPager().getNextPage();
            if (next == -1) 
              break;
            else {
              cat.setPage(next);
              cat.fetch();
            }
        }
    }
}

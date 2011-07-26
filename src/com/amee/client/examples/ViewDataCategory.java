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
import com.amee.client.model.data.*;
import com.amee.client.util.*;

public class ViewDataCategory {

    public static void main(String[] args) throws AmeeException {
        
      System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
      System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
      System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
      System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");

      // Set up AMEE connection
        AmeeContext.getInstance().setUsername("username-here");
        AmeeContext.getInstance().setPassword("password-here");
        AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");
        AmeeContext.getInstance().setItemsPerPage(10);

        // Parameters
        String dataCategory = "home/energy/electricity";

        // Get category
        AmeeDataCategory cat = AmeeObjectFactory.getInstance().getDataCategory(dataCategory);

        // Print data
        System.out.println("---------------------");
        System.out.print("Category: ");
        System.out.println(cat.getName());
        System.out.print("Path: ");
        System.out.println(cat.getUri());
        System.out.print("UID: ");
        System.out.println(cat.getUid());
        System.out.println("Subcategories:");
        for (AmeeDataCategory c : cat.getDataCategories()) {
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
            for (AmeeDataItem i : cat.getDataItems()) {
                System.out.print("  - ");
                System.out.print(i.getUri());
                System.out.print(" (");
                System.out.print(i.getLabel());
                System.out.println(")");
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

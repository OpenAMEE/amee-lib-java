/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.examples;

import java.util.ArrayList;
import java.util.List;
import com.amee.client.AmeeException;
import com.amee.client.model.base.AmeeValue;
import com.amee.client.service.*;
import com.amee.client.model.data.*;
import com.amee.client.util.Choice;

public class CreateDataItem {

    public static void main(String[] args) throws AmeeException {

        // Note that this will only be possible if your user Id has the correct permissions.

        // Set up AMEE connection
        AmeeContext.getInstance().setUsername("username-here");
        AmeeContext.getInstance().setPassword("password-here");
        AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");

        // Parameters
        String dataCategory = "test/testing";
        String dataItemDefinitionUid = "46AB0CFA87ED"; // This is the UID of the data item type you want to create.
                                                       // AMEE can tell you what this should be for your data.

        // Get category
        AmeeDataCategory cat = AmeeObjectFactory.getInstance().getDataCategory(dataCategory);
        // Set options, to set item values
        List<Choice> values = new ArrayList<Choice>();
        // itemName is required for all items
        values.add(new Choice("itemName", "Tank"));
        // These are fields specific to the data item you are creating
        values.add(new Choice("weight", "5000"));
        values.add(new Choice("lifespan", "15"));
        // Create the item
        AmeeDataItem item = cat.addDataItem(dataItemDefinitionUid, values);
        System.out.println("Created new item OK");
        // Print data
        System.out.println("---------------------");
        System.out.print("Name: ");
        System.out.println(item.getName());
        System.out.print("Path: ");
        System.out.println(item.getUri());
        System.out.print("Label: ");
        System.out.println(item.getLabel());
        System.out.print("UID: ");
        System.out.println(item.getUid());
        System.out.println("Values:");
        for (AmeeValue v : item.getValues()) {
            System.out.print("  - ");
            System.out.print(v.getName());
            System.out.print(": ");
            System.out.println(v.getValue());
        }
    }
}

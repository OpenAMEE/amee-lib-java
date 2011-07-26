/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.amee.client.model.base.AmeeCategory;
import com.amee.client.model.base.AmeeItem;
import com.amee.client.model.base.AmeeObjectReference;
import com.amee.client.model.base.AmeeObjectType;
import com.amee.client.model.base.AmeeValue;
import com.amee.client.model.data.AmeeDataCategory;
import com.amee.client.model.data.AmeeDataItem;
import com.amee.client.model.data.AmeeDrillDown;
import com.amee.client.model.profile.AmeeProfile;
import com.amee.client.model.profile.AmeeProfileCategory;
import com.amee.client.model.profile.AmeeProfileItem;
import com.amee.client.service.AmeeContext;
import com.amee.client.service.AmeeObjectFactory;
import com.amee.client.util.Choice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AmeeTest {
    
    public static void main(String[] args) throws AmeeException {
        long start = Calendar.getInstance().getTimeInMillis();
        System.out.println("Start: " + start);
        AmeeContext.getInstance().setUsername("USER");
        AmeeContext.getInstance().setPassword("PASS");
        AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");
        // AmeeObjectFactory.getInstance().setCache(null);
        // testObjectReference();
        // testDataCategoryA();
        // testDataCategoryPagination();
        // testDataCategoryTree();
        // testDrillDown();
        // testAuthRenewal(null);
        // testNewProfile();
        // testProfileCategoryPagination();
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println("End: " + end);
        System.out.println("Duration: " + (end - start));
    }
    
    private static long startAR=-1;
    public static void testAuthRenewal(final String puid){
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JButton idleTimeButton = new JButton("Print idle time");
        final JTextArea jta = new JTextArea();
        final JButton sendButton = new JButton("Send request");
        jf.getContentPane().add(idleTimeButton,BorderLayout.NORTH);
        jf.getContentPane().add(sendButton,BorderLayout.SOUTH);
        ActionListener al = new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                AmeeObjectFactory objectFactory=null;
                long currentTime = Calendar.getInstance().getTimeInMillis();
                long diff = currentTime-startAR;
                System.err.println("Idle time (sec) = "+diff/1000);
                if(evt.getSource().equals(sendButton)){
                    startAR = currentTime;
                    System.err.println("======= "+startAR);
                    if(objectFactory==null)
                        objectFactory = AmeeObjectFactory.getInstance();
                    try {
                        AmeeProfile profile = null;
                        if(puid!=null)
                            profile = objectFactory.getProfile(puid);
                        else
                            profile = objectFactory.addProfile();
                        System.err.println("profile UID = "+profile.getUid());
                    } catch (AmeeException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        idleTimeButton.addActionListener(al);
        sendButton.addActionListener(al);
        jf.pack();
        jf.setVisible(true);
    }
    
    public static void testDataCategoryA() throws AmeeException {
        AmeeObjectFactory ameeObjectFactory = AmeeObjectFactory.getInstance();
        AmeeDataCategory dataCategory = ameeObjectFactory.getDataCategory("home/appliances/computers/generic");
        while (dataCategory != null) {
            printCategory(dataCategory);
            dataCategory = (AmeeDataCategory) dataCategory.getParent();
        }
    }
    
    public static void testDataCategoryPagination() throws AmeeException {
        AmeeObjectFactory ameeObjectFactory = AmeeObjectFactory.getInstance();
        AmeeDataCategory dataCategory = ameeObjectFactory.getDataCategory("home/appliances/kitchen/generic");
        for (int i = 1; i <= dataCategory.getItemsPager().getLastPage(); i++) {
            dataCategory.setPage(i);
            dataCategory.fetch();
            System.out.println("Page: " + i);
            printCategory(dataCategory);
        }
    }
    
    public static void testDataCategoryTree() throws AmeeException {
        AmeeObjectFactory ameeObjectFactory = AmeeObjectFactory.getInstance();
        printCategoryTree(ameeObjectFactory.getDataCategoryRoot());
    }
    
    public static void printCategoryTree(AmeeCategory category) throws AmeeException {
        String out;
        AmeeProfileCategory profileCategory;
        for (AmeeCategory childCategory : category.getCategories()) {
            out = childCategory.getUri();
            if (childCategory instanceof AmeeProfileCategory) {
                profileCategory = (AmeeProfileCategory) childCategory;
                out = out + " (Amount=" + profileCategory.getAmount() + ")";
            }
            System.out.println(out);
            printCategoryTree(childCategory);
        }
        for (AmeeItem childItem : category.getItems()) {
            System.out.println(childItem.getUri() + "=" + childItem.getLabel());
            for (AmeeValue value : childItem.getValues()) {
                System.out.println(value.getUri() + "=" + value.getValue());
                value.fetch();
            }
        }
    }
    
    public static void printCategory(AmeeCategory category) throws AmeeException {
        System.out.println(category.getUri());
        for (AmeeCategory childCategory : category.getCategories()) {
            System.out.println(childCategory.getUri());
        }
        for (AmeeItem childItem : category.getItems()) {
            System.out.println(childItem.getUri() + "=" + childItem.getLabel());
            for (AmeeValue value : childItem.getValues()) {
                System.out.println(value.getUri() + "=" + value.getValue());
                value.fetch();
            }
            childItem.fetch();
        }
    }
    
    public static void testDrillDown() throws AmeeException {
        AmeeObjectFactory objectFactory = AmeeObjectFactory.getInstance();
        AmeeDrillDown ameeDrillDown = objectFactory.getDrillDown("home/appliances/computers/generic/drill");
        while (ameeDrillDown.hasChoices()) {
            ameeDrillDown.addSelection(ameeDrillDown.getChoiceName(), ameeDrillDown.getChoices().get(0).getValue());
            ameeDrillDown.fetch();
        }
        AmeeDataItem ameeDataItem = ameeDrillDown.getDataItem();
        if (ameeDataItem != null) {
            System.out.println("DataItem Label: " + ameeDataItem.getLabel());
            System.out.println("DataItem UID: " + ameeDataItem.getUid());
        }
    }
    
    public static void testExistingProfile(String profileUid) throws AmeeException {
        AmeeObjectFactory objectFactory = AmeeObjectFactory.getInstance();
        AmeeProfile profile = objectFactory.getProfile(profileUid);
        //printCategoryTree(profile);
    }
    
    public static void testNewProfile() throws AmeeException {
        List<Choice> values;
        AmeeObjectFactory objectFactory = AmeeObjectFactory.getInstance();
        // create the profile
        AmeeProfile profile = objectFactory.addProfile();
        // get a category
        AmeeProfileCategory profileCategory = objectFactory.getProfileCategory(profile, "home/appliances/computers/generic");
        profileCategory.getItemsPager().goNextPage();
        // create an item
        values = new ArrayList<Choice>();
        values.add(new Choice("numberOwned", "20"));
        AmeeProfileItem profileItem = profileCategory.addProfileItem("B32624F8CD5F", values);
        // print the profile
        System.out.println("*** After adding an item ***");
        printCategoryTree(profile);
        // update the value
        AmeeValue value = profileItem.getValue("numberOwned");
        value.setValue("30");
        value.save();
        // print the profile
        System.out.println("*** After updating a value directly ***");
        printCategoryTree(profile);
        // update the item
        values = new ArrayList<Choice>();
        values.add(new Choice("numberOwned", "40"));
        profileItem.setValues(values);
        // print the profile
        System.out.println("*** After updating values via in item ***");
        printCategoryTree(profile);
        // delete the item
        profileItem.delete();
        // print the profile
        System.out.println("*** After deleting the item ***");
        printCategoryTree(profile);
        // delete the profile
        profile.delete();
        System.out.println("*** done ***");
    }
    
    public static void testProfileCategoryPagination() throws AmeeException {
        AmeeObjectFactory objectFactory = AmeeObjectFactory.getInstance();
        AmeeProfile profile = objectFactory.addProfile();
        AmeeProfileCategory profileCategory = objectFactory.getProfileCategory(profile, "home/appliances/kitchen/generic");
        AmeeDrillDown drillDown = objectFactory.getDrillDown("home/appliances/kitchen/generic/drill");
        testProfileCategoryPagination(profileCategory, drillDown);
        for (int i = 1; i <= profileCategory.getItemsPager().getLastPage(); i++) {
            profileCategory.setPage(i);
            profileCategory.fetch();
            System.out.println("Page: " + i);
            printCategoryTree(profileCategory);
        }
    }
    
    public static void testProfileCategoryPagination(AmeeProfileCategory profileCategory, AmeeDrillDown drillDown) throws AmeeException {
        String dataItemUid = drillDown.getDataItemPathSegment();
        AmeeDrillDown newDrillDown;
        if (dataItemUid == null) {
            // drill further
            for (Choice choice: drillDown.getChoices()) {
                newDrillDown = (AmeeDrillDown) drillDown.getCopy();
                newDrillDown.addSelection(drillDown.getChoiceName(), choice.getValue());
                newDrillDown.fetch();
                testProfileCategoryPagination(profileCategory, newDrillDown);
            }
        } else {
            // stop drilling - add profile item
            System.out.println("Adding: " + dataItemUid);
            profileCategory.addProfileItem(dataItemUid);
        }
    }
    
    public static void testObjectReference() throws AmeeException {
        printObjectReference(new AmeeObjectReference("", AmeeObjectType.UNKNOWN));
        printObjectReference(new AmeeObjectReference("/", AmeeObjectType.UNKNOWN));
        printObjectReference(new AmeeObjectReference("/hello.txt", AmeeObjectType.UNKNOWN));
        printObjectReference(new AmeeObjectReference("big/hello.txt", AmeeObjectType.UNKNOWN));
        printObjectReference(new AmeeObjectReference("very/big/hello.txt", AmeeObjectType.UNKNOWN));
        printObjectReference(new AmeeObjectReference("/very/big/hello.txt", AmeeObjectType.UNKNOWN));
        printObjectReference(new AmeeObjectReference("/very/big/", AmeeObjectType.UNKNOWN));
        printObjectReference(new AmeeObjectReference("/very/", AmeeObjectType.UNKNOWN));
        printObjectReference(new AmeeObjectReference("very", AmeeObjectType.UNKNOWN));
        printObjectReference(new AmeeObjectReference("very/big", AmeeObjectType.UNKNOWN));
    }
    
    public static void printObjectReference(AmeeObjectReference ref) {
        System.out.println("Path: " + ref.getUri());
        System.out.println("  LocalPath: " + ref.getLocalPart());
        System.out.println("  ParentPath: " + ref.getParentUri());
    }
}
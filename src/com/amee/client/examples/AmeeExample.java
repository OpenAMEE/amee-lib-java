/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.examples;

import com.amee.client.*;
import com.amee.client.model.data.AmeeDataItem;
import com.amee.client.model.profile.AmeeProfile;
import com.amee.client.model.profile.AmeeProfileCategory;
import com.amee.client.model.profile.AmeeProfileItem;
import com.amee.client.service.AmeeContext;
import com.amee.client.service.AmeeObjectFactory;
import com.amee.client.util.Choice;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nalu
 */
public abstract class AmeeExample extends JFrame {
    protected AmeeObjectFactory objectFactory = AmeeObjectFactory.getInstance();
    protected AmeeProfile ameeProfile;
    protected JLabel resultLabel = new JLabel("Make selection for fuel and size above. Distance is set at 1000 km.");
    protected String path;
    
    public static void setLoginDetails(String user, String pass, String url){
        //NOTE: Please replace the following with your login details
        AmeeContext.getInstance().setUsername(user);
        AmeeContext.getInstance().setPassword(pass);
        AmeeContext.getInstance().setBaseUrl(url);        
    }
    
    /**
     * Gets or creates the profile and setups up the UI
     */
    public AmeeExample(String profileUID, String path) {
        this.path=path;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("AMEE simple example");
        try { //Try using this profile
            ameeProfile = objectFactory.getProfile(profileUID);
        } catch (AmeeException ex) {
            try {
                //ex.printStackTrace();
                ameeProfile = objectFactory.addProfile();
                System.out.println("CREATING NEW PROFILE");
            } catch (AmeeException ex2) {
                ex2.printStackTrace();
            }
        }
        
        try {//If it doesn't exist, create one - see NOTE above
            System.out.println("Profile UID = "+ameeProfile.getUid());
            addComboBoxes();
        } catch (AmeeException ex) {
            ex.printStackTrace();
        }
        getContentPane().add(resultLabel,java.awt.BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    
    protected void createProfileItem(AmeeDataItem ameeDataItem) throws AmeeException {
        System.out.println("duid="+ameeDataItem.getUid());
        AmeeProfileCategory profileCategory = objectFactory.getProfileCategory(ameeProfile, path);
        profileCategory.fetch();//the fetch() method forces a call to the AMEE API
        //Delete all items found in the profileCategory
        //trying to create an identical item of the same (default) name will fail
        for (AmeeProfileItem item : profileCategory.getProfileItems()) {
            item.fetch();
            item.delete();
        }
        //Now set the distance driven per month to 1000 - hardwired for simplicity, but could easily get from user
        List<Choice> values = new ArrayList<Choice>();
        values.add(new Choice("distance", "1000"));
        values.add(new Choice("distancePerUnit", "month"));
        AmeeProfileItem profileItem = profileCategory.addProfileItem(ameeDataItem, values);
        BigDecimal result = profileItem.getAmount();
        System.out.println("amount="+profileItem.getAmount());
        resultLabel.setText("1000 km per month emits "+result+" "+profileItem.getAmountUnit());
    }
    
    public abstract void addComboBoxes() throws AmeeException;
    public abstract void processComboSelection(java.awt.event.ActionEvent evt) throws AmeeException;
}


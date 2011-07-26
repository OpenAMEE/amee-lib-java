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
import com.amee.client.model.profile.*;

public class CreateProfile {

    public static void main(String[] args) throws AmeeException {
        
      // Set up AMEE connection
        AmeeContext.getInstance().setUsername("username-here");
        AmeeContext.getInstance().setPassword("password-here");
        AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");

        // Create a new profile
        AmeeProfile profile = AmeeObjectFactory.getInstance().addProfile();
        System.out.print(profile.getUid());
        System.out.println(" created");
    }
}

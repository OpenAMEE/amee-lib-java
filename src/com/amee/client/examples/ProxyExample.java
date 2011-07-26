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

import org.apache.commons.httpclient.auth.*;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

public class ProxyExample {

    public static void main(String[] args) throws AmeeException {
        
        // Set up AMEE connection
        AmeeContext.getInstance().setUsername("username-here");
        AmeeContext.getInstance().setPassword("password-here");
        AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");

        // Configure proxy
        String proxy_host = "proxy.mydomain.com";
        Integer proxy_port = 3128;
        String proxy_username = "proxy-username";
        String proxy_password = "proxy-password";
        AmeeContext.getInstance().getClient().getHostConfiguration().setProxy(proxy_host, proxy_port);
        AmeeContext.getInstance().getClient().getState().setProxyCredentials(new AuthScope(proxy_host, proxy_port), new UsernamePasswordCredentials(proxy_username, proxy_password));

        // Get category
        String dataCategory = "home/energy/electricity";
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
        System.out.println("Items:");
        for (AmeeDataItem i : cat.getDataItems()) {
            System.out.print("  - ");
            System.out.print(i.getUri());
            System.out.print(" (");
            System.out.print(i.getLabel());
            System.out.println(")");
        }
    }
}

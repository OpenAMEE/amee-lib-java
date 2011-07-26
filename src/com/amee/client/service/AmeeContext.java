/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.service;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

import java.io.Serializable;

public class AmeeContext implements Serializable {


    public static String BASE_URL;
    public static Header ACCEPT_JSON = new Header("Accept", "application/json");
    public static Integer ITEMS_PER_PAGE = 100;
    
    private String baseUrl = BASE_URL;
    private Integer itemsPerPage = ITEMS_PER_PAGE;
    private HttpClient client = null;
    private String username = null;
    private String password = null;
    private String authToken = null;
    private APIVersion apiVersion;

    private static AmeeContext instance = new AmeeContext();

    public static AmeeContext getInstance() {
        return instance;
    }

    private AmeeContext() {
        super();
        setClient(new HttpClient());
    }

    public GetMethod getGetMethod(String path) {
        return (GetMethod) prepareHttpMethod(new GetMethod(getBaseUrl() + preparePath(path)));
    }

    public PostMethod getPostMethod(String path) {
        return (PostMethod) prepareHttpMethod(new PostMethod(getBaseUrl() + preparePath(path)));
    }

    public PutMethod getPutMethod(String path) {
        return (PutMethod) prepareHttpMethod(new PutMethod(getBaseUrl() + preparePath(path)));
    }

    public DeleteMethod getDeleteMethod(String path) {
        return (DeleteMethod) prepareHttpMethod(new DeleteMethod(getBaseUrl() + preparePath(path)));
    }

    public HttpMethod prepareHttpMethod(HttpMethod method) {
        method.addRequestHeader(ACCEPT_JSON);
        method.addRequestHeader(new Header("ItemsPerPage",Integer.toString(itemsPerPage)));
        method.setFollowRedirects(false);
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        if (getAuthToken() != null) {
            method.removeRequestHeader("authToken");//re-auth fix
            method.addRequestHeader("authToken", getAuthToken());
        }
        return method;
    }

    public static String preparePath(String path) {
        if (path == null) {
            path = "/";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    public boolean isValid() {
        return (getUsername() != null) && (getPassword() != null);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            baseUrl = BASE_URL;
        }
        this.baseUrl = baseUrl;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        if (itemsPerPage == null) {
            itemsPerPage = ITEMS_PER_PAGE;
        }
        this.itemsPerPage = itemsPerPage;
    }

    public HttpClient getClient() {
        return client;
    }

    public void setClient(HttpClient client) {
        if (client == null) {
            client = new HttpClient();
        }
        this.client = client;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setAPIVersion(String apiVersion) {
        this.apiVersion = APIVersion.fromString(apiVersion);
    }

    public APIVersion getAPIVersion() {
        return apiVersion;
    }
}
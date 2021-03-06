/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.service;

import com.amee.client.AmeeException;
import com.amee.client.util.Choice;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.net.URL;

public class AmeeInterface implements Serializable {
    
    private final static Log log = LogFactory.getLog(AmeeInterface.class);
    
    public final static int ATTEMPTS = 1;
    public final static int SUCCESS_OK = 200;
    public final static int SUCCESS_CREATED = 201;
    public final static int CLIENT_ERROR_BAD_REQUEST = 400;
    public final static int CLIENT_ERROR_UNAUTHORIZED = 401;
    public final static int CLIENT_ERROR_FORBIDDEN = 403;
    public final static int CLIENT_ERROR_NOT_FOUND = 404;
    public final static int CLIENT_ERROR_METHOD_NOT_ALLOWED = 405;
    public final static int SERVER_ERROR_INTERNAL = 500;
    
    // TODO: inject
    private static AmeeContext ameeContext = AmeeContext.getInstance();
    
    private static AmeeInterface instance = new AmeeInterface();

    public static AmeeInterface getInstance() {
        return instance;
    }
    
    private AmeeInterface() {
        super();
    }
    
    // *** Authentication ***
    
    public boolean signIn() throws AmeeException {
        ameeContext.setAuthToken(null);
        if (ameeContext.isValid()) {
            PostMethod post = ameeContext.getPostMethod("/auth/signIn");
            post.addParameter("username", ameeContext.getUsername());
            post.addParameter("password", ameeContext.getPassword());

            try {
                ameeContext.getClient().executeMethod(post);
                Header headers[] = post.getResponseHeaders("authToken");
                if (headers.length > 0) {
                    ameeContext.setAuthToken(headers[0].getValue());
                    JSONObject json = new JSONObject(post.getResponseBodyAsString());
                    String apiVersion = json.getJSONObject("auth").getString("apiVersion");
                    ameeContext.setAPIVersion(apiVersion);
                }

            } catch (JSONException e) {
                throw new AmeeException("Caught JSONException: " + e.getMessage());
            } catch (IOException e) {
                throw new AmeeException("Caught IOException: " + e.getMessage());
            } finally {
                post.releaseConnection();
            }
        }
        return ameeContext.getAuthToken() != null;
    }
    
    private void checkAuthenticated() throws AmeeException {
        if ((ameeContext.getAuthToken() == null)) {
            if (!signIn()) {
                throw new AmeeException("Could not authenticate.");
            }
        }
    }
    
    // *** API Calls ***
    
    public String getAmeeResource(String url) throws AmeeException {
      return getAmeeResource(url, null);
    }

    public String getAmeeResource(String url, List<Choice> parameters) throws AmeeException {
        GetMethod get = null;
        checkAuthenticated();
        try {
            // prepare method
            get = ameeContext.getGetMethod(url);
            // execute method and allow retries
            execute(get);
            return get.getResponseBodyAsString();
        } catch (IOException e) {
            throw new AmeeException("Caught IOException: " + e.getMessage());
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
    }
    
    public String postAmeeResource(String url, List<Choice> parameters) throws AmeeException {
        return postOrPutAmeeResource(url, parameters, false);
    }
    
    public String putAmeeResource(String url, List<Choice> parameters) throws AmeeException {
        return postOrPutAmeeResource(url, parameters, true);
    }
    
    public String postOrPutAmeeResource(String url, List<Choice> parameters, boolean tunnelPut) throws AmeeException {
        PostMethod post = null;
        checkAuthenticated();
        try {
            // prepare method
            if (tunnelPut) {
                if (url.indexOf("?") == -1) {
                    url = url + "?";
                } else {
                    url = url + "&";
                }
                url = url + "method=put";
            }
            post = ameeContext.getPostMethod(url);
            for (Choice parameter : parameters) {
                post.addParameter(parameter.getName(), parameter.getValue());
            }
            // execute method and allow retries
            execute(post);

            // V2 POST behaviour - need to get the created resource 
            Header location = post.getResponseHeader("Location");
            if (location != null) {
                String resourceUrl = new URL(location.getValue()).getPath();
                // Add the returnUnit, returnPerUnit parameters to the query string if set in the POST choices.
                String returnUnits = "";
                //TODO - could be more elegant - perhaps pass these into an overloaded version of this method
                for (Choice parameter : parameters) {
                    if ("returnUnit".equals(parameter.getName()))
                        returnUnits += "&returnUnit=" + parameter.getValue();
                    if ("returnPerUnit".equals(parameter.getName()))
                        returnUnits += "&returnPerUnit=" + parameter.getValue();
                }
                if (returnUnits.length() > 0) {
                    resourceUrl += "?" + returnUnits.substring(1);
                }
                return getAmeeResource(resourceUrl);
            }

            // V2 PUT behaviour - need to get the modified resource
            if ( (post.getStatusCode() == SUCCESS_OK) && tunnelPut) {
                return getAmeeResource(url);
            }

            return post.getResponseBodyAsString();
        } catch (IOException e) {
            throw new AmeeException("Caught IOException: " + e.getMessage());
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
    }
    
    public void deleteAmeeResource(String url) throws AmeeException {
        DeleteMethod delete = null;
        checkAuthenticated();
        try {
            // prepare method
            delete = ameeContext.getDeleteMethod(url);
            // execute method and allow retries
            execute(delete);
        } catch (IOException e) {
            throw new AmeeException("Caught IOException: " + e.getMessage());
        } finally {
            if (delete != null) {
                delete.releaseConnection();
            }
        }
    }
    
    // *** Utility ***
    
    private void execute(HttpMethodBase method) throws IOException, AmeeException {
        for (int i = 0; i < ATTEMPTS; i++) {
            ameeContext.getClient().executeMethod(method);
            switch (method.getStatusCode()) {
                case SUCCESS_OK:
                    // done, don't retry
                    return;
                case SUCCESS_CREATED:
                    // done, don't retry
                    return;
                case CLIENT_ERROR_BAD_REQUEST:
                    // Bad request - check if it's because it's a duplicate and give a more useful error if so.
                    // don't retry
                    if (method.getResponseBodyAsString().matches(".*duplicate resource.*")) {
                      throw new AmeeException("The request could not be processed, as it would have created a duplicate resource (" + method.getURI() + ").");
                    }
                    else {
                      throw new AmeeException("The request could not be understood (" + method.getURI() + ").");
                    }
                case CLIENT_ERROR_NOT_FOUND:
                    // don't retry
                    throw new AmeeException("The resource could not be found (" + method.getURI() + ").");
                case CLIENT_ERROR_FORBIDDEN:
                    // don't retry
                    throw new AmeeException("Access to this resource is forbidden (" + method.getURI() + ").");
                case CLIENT_ERROR_METHOD_NOT_ALLOWED:
                    // don't retry
                    throw new AmeeException("Method is not allowed (" + method.getURI() + ").");
                case CLIENT_ERROR_UNAUTHORIZED:
                    // authentication may have expired, try authenticating again
                    if (!signIn()) {
                        throw new AmeeException("Could not authenticate (" + method.getURI() + ").");
                    }
                    ameeContext.prepareHttpMethod(method);//re-auth fix
                    // allow retries
                    break;
                default:
                    // allow retries - like with 500s or something else
                    break;
            }
        }
        throw new AmeeException("Could not execute request (" + method.getURI() + ").");
    }
}
/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.util;

// TODO: make these tolerant of query strings

public class UriUtils {

    public static String getLastPart(String uri) {
        int pos = uri.lastIndexOf("/");
        if (pos >= 0) {
            pos++;
            if (pos < uri.length()) {
                return uri.substring(pos);
            } else {
                uri = "";
            }
        }
        return uri;
    }

    public static String getParentUri(String uri) {
        String s = "";
        if (uri != null) {
            int i = uri.lastIndexOf("/");
            if (i >= 0) {
                return uri.substring(0, i);
            }
        }
        return s;
    }

    public static String getUriExceptFirstPart(String uri) {
        return getUriExceptXParts(uri, 1);
    }

    public static String getUriExceptFirstTwoParts(String uri) {
        return getUriExceptXParts(uri, 2);
    }

    public static String getUriExceptXParts(String uri, int pos) {
        String s = "";
        String[] parts = uri.split("/");
        for (int i = pos; i < parts.length; i++) {
            s = s + parts[i];
            if (i < parts.length - 1) {
                s = s + "/";
            }
        }
        return s;
    }

    public static String getUriFirstTwoParts(String uri) {
        String s = null;
        String[] parts = uri.split("/");
        if (parts.length >= 2) {
            s = parts[0] + "/" + parts[1];
        }
        return s;
    }
}

/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client;

public class AmeeException extends Exception {

    public AmeeException() {
        super();
    }

    public AmeeException(String message) {
        super(message);
    }

    public AmeeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmeeException(Throwable cause) {
        super(cause);
    }
}

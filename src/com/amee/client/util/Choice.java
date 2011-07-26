/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Forked from com.jellymold.sheet.Choice. Should be merged back at some point.
 */
public class Choice implements Serializable, Comparable {

    private String name = "";
    private String value = "";

    private Choice() {
        super();
    }

    public Choice(String value) {
        this();
        setName(value);
        setValue(value);
    }

    public Choice(String name, String value) {
        this();
        setName(name);
        setValue(value);
    }

    public Choice(String name, Integer value) {
        this();
        setName(name);
        setValue(value.toString());
    }

    public Choice(String name, Double value) {
        this();
        setName(name);
        setValue(value.toString());
    }

    public boolean equals(Object o) {
        Choice other = (Choice) o;
        return getName().equalsIgnoreCase(other.getName());
    }

    public int compareTo(Object o) {
        Choice other = (Choice) o;
        return getName().compareToIgnoreCase(other.getName());
    }

    public int hashCode() {
        return getName().toLowerCase().hashCode();
    }

    public String toString() {
        return getName();
    }

    public static Choice parseNameAndValue(String nameAndValue) {
        Choice choice = new Choice();
        if (nameAndValue != null) {
            String[] arr = nameAndValue.trim().split("=");
            if (arr.length > 1) {
                choice.setName(arr[0]);
                choice.setValue(arr[1]);
            } else if (arr.length > 0) {
                choice.setName(arr[0]);
                choice.setValue(arr[0]);
            }
        }
        return choice;
    }

    public static List<Choice> parseChoices(String c) {
        List<Choice> choices = new ArrayList<Choice>();
        if (c != null) {
            String[] arr = c.split(",");
            for (String s : arr) {
                choices.add(Choice.parseNameAndValue(s));
            }
        }
        return choices;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
    }

    public String getValue() {
        return value;
    }

    private void setValue(String value) {
        if (value != null) {
            this.value = value.trim();
        }
    }
}
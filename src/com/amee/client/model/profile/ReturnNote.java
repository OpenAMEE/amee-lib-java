/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.amee.client.model.profile;

/**
 *
 * @author james
 */
public class ReturnNote {

    protected String name;
    protected String note;

    public ReturnNote(String _name, String _note) {
        name = _name;
        note = _note;
    }


    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

}

/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.model.profile;

import com.amee.client.AmeeException;
import com.amee.client.model.base.*;
import com.amee.client.model.data.AmeeDataItem;
import com.amee.client.service.AmeeObjectFactory;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

// TODO: code here to do with dates should be moved elsewhere

public class AmeeProfileItem extends AmeeItem implements Serializable, HasResult {

    private AmeeObjectReference profileRef;
    private AmeeObjectReference dataItemRef;

    private Date validFrom = Calendar.getInstance().getTime();
    private Boolean end = false;

    private static final DateTimeFormatter FMT = ISODateTimeFormat.dateTimeNoMillis();
    private Date startDate = Calendar.getInstance().getTime();
    private Date endDate;

    private ArrayList<ReturnValue> returnValues = new ArrayList<ReturnValue>();
    private ArrayList<ReturnNote> returnNotes = new ArrayList<ReturnNote>();

    public AmeeProfileItem() {
        super();
    }

    public AmeeProfileItem(AmeeObjectReference ref) {
        super(ref);
    }

    public AmeeProfileItem(String path, AmeeObjectType objectType) {
        super(path, objectType);
    }

    public void populate(AmeeProfileItem copy) {
        super.populate(copy);
        copy.setProfileRef(profileRef);
        copy.setDataItemRef(dataItemRef);

        //TODO - V1
        copy.setValidFrom(validFrom);
        copy.setEnd(end);

        //TODO - V2
        copy.setStartDate(startDate);
        copy.setEndDate(endDate);
        copy.returnValues = returnValues;
        copy.returnNotes = returnNotes;
    }

    public AmeeObject getCopy() {
        AmeeProfileItem copy = new AmeeProfileItem();
        populate(copy);
        return copy;
    }

    public AmeeDataItem getDataItem() throws AmeeException {
        AmeeDataItem dataItem = null;
        if (getDataItemRef() != null) {
            dataItem = (AmeeDataItem) AmeeObjectFactory.getInstance().getObject(getDataItemRef());
        }
        return dataItem;
    }

    public void setParentRef() {
        setParentRef(new AmeeObjectReference(getObjectReference().getParentUri(), AmeeObjectType.PROFILE_CATEGORY));
    }

    public AmeeObjectReference getProfileRef() {
        if (profileRef == null) {
            setProfileRef();
        }
        return profileRef;
    }

    public void setProfileRef(AmeeObjectReference profileRef) {
        if (profileRef != null) {
            this.profileRef = profileRef;
        }
    }

    public void setProfileRef() {
        String ref = getObjectReference().getUriFirstTwoParts();
        if (ref != null) {
            setProfileRef(new AmeeObjectReference(ref, AmeeObjectType.PROFILE));
        }
    }

    public AmeeObjectReference getDataItemRef() {
        return dataItemRef;
    }

    public void setDataItemRef(AmeeObjectReference dataItemRef) {
        if (dataItemRef != null) {
            this.dataItemRef = dataItemRef;
        }
    }

    //TODO - V1
    public Date getValidFrom() {
        return validFrom;
    }

    //TODO - V1
    public String getValidFromFormatted() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(getValidFrom());
    }

    //TODO - V1
    public void setValidFrom(Date validFrom) {
        if (validFrom != null) {
            this.validFrom = validFrom;
        } else {
            setValidFrom();
        }
    }

    //TODO - V1
    public void setValidFrom(String validFromStr) {
        setValidFrom(getFullDate(validFromStr));
    }

    //TODO - V1
    public void setValidFrom() {
        Calendar validFromCal = Calendar.getInstance();
        int year = validFromCal.get(Calendar.YEAR);
        int month = validFromCal.get(Calendar.MONTH);
        validFromCal.clear();
        validFromCal.set(year, month, 1); // first of the month
        setValidFrom(validFromCal.getTime());
    }

    //TODO - V1
    public static Date getFullDate(String date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            try {
                return dateFormat.parse(date);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    //TODO - V1
    public Boolean getEnd() {
        return end;
    }

    //TODO - V1
    public void setEnd(boolean end) {
        this.end = end;
    }

    //TODO - V1
    public void setEnd(String endStr) {
        setEnd(Boolean.valueOf(endStr));
    }

    //TODO - V2
    public Date getStartDate() {
        return startDate;
    }

    //TODO - V2
    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = startDate;
        } else {
            this.startDate = new Date();
        }
    }

    //TODO - V2
    public void setStartDate(String startDateStr) throws IllegalArgumentException {
        if (startDateStr == null || startDateStr.length() == 0)
            return;
        setStartDate(FMT.parseDateTime(startDateStr).toDate());
    }

    //TODO - V2
    public Date getEndDate() {
        return endDate;
    }

    //TODO - V2
    public void setEndDate(Date endDate) {
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

    //TODO - V2
    public void setEndDate(String endDateStr) throws IllegalArgumentException {
        if (endDateStr == null || endDateStr.length() == 0)
            return;
        setEndDate(FMT.parseDateTime(endDateStr).toDate());
    }

    @Deprecated
    public BigDecimal getAmount() throws AmeeException {
        return getDefaultReturnValue().getValue();
    }

    @Deprecated
    public String getAmountUnit() throws AmeeException {
        return getDefaultReturnValue().getUnit();
    }

    public void addReturnValue(ReturnValue value) {
        returnValues.add(value);
    }

    public ArrayList<ReturnValue> getReturnValues() throws AmeeException {
        return returnValues;
    }

    public ReturnValue getDefaultReturnValue() throws AmeeException {
        for (ReturnValue value: getReturnValues()) {
            if (value.isDefaultValue())
                return value;
        }
        return null;
    }

    public void addNote(ReturnNote note) {
        returnNotes.add(note);
    }

    public ArrayList<ReturnNote> getNotes() throws AmeeException {
        return returnNotes;
    }

}
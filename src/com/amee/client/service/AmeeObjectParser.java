package com.amee.client.service;

import com.amee.client.AmeeException;
import com.amee.client.model.base.AmeeItem;
import com.amee.client.model.base.AmeeObject;
import com.amee.client.model.base.AmeeValue;
import com.amee.client.model.data.AmeeDataCategory;
import com.amee.client.model.data.AmeeDrillDown;
import com.amee.client.model.profile.AmeeProfileCategory;
import com.amee.client.model.profile.AmeeProfile;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;


public abstract class AmeeObjectParser {

    protected void parse(AmeeObject ameeObject, String s) throws AmeeException {
        try {
            parse(ameeObject, new JSONObject(s));
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }

    protected void parse(AmeeObject object, JSONObject jsonObj) throws AmeeException {
        switch (object.getObjectType()) {
            case DATA_CATEGORY:
                parseDataCategory((AmeeDataCategory) object, jsonObj);
                break;
            case PROFILE_CATEGORY:
                parseProfileCategory((AmeeProfileCategory) object, jsonObj);
                break;
            case DATA_ITEM:
            case PROFILE_ITEM:
                parseItem((AmeeItem) object, jsonObj);
                break;
            case DRILL_DOWN:
                parseDrillDown((AmeeDrillDown) object, jsonObj);
                break;
            case PROFILE:
                parseProfile((AmeeProfile) object, jsonObj);
                break;
            case VALUE:
                parseValue((AmeeValue) object, jsonObj);
                break;
            case UNKNOWN:
            default:
                break;
        }
    }

    protected abstract void parseValue(AmeeValue value, JSONObject jsonObject) throws AmeeException;

    protected abstract void parseItem(AmeeItem item, JSONObject jsonObj) throws AmeeException;

    protected abstract void parseDataCategory(AmeeDataCategory dataCategory, JSONObject json) throws AmeeException;

    protected abstract void parseProfileCategory(AmeeProfileCategory profileCategory, JSONObject json) throws AmeeException;

    protected abstract void parseProfile(AmeeProfile profile, JSONObject json) throws AmeeException;

    // AmeeDrillDown parsing
    public void parseDrillDown(AmeeDrillDown drillDown, JSONObject object) throws AmeeException {
        try {
            // load choice info
            parseDrillDownChoiceInfo(drillDown, object);
            // load selection info
            parseDrillDownSelectionInfo(drillDown, object);
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
        drillDown.setFetched(true);
    }

    private void parseDrillDownChoiceInfo(AmeeDrillDown drillDown, JSONObject object) throws JSONException {
        JSONObject choice;
        JSONObject choicesWrapper = object.getJSONObject("choices");
        drillDown.setChoiceName(choicesWrapper.getString("name"));
        JSONArray choices = choicesWrapper.getJSONArray("choices");
        drillDown.clearChoices();
        for (int i = 0; i < choices.length(); i++) {
            choice = choices.getJSONObject(i);
            drillDown.addChoice(choice.getString("name"), choice.getString("value"));
        }
    }

    private void parseDrillDownSelectionInfo(AmeeDrillDown drillDown, JSONObject object) throws JSONException {
        JSONArray selections = object.getJSONArray("selections");
        drillDown.clearSelections();
        for (int i = 0; i < selections.length(); i++) {
            JSONObject selectionObj = selections.getJSONObject(i);
            drillDown.addSelection(selectionObj.getString("name"), selectionObj.getString("value"));
        }
    }
}

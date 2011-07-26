package com.amee.client.service;

import com.amee.client.AmeeException;
import com.amee.client.model.base.*;
import com.amee.client.model.data.AmeeDataCategory;
import com.amee.client.model.data.AmeeDataItem;
import com.amee.client.model.profile.AmeeProfile;
import com.amee.client.model.profile.AmeeProfileCategory;
import com.amee.client.model.profile.AmeeProfileItem;
import com.amee.client.model.profile.ReturnNote;
import com.amee.client.model.profile.ReturnValue;
import com.amee.client.util.Pager;
import com.amee.client.util.UriUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AmeeV2ObjectParser extends AmeeObjectParser {

    protected void parseValue(AmeeValue value, JSONObject json) throws AmeeException {
        JSONObject valueObj;
        try {
            valueObj = json.getJSONObject("itemValue");
            value.setUid(valueObj.getString("uid"));
            value.setName(valueObj.getString("name"));
            value.setValue(valueObj.getString("value"));
            value.setUnit(valueObj.getString("unit"));
            value.setPerUnit(valueObj.getString("perUnit"));
            if (json.has("profile")) {
                value.setItemRef(new AmeeObjectReference(value.getParentUri(), AmeeObjectType.PROFILE_ITEM));
            } else {
                value.setItemRef(new AmeeObjectReference(value.getParentUri(), AmeeObjectType.DATA_ITEM));
            }
            value.setFetched(true);
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }

    @Override
    protected void parseItem(AmeeItem item, JSONObject json) throws AmeeException {
        JSONObject itemObj;
        AmeeProfileItem profileItem;
        AmeeDataItem dataItem;
        try {
            if (item instanceof AmeeProfileItem) {
                itemObj = json.getJSONObject("profileItem");
                profileItem = (AmeeProfileItem) item;
                parseReturnValues(profileItem, itemObj.getJSONObject("amounts"));
                profileItem.setStartDate(itemObj.getString("startDate"));
                profileItem.setEndDate(itemObj.getString("endDate"));                

                profileItem.setLabel(""); // TODO: label is missing from API
                String dataItemUri = "data/" + UriUtils.getUriExceptFirstTwoParts(profileItem.getParentUri()) + "/" + itemObj.getJSONObject("dataItem").getString("uid");
                profileItem.setDataItemRef(new AmeeObjectReference(dataItemUri, AmeeObjectType.DATA_ITEM));
            } else {
                itemObj = json.getJSONObject("dataItem");
                dataItem = (AmeeDataItem) item;
                dataItem.setLabel(itemObj.getString("label"));
            }
            parseItemValues(item, itemObj.getJSONArray("itemValues"));
            item.setUid(itemObj.getString("uid"));
            item.setName(itemObj.getString("name"));
            item.setFetched(true);
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }

    @Override
    protected void parseDataCategory(AmeeDataCategory dataCategory, JSONObject json) throws AmeeException {
        AmeeV1ObjectParser p = new AmeeV1ObjectParser();
        p.parseDataCategory(dataCategory, json);
    }
    
    protected void parseReturnValues(AmeeProfileItem profileItem, JSONObject returnObj) throws JSONException {
        JSONArray amountsArr = returnObj.getJSONArray("amount");
        for (int i = 0; i < amountsArr.length(); i++) {
            JSONObject amount = amountsArr.getJSONObject(i);
            profileItem.addReturnValue(
                new ReturnValue(
                    amount.getString("type"),
                    amount.getString("value"),
                    amount.getString("unit"),
                    amount.getString("perUnit"),
                    amount.getBoolean("default")));
        }
        if (returnObj.has("note")) {
            JSONArray notesArr = returnObj.getJSONArray("note");
            for (int i = 0; i < notesArr.length(); i++) {
                JSONObject note = notesArr.getJSONObject(i);
                profileItem.addNote(
                    new ReturnNote(
                        note.getString("type"),
                        note.getString("value")));
            }
        }
    }

    protected void parseItemValues(AmeeItem item, JSONArray valuesArr) throws AmeeException {
        JSONObject valueObj;
        AmeeObjectReference ameeObjectReference;
        AmeeValue value;
        try {
            item.clearValueRefs();
            for (int i = 0; i < valuesArr.length(); i++) {
                valueObj = valuesArr.getJSONObject(i);
                // create reference and add to parent
                ameeObjectReference =
                        new AmeeObjectReference(
                                item.getUri() + "/" + valueObj.getString("path"),
                                AmeeObjectType.VALUE);
                item.addValueRef(ameeObjectReference);
                // look for object in cache
                value = (AmeeValue) AmeeObjectFactory.getInstance().getObject(ameeObjectReference, false);
                if (value == null) {
                    // create object and cache
                    value = new AmeeValue(ameeObjectReference);
                    AmeeObjectFactory.getInstance().addObjectToCache(value);
                }
                // update object
                value.setUid(valueObj.getString("uid"));
                value.setName(valueObj.getString("name"));
                value.setValue(valueObj.getString("value"));
                value.setPerUnit(valueObj.getString("perUnit"));
                value.setUnit(valueObj.getString("unit"));
                value.setItemRef(item.getObjectReference());
                value.setFetched(true);
            }
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }

    protected void parseProfileCategory(AmeeProfileCategory profileCategory, JSONObject json) throws AmeeException {
        JSONObject dataCategoryJson;
        try {
            dataCategoryJson = json.getJSONObject("dataCategory");
            profileCategory.setUid(""); // no UID for Profile Categories
            profileCategory.setName(dataCategoryJson.getString("name"));
            if (json.has("totalAmount")) {
                Object o = json.get("totalAmount");
                if (o instanceof JSONObject) {
                    profileCategory.setAmount(json.getJSONObject("totalAmount").getString("value"));
                    profileCategory.setAmountUnit(json.getJSONObject("totalAmount").getString("unit"));
                } else {
                    profileCategory.setAmount(json.getString("totalAmount"));
                }
            }
            parseCategoryChildren(profileCategory, json);
            profileCategory.setFetched(true);
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }

    protected void parseProfile(AmeeProfile profile, JSONObject json) throws AmeeException {
        JSONObject profileJson;
        try {
            profileJson = json.getJSONObject("profile");
            profile.setUid(profileJson.getString("uid"));
            profile.setName(profileJson.getString("name"));
            parseCategoryChildren(profile, json);
            profile.setFetched(true);
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }

    protected void parseCategoryChildren(AmeeCategory category, JSONObject json) throws AmeeException {
        JSONArray categoriesArr;
        JSONObject pagerObj;
        JSONObject itemsObj;
        JSONArray itemsArr;
        JSONObject childJson;
        AmeeObjectReference ameeObjectReference;
        AmeeCategory childCategory;
        AmeeItem childItem;
        try {
            if (json.has("profileCategories")) {
                category.clearCategoryRefs();
                categoriesArr = json.getJSONArray("profileCategories");
                for (int i = 0; i < categoriesArr.length(); i++) {
                    childJson = categoriesArr.getJSONObject(i);
                    if (childJson.has("dataCategory") ){
                        // create reference and add to parent
                        String path = category.getObjectReference().getPath();
                        if (!path.endsWith("/")) {
                          path += "/";
                        }
                        ameeObjectReference =
                                new AmeeObjectReference(
                                        path + childJson.getJSONObject("dataCategory").getString("path"),
                                        category.getChildCategoryObjectType());
                        category.addCategoryRef(ameeObjectReference);
                        AmeeProfileCategory childProfileCategory = (AmeeProfileCategory) category.getNewChildCategory(ameeObjectReference);
                        parseProfileCategory(childProfileCategory, childJson);
                        AmeeObjectFactory.getInstance().addObjectToCache(childProfileCategory);
                    }
                    else {
                        // create reference and add to parent
                        ameeObjectReference =
                                new AmeeObjectReference(
                                        category.getObjectReference().getPath() + "/" + childJson.getString("path"),
                                        category.getChildCategoryObjectType());
                        category.addCategoryRef(ameeObjectReference);
                        // look for object in cache
                        childCategory = (AmeeCategory) AmeeObjectFactory.getInstance().getObject(ameeObjectReference, false);
                        if (childCategory == null) {
                            // create object, add to cache and update
                            childCategory = category.getNewChildCategory(ameeObjectReference);
                            if (childCategory instanceof AmeeDataCategory) {
                                childCategory.setUid(childJson.getString("uid"));
                            } else if (childCategory instanceof AmeeDataCategory) {
                                childCategory.setUid(""); // no UID for Profile Categories
                            }
                            childCategory.setName(childJson.getString("name"));
                            AmeeObjectFactory.getInstance().addObjectToCache(childCategory);
                        }
                    }
                }
            }

            String s = category.getChildItemObjectType().equals(AmeeObjectType.DATA_ITEM) ? "dataItems" : "profileItems";
            if (json.has(s) && json.get(s) instanceof JSONArray) {
                category.clearItemRefs();
                itemsArr = json.getJSONArray(s);
                for (int i = 0; i < itemsArr.length(); i++) {
                    childJson = itemsArr.getJSONObject(i);
                    // create reference and add to parent
                    ameeObjectReference =
                            new AmeeObjectReference(
                                    category.getObjectReference().getPath() + "/" + childJson.getString("uid"),
                                    category.getChildItemObjectType());
                    category.addItemRef(ameeObjectReference);
                    // look for object in cache
                    childItem = (AmeeItem) AmeeObjectFactory.getInstance().getObject(ameeObjectReference, false);
                    if (childItem == null) {
                        // create object, add to cache and update
                        childItem = category.getNewChildItem(ameeObjectReference);
                        parseCategoryItemProperties(childItem, childJson);
                        childItem.setFetched(true);
                        AmeeObjectFactory.getInstance().addObjectToCache(childItem);
                    }
                }
            }
            if (json.has("pager")) {
                pagerObj = json.getJSONObject("pager");
                if (pagerObj.has("items") && pagerObj.has("itemsPerPage") && pagerObj.has("currentPage")) {
                    category.setPage(pagerObj.getInt("currentPage"));
                    category.setItemsPager(new Pager(pagerObj.getInt("items"), pagerObj.getInt("itemsPerPage"), pagerObj.getInt("currentPage")));
                } else {
                    category.setPage(1);
                    category.setItemsPager(new Pager(0, 10));
                }
            }
        } catch (ClassCastException e) {
            throw new AmeeException("Caught ClassCastException: " + e.getMessage());
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }

    protected void parseCategoryItemProperties(AmeeItem item, JSONObject json) throws AmeeException {
        AmeeObjectReference ameeObjectReference;
        AmeeValue ameeValue;
        String dataItemUri;
        AmeeProfileItem profileItem;
        AmeeDataItem dataItem;
        try {

            // set common values
            item.setUid(json.getString("uid"));

            // set implementation specific values
            if (item instanceof AmeeDataItem) {
                dataItem = (AmeeDataItem) item;
                dataItem.setLabel(json.getString("label"));
            } else if (item instanceof AmeeProfileItem) {
                profileItem = (AmeeProfileItem) item;
                profileItem.setName(json.getString("name"));
                profileItem.setLabel(json.getJSONObject("dataItem").getString("Label"));
                profileItem.setStartDate(json.getString("startDate"));
                profileItem.setEndDate(json.getString("endDate"));
                parseReturnValues(profileItem, json.getJSONObject("amounts"));
                dataItemUri = "data/" + UriUtils.getUriExceptFirstTwoParts(profileItem.getParentRef().getPath()) + "/" +
                        json.getJSONObject("dataItem").getString("uid");
                profileItem.setDataItemRef(
                        new AmeeObjectReference(
                                dataItemUri,
                                AmeeObjectType.DATA_ITEM));
            }
            // parse values
            // iterate over all keys but only work with keys not in excludes list
            item.clearValueRefs();
            JSONArray itemValues = json.getJSONArray("itemValues");
            for (int i = 0; i < itemValues.length(); i++) {
                JSONObject itemValue = itemValues.getJSONObject(i);
                String path = itemValue.getString("path");
                // create reference and add to parent
                ameeObjectReference =
                        new AmeeObjectReference(
                                item.getUri() + "/" + path,
                                AmeeObjectType.VALUE);
                item.addValueRef(ameeObjectReference);
                // look for object in cache
                ameeValue = (AmeeValue) AmeeObjectFactory.getInstance().getObject(ameeObjectReference, false);
                if (ameeValue == null) {
                    // create object and cache
                    ameeValue = new AmeeValue(ameeObjectReference);
                    AmeeObjectFactory.getInstance().addObjectToCache(ameeValue);
                }
                // update object
                ameeValue.setName(path);
                ameeValue.setValue(itemValue.getString("value"));
                ameeValue.setUnit(itemValue.getString("unit"));
                ameeValue.setPerUnit(itemValue.getString("perUnit"));
            }
        } catch (ClassCastException e) {
            throw new AmeeException("Caught ClassCastException: " + e.getMessage());
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }
}

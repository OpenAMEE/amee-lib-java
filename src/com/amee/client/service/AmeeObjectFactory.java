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
import com.amee.client.cache.AmeeObjectCache;
import com.amee.client.cache.AmeeObjectCacheDummyImpl;
import com.amee.client.cache.AmeeObjectCacheEntry;
import com.amee.client.cache.AmeeObjectCacheImpl;
import com.amee.client.model.base.*;
import com.amee.client.model.data.AmeeDataCategory;
import com.amee.client.model.data.AmeeDataItem;
import com.amee.client.model.data.AmeeDrillDown;
import com.amee.client.model.profile.AmeeProfile;
import com.amee.client.model.profile.AmeeProfileCategory;
import com.amee.client.model.profile.AmeeProfileItem;
import com.amee.client.util.Choice;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmeeObjectFactory implements Serializable {

    private AmeeObjectCache cache = new AmeeObjectCacheImpl();

    private static AmeeObjectFactory instance = new AmeeObjectFactory();

    private static AmeeContext ameeContext = AmeeContext.getInstance();

    private static Map<APIVersion, AmeeObjectParser> parserMap = new HashMap<APIVersion, AmeeObjectParser>();
    static {
        parserMap.put(APIVersion.TWO, new AmeeV2ObjectParser());
    }

    public AmeeObjectParser getParser() throws AmeeException {
        return getParser(ameeContext.getAPIVersion());
    }

    public AmeeObjectParser getParser(APIVersion apiVersion) throws AmeeException {
        AmeeObjectParser parser = parserMap.get(apiVersion);
        if (parser != null) {
          return parser;
        }
        else {
          throw new AmeeException("Unsupported API version: " + apiVersion);
        }
    }

    public static AmeeObjectFactory getInstance() {
        return instance;
    }

    private AmeeObjectFactory() {
        super();
    }

    // factory methods

    public AmeeDataCategory getDataCategoryRoot() throws AmeeException {
        return (AmeeDataCategory) getObject("data", AmeeObjectType.DATA_CATEGORY);
    }

    public AmeeDataCategory getDataCategory(String path) throws AmeeException {
        return (AmeeDataCategory) getObject("data/" + path, AmeeObjectType.DATA_CATEGORY);
    }

    public AmeeDataItem getDataItem(AmeeDataCategory dataCategory, String dataItemDefinitionUid, List<Choice> values) throws AmeeException {
        String response;
        AmeeDataItem dataItem;
        JSONObject jsonObj;
        List<Choice> parameters = new ArrayList<Choice>();
        parameters.add(new Choice("itemDefinitionUid", dataItemDefinitionUid));
        parameters.add(new Choice("newObjectType", "DI"));
        if (values != null) {
            parameters.addAll(values);
        }
        response = AmeeInterface.getInstance().postAmeeResource(dataCategory.getUri(), parameters);
        try {
            // create DataItem and add to cache
            jsonObj = new JSONObject(response);
            dataItem = new AmeeDataItem(
                    dataCategory.getObjectReference().getPath() + "/" + jsonObj.getJSONObject("dataItem").getString("uid"), AmeeObjectType.DATA_ITEM);
            getParser().parse(dataItem, jsonObj);
            addObjectToCache(dataItem, true);
            // invalidate ProfileCategory
            getCache().remove(dataCategory.getUri());
            dataCategory.setFetched(false);
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
        return dataItem;
    }

    public AmeeDrillDown getDrillDown(String path) throws AmeeException {
        return (AmeeDrillDown) getObject("data/" + path, AmeeObjectType.DRILL_DOWN);
    }

    public AmeeDrillDown getDrillDown(AmeeDataCategory dataCategory) throws AmeeException {
        return (AmeeDrillDown) getObject(dataCategory.getUri() + "/drill", AmeeObjectType.DRILL_DOWN);
    }

    public AmeeProfile getProfile(String uid) throws AmeeException {
        return (AmeeProfile) getObject("profiles/" + uid, AmeeObjectType.PROFILE);
    }

    /**
     * @deprecated As of release 2.1, replaced by {@link #addProfile()}
     */
    public AmeeProfile getProfile() throws AmeeException {
        return addProfile();
    }

    public AmeeProfile addProfile() throws AmeeException {
        String response;
        AmeeProfile profile;
        JSONObject profileObj;
        List<Choice> parameters = new ArrayList<Choice>();
        parameters.add(new Choice("profile", "true"));
        response = AmeeInterface.getInstance().postAmeeResource("/profiles", parameters);
        try {
            profileObj = new JSONObject(response);
            profileObj = profileObj.getJSONObject("profile");
            profile = new AmeeProfile("/profiles/" + profileObj.getString("path"), AmeeObjectType.PROFILE);
            profile.setUid(profileObj.getString("uid"));
            profile.setName(profileObj.getString("name"));
            addObjectToCache(profile, true);
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
        return profile;
    }

    public AmeeProfileItem getProfileItem(String profileUid, String categoryPath, String profileItemUid) throws AmeeException {
        return (AmeeProfileItem) getObject("profiles/" + profileUid + "/" + categoryPath + "/" + profileItemUid, 
                AmeeObjectType.PROFILE_ITEM);
    }

    /**
     * @deprecated As of release 2.1, replaced by {@link #addProfileItem()}
     */
    public AmeeProfileItem getProfileItem(AmeeProfileCategory profileCategory, String dataItemUid, List<Choice> values) throws AmeeException {
        return addProfileItem(profileCategory, dataItemUid, values);
    }

    public AmeeProfileItem addProfileItem(AmeeProfileCategory profileCategory, String dataItemUid, List<Choice> values) throws AmeeException {
        AmeeProfileItem item = addProfileItem(profileCategory.getUri(), dataItemUid, values);
        profileCategory.setFetched(false);
        return item;
    }

    public AmeeProfileItem addProfileItem(AmeeProfile profile, String categoryUri, String dataItemUid, List<Choice> values) throws AmeeException {
        return addProfileItem(profile.getUri()+"/"+categoryUri, dataItemUid, values);
    }

    public AmeeProfileItem addProfileItem(String profileUID, String categoryUri, String dataItemUid, List<Choice> values) throws AmeeException {
        return addProfileItem("/profiles/"+profileUID+"/"+categoryUri, dataItemUid, values);
    }

    public AmeeProfileItem addProfileItem(String profileCategoryUri, String dataItemUid, List<Choice> values) throws AmeeException {
        String response;
        AmeeProfileItem profileItem;
        JSONObject jsonObj;
        List<Choice> parameters = new ArrayList<Choice>();
        parameters.add(new Choice("dataItemUid", dataItemUid));
        if (values != null) {
            parameters.addAll(values);
        }
        response = AmeeInterface.getInstance().postAmeeResource(profileCategoryUri, parameters);
        try {
            // create ProfileItem and add to cache
            jsonObj = new JSONObject(response);
            String path = profileCategoryUri;
            int pos = path.indexOf("?");
            if (pos >= 0) {
                path = path.substring(0, pos);
            }
            profileItem = new AmeeProfileItem(path + "/" + jsonObj.getJSONObject("profileItem").getString("uid"), AmeeObjectType.PROFILE_ITEM);
            getParser().parse(profileItem, jsonObj);
            addObjectToCache(profileItem, true);
            // invalidate ProfileCategory
            getCache().remove(profileCategoryUri);
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
        return profileItem;
    }

    public void setItemValues(AmeeItem item, List<Choice> values) throws AmeeException {
        String response;
        JSONObject jsonObj;
        List<Choice> parameters = new ArrayList<Choice>();
        if (values != null) {
            parameters.addAll(values);
        }
        response = AmeeInterface.getInstance().putAmeeResource(item.getUri(), parameters);
        try {
            // invalidate item and parent
            invalidate(item);
            getCache().remove(item.getParentUri());
            // update ProfileItem and replace in cache
            jsonObj = new JSONObject(response);
            getParser().parse(item, jsonObj);
            addObjectToCache(item, true);
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }

    public AmeeProfileCategory getProfileCategory(AmeeProfile profile, String path) throws AmeeException {
        return (AmeeProfileCategory) getObject(profile.getUri() + "/" + path, AmeeObjectType.PROFILE_CATEGORY);
    }

    //TODO - V1
    public AmeeProfileCategory getProfileCategoryRecursive(AmeeProfile profile, String path) throws AmeeException {
        return (AmeeProfileCategory) getObject(profile.getUri() + "/" + path + "?recurse=true", AmeeObjectType.PROFILE_CATEGORY);
    }

    public AmeeObject getObject(String uri, AmeeObjectType objectType) throws AmeeException {
        return getObject(new AmeeObjectReference(uri, objectType));
    }

    public AmeeObject getObject(AmeeObjectReference ref) throws AmeeException {
        return getObject(ref, true);
    }

    protected AmeeObject getObject(AmeeObjectReference ref, boolean create) throws AmeeException {
        AmeeObject ameeObject = null;
        AmeeObjectCacheEntry ameeObjectCacheEntry = getObjectCacheEntry(ref, create);
        if (ameeObjectCacheEntry != null) {
            ameeObject = ameeObjectCacheEntry.getObject();
            if (ameeObject != null) {
                return ameeObject.getCopy();
            }
        }
        return ameeObject;
    }

    protected AmeeObjectCacheEntry getObjectCacheEntry(AmeeObjectReference ref) throws AmeeException {
        return getObjectCacheEntry(ref, true);
    }

    protected AmeeObjectCacheEntry getObjectCacheEntry(AmeeObjectReference ref, boolean create) throws AmeeException {
        String response;
        AmeeObject ameeObject;
        AmeeObjectCacheEntry ameeObjectCacheEntry = getCache().get(ref.getUri());
        if (create &&
                ((ameeObjectCacheEntry == null) ||
                        !ameeObjectCacheEntry.getObjectReference().
                                getObjectType().equals(ref.getObjectType()))) {
            ameeObject = getNewObject(ref);
            response = AmeeInterface.getInstance().getAmeeResource(ameeObject.getUri());
            getParser().parse(ameeObject, response);
            ameeObjectCacheEntry = addObjectToCache(ameeObject);
        }
        return ameeObjectCacheEntry;
    }

    public void fetch(AmeeObject object) throws AmeeException {
        String response = AmeeInterface.getInstance().getAmeeResource(object.getUri());
        getParser().parse(object, response);
        addObjectToCache(object, true);
    }

    public void save(AmeeObject object) throws AmeeException {
        switch (object.getObjectType()) {
            case DATA_CATEGORY:
                break;
            case PROFILE_CATEGORY:
                break;
            case DATA_ITEM:
                break;
            case DRILL_DOWN:
                break;
            case PROFILE:
                break;
            case PROFILE_ITEM:
                break;
            case VALUE:
                saveValue((AmeeValue) object);
                return;
            default:
                break;
        }
        throw new AmeeException("Save not supported for this object.");
    }

    public void delete(AmeeObject object) throws AmeeException {
        switch (object.getObjectType()) {
            case DATA_CATEGORY:
                break;
            case PROFILE_CATEGORY:
                break;
            case DATA_ITEM:
                break;
            case DRILL_DOWN:
                break;
            case PROFILE:
                deleteProfile((AmeeProfile) object);
                return;
            case PROFILE_ITEM:
                deleteProfileItem((AmeeProfileItem) object);
                return;
            case VALUE:
                break;
            default:
                break;
        }
        throw new AmeeException("Delete not supported for this object.");
    }

    protected AmeeObjectCacheEntry getObjectCacheEntry(AmeeObject object) throws AmeeException {
        AmeeObjectCacheEntry ameeObjectCacheEntry;
        String response = AmeeInterface.getInstance().getAmeeResource(object.getUri());
        getParser().parse(object, response);
        ameeObjectCacheEntry = addObjectToCache(object);
        return ameeObjectCacheEntry;
    }

    protected AmeeObject getNewObject(AmeeObjectReference ref) throws AmeeException {
        switch (ref.getObjectType()) {
            case DATA_CATEGORY:
                return new AmeeDataCategory(ref);
            case PROFILE_CATEGORY:
                return new AmeeProfileCategory(ref);
            case DATA_ITEM:
                return new AmeeDataItem(ref);
            case DRILL_DOWN:
                return new AmeeDrillDown(ref);
            case PROFILE:
                return new AmeeProfile(ref);
            case PROFILE_ITEM:
                return new AmeeProfileItem(ref);
            case VALUE:
                return new AmeeValue(ref);
        }
        throw new AmeeException("Object Type not recognised.");
    }

    protected AmeeObjectCacheEntry addObjectToCache(AmeeObject object) {
        return addObjectToCache(object, false);
    }

    protected AmeeObjectCacheEntry addObjectToCache(AmeeObject object, boolean copy) {
        if (copy) {
            object = object.getCopy();
        }
        AmeeObjectCacheEntry ameeObjectCacheEntry =
                new AmeeObjectCacheEntry(object.getObjectReference(), object);
        getCache().put(ameeObjectCacheEntry);
        return ameeObjectCacheEntry;
    }

    // delete operations

    protected void deleteProfileItem(AmeeProfileItem profileItem) throws AmeeException {
        // delete the resource
        AmeeInterface.getInstance().deleteAmeeResource(profileItem.getUri());
        // invalidate this
        invalidate(profileItem);
        // invalidate parent (if it exists)
        getCache().remove(profileItem.getParentUri());
        // mark as not fetched
        profileItem.setFetched(false);
    }

    protected void deleteProfile(AmeeProfile profile) throws AmeeException {
        // delete the resource
        AmeeInterface.getInstance().deleteAmeeResource(profile.getUri());
        // invalidate this
        invalidate(profile);
        // mark as not fetched
        profile.setFetched(false);
    }


    // util

    protected void saveValue(AmeeValue value) throws AmeeException {
        try {
            // setup and make request
            List<Choice> parameters = new ArrayList<Choice>();
            parameters.add(new Choice("value", value.getValue()));
            String response = AmeeInterface.getInstance().putAmeeResource(value.getUri(), parameters);
            // parse response into AmeeValue and update cache entry
            getParser().parseValue(value, new JSONObject(response));
            addObjectToCache(value, true);
            // invalidate parent Profile Item, if it exists
            getCache().remove(value.getItemRef().getUri());
            // invalidate parent Profile Catagory, if it exists
            getCache().remove(value.getItemRef().getParentUri());
        } catch (JSONException e) {
            throw new AmeeException("Caught JSONException: " + e.getMessage());
        }
    }

    protected void invalidate(AmeeCategory category) throws AmeeException {
        AmeeObjectCacheEntry aoce;
        getCache().remove(category.getUri());
        for (AmeeObjectReference ref : category.getCategoryRefs(false)) {
            aoce = getCache().get(ref.getUri());
            if (aoce != null) {
                invalidate((AmeeCategory) aoce.getObject());
            }
        }
        for (AmeeObjectReference ref : category.getItemRefs(false)) {
            aoce = getCache().get(ref.getUri());
            if (aoce != null) {
                invalidate((AmeeItem) aoce.getObject());
            }
        }
    }

    public void invalidate(AmeeItem item) throws AmeeException {
        getCache().remove(item.getUri());
        for (AmeeObjectReference ref : item.getValueRefs(false)) {
            getCache().remove(ref.getUri());
        }
    }

    // local properties

    public AmeeObjectCache getCache() {
        return cache;
    }

    /**
     * Set the AmeeObjectCache implementation.
     * Supply null to use the dummy no-op implementation (AmeeObjectCacheDummyImpl).
     *
     * @param cache an AmeeObjectCache implementation
     */
    public void setCache(AmeeObjectCache cache) {
        if (cache == null) {
            cache = new AmeeObjectCacheDummyImpl();
        }
        this.cache = cache;
    }
}
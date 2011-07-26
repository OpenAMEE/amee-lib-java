/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.amee.client.model.base.AmeeValue;
import com.amee.client.model.data.AmeeDataItem;
import com.amee.client.model.data.AmeeDrillDown;
import com.amee.client.model.profile.AmeeProfile;
import com.amee.client.model.profile.AmeeProfileCategory;
import com.amee.client.model.profile.AmeeProfileItem;
import com.amee.client.service.AmeeContext;
import com.amee.client.service.AmeeObjectFactory;
import com.amee.client.util.Choice;

/**
 * This class copies from one profile to another. It can copy
 * profiles between two different servers, e.g. stage and live.
 * 
 * KNOWN ISSUES:
 * - metadata item can't be copied
 * - should warn if toProfile or item exists
 * - name of create profiled items seems to be set to the dataItemUid???
 * @author nalu
 */
public class ProfileCopy {

    public static FileWriter writer;
    private String toProfileUid,  fromProfileUid;
    private String fromUrl,  toUrl;
    private Map dataItemCache = new HashMap();
    private static boolean isTest = false;

    /**
     * The fromUid must not be null, but the toUid can be null in which case
     * the new profile will be created.
     */
    public ProfileCopy(String fromUid, String toUid, String fromUrl, String toUrl) {
        fromProfileUid = fromUid;
        toProfileUid = toUid;
        this.fromUrl = fromUrl;
        this.toUrl = toUrl;
    }

    /**
     * 
     * @param path A path, e.g. home/lighting - leave off first and last slashes.
     * @return The number of profile items copied.
     * @throws com.amee.client.AmeeException
     */
    public int copyCategory(String path) throws AmeeException {
        AmeeProfile profile;
        AmeeObjectFactory objectFactory = AmeeObjectFactory.getInstance();

        //Get the existing items
        AmeeContext.getInstance().setBaseUrl(fromUrl);
        profile = objectFactory.getProfile(fromProfileUid);
        AmeeProfileCategory profileCategory = objectFactory.getProfileCategory(profile, path);
        profileCategory.fetch();//the fetch() method forces a call to the AMEE API
        List<AmeeProfileItem> profileItems = profileCategory.getProfileItems();

        String fromDataItemUid;
        if (profileItems.isEmpty()) {
            return 0;
        }
        for (AmeeProfileItem item : profileItems) {
            item.fetch();
            //System.err.println("profileItemUid=" + item.getUid());
            //NOTE: important to get data so it is cached and available on line ***1 below
            AmeeDataItem dataItem = item.getDataItem();
        /*System.err.println("  dataItemUid=" + dataItem.getUid());
        for (AmeeValue value : item.getValues()) {
        System.err.println("  " + value.getName() + "=" + value.getValue());
        }*/
        }

        //create the new ones
        AmeeContext.getInstance().setBaseUrl(toUrl);

        profile = objectFactory.getProfile(toProfileUid);

        for (AmeeProfileItem fromProfileItem : profileItems) {
            try {
                fromDataItemUid = fromProfileItem.getDataItem().getUid();
                String toDataItemUid = (String) dataItemCache.get(fromDataItemUid);
                if (toDataItemUid == null) {
                    toDataItemUid = doDrillDown(path, fromProfileItem.getDataItem().getValues());//***1
                    dataItemCache.put(fromDataItemUid, toDataItemUid);
                }

                profileCategory = objectFactory.getProfileCategory(profile, path);
                //using data item uid explicitly will need to drill and cache values
                List<Choice> values = new ArrayList<Choice>();
                for (AmeeValue value : fromProfileItem.getValues()) {
                    values.add(new Choice(value.getName(), value.getValue()));
                //System.err.println(value.getName()+"="+value.getValue());
                }
                /*System.err.println("value.size()=" + values.size());
                if (values.size() > 0) {
                toProfileItem.setValues(values);
                }*/
                values.add(new Choice("name", fromProfileItem.getName()));
                //System.err.println("toDataItemUid="+toDataItemUid);
                writer.write("cache," + path + "," + fromDataItemUid + "," + toDataItemUid + "\n");
                if (!isTest) {
                    AmeeProfileItem toProfileItem = profileCategory.addProfileItem(toDataItemUid, values);
                    String s = path + "," + fromProfileItem.getUid() + "," + toProfileItem.getUid();
                    System.err.println(s);
                    writer.write(s + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return profileItems.size();
    }

    /** Given a set of data item values and a path, this drills down and returns the
     *  data item uid on the current server.
     * @param path
     * @param values
     * @return
     * @throws com.amee.client.AmeeException
     */
    private String doDrillDown(String path, List<AmeeValue> values) throws AmeeException {
        Map drillMap = new HashMap();
        for (AmeeValue value : values) {
            drillMap.put(value.getLocalPath(), value.getValue());
        }
        //System.err.println("drillMap=" + drillMap);

        AmeeDrillDown ameeDrillDown = AmeeObjectFactory.getInstance().getDrillDown(path + "/drill");
        while (ameeDrillDown.hasChoices()) {
            String choiceName = ameeDrillDown.getChoiceName();
            String selection = (String) drillMap.get(ameeDrillDown.getChoiceName());
            //System.err.println("choiceName=" + choiceName);
            //System.err.println("drillSection=" + selection);
            ameeDrillDown.addSelection(choiceName, selection);
            ameeDrillDown.fetch();
        }
        AmeeDataItem item = ameeDrillDown.getDataItem();
        //System.err.println("dataItemUid=" + item.getUid());
        return item.getUid();
    }

    public static Map getUids(String firstUid, File file) {
        return getUids(firstUid, file, null);
    }

    /** 
     * 
     * @param firstUid Ignore all uids in the file until this one.
     * @param file A text file which contains a 12 digit profile uid
     * at the start of each line, e.g.
     * B8380EED27F2
     * F091E18C5C03
     * 1A7A2F6654DE
     * 072B0A2FADB0
     * 2AF8684FDA4A
     * @param blacklist A text file, same format as file, that contains
     * profile uids to ignore.
     * @return A map in which keys are Strings containing the uids. The map values are null,
     * though the code can be tweaked to read toUids.
     */
    public static Map getUids(String firstUid, File file, File blacklist) {
        Map uidMap, blacklistMap = null;
        if (blacklist != null) {
            blacklistMap = loadUidsFromCSV(firstUid, blacklist, null);
        }
        uidMap = loadUidsFromCSV(firstUid, file, blacklistMap);

        return uidMap;
    }

    public static Map loadUidsFromCSV(String firstUid, File file, Map blacklistMap) {
        System.err.println("Loading uids from " + file + "...");
        //ArrayList list = new ArrayList();
        Map map = new LinkedHashMap();
        boolean foundFirst = false;

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                line = line.trim();
                if (line.length() > 0) {
                    //list.add(line.trim().substring(0, 12));
                    //line = line.trim();
                    String fromUid = line.substring(0, 12);
                    String toUid = null;//line.substring(21, 33);
                    if (firstUid == null || fromUid.equals(firstUid)) {
                        foundFirst = true;
                    }
                    if (foundFirst) {
                        if (blacklistMap == null || blacklistMap.containsKey(fromUid) == false) {
                            map.put(fromUid, toUid);
                        }
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("...finished reading uids - map.size() = " + map.size());
        return map;
    }

    /**
     * The main method implements an example where profiles are copied from
     * stage to live for an AMEE user specified in the command line args.
     * You'll need to supply a list of valid profile uids in the
     * uid_list.txt file - see javadoc for getUids.
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Required args: username password");
            System.exit(0);
        }
        //isTest = true; //Don't actually copy
        File dir = new File("/home/nalu/docs/amee/clients/est/stage2live/13nov08");
        try {
            writer = new FileWriter(new File(dir, "output_cache.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AmeeContext.getInstance().setUsername(args[0]);
        AmeeContext.getInstance().setPassword(args[1]);

        String[] paths = {//metadata doesn't work
            "home/appliances/computers/generic", "home/appliances/cooking", "home/appliances/entertainment/generic", "home/appliances/kitchen/generic", "home/appliances/televisions/generic", "home/energy/electricity", "home/energy/electricityiso", "home/energy/quantity", "home/energy/uk/price", "home/energy/uk/seasonal", "home/heating", "home/lighting",
            "transport/car/generic", "transport/motorcycle/generic", "transport/plane/generic", "transport/bus/generic", "transport/train/generic", "transport/taxi/generic", "transport/other",
            "home/appliances/computers/generic", "home/appliances/entertainment/generic", "home/appliances/kitchen/generic", "home/appliances/televisions/generic"
        };
        //this file contains uids to be copied from stage
        File file = new File(dir, "ezameeuser_est_live.csv");
        //this file contains uids that should be ignored
        File blacklist = new File(dir, "blacklist.csv");
        Map uids = getUids(null, file, blacklist);
        //Map uids = new HashMap();
        //uids.put("516F8D8C179F", null);
        //ArrayList uids = new ArrayList();
        //uids.add("F1E658758A87");
        //String[] paths = {"home/appliances/cooking"};
        Iterator iter = uids.keySet().iterator();
        //iter.next();//skip the first one
        int nProfile = 1;
        long start = System.currentTimeMillis(), last = start;
        while (iter.hasNext()) {
            long now = System.currentTimeMillis();
            long average = (now - start) / nProfile;
            long eta = average * (uids.size() - nProfile);
            eta /= 60000l;
            long lastDuration = (now - last);
            last = now;
            String sh = "=== " + nProfile + ", last=" + lastDuration + ", av=" + average + ", eta=" + eta;
            System.err.println(sh);
            try {
                writer.write(sh + "\n");
                String fromUid = (String) iter.next();
                //String toUid = (String) uids.get(fromUid);
                AmeeContext.getInstance().setBaseUrl("http://live.amee.com");
                AmeeProfile toProfile = AmeeObjectFactory.getInstance().addProfile();
                String s = "from " + fromUid + " to " + toProfile.getUid();
                System.err.println(s);
                writer.write(s + "\n");
                //System.err.println("from " + fromUid + " to " + toUid);

                ProfileCopy pc = new ProfileCopy(fromUid, toProfile.getUid(), "http://stage.amee.com", "http://live.amee.com");
                for (int i = 0; i < paths.length; i++) {
                    int n = pc.copyCategory(paths[i]);
                    System.err.println(paths[i] + "," + n);
                }
            } catch (AmeeException ex) {
                ex.printStackTrace();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            nProfile++;
        /*if (nProfile > 3) {
        break;
        }*/
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

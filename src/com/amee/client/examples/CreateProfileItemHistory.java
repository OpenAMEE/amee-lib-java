/**
 * This file is part of the AMEE Java Client Library.
 *
 * Copyright (c) 2008 AMEE UK Ltd. (http://www.amee.com)
 *
 * The AMEE Java Client Library is free software, released under the MIT
 * license. See mit-license.txt for details.
 */

package com.amee.client.examples;

import java.util.List;
import java.util.ArrayList;
import com.amee.client.AmeeException;
import com.amee.client.service.*;
import com.amee.client.model.profile.*;
import com.amee.client.util.Choice;
import org.joda.time.DateTime;
import org.joda.time.format.*;

public class CreateProfileItemHistory {

    private static final DateTimeFormatter FMT = ISODateTimeFormat.dateTimeNoMillis();

    public static void main(String[] args) throws AmeeException {
        
        // Set up AMEE connection
        AmeeContext.getInstance().setUsername("username-here");
        AmeeContext.getInstance().setPassword("password-here");
        AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");

        // Parameters
        String profileCategory = "home/energy/quantity";
        String dataItemUID = "66056991EE23"; // The data item UID that you want to create a profile item for.
        DateTime dt = (new DateTime()).minusDays(3); // We will start our sequence 3 days ago

        // Create a profile to put data in
        AmeeProfile profile = AmeeObjectFactory.getInstance().addProfile();
        // Get category
        AmeeProfileCategory cat = AmeeObjectFactory.getInstance().getProfileCategory(profile, profileCategory);

        // We will create three items with different start dates to make a sequence
        // The first two are day long; because they have the same name, they are treated as a sequence.
        
        // First
        List<Choice> values = new ArrayList<Choice>();
        values.add(new Choice("name", "sequence1"));
        values.add(new Choice("energyConsumption", 10));
        values.add(new Choice("energyConsumptionUnit", "kWh"));
        values.add(new Choice("energyConsumptionPerUnit", "day"));
        values.add(new Choice("startDate", FMT.print(dt)));
        values.add(new Choice("endDate", FMT.print(dt.plusDays(1)))); // Set an explicit end date
        // Create the item
        cat.addProfileItem(dataItemUID, values);
        
        // Second
        values = new ArrayList<Choice>();
        values.add(new Choice("name", "sequence1"));
        values.add(new Choice("energyConsumption", 12));
        values.add(new Choice("energyConsumptionUnit", "kWh"));
        values.add(new Choice("energyConsumptionPerUnit", "day"));
        values.add(new Choice("startDate", FMT.print(dt.plusDays(1))));
        values.add(new Choice("duration", "P1D")); // Use a duration instead of an end date
        // Create the item
        cat.addProfileItem(dataItemUID, values);

        // Third
        values = new ArrayList<Choice>();
        values.add(new Choice("name", "sequence1"));
        values.add(new Choice("energyConsumption", 8));
        values.add(new Choice("energyConsumptionUnit", "kWh"));
        values.add(new Choice("energyConsumptionPerUnit", "day"));
        values.add(new Choice("startDate", FMT.print(dt.plusDays(2)))); // Items without an end date are assumed to go on forever.
        // Create the item
        cat.addProfileItem(dataItemUID, values);

        // Reload category to display the items
        cat = AmeeObjectFactory.getInstance().getProfileCategory(profile, profileCategory);

        // Show items
        System.out.printf("/profiles/%s/%s\n", profile.getUid(), profileCategory);
        for (AmeeProfileItem item : cat.getProfileItems()) {
            // Print data
            System.out.println("---------------------");
            System.out.printf(" - startDate: %s\n", item.getStartDate());
            System.out.printf(" - endDate: %s\n", item.getEndDate());
            System.out.printf(" - CO2 total: %s %s\n", item.getAmount(), item.getAmountUnit());
        }

    }
}

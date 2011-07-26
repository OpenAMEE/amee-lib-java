package com.amee.client;

import com.amee.client.model.base.AmeeValue;
import com.amee.client.model.data.AmeeDataCategory;
import com.amee.client.model.data.AmeeDataItem;
import com.amee.client.model.data.AmeeDrillDown;
import com.amee.client.model.profile.AmeeProfile;
import com.amee.client.model.profile.AmeeProfileCategory;
import com.amee.client.model.profile.AmeeProfileItem;
import com.amee.client.service.AmeeContext;
import com.amee.client.service.AmeeObjectFactory;
import com.amee.client.util.Choice;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//TODO - AMEE Context supplies default connection params
//TODO - Look at cache expiring (profileitems and categories)
//TODO - What should happen to the category when i delete and recreate an item - is amount ammended
public class AmeeClientTest {

    protected AmeeObjectFactory objectFactory = AmeeObjectFactory.getInstance();

    private static final DateTimeFormatter FMT = ISODateTimeFormat.dateTimeNoMillis();

    private static final String CATEGORY_PATH = "transport/train/route";
    private static final String DATA_ITEM_UID = "77C1D6CA6A0E";
    private static final String DATA_ITEM_REF = "data/" + CATEGORY_PATH + "/" + DATA_ITEM_UID;

    private AmeeDataCategory dataCategory;
    private AmeeProfileCategory category;
    private AmeeProfile profile;
    private AmeeProfileItem profileItem;

    private String amountUnit = "kg/year";
    private String profileItemName;
    private Date startDate;
    private Date endDate;

    @Parameters({"username", "password", "baseUrl"})
    @BeforeTest(alwaysRun=true)
    public void initAmeeContext(String username, String password, String baseUrl) {
        System.out.println("initAmeeContext()");
        AmeeContext.getInstance().setAuthToken(null);
        AmeeContext.getInstance().setUsername(username);
        AmeeContext.getInstance().setPassword(password);
        AmeeContext.getInstance().setBaseUrl(baseUrl);
    }

    @BeforeGroups(groups={"datatest"})
    public void getDataCategory() throws AmeeException {
        System.out.println("getDataCategory()");
        dataCategory = AmeeObjectFactory.getInstance().getDataCategory(CATEGORY_PATH);
    }

    @BeforeGroups(groups={"profiletest", "profileitemtest"})
    public void createProfile() throws AmeeException {
        System.out.println("createProfile()");
        profile = AmeeObjectFactory.getInstance().addProfile();
        category = AmeeObjectFactory.getInstance().getProfileCategory(profile, CATEGORY_PATH);
    }

    @AfterGroups(groups={"profiletest", "profileitemtest"})
    public void deleteProfile() throws AmeeException {
        System.out.println("deleteProfile()");
        profile.delete();
    }

    @AfterMethod(groups={"profileitemtest"})
    public void deleteProfileItem() throws AmeeException {
        System.out.println("deleteProfileItem()");
        if (profileItem != null)
            profileItem.delete();
    }

    @Parameters({"amountUnit", "amountPerUnit"})
    @BeforeMethod(groups={"profileitemtest"})
    public void createProfileItem(@Optional String amountUnit, @Optional String amountPerUnit) throws AmeeException {
        System.out.println("createProfileItem()");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR,1);
        endDate = cal.getTime();

        List<Choice> params = new ArrayList<Choice>();
        params.add(new Choice("trainType", "national"));
        params.add(new Choice("station1", "london"));
        params.add(new Choice("station2", "manchester"));
        params.add(new Choice("startDate", FMT.print(startDate.getTime())));
        params.add(new Choice("endDate", FMT.print(endDate.getTime())));
        profileItemName = "test_profileitem_"+System.nanoTime();
        params.add(new Choice("name", profileItemName));
        if (amountUnit != null && amountPerUnit != null) {
            params.add(new Choice("returnUnit", amountUnit));
            params.add(new Choice("returnPerUnit", amountPerUnit));
            this.amountUnit = amountUnit + "/" + amountPerUnit;
        }

        profileItem = category.addProfileItem(DATA_ITEM_UID, params);
    }


    @Test(groups={"profileitemtest"})
    public void testUpdateProfileItem() throws AmeeException {
        System.out.println("testUpdateProfileItem()");

        // Set new options for the item
        List<Choice> values = new ArrayList<Choice>();
        String newName = "test_profile_item_with_new_name_"+System.nanoTime();
        values.add(new Choice("name", newName));
        profileItem.setValues(values);

        // Clear the cache
        AmeeObjectFactory.getInstance().invalidate(profileItem);

        profileItem = AmeeObjectFactory.getInstance().getProfileItem(profile.getUid(), CATEGORY_PATH, profileItem.getUid());
        assertEquals(newName, profileItem.getName());
    }

    @Test(groups={"profileitemtest"})
    public void testDeleteProfileItem() throws AmeeException {
        System.out.println("testDeleteProfileItem");

        profileItem.delete();
        try {
            AmeeObjectFactory.getInstance().getProfileItem(profile.getUid(), CATEGORY_PATH, profileItem.getUid());
            assert(false);
        } catch (AmeeException e) {
            assert(true);
            profileItem = null;
        }
    }

    @Test(groups={"profileitemtest"})
    public void testCreateProfileItem() throws AmeeException {
        System.out.println("testCreateProfileItem");
        assertEquals(true, profileItem.getAmount().compareTo(BigDecimal.ZERO) !=0);
        assertEquals(amountUnit, profileItem.getAmountUnit());
        assertEquals(DATA_ITEM_REF, profileItem.getDataItemRef().toString());
        assertEquals(profileItemName, profileItem.getName());
        assertEquals(startDate.compareTo(profileItem.getStartDate()), 0);
        assertEquals(endDate.compareTo(profileItem.getEndDate()), 0);
        for (AmeeValue value : profileItem.getValues()) {
            if ("Total Distance".equals(value.getName())) {
                assertEquals(value.getValue(),"291.258");
                return;
            }
        }
        assert(false);
    }

    @Test(groups={"profileitemtest"})
    public void testViewProfileCategory() throws AmeeException {
        System.out.println("testViewProfileCategory");

        assertEquals("Route", category.getName());
        for (AmeeProfileItem item : category.getProfileItems()) {
            assertEquals(amountUnit, item.getAmountUnit());
            assertEquals(DATA_ITEM_REF, item.getDataItemRef().toString());
            assertEquals(profileItemName, item.getName());

        }
    }

    @Test(groups={"datatest"})
    public void testViewDataCategory() throws AmeeException {
        System.out.println("testViewDataCategory");
        assertEquals("Route",dataCategory.getName());
    }

    @Test(groups={"datatest"})
    public void testDrillDown() throws AmeeException {
        System.out.println("testDrillDown");
        AmeeDrillDown ameeDrillDown = objectFactory.getDrillDown("transport/car/generic/drill?fuel=average");
        ameeDrillDown.fetch();
        assertNotNull(ameeDrillDown.getDataItem());

    }

    @Test(groups={"datatest"})
    public void testViewDataItem() throws AmeeException {
        System.out.println("testViewDataCategory");
        String dataItemLabel = "auto";
        for (AmeeDataItem item : dataCategory.getDataItems()) {
            if (item.getLabel().compareTo(dataItemLabel) == 0) {
                assertEquals(item.getLabel(), dataItemLabel);
                return;
            }
        }
        assert(false);
    }
}


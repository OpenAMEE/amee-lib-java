The AMEE Java client allows you to use the AMEE API using a set of Java classes, rather than having to work with HTTP and JSON yourself.

NOTE: Version 2 and up of the Java client only work with AMEE 2 API keys. If you have an old version 1 key, you will need to use version 0.9 or lower.

JAR Download
------------

Click the 'Download' button on the github page to download the latest release as a JAR file.

Requirements
------------

The client should run on any Java 5 (1.5) or Java 6 (1.6) compatible platform. It depends on the following libraries:

 * Apache Jakarta Commons HTTP Client 3.1
 * Apache Commons Codec 1.3
 * Apache Commons Logging 1.1
 * JSON 2.0
 * Joda Time 

Configuration
-------------

The com.amee.client.service.AmeeContext class encapsulates configuration properties for an AMEE session. Here are the available properties:

 * Username - Your AMEE username.
 * Password - Your AMEE password.
 * BaseUrl - The base URL (such as ' http://stage.amee.com').
 * AuthToken - An AuthToken returned during an AMEE session or supplied by your application. 

There is currently only one global AmeeContext instance (a singleton). You can set the above properties as follows:

    AmeeContext.getInstance().setUsername("your-username");
    AmeeContext.getInstance().setPassword("your-password");
    AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");

Examples
--------

There are a number of simple examples in the  com.amee.client.examples package. These cover the most important AMEE operations; creating profiles, creating profile items, and viewing existing profile items.

Creating A New Profile

    // Use the AmeeObjectFactory to create a new Profile
    AmeeObjectFactory objectFactory = AmeeObjectFactory.getInstance();
    AmeeProfile profile = objectFactory.getProfile();

Storing Profile Items

    // Add a Profile Item to the new Profile with a known Data Item UID
    String category = "home/appliances/computers/generic";
    AmeeProfileCategory profileCategory = objectFactory.getProfileCategory(profile, category);
    profileCategory.addProfileItem("B32624F8CD5F");
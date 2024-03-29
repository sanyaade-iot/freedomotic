/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.mazzoni.twilight;

;
import it.freedomotic.events.GenericEvent;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Matteo Mazzoni <matteo@bestmazzo.it>
 */


public class TwilightTest {

    private static TwilightUtils twu;

    @BeforeClass
    public static void setUpClass() throws Exception {
        twu = new TwilightUtils(10000);
       }

    @Test
    public void NoonTest() {
        twu.setSunriseTime(new DateTime(2013, 11, 20, 5, 0));
        twu.setSunsetTime(new DateTime(2013, 11, 20, 17, 0));
        
        DateTime noon = new DateTime(2013, 11, 21, 12, 0);
        System.out.print(twu.getSunriseTime().toString() +" - "+ twu.getSunsetTime() +" - "+ noon);
        GenericEvent twAtNoon = twu.prepareEvent(noon);
        Assert.assertEquals("300", twAtNoon.getProperty("beforeSunset"));
        Assert.assertEquals("", twAtNoon.getProperty("afterSunset"));
        Assert.assertEquals("", twAtNoon.getProperty("isSunset"));
        Assert.assertEquals("", twAtNoon.getProperty("isSunrise"));
        Assert.assertEquals("420", twAtNoon.getProperty("afterSunrise"));
        Assert.assertEquals("", twAtNoon.getProperty("beforeSunrise"));
        
        noon = new DateTime(2013, 11, 21, 12, 1);
        System.out.print(twu.getSunriseTime().toString() +" - "+ twu.getSunsetTime() +" - "+ noon);
        twAtNoon = twu.prepareEvent(noon);
        Assert.assertEquals("299", twAtNoon.getProperty("beforeSunset"));
        Assert.assertEquals("", twAtNoon.getProperty("afterSunset"));
        Assert.assertEquals("", twAtNoon.getProperty("isSunset"));
        Assert.assertEquals("", twAtNoon.getProperty("isSunrise"));
        Assert.assertEquals("421", twAtNoon.getProperty("afterSunrise"));
        Assert.assertEquals("", twAtNoon.getProperty("beforeSunrise"));
    }

    @Test
    public void MidnightTest() {
        twu.setSunriseTime(new DateTime(2013, 11, 20, 5, 0));
        twu.setSunsetTime(new DateTime(2013, 11, 20, 17, 0));
        
        DateTime midnight = new DateTime(2013, 11, 22, 0, 0);
        GenericEvent twAtMidnight = twu.prepareEvent(midnight);
        Assert.assertEquals("", twAtMidnight.getProperty("beforeSunset"));
        Assert.assertEquals("420", twAtMidnight.getProperty("afterSunset"));
        Assert.assertEquals("", twAtMidnight.getProperty("isSunset"));
        Assert.assertEquals("", twAtMidnight.getProperty("isSunrise"));
        Assert.assertEquals("", twAtMidnight.getProperty("afterSunrise"));
        Assert.assertEquals("300", twAtMidnight.getProperty("beforeSunrise"));

    }

    @Test
    public void sunriseTest() {
        twu.setSunriseTime(new DateTime(2013, 11, 20, 5, 0));
        twu.setSunsetTime(new DateTime(2013, 11, 20, 17, 0));
        
        DateTime sunrise = new DateTime(2013, 11, 20, 5, 0);
        GenericEvent twAtSunrise = twu.prepareEvent(sunrise);
        Assert.assertEquals("720", twAtSunrise.getProperty("beforeSunset"));
        Assert.assertEquals("", twAtSunrise.getProperty("afterSunset"));
        Assert.assertEquals("", twAtSunrise.getProperty("isSunset"));
        Assert.assertEquals("true", twAtSunrise.getProperty("isSunrise"));
        Assert.assertEquals("", twAtSunrise.getProperty("afterSunrise"));
        Assert.assertEquals("", twAtSunrise.getProperty("beforeSunrise"));
        
        sunrise = new DateTime(2013, 11, 20, 5, 1);
        twAtSunrise = twu.prepareEvent(sunrise);
        Assert.assertEquals("719", twAtSunrise.getProperty("beforeSunset"));
        Assert.assertEquals("", twAtSunrise.getProperty("afterSunset"));
        Assert.assertEquals("", twAtSunrise.getProperty("isSunset"));
        Assert.assertEquals("", twAtSunrise.getProperty("isSunrise"));
        Assert.assertEquals("1", twAtSunrise.getProperty("afterSunrise"));
        Assert.assertEquals("", twAtSunrise.getProperty("beforeSunrise"));
    }

    @Test
    public void sunsetTest() {
        twu.setSunriseTime(new DateTime(2013, 11, 20, 5, 0));
        twu.setSunsetTime(new DateTime(2013, 11, 20, 17, 0));
        
        DateTime sunset = new DateTime(2013,11,23, 17,0);
        GenericEvent twAtSunset = twu.prepareEvent(sunset);
        Assert.assertEquals("", twAtSunset.getProperty("beforeSunset"));
        Assert.assertEquals("", twAtSunset.getProperty("afterSunset"));
        Assert.assertEquals("true", twAtSunset.getProperty("isSunset"));
        Assert.assertEquals("", twAtSunset.getProperty("isSunrise"));
        Assert.assertEquals("720", twAtSunset.getProperty("afterSunrise"));
        Assert.assertEquals("", twAtSunset.getProperty("beforeSunrise"));
        
        DateTime postSunset = new DateTime(2013, 11, 23, 17, 1);
        GenericEvent twPostSunset = twu.prepareEvent(postSunset);
        Assert.assertEquals("", twPostSunset.getProperty("beforeSunset"));
        Assert.assertEquals("1", twPostSunset.getProperty("afterSunset"));
        Assert.assertEquals("", twPostSunset.getProperty("isSunset"));
        Assert.assertEquals("", twPostSunset.getProperty("isSunrise"));
        Assert.assertEquals("", twPostSunset.getProperty("afterSunrise"));
        Assert.assertEquals("719", twPostSunset.getProperty("beforeSunrise"));
        
        
        
    }
}

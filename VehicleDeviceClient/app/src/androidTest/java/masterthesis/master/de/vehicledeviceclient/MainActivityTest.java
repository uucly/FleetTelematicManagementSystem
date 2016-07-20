package masterthesis.master.de.vehicledeviceclient;

import junit.framework.TestCase;

/**
 * Created by uucly on 18.05.2016.
 */
public class MainActivityTest extends TestCase {

    public void testOnCreate() throws Exception {

        assertNotNull(getClass().getClassLoader().getResourceAsStream("assets/connection.properties"));
    }
}
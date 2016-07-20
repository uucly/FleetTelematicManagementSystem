package masterthesis.master.de.mymasterthesisclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import junit.framework.TestCase;

import rest.VehicleOrderRest;

/**
 * Created by uucly on 07.03.2016.
 */
public class GeocodeAddressIntentServiceTest extends TestCase {

    public void testOnHandleIntent() throws Exception {
        String dateString = "{\"orderID\":3191,\"source\":\"POINT (1 1)\",\"target\":\"POINT (1 1)\",\"costs\":0.0317661,\"in_progress\":1,\"oldCosts\":0.0,\"waitCosts\":0.0,\"newCosts\":0.0,\"orderCosts\":0.0317661,\"totalCosts\":9.99999990317661E7,\"deviceID\":-1,\"date\":\"Mar 7, 2016 2:57:13 PM\"}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.readValue(dateString, VehicleOrderRest.class);
        assertTrue(true);
    }
}
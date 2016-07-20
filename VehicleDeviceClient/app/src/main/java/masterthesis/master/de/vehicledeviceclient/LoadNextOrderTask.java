package masterthesis.master.de.vehicledeviceclient;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import rest.VehicleOrderRest;
import server.CentralRestServer;

/**
 * Created by uucly on 11.03.2016.
 */
public class LoadNextOrderTask extends AsyncTask<Void, Integer, VehicleOrderRest> {
    private static final String TAG = "NewVehicleTask";
    private static final String IP = "192.168.0.11";

    private int deviceId;
    private CentralRestServer server;

    public LoadNextOrderTask(int deviceID, CentralRestServer server) {
        this.deviceId = deviceID;
        this.server = server;
    }

    @Override
    protected VehicleOrderRest doInBackground(Void[] params) {
        HttpGet target = new HttpGet(server.createURL("vehicleDevice" , "nextVehicleOrder", String.valueOf(deviceId)));
        target.addHeader("accept", "application/json");
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            Log.i(TAG, "Post: " + target.getRequestLine().getUri());
            HttpResponse response = client.execute(target);
            Log.i(TAG, "Response: " + response.getStatusLine().getStatusCode());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            VehicleOrderRest vehicleOrderPojo = null;
            String output;
            while ((output = reader.readLine()) != null) {
                Log.i(TAG, output);
                ObjectMapper mapper = new ObjectMapper();
                vehicleOrderPojo = mapper.readValue(output, VehicleOrderRest.class);
            }
            Log.i(TAG, "VehicleOrder: " + vehicleOrderPojo);
            return vehicleOrderPojo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}

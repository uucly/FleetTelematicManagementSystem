package masterthesis.master.de.carsharingApp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import rest.VehicleOrderRest;
import server.CentralRestServer;

/**
 * Created by uucly on 15.03.2016.
 */
public class LoadOrderInfoTask extends AsyncTask<Integer, Integer, VehicleOrderRest> {

    private static final String TAG = "LoadOrderInfoTask";
    private TextView orderInfoText;
    private CentralRestServer webserver;

    public LoadOrderInfoTask(TextView orderInfoText, CentralRestServer webserver) {
        this.orderInfoText = orderInfoText;
        this.webserver = webserver;
    }

    @Override
    protected VehicleOrderRest doInBackground(Integer... params) {
        HttpGet getRequest = new HttpGet(webserver.createURL("vehicleOrder", "order", String.valueOf(params[0])));
        getRequest.addHeader("accept", webserver.getContentType());

        DefaultHttpClient client = new DefaultHttpClient();

        try {
            Log.i(TAG, String.valueOf(getRequest.getURI()));
            HttpResponse response = client.execute(getRequest);
            Log.i(TAG, "Response: " + response.getStatusLine().getStatusCode());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            VehicleOrderRest vehicleOrderRest = null;
            String output;
            while ((output = reader.readLine()) != null) {
                Log.i(TAG, output);
                ObjectMapper mapper = new ObjectMapper();
                vehicleOrderRest = mapper.readValue(output, VehicleOrderRest.class);
            }
            Log.i(TAG, "VehicleOrder: " + vehicleOrderRest);
            return vehicleOrderRest;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(VehicleOrderRest vehicleOrderRest) {
        String orderInfo = "No order";
        if(vehicleOrderRest != null){
            orderInfo = "Fahrzeug Nummer: " + vehicleOrderRest.getDeviceID();
            orderInfo += "\nWartezeit: " + roundInfo(vehicleOrderRest.getWaitCosts() * 60) + " Minuten";
            orderInfo += "\nTransportdauer: " + roundInfo(vehicleOrderRest.getTargetCosts() * 60) + " Minuten";
            orderInfo += "\nGesamtdauer: " + roundInfo(vehicleOrderRest.getTotalCosts() * 60) + " Minuten";
        }
        orderInfoText.setText(orderInfo);
    }


    private double roundInfo(double min){
        return Math.round(min*10)/10.;
    }
}

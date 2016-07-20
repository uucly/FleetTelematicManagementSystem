package masterthesis.master.de.carsharingApp;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.internal.or;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import rest.VehicleOrderRest;
import server.CentralRestServer;

/**
 * Created by uucly on 07.03.2016.
 */
public class OrderNewVehicelTask extends AsyncTask<Location, Integer, VehicleOrderRest> {
    private static final String TAG = "NewVehicleTask";
    private static final String IP_HOME = "192.168.0.12";
    private static final String IP_UNI = "10.0.2.2";

    private int orderID;
    private CentralRestServer webserver;
    private TextView orderInfoText;

    public OrderNewVehicelTask(int orderID, CentralRestServer webserver, TextView orderInfoText) {
        this.orderID = orderID;
        this.webserver = webserver;
        this.orderInfoText = orderInfoText;
    }

    @Override
    protected VehicleOrderRest doInBackground(Location[] params) {
        HttpPost target = new HttpPost(webserver.createURL("vehicleOrder", "newVehicleDevice"));
        target.addHeader("accept", webserver.getContentType());

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        DefaultHttpClient client = new DefaultHttpClient(httpParams);

        String targetGeom = "\"target\":\"Point(" + params[1].getLongitude() + "," + params[1].getLatitude() + ")\"";
        String sourceGeom = "\"source\":\"Point(" + params[0].getLongitude() + "," + params[0].getLatitude() + ")\"";
        try {
            StringEntity entity = new StringEntity("{\"orderID\":" + orderID + "," + sourceGeom + "," + targetGeom + "}");
            entity.setContentType("application/json");
            target.setEntity(entity);
            Log.i(TAG, "Post: " + target.getRequestLine().getUri());
            HttpResponse response = client.execute(target);

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
        showOrderInfo(vehicleOrderRest, orderInfoText);
    }

    private static void showOrderInfo(VehicleOrderRest vehicleOrderRest, TextView orderInfoText) {
        if (vehicleOrderRest != null) {
            String orderInfo = "Fahrzeug Nummer: " + vehicleOrderRest.getDeviceID();
            orderInfo += "\nWartezeit: " + roundInfo(vehicleOrderRest.getWaitCosts() * 60) + " Minuten";
            orderInfo += "\nTransportdauer: " + roundInfo(vehicleOrderRest.getTargetCosts() * 60) + " Minuten";
            orderInfo += "\nGesamtdauer: " + roundInfo(vehicleOrderRest.getTotalCosts() * 60) + " Minuten";
            orderInfoText.setText(orderInfo);
        }
    }

    private static double roundInfo(double min) {
        return Math.round(min * 10) / 10.;
    }
}

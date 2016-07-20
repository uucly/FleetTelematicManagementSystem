package masterthesis.master.de.vehicledeviceclient;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
 * Created by uucly on 17.03.2016.
 */
public class LoadCurrentOrderTask extends AsyncTask<Object, Void, VehicleOrderRest>{

    private static final String TAG = "LoadCurrentOrderTask";
    private final CentralRestServer server;
    private int vehicleID;

    public LoadCurrentOrderTask(CentralRestServer server, int vehicleID) {
        this.server = server;
        this.vehicleID = vehicleID;
    }

    @Override
    protected VehicleOrderRest doInBackground(Object[] params) {

        HttpGet httpGet = new HttpGet(server.createURL("vehicleDevice", "getCurrentOrder", String.valueOf(vehicleID)));
        httpGet.addHeader("accept", server.getContentType());
        Log.i(TAG, "Get: " + httpGet.getURI());

        HttpParams httpParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParams,5000);
        DefaultHttpClient client = new DefaultHttpClient(httpParams);
        try {
            HttpResponse response = client.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();

            if(responseCode == 200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String output;
                while ((output = reader.readLine()) != null){
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(output, VehicleOrderRest.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return VehicleOrderRest.EMPTY_ORDER;
    }
}

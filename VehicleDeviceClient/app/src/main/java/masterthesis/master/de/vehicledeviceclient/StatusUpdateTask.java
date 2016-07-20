package masterthesis.master.de.vehicledeviceclient;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URL;

import server.CentralRestServer;

/**
 * Created by uucly on 12.03.2016.
 */
public class StatusUpdateTask extends AsyncTask<Integer, Integer, Boolean> {

    private static final String CLASSTAG = "StatusUpdateTask";

    private final int orderID;
    private CentralRestServer server;

    public StatusUpdateTask(int orderID, CentralRestServer server) {
        this.orderID = orderID;
        this.server = server;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        try {
            return synchronizeLayer(params[0], orderID);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(CLASSTAG, "Error with Transaction in doInBackground : " + e.getMessage() + " - Transaction success status: " + false);
            return false;
        }
    }

    /**
     * Exports new features of a certain layer.
     *
     * @return booleean, false if it created an error
     */
    private boolean synchronizeLayer(int status, int vehicleID) throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPut put = new HttpPut(server.createURL("vehicleDevice", "status"));
        put.addHeader("accept", server.getContentType());
        try {
            String statusEntity = "\"status\":\"" + status + "\"";
            String idEntity = "\"id\":\"" + vehicleID + "\"";
            String stringEntity = "{" + statusEntity + "," + idEntity + "}";
            StringEntity entity = new StringEntity(stringEntity);
            entity.setContentType("application/json");
            put.setEntity(entity);
            Log.e(CLASSTAG, put.getURI().toString());
            HttpResponse response = client.execute(put);
            int responseCode = response.getStatusLine().getStatusCode();
            Log.e(CLASSTAG, "Status update: response code = " + responseCode);
            return responseCode == 200 ? true : false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

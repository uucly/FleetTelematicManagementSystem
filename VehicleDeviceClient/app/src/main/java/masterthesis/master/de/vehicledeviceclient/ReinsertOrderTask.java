package masterthesis.master.de.vehicledeviceclient;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import server.CentralRestServer;

/**
 * Created by uucly on 03.06.2016.
 */
public class ReinsertOrderTask extends AsyncTask {

    private static final String TAG = "ReinsertTask";
    private CentralRestServer server;
    private String vehicleID;

    public ReinsertOrderTask(CentralRestServer server, String vehicleID) {
        this.server = server;
        this.vehicleID = vehicleID;
    }


    @Override
    protected Object doInBackground(Object[] params) {
        HttpPut httpPut = new HttpPut(server.createURL("vehicleDevice", "reinsert"));
        httpPut.addHeader("accept", server.getContentType());
        Log.i(TAG, "Get: " + httpPut.getURI());

        HttpParams httpParams = new BasicHttpParams();
      //  HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        DefaultHttpClient client = new DefaultHttpClient(httpParams);

        try {
            StringEntity entity = new StringEntity("{\"id\":" + "\""+ vehicleID + "\"}");
            entity.setContentType(server.getContentType());
            httpPut.setEntity(entity);
            HttpResponse response = client.execute(httpPut);
            int responseCode = response.getStatusLine().getStatusCode();

            return responseCode == 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

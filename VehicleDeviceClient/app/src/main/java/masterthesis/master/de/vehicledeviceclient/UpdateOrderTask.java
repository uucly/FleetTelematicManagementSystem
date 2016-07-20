package masterthesis.master.de.vehicledeviceclient;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import server.CentralRestServer;

/**
 * Created by uucly on 27.03.2016.
 */
public class UpdateOrderTask extends AsyncTask <Object, Integer, Integer>{

    private CentralRestServer server;

    public UpdateOrderTask(CentralRestServer server) {
        this.server = server;
    }

    @Override
    protected Integer doInBackground(Object[] params) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(server.createURL("vehicleDevice", "updateOrder"));
        httpPut.addHeader("accept", server.getContentType());
        try {
            String stringEntity = "{\"orderID\":\""+ params[0] +"\"}";
            StringEntity entity = new StringEntity(stringEntity);
            entity.setContentType("application/json");
            httpPut.setEntity(entity);
            HttpResponse response = client.execute(httpPut);
            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

package masterthesis.master.de.vehicledeviceclient;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URL;

import server.Geoserver;


/**
 * Created by uucly on 07.03.2016.
 */
public class NewLocationTask extends AsyncTask<Location, Integer, Boolean> {

    private static final String USER = "admin";
    private static final String PASSWORD = "geoserver";

    private static final String CLASSTAG = "WFSLayerSynchronization";

    private final int deviceID;
    private final Geoserver geoserver;

    public NewLocationTask(int deviceID, Geoserver geoserver) {
        this.deviceID = deviceID;
        this.geoserver = geoserver;
    }

    @Override
    protected Boolean doInBackground(Location... params) {
        try {
            return synchronizeLayer(params[0],deviceID);
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
    private boolean synchronizeLayer(Location location, int deviceId) throws Exception {
        String xmlStatement = WfsUpdateGmlGenerator.generate(location, deviceId);
        Log.i(CLASSTAG, "Sent XML Transaction Request: \n" + xmlStatement);
        // Sending HTTP request
        URL url = geoserver.getTransactionURL();//new URL("http://"+ ipAddress + ":" + geoserver.ge +"/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=Transaction");
        Log.i(CLASSTAG, "Sending WFS Transaction Request to URL: \n" + url.toString());
        DefaultHttpClient client = new DefaultHttpClient();
        /// wenn username gesetzt - authenticated abfrage
        client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(USER, PASSWORD));
        HttpPost post = new HttpPost(url.toURI());
        StringEntity data = new StringEntity(xmlStatement);
        data.setContentType("text/xml");
        post.setHeader("Content-Type", "application/xml;charset=UTF-8");
        post.setEntity(data);
        HttpResponse response = client.execute(post);
        // Check for transaction success and update database
        return response.getStatusLine().getStatusCode() == 200;
    }

}

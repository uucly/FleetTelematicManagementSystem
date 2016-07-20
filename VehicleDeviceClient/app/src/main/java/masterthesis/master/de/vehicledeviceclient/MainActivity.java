package masterthesis.master.de.vehicledeviceclient;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ConnectionProperties.PropertiesLoader;
import rest.VehicleOrderRest;
import server.CentralRestServer;
import server.CentralServerProperties;
import server.GeoServerProperties;
import server.Geoserver;

public class MainActivity extends ActionBarActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    private GoogleApiClient googleApiClient;
    private Button statusChangeButton;
    private Button newOrderListenerButton;

    public static TextView newOrderTextView;
    public static EditText vehicleIdEdit;

    private static VehicleOrderRest currentOrder;
    static PropertiesLoader propertiesLoader;
    static CentralRestServer webServer;
    private static Geoserver geoserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadProperties();
        webServer = new CentralRestServer(propertiesLoader.getValue("ipaddress"), new CentralServerProperties(propertiesLoader.getProperties()));
        geoserver = new Geoserver(propertiesLoader.getValue("ipaddress"), new GeoServerProperties(propertiesLoader.getProperties()));

        googleApiClient = createGoogleApiClient(googleApiClient);
        setContentView(R.layout.activity_main);
        currentOrder = loadCurrentOrder(Constants.VEHICLE_ID);

        WakefulIntentService.cancelAlarms(this);
        Toast.makeText(this, "Polling inactive!",
                Toast.LENGTH_LONG).show();

    }

    private void loadProperties() {
        try {
            propertiesLoader = new PropertiesLoader(getBaseContext().getAssets().open("connection.properties"));
        } catch (IOException e) {
            throw new RuntimeException(new FileNotFoundException());
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        statusChangeButton = (Button) findViewById(R.id.statusButton);
        newOrderListenerButton = (Button) findViewById(R.id.newOrderListenerButton);
        newOrderTextView = (TextView) findViewById(R.id.newOrderTextView);
        vehicleIdEdit = (EditText) findViewById(R.id.vehicleIDEdit);
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "New location: " + location);
        try {
            int deviceID = Integer.parseInt(vehicleIdEdit.getText().toString());
            new NewLocationTask(deviceID, geoserver).execute(location);
            if (hasOrder()) {
                new UpdateOrderTask(webServer).execute(getCurrentOrder().getOrderID());
            }
        } catch (NumberFormatException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();
        vehicleIdEdit.setText(String.valueOf(Constants.VEHICLE_ID));

        if (currentOrder != null) {
            newOrderTextView.setText(getOrderInfo(currentOrder));
        } else {
            Log.e(TAG, "Current order is null");
        }
        if (!googleApiClient.isConnected()) {
            Log.e(TAG, "on start: connect google api location");
            googleApiClient.connect();
        } else {
            Log.i(TAG, "google play is connecte. update fragments");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        Log.i(TAG, "on resume. start location updates");
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
        }
    }

    /* methods */


    private VehicleOrderRest loadCurrentOrder(int vehicleID) {
        try {
            return new LoadCurrentOrderTask(webServer, vehicleID).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return VehicleOrderRest.EMPTY_ORDER;
    }

    private GoogleApiClient createGoogleApiClient(GoogleApiClient googleApiClient) {
        if (googleApiClient == null) {
            return new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        } else {
            return googleApiClient;
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public void changeOrderStatus(View view) {
        new StatusUpdateTask(currentOrder.getOrderID(), webServer).execute(2);
        currentOrder = loadCurrentOrder(getVehicleID());
    }

    public void onClickFinish(View view) {
        try {
            new LoadNextOrderTask(getVehicleID(), webServer).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        currentOrder = loadCurrentOrder(getVehicleID());
    }

    public void newOrderListener(View view) {
        int deviceID = Integer.parseInt(vehicleIdEdit.getText().toString());
        currentOrder = loadCurrentOrder(deviceID);
        newOrderTextView.setText(getOrderInfo(currentOrder));
    }

    public void newVehicleOrderListener(View view) {
        WakefulIntentService.scheduleAlarms(new AppListener(), this);
    }

    public void stopNewVehicleOrderListener(View view) {
        WakefulIntentService.cancelAlarms(this);
        Toast.makeText(this, "Polling canceled", Toast.LENGTH_LONG);
    }

    public static VehicleOrderRest getCurrentOrder() {
        return currentOrder != null ? currentOrder : VehicleOrderRest.EMPTY_ORDER;
    }

    public static void setCurrentOrder(Context context, VehicleOrderRest order) {
        currentOrder = order;
        if (!VehicleOrderRest.EMPTY_ORDER.equals(order)) {
            WakefulIntentService.cancelAlarms(context);
            Toast.makeText(context, "Polling canceld", Toast.LENGTH_LONG);
        }
    }

    public static boolean hasOrder() {
        return !VehicleOrderRest.EMPTY_ORDER.equals(getCurrentOrder());
    }

    public int getVehicleID() {
        try {
            return Integer.parseInt(vehicleIdEdit.getText().toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String getOrderInfo(VehicleOrderRest order) {
        String info = "Order ID:" + order.getOrderID();
        info += "\nSource: " + order.getSource();//\"POINT(8.477926 49.000822)\""; //+ order.getSource();
        info += "\nTarget: " + order.getTarget();//\"POINT(8.410697 49.010877)\"";// + order.getTarget();
        return info;
    }

    public void reinsertOrder(View view) {
        new ReinsertOrderTask(webServer, vehicleIdEdit.getText().toString()).execute();
    }
}

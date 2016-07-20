package masterthesis.master.de.carsharingApp;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ConnectionProperties.PropertiesLoader;
import masterthesis.master.de.mymasterthesisclient.R;
import rest.VehicleOrderRest;
import server.CentralRestServer;
import server.CentralServerProperties;

public class MainActivity extends ActionBarActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    AddressResultReceiver mResultReceiver;

    EditText addressEdit;
    //ProgressBar progressBar;
    // TextView infoText;
    CheckBox checkBox;

    Location target;
    boolean fetchAddress;
    int fetchType = Constants.USE_ADDRESS_LOCATION;

    private static final String TAG = "MAIN_ACTIVITY";
    private TextView orderInfoText;
    private VehicleOrderRest vehicleOrderRest;
    private EditText orderIdEdit;

    private PropertiesLoader propertiesLoader;

    private CentralRestServer webserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadProperties();
        webserver = new CentralRestServer(propertiesLoader.getValue("ipaddress"), new CentralServerProperties(propertiesLoader.getProperties()));

        target = new Location("");
        googleApiClient = createGoogleApiClient(googleApiClient);

        addressEdit = (EditText) findViewById(R.id.addressEdit);
        orderIdEdit = (EditText) findViewById(R.id.orderIDEdit);
        // progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //infoText = (TextView) findViewById(R.id.infoText);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        orderInfoText = (TextView) findViewById(R.id.orderInfo);

        mResultReceiver = new AddressResultReceiver(null, this, null, null, target);

        fetchAddress = false;
        fetchType = Constants.USE_ADDRESS_NAME;
        addressEdit.setEnabled(true);
        addressEdit.requestFocus();
    }

    private void loadProperties() {
        try {
            propertiesLoader = new PropertiesLoader(getBaseContext().getAssets().open("connection.properties"));
        } catch (IOException e) {
            throw new RuntimeException(new FileNotFoundException("File not found: connection.properties"));
        }
    }

    private void loadTarget() {
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
        if (addressEdit.getText().length() == 0) {
            Toast.makeText(this, "Please enter an address name", Toast.LENGTH_LONG).show();
            return;
        }
        intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, addressEdit.getText().toString());

        // infoText.setVisibility(View.INVISIBLE);
        // progressBar.setVisibility(View.VISIBLE);
        Log.e(TAG, "Starting Service");
        startService(intent);
    }

    public void onPostClick(View view) {
        loadTarget();
        Location source = getLocation(googleApiClient);
        if (!locationExists(source)) {
            Toast.makeText(this, "No location available", Toast.LENGTH_LONG).show();
            return;
        }
        if (!locationExists(target)) {
            Toast.makeText(this, "Adress is not correct", Toast.LENGTH_LONG).show();
            return;
        }
        String orderID = orderIdEdit.getText().toString();
        if (orderID.equals("") || orderID == null) {
            Toast.makeText(this, "No ID", Toast.LENGTH_LONG).show();
            return;
        }
        Log.i(TAG, "Source: " + source);
        Log.i(TAG, "target: " + target);

        try {
            vehicleOrderRest = new OrderNewVehicelTask(Integer.parseInt(orderID), webserver, orderInfoText).execute(source, target).get();
            showOrderInfo(vehicleOrderRest, orderInfoText);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean locationExists(Location location) {
        return !(location == null || location.getLatitude() == 0 || location.getLongitude() == 0);
    }

    public void onOrderInfoClick(View View) {
        if (vehicleOrderRest == null) {
            try {
                vehicleOrderRest = new LoadOrderInfoTask(orderInfoText, webserver).execute(Integer.parseInt(orderIdEdit.getText().toString())).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            new LoadOrderInfoTask(orderInfoText, webserver).execute(vehicleOrderRest.getOrderID());
        }
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

    @Override
    public void onLocationChanged(Location location) {

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

    @Nullable
    private static Location getLocation(GoogleApiClient googleApiClient) {
        return googleApiClient.isConnected()
                ? LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
                : null;
    }


    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
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
        if (!googleApiClient.isConnected()) {
            Log.e(TAG, "on start: connect google api location");
            googleApiClient.connect();
        } else {
            Log.i(TAG, "google play is connecte. update fragments");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "on stop: disconnect google api location");
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
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










    /* ------------------------------------------------------------- */

    class AddressResultReceiver extends ResultReceiver {

        Parcelable.Creator CREATOR;
        private final Activity context;

        ProgressBar progressBar;
        TextView infoText;

        public Location getTarget() {
            return target;
        }

        private Location target;

        public AddressResultReceiver(Handler handler, Activity context, ProgressBar progressBar, TextView infoText, Location target) {
            super(handler);
            this.context = context;
            this.progressBar = progressBar;
            this.infoText = infoText;
            this.target = target;
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                //target = new Location("");
                target.setLongitude(address.getLongitude());
                target.setLatitude(address.getLatitude());
               /* context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        infoText.setVisibility(View.VISIBLE);
                        infoText.setText("Latitude: " + address.getLatitude() + "\n" +
                                "Longitude: " + address.getLongitude() + "\n" +
                                "Address: " + resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });*/
            } else {
                /*context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        //  infoText.setVisibility(View.VISIBLE);
                        //  infoText.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });*/
            }
        }
    }

}

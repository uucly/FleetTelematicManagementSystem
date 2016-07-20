package masterthesis.master.de.masterapplibrary;

import android.os.AsyncTask;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import rest.VehicleOrderRest;
import server.Server;

/**
 * Created by uucly on 30.03.2016.
 */
public abstract class OrderTask<T,P> extends AsyncTask<T,P, VehicleOrderRest> {

    private final TextView orderInfo;
    private Server server;

    public OrderTask(Server server){
        this(null, server);
    }

    public OrderTask(TextView orderInfo, Server server) {
        this.orderInfo = orderInfo;
        this.server = server;
    }

    @Override
    protected VehicleOrderRest doInBackground(T... ts) {
        return doInBackground(server);
    }

    protected abstract VehicleOrderRest doInBackground(Server server);

    @Override
    protected void onPostExecute(VehicleOrderRest vehicleOrderRest) {
        if(orderInfo != null){
            orderInfo.setText("Dauer bis zum Transportbeginn: " + vehicleOrderRest.getCosts() * 60 + " Minuten\n" + "Tranportdauer: " + vehicleOrderRest.getOrderCosts() * 60 + " Minuten" + "\n\n" + vehicleOrderRest);
        }
    }

    public VehicleOrderRest getSilent(){
        try {
            return this.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return VehicleOrderRest.EMPTY_ORDER;
    }
}

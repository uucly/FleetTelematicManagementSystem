package masterthesis.master.de.vehicledeviceclient;

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.concurrent.ExecutionException;

/**
 * Created by uucly on 10.03.2016.
 */
public class AppService extends WakefulIntentService {
    private static final String TAG = "WakeFull";

    public AppService() {
        super("AppService");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        Log.i(TAG, "Fetch current order");
        try {
            MainActivity.setCurrentOrder(getBaseContext(), new LoadCurrentOrderTask(MainActivity.webServer, Integer.parseInt(MainActivity.vehicleIdEdit.getText().toString())).execute().get());
            Log.i(TAG, MainActivity.getCurrentOrder().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

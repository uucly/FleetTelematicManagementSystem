package masterthesis.master.de.vehicledeviceclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import rest.VehicleOrderRest;

/**
 * Created by uucly on 10.03.2016.
 */
public class AppListener implements WakefulIntentService.AlarmListener {

    public static final int INTERVAL_MILLIS = 10000;
    private static final String TAG = "Wakefull";


    @Override
    public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context ctxt) {
        mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, INTERVAL_MILLIS, pi);
    }

    @Override
    public void sendWakefulWork(Context ctxt) {
        if(MainActivity.hasOrder()){
            MainActivity.newOrderTextView.setText(MainActivity.getCurrentOrder().toString());
            WakefulIntentService.cancelAlarms(ctxt);
            Log.e(TAG, "Canceld Alarm");
        } else {
            WakefulIntentService.sendWakefulWork(ctxt, AppService.class);
            Log.e(TAG, "Try to get new order");
        }

    }

    @Override
    public long getMaxAge() {
        return (INTERVAL_MILLIS);
    }
}

package com.sp.smarttaskmanagerv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "GeofencingEvent error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        for (Geofence geofence : triggeringGeofences) {
            String geofenceId = geofence.getRequestId();
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Toast.makeText(context, "Entered geofence: " + geofenceId, Toast.LENGTH_SHORT).show();
                // Trigger your reminder or task here
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Toast.makeText(context, "Exited geofence: " + geofenceId, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
package jp.xiaomi.wifi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class WifiReceiver extends WakefulBroadcastReceiver {

    public static final String ACTION_FIRE_ALARM_FOR_WIFI = "action.FIRE_ALARM_FOR_WIFI";
    public static final String ACTION_START_WIFI_ANALYZED = "action.START_WIFI_ANALYZED";
    private static final String TAG = WifiReceiver.class.getSimpleName();
    private WifiManager wifiManager;
    private boolean airPlaneModeON;
    private boolean wifiON;
    private boolean permissionGranted;

    public WifiReceiver() {
        Log.d(TAG, "receiver created");
    }

    private boolean canStartWifiScan(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            airPlaneModeON = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, -1) == 1;
        } else {
            airPlaneModeON = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, -1) == 1;
        }
        Log.d(TAG, "airplane mode:" + wifiON);

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            Log.d(TAG, "Wifi enabled");
            wifiON = true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && wifiManager.isScanAlwaysAvailable()) {
            Log.d(TAG, "Wifi disabled but scan available");
            wifiON = true;
        } else {
            Log.d(TAG, "Wifi disabled");
            wifiON = false;
        }

        permissionGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "Permission Granted:" + permissionGranted);

        return wifiON && permissionGranted && !airPlaneModeON;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d(TAG, "action:" + action);
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            airPlaneModeON = intent.getBooleanExtra("state", false);
            Log.d(TAG, "");
        } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)) {
                case WifiManager.WIFI_STATE_ENABLED:
                    wifiON = true;
                    break;
                default:
                    wifiON = false;
                    break;
            }
            Log.d(TAG, "Wifi ON:" + wifiON);
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "result available");
            Intent i = new Intent(context, WifiService.class);
            i.setAction("lolol");
            startWakefulService(context, i);

        } else if (ACTION_FIRE_ALARM_FOR_WIFI.equals(action)) {
            Log.d(TAG, "FIRE ALARM !");
        } else if (ACTION_START_WIFI_ANALYZED.equals(action)) {
            if (canStartWifiScan(context)) {
                Log.d(TAG, "Start wifi analyzed");
                wifiManager.startScan();
            } else {
                Log.w(TAG, "Cannot start, wifi:" + wifiON + ", airplane:" + airPlaneModeON);
            }
        }

    }

}

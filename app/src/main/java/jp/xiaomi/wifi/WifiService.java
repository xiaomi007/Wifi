package jp.xiaomi.wifi;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WifiService extends IntentService {

    private static final String TAG = WifiService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WifiService() {
        super(WifiService.class.getName());
        Log.d(TAG, "Service constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "on handle intent");
        String action = intent.getAction();
        if ("lolol".equals(action)) {
            Log.d(TAG, "scan result");
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            List<ScanResult> resultList = wifiManager.getScanResults();
            if (resultList != null) {
                StringBuilder sb = new StringBuilder();
                for (ScanResult result : resultList) {
                    sb.append(result.SSID).append("; ");
                }
                Log.d(TAG, sb.toString());
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "Service onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service onDestroy");
        super.onDestroy();
    }

}

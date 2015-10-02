package jp.xiaomi.wifi;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int LOCATION_PERMISSION = 1;

    private ComponentName componentReceiver;
    private IntentFilter intentFilter = new IntentFilter();
    private boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        componentReceiver = new ComponentName(this, WifiReceiver.class);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            registerBroadcastReceiver();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }
    }

    @Override
    protected void onDestroy() {
        if (isRegistered) {
            Log.d(TAG, "receiver unregister");
            getPackageManager().setComponentEnabledSetting(componentReceiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION) {
            if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[0]) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerBroadcastReceiver();
            } else {
                Toast.makeText(MainActivity.this, "Request Permission Denied", Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.root), "Go to settings", Snackbar.LENGTH_LONG).setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "settings", Toast.LENGTH_SHORT).show();
                        try {
                            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())));
                        } catch (ActivityNotFoundException e) {
                            Log.e(TAG, "no detail", e);
                            startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
                        }
                    }
                }).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void registerBroadcastReceiver() {
        if (!isRegistered) {
            intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            getPackageManager().setComponentEnabledSetting(
                    componentReceiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
            );
            isRegistered = true;
            Log.d(TAG, "receiver registered");
        }
    }

    public void sendIntent(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!isRegistered) {
                registerBroadcastReceiver();
            }
            Intent intent = new Intent(this, WifiReceiver.class);
            intent.setAction(WifiReceiver.ACTION_FIRE_ALARM_FOR_WIFI);
            sendBroadcast(intent);
        } else {
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }
    }
}

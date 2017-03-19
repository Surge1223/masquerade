package projekt.interfacer.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

import projekt.interfacer.ThemeInterface;
import projekt.interfacer.services.JobService;

public class InterfacerActivity extends Activity {
    private static final String LOG_TAG = InterfacerActivity.class.getSimpleName();
    private String SERVICE_NAME = "overlay";
    ThemeInterface mOverlayManager;
    serviceConnection connection;

    class serviceConnection implements ServiceConnection {
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(LOG_TAG, "Service connected");
                mOverlayManager = ThemeInterface.Stub.asInterface(service);
                Toast.makeText(InterfacerActivity.this, "Service connected", Toast.LENGTH_SHORT).show();
                try {
                    mOverlayManager.setData("SexyAf");
                } catch (RemoteException e) {

                }
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.e(LOG_TAG, "disconnected");
                mOverlayManager = null;
                Toast.makeText(InterfacerActivity.this, "Service disconnected", Toast.LENGTH_SHORT)
                        .show();
            }

        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    getOverlayManagerService();
        System.out.println("Interfacer onCreate: getUid()=" + android.os.Process.myUid());
        bindService();
        getOverlayManagerService();
        finish();
    }


    @Override
    public void onDestroy() {
        System.out.println("Interfacer onDestroy: this=" + this);
        unbindService();
        super.onDestroy();
    }

    private void bindService() {
        Context context = getApplicationContext();
        Intent i = new Intent(this, JobService.class);
        bindService(i, connection, Context.BIND_AUTO_CREATE);
        context.startService(i);
        if (!bindService(i, connection, Context.BIND_AUTO_CREATE)) {
            Toast.makeText(InterfacerActivity.this, "Bind Service Failed", Toast.LENGTH_LONG)
                    .show();
        }
        Log.d(LOG_TAG, "Interfacer startng JobService!");
    //       System.loadLibrary("oms");
        Log.d(LOG_TAG, "UID " + this.getUserId());

    }

    private void unbindService() {
        unbindService(connection);
        connection = null;
    }

    @Override
    public void finalize() {
        System.out.println("Interfacer finalize: this=" + this);
    }

    /*
     * Get binder service
     */
    private void getOverlayManagerService()
    {

        IBinder binder=null;
        Log.d(LOG_TAG,"getOverlayManagerService");
        try{
            Object object = new Object();
            Method getService = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            Object obj = getService.invoke(object, new Object[]{new String(SERVICE_NAME)});
            binder = (IBinder)obj;
        }catch(Exception e){
            Log.d(LOG_TAG, e.toString());
        }
        if(binder != null){
            mOverlayManager = projekt.interfacer.ThemeInterface.Stub.asInterface(binder);
            Log.d(LOG_TAG, "InterfacerActivity found binder");
        }
        else
            Log.d(LOG_TAG,"Service is null.");
    }
}



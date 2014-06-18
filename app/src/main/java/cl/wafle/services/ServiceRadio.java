package cl.wafle.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import cl.wafle.R;
import cl.wafle.utils.Constants;
import io.vov.vitamio.MediaPlayer;

/**
 * Created by ezepeda on 18-06-14.
 */
public class ServiceRadio extends Service {
    private final String TAG = this.getClass().getName();

    private MediaPlayer mMediaPlayer;
    private final IBinder mBinder = new RadioBinder();
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onStartCommand");

        if(intent != null){
            if(intent.getAction().equals(Constants.service_mediaplayer_play)){
                generateNotification();
                Log.i(TAG, "onStartCommand - PLAY");
            }else if(intent.getAction().equals(Constants.service_mediaplayer_stop)){
                Log.i(TAG, "onStartCommand - STOP");
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "onBind", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "onUnbind", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Toast.makeText(this, "onRebind", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onRebind");
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Toast.makeText(this, "onTaskRemoved", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onTaskRemoved");
    }


    public void generateNotification(){
        RemoteViews statusBarMinimal = new RemoteViews(getPackageName(), R.layout.statusbar_minimal);
        RemoteViews statusBarExpanded = new RemoteViews(getPackageName(), R.layout.statusbar_expanded);

        Notification notification = new Notification();

        if(notification != null){
            notification.contentView = statusBarMinimal;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification.bigContentView = statusBarExpanded;
            }

            notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        }

        if(mNotificationManager != null){
            mNotificationManager.notify();
        }


    }


    public class RadioBinder extends Binder {
        public ServiceRadio getService() {
            return ServiceRadio.this;
        }
    }
}

package cl.wafle.mediaplayer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import cl.wafle.mediaplayer.R;
import cl.wafle.mediaplayer.utils.Constants;
import cl.wafle.mediaplayer.views.Index;
import io.vov.vitamio.MediaPlayer;

/**
 * Created by ezepeda on 18-06-14.
 */
public class ServiceRadio extends Service implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener,
                                                    MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener{
    private final String TAG = ServiceRadio.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private final IBinder mBinder = new RadioBinder();
    private NotificationManager mNotificationManager;

    private String mediaName;
    private String status;

    private boolean isRunning = false;

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

                setRunning(true);
                Log.i(TAG, "onStartCommand - PLAY");

                if(intent.hasExtra("url")){
                    setUpMediaPlayer(intent.getStringExtra("url"));
                }

                if(intent.hasExtra("name")){
                    mediaName = intent.getStringExtra("name");
                    generateNotification(mediaName, null, null);
                }

            }else if(intent.getAction().equals(Constants.service_mediaplayer_stop)){
                Log.i(TAG, "onStartCommand - STOP");
                setRunning(false);
                removeNotification();
                stopMediaPlayer();
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
        removeNotification();
        stopMediaPlayer();
        super.onTaskRemoved(rootIntent);
        Toast.makeText(this, "onTaskRemoved", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onTaskRemoved");
    }


    //MEDIAPLAYER LISTENERS
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Log.v(TAG, "MediaPlayer => onBufferingUpdate");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what){
            case MediaPlayer.MEDIA_ERROR_IO:
                Log.v(TAG, "MediaPlayer => MEDIA_ERROR_IO");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.v(TAG, "MediaPlayer => MEDIA_ERROR_TIMED_OUT");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Log.v(TAG, "MediaPlayer => MEDIA_ERROR_MALFORMED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.v(TAG, "MediaPlayer => MEDIA_ERROR_UNKNOWN");
                sendBroadcastEventStatus(Constants.STATUS_FAILED);
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Log.v(TAG, "MediaPlayer => MEDIA_ERROR_UNSUPPORTED");
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        sendBroadcastEventStatus(Constants.STATUS_LIVE);
        sendBroadCastEventValues(Constants.receiver_mediaplayer_content_medianame, mediaName);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what){
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.v(TAG, "MediaPlayer Buffering Start");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.v(TAG, "MediaPlayer Buffering End");
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                break;
        }

        return false;
    }

    //GETTER AND SETTER
    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void generateNotification(String nameMedia, String artistName, String trackName){
        RemoteViews statusBarMinimal = new RemoteViews(getPackageName(), R.layout.statusbar_minimal);
        statusBarMinimal.setTextViewText(R.id.tvNotificationMediaName, nameMedia);

        if(trackName != null) {
            statusBarMinimal.setTextViewText(R.id.tvNotificationTrackName, trackName);
        }


        RemoteViews statusBarExpanded = new RemoteViews(getPackageName(), R.layout.statusbar_expanded);

        statusBarExpanded.setTextViewText(R.id.tvNotificationMediaName, nameMedia);
        if(artistName != null) {
            statusBarExpanded.setTextViewText(R.id.tvNotificationArtistName, artistName);
        }

        if(trackName != null) {
            statusBarExpanded.setTextViewText(R.id.tvNotificationTrackName, trackName);
        }


        Intent intent = new Intent(getBaseContext(), Index.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Constants.ID_NOTIFICATION_SERVICE_RADIO, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContent(statusBarMinimal);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, Index.class);
        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Index.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //remoteViews.setOnClickPendingIntent(R.id.button1, resultPendingIntent);

        Notification notification = mBuilder.build();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) {
            notification.bigContentView = statusBarExpanded;
        }
        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR;

        // mId allows you to update the notification later on.
        startForeground(Constants.ID_NOTIFICATION_SERVICE_RADIO, notification);

    }

    public void removeNotification(){
        stopForeground(true);
    }

    public class RadioBinder extends Binder {
        public ServiceRadio getService() {
            return ServiceRadio.this;
        }
    }

    public void setUpMediaPlayer(String url){
        try{
            sendBroadcastEventStatus(Constants.STATUS_BUFFERING);

            if(mMediaPlayer != null){
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }

            mMediaPlayer = new MediaPlayer(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();


        }catch (Exception ex){
            ex.printStackTrace();
            Log.e(TAG, "setUpMediaPlayer => "+ex.getMessage());
        }
    }

    public void stopMediaPlayer(){
        try{
            if(mMediaPlayer != null){
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }

            sendBroadcastEventStatus(Constants.STATUS_STOPPED);
        }catch (Exception ex){
            ex.printStackTrace();
            Log.e(TAG, " stopMediaPlayer => "+ex.getMessage());
        }
    }

    public void sendBroadcastEventStatus(String status){
        Log.i(TAG, "sendBroadcastEventStatus");
        this.status = status;
        Intent intent = new Intent(Constants.receiver_mediaplayer_status);
        intent.putExtra(Constants.receiver_mediaplayer_status, status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendBroadCastEventValues(String type, String value){
        Log.i(TAG, "sendBroadCastEventValues");
        Intent intent = new Intent(Constants.receiver_mediaplayer_content);
        intent.putExtra(type, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }



}

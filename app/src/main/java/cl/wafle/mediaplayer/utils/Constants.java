package cl.wafle.mediaplayer.utils;

/**
 * Created by ezepeda on 18-06-14.
 */
public final class Constants {

    //Numero de identificador del servicio de radio
    public final static int ID_NOTIFICATION_SERVICE_RADIO = 5000;

    //Constantes de las acciones del servicio
    public final static String service_mediaplayer_play = "cl.wafle.mediaplayer.services.PLAY";
    public final static String service_mediaplayer_stop = "cl.wafle.mediaplayer.services.STOP";

    //Constantes del receiver del servicio
    public final static String receiver_mediaplayer_status = "cl.wafle.mediaplayer.STATUS";

    //Constants status
    public final static String STATUS_BUFFERING = "buffering";
    public final static String STATUS_LIVE = "en vivo";
    public final static String STATUS_STOPPED = "stopped";
    public final static String STATUS_FAILED = "failed";

    //Constants Content
    public final static String receiver_mediaplayer_content = "cl.wafle.mediaplayer.CONTENT";
    public final static String receiver_mediaplayer_content_trackname = "cl.wafle.mediaplayer.TRACKNAME";
    public final static String receiver_mediaplauer_content_artist = "cl.wafle.mediaplayer.ARTIST";
    public final static String receiver_mediaplayer_content_medianame = "cl.wafle.mediaplayer.MEDIANAME";


}

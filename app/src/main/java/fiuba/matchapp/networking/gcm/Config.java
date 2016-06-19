package fiuba.matchapp.networking.gcm;

/**
 * Created by german on 4/21/2016.
 */
public class Config {

    // flag to identify whether to show single line
    // or multi line text in push notification tray
    public static boolean appendNotificationMessages = false;

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final String PUSH_TYPE_NEW_MATCH = "1";
    public static final String PUSH_TYPE_NEW_MESSAGE = "2";

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

}

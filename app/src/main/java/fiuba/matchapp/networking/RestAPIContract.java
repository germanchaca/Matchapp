package fiuba.matchapp.networking;

/**
 * Created by german on 4/21/2016.
 */
public class RestAPIContract {

    // localhost url -
    public static final String BASE_URL = "http://localhost:8080/gcm_chat/v1";

    private static final String GET_USER = BASE_URL + "/users/_ID_";
    private static final String PUT_USER = BASE_URL + "/users/_ID_";

    //Alta de usuario
    public static final String POST_USER = BASE_URL + "/users";

    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";

    //Consulta perfil usuario
    public static String GET_USER(String userId) {
        return GET_USER.replace("_ID_", userId);
    }
    //Modificacion de perfil de usuario
    public static String PUT_USER(String userId) {
        return GET_USER.replace("_ID_", userId);
    }
}

package fiuba.matchapp.networking;

/**
 * Created by german on 4/21/2016.
 */
public class RestAPIContract {

    // localhost url -
    public static final String BASE_URL = "http://192.168.1.107:8080";

    private static final String GET_USER = BASE_URL + "/users/_ID_";
    private static final String PUT_USER = BASE_URL + "/users/_ID_";
    private static final String PUT_PHOTO_USER = BASE_URL + "/users/_ID_//photo/";

    //Alta de usuario
    public static final String POST_USER = BASE_URL + "/users/";

    //Sign In de usuario
    public static final String POST_SIGN_IN = BASE_URL + "/users/token/singin/";

    //Consulta de intereses disponibles para que el usuario elija
    public static final String GET_INTERESTS = BASE_URL + "/interests/";


    //Post de Token cada vez que me loggeo
    public static final String LOGIN = BASE_URL + "/token";

    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";

    //Consulta perfil usuario
    public static String GET_USER(String userId) {
        return GET_USER.replace("_ID_", userId);
    }

    //Modificacion de perfil de usuario
    public static String PUT_USER(String userId) {
        return PUT_USER.replace("_ID_", userId);
    }

    //Modificacion de foto del perfil de usuario
    public static String PUT_PHOTO_USER(String userId) {
        return PUT_PHOTO_USER.replace("_ID_", userId);
    }


}

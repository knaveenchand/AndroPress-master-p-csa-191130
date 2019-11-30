package ir.parishkaar.wordpressjsonclient.app;

/**
 * Created by admin on 06/08/19.
 */

public class AppConfig {

    public static String URL = "http://pestoscope.ekrishi.net/";
//    public static String ENDPOINT_LOGIN = "wp-json/user/login";
//    public static String ENDPOINT_REGISTER = "wp-json/user/register";

    public static String ENDPOINT_LOGIN = "api/user/generate_auth_cookie/?insecure=cool";
    public static String ENDPOINT_REGISTER = "api/user/register/?insecure=cool";
        public static String MIDPOINT_REGISTER = "api/get_nonce/?controller=user&method=register&insecure=cool";


//    get nonce http://pestoscope.ekrishi.net/api/get_nonce/?controller=user&method=register

//    http://localhost/api/user/register/?username=john&email=john@domain.com&nonce=8bdfeb4e16&display_name=John&notify=both&user_pass=YOUR-PASSWORD


//    http://pestoscope.ekrishi.net/api/user/generate_auth_cookie/?insecure=cool&username=xyz@mno.org&password=menyou

}

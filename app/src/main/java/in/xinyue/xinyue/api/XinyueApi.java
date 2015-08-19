package in.xinyue.xinyue.api;

/**
 * Created by pzheng on 7/24/2015.
 */
public class XinyueApi {

    private static final String HOST = "http://www.xinyue.in/wp-json/posts/";

    public static final String LIST = HOST + "?filter[category_name]=%1$s&page=%2$s";
}

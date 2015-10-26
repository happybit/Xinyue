package in.xinyue.xinyue.request;


public interface RequestCallback {
    void onRetrievePosts();
    void onVolleyError();
    void onNoMorePosts();
    void onNoDataConnection();
}

package in.xinyue.xinyue.request;


import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import in.xinyue.xinyue.api.XinyueApi;
import in.xinyue.xinyue.request.json.PostJson;
import in.xinyue.xinyue.ui.fragment.ContentFragment;

public class AsyncQueryRequest {
    private Context context;
    private String pageNum;
    private String category;
    private ContentFragment fragment;

    public AsyncQueryRequest(Context context, String category, String pageNum, ContentFragment fragment) {
        this.context = context;
        this.category = category;
        this.pageNum = pageNum;
        this.fragment = fragment;
    }

    public void loadingPage() {
        if (isInternetConnected()) {
            queryPosts(category, pageNum);
        } else {
            fragment.dismissProgressBarAndMakeToastIfNoDataConnection();
            return;
        }
    }

    private boolean isInternetConnected() {
        return DataAndWifiConnectionStatus.isInternetConnected(context);
    }

    private void queryPosts(String category, final String pageNumber) {
        String url = String.format(XinyueApi.LIST, category, pageNumber);
        Log.d(XinyueApi.XINYUE_LOG_TAG, "request url is: " + url);
        GsonRequest<PostJson[]> gsonRequest = new GsonRequest<>(url,
                PostJson[].class,
                null,
                new ResponseListener(context, fragment),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(XinyueApi.XINYUE_LOG_TAG, volleyError.toString());
                        fragment.dismissProgressBarIfLoadFailed();
                    }
                });

        RequestSingleton.getInstance(context).addToRequestQueue(gsonRequest);
    }

}

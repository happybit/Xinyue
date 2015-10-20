package in.xinyue.xinyue.request;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Arrays;
import java.util.List;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.api.XinyueApi;
import in.xinyue.xinyue.contentprovider.PostContentProvider;
import in.xinyue.xinyue.contentprovider.database.PostReaderContract;
import in.xinyue.xinyue.request.json.PostJson;
import in.xinyue.xinyue.request.json.TermsJson;

public class AsyncQueryRequest {
    private Context context;
    private String pageNum;
    private String category;

    public AsyncQueryRequest(Context context, String category, String pageNum) {
        this.context = context;
        this.category = category;
        this.pageNum = pageNum;
    }

    public void loadingDataAndRefreshLayout() {
        queryRequestOrThrowExceptionIfFailed(pageNum);
    }

    private void queryRequestOrThrowExceptionIfFailed(String pageNum)
            throws NoDataConnectionException {
        if (isInternetConnected()) {
            queryPosts(category, pageNum);
        } else {
            throw new NoDataConnectionException("Data or WiFi is not connected!");
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
                new XinyueResponseListener(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                            throws VolleyErrorException {
                        Log.d(XinyueApi.XINYUE_LOG_TAG, volleyError.toString());
                        throw new VolleyErrorException("Volley error!");
                    }
                });

        RequestSingleton.getInstance(context).addToRequestQueue(gsonRequest);
    }

    private class XinyueResponseListener implements Response.Listener<PostJson[]> {
        @Override
        public void onResponse(PostJson[] response) {
            List<PostJson> posts = Arrays.asList(response);
            checkResponseAndReturnIfNoMorePosts(posts);
            insertPostsToDB(posts);
        }

        private void checkResponseAndReturnIfNoMorePosts(List<PostJson> posts)
                throws NoMorePostsException {
            if (posts.isEmpty()) {
                throw new NoMorePostsException("No more posts!");
            }
        }

        private void insertPostsToDB(List<PostJson> posts) {
            for(int i=0; i<posts.size(); i++) {
                PostJson post = posts.get(i);
                insertSinglePost(post);
            }
        }

        private void insertSinglePost(PostJson post) {
            ContentValues postValues = getPostValues(post);
            if (context == null) return;
            Uri providerUri = context.getContentResolver().
                    insert(PostContentProvider.CONTENT_URI, postValues);
        }

        private ContentValues getPostValues(PostJson post) {
            String postId = String.valueOf(post.getID());
            String title = post.getTitle();
            String category = getCategory(post);
            String createddate = post.getDate();
            String link = post.getLink();
            String cover = getCoverOrSetDefaultIfFail(post);
            String content = post.getContent();

            ContentValues postValues = new ContentValues();
            postValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, postId);
            postValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, title);
            postValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, category);
            postValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, cover);
            postValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, createddate);
            postValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, link);
            postValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, content);
            return postValues;
        }

        private String getCoverOrSetDefaultIfFail(PostJson post) {
            return (isImageExisted(post) ? getFeaturedImage(post) : getDefaultImage());
        }

        @NonNull
        private String getDefaultImage() {
            return "drawable://" + R.drawable.fail_empty_image;
        }

        private String getFeaturedImage(PostJson post) {
            return post.getFeatured_image().getSource();
        }

        private boolean isImageExisted(PostJson post) {
            return post.getFeatured_image() != null;
        }

        private String getCategory(PostJson post) {
            List<TermsJson.CategoryJson> categories = post.getTerms().getCategory();
            StringBuilder categorySb = new StringBuilder();
            appendCategoryForAllSlugNames(categories, categorySb);
            return categorySb.toString();
        }

        private void appendCategoryForAllSlugNames(List<TermsJson.CategoryJson> categories,
                                                   StringBuilder categorySb) {
            for (TermsJson.CategoryJson category : categories) {
                String slugName = category.getSlug() + "; ";
                categorySb.append(slugName);
            }
        }
    }

    public class NoDataConnectionException extends RuntimeException {
        public NoDataConnectionException (String msg){
            super(msg);
        }
    }

    public class VolleyErrorException extends RuntimeException {
        public VolleyErrorException (String msg){
            super(msg);
        }
    }

    public class NoMorePostsException extends RuntimeException {
        public NoMorePostsException (String msg){
            super(msg);
        }
    }

}

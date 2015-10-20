package in.xinyue.xinyue.contentprovider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import in.xinyue.xinyue.request.AsyncQueryRequest;

public class PostsCursorLoader extends CursorLoader {

    private AsyncQueryRequest request;

    public PostsCursorLoader(Context context, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder,
                             String category, String pageNum) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        request = new AsyncQueryRequest(context, category, pageNum);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor c = super.loadInBackground();
        request.loadingDataAndRefreshLayout();
        return c;
    }



}

package in.xinyue.xinyue.ui.adapter;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.api.XinyueApi;
import in.xinyue.xinyue.contentprovider.database.PostReaderContract;
import in.xinyue.xinyue.request.UILImageGetter;

/**
 * view binder to set title and cover for each row.
 */
public class ListViewBinder implements SimpleCursorAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (isTitle(cursor, columnIndex)) {
            return setTitle(view, cursor.getString(columnIndex));
        } else if (isCover(cursor, columnIndex)) {
            return setCover(view, cursor.getString(columnIndex));
        }

        return false;
    }

    private boolean isTitle(Cursor cursor, int columnIndex) {
        return columnIndex == cursor.getColumnIndexOrThrow(PostReaderContract.
                PostTable.COLUMN_NAME_TITLE);
    }

    private boolean isCover(Cursor cursor, int columnIndex) {
        return columnIndex == cursor.getColumnIndexOrThrow(PostReaderContract.
                PostTable.COLUMN_NAME_COVER);
    }

    private boolean setTitle(View view, String title) {
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);
        return true;
    }

    private boolean setCover(View view, String imageUri) {
        Log.d(XinyueApi.XINYUE_LOG_TAG, "image url: " + imageUri);
        ImageView coverView = (ImageView) view.findViewById(R.id.cover);
        ImageAware imageAware = new ImageViewAware(coverView, false);
        DisplayImageOptions options = UILImageGetter.getDisplayImageOptions();
        ImageLoader.getInstance().displayImage(imageUri, imageAware, options);
        return true;
    }
}
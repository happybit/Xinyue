package in.xinyue.xinyue.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import in.xinyue.xinyue.R;

/**
 * Created by pzheng on 7/22/2015.
 */
public class UILImageGetter implements Html.ImageGetter {

    TextView mTextView;
    Context mContext;

    public UILImageGetter(TextView textView, Context context) {
        this.mTextView = textView;
        this.mContext = context;
    }

    @Override
    public Drawable getDrawable(String source) {
        final URLDrawable urlDrawable = new URLDrawable();
        Log.d("XinyueLog", source);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        ImageLoader.getInstance().loadImage(source, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                 urlDrawable.setBitmap(loadedImage);
                Log.d("XinyueLog", "Drawable width: " + loadedImage.getWidth());
                Log.d("XinyueLog", "Drawable height: " + loadedImage.getHeight());
                urlDrawable.setBounds(getRect(loadedImage));
                mTextView.invalidate();
                mTextView.setText(mTextView.getText());
            }
        });

        return urlDrawable;
    }

    public Rect getRect(Bitmap bitmap) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int viewWidth = size.x -
                2 * mContext.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);

        // int viewWidth = mTextView.getWidth();
        Log.d("XinyueLog", "View width: " + viewWidth);
        return new Rect(0, 0, viewWidth,
                viewWidth * bitmap.getHeight() / bitmap.getWidth());
    }


    private class URLDrawable extends BitmapDrawable {
        private Bitmap bitmap;
        Paint paint = new Paint();

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void draw(Canvas canvas) {
            if (bitmap != null) {
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setDither(true);

                canvas.drawBitmap(bitmap, null, getRect(bitmap), paint);

                Log.d("XinyueLog", "bitmap width: " + bitmap.getWidth());
                Log.d("XinyueLog", "bitmap height: " + bitmap.getHeight());

            }
        }
    }

}

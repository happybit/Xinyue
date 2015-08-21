package in.xinyue.xinyue.ui;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.contentprovider.PostContentProvider;
import in.xinyue.xinyue.database.PostReaderContract;
import in.xinyue.xinyue.database.UILImageGetter;
import in.xinyue.xinyue.swipeback.SwipeBackActivity;

public class PostDetailActivity extends SwipeBackActivity {

    private Toolbar toolbar;
    private TextView mPostTitle;
    private TextView mPostCategory;
    private TextView mPostContent;

    private Uri postUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPostTitle = (TextView) findViewById(R.id.post_title);
        mPostCategory = (TextView) findViewById(R.id.post_category);
        mPostContent = (TextView) findViewById(R.id.post_content);

        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
        postUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState
                .getParcelable(PostContentProvider.CONTENT_ITEM_TYPE);

        if (extras != null) {
            Log.d("XinyueLog", "extras is not null");
            postUri = extras.getParcelable(PostContentProvider.CONTENT_ITEM_TYPE);

            if (postUri != null) {
                Log.d("XinyueLog", postUri.toString());
            }

            fillData(postUri);
        }
    }

    private void fillData(Uri uri) {
        String[] projection = {PostReaderContract.PostTable.COLUMN_NAME_TITLE,
                PostReaderContract.PostTable.COLUMN_NAME_CATEGORY,
                PostReaderContract.PostTable.COLUMN_NAME_CONTENT};

        Log.d("XinyueLog", uri.toString());

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            Log.d("XinyueLog", "cursor is not null");

            cursor.moveToFirst();
            mPostTitle.setText(cursor.getString(cursor.
                    getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_TITLE)));
            mPostCategory.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY)));
            mPostContent.setText(Html.fromHtml(cursor.getString(cursor.
                    getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CONTENT)),
                    new UILImageGetter(mPostContent, this), null));

            setTitle(cursor.getString(cursor.
                    getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_TITLE)));

            Log.d("XinyueLog", "title is " + cursor.getString(cursor.
                    getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_TITLE)));
            Log.d("XinyueLog", "category is " + cursor
                    .getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY));
            Log.d("XinyueLog", "category is " + cursor
                    .getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CONTENT));

            // always close the cursor
            cursor.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}

package in.xinyue.xinyue.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.contentprovider.PostContentProvider;
import in.xinyue.xinyue.request.RequestSingleton;
import in.xinyue.xinyue.contentprovider.database.PostReaderContract;
import in.xinyue.xinyue.request.UILImageGetter;
import in.xinyue.xinyue.api.Category;
import in.xinyue.xinyue.view.swipeback.SwipeBackActivity;

public class PostDetailActivity extends SwipeBackActivity {

    private Toolbar toolbar;
    private TextView mPostTitle;
    private TextView mPostCategory;
    private TextView mPostContent;
    private String mWebUrl;
    private String title;
    private ShareActionProvider shareActionProvider;

    private Uri postUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(12.0f);
        }

        setTitle(getResources().getString(R.string.post_detail_title));

        mPostTitle = (TextView) findViewById(R.id.post_title);
        mPostCategory = (TextView) findViewById(R.id.post_category);
        mPostContent = (TextView) findViewById(R.id.post_content);
        mWebUrl = "http://www.xinyue.in";

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
                PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE,
                PostReaderContract.PostTable.COLUMN_NAME_LINK,
                PostReaderContract.PostTable.COLUMN_NAME_CONTENT};

        Log.d("XinyueLog", uri.toString());

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            Log.d("XinyueLog", "cursor is not null");

            cursor.moveToFirst();

            title = cursor.getString(cursor.
                    getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_TITLE));
            mPostTitle.setText(title);

            String createdDate = cursor.getString(cursor.getColumnIndexOrThrow(PostReaderContract
                    .PostTable.COLUMN_NAME_CREATED_DATE));
            mWebUrl = cursor.getString(cursor.getColumnIndexOrThrow(PostReaderContract
                    .PostTable.COLUMN_NAME_LINK));
            mPostCategory.setText(getCategoryName(cursor.getString(cursor
                    .getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY)),
                    createdDate));
            mPostContent.setText(Html.fromHtml(cursor.getString(cursor.
                            getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CONTENT)),
                    new UILImageGetter(mPostContent, this), null));

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

    private String getCategoryName(String categoryString, String date) {
        StringBuilder categoryName = new StringBuilder();
        String[] categoryArray = getResources().getStringArray(R.array.categories);
        if (categoryString.contains("earrings")) {
            categoryName.append(categoryArray[Category.earrings.ordinal()]);
        } else if (categoryString.contains("pendants")) {
            categoryName.append(categoryArray[Category.pendants.ordinal()]);
        } else if (categoryString.contains("necklaces")) {
            categoryName.append(categoryArray[Category.necklaces.ordinal()]);
        } else if (categoryString.contains("; rings;")) {
            categoryName.append(categoryArray[Category.rings.ordinal()]);
        } else if (categoryString.contains("bracelets")) {
            categoryName.append(categoryArray[Category.bracelets.ordinal()]);
        } else if (categoryString.contains("brooches")) {
            categoryName.append(categoryArray[Category.brooches.ordinal()]);
        } else {
            categoryName.append(getResources().getString(R.string.category_others));
        }

        date = date.replace("T", " ");

        return date + "\n" +
                getResources().getString(R.string.category) + ": " + categoryName.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());

        return true;
    }

    private Intent createShareIntent() {
        String shareIndication = getResources().getString(R.string.share_indication);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareIndication + mWebUrl + " " + title);
        return shareIntent;
    }

    private void setShareIntent(Intent shareIntent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menu_item_share:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestSingleton.cancelAll(this);
    }
}

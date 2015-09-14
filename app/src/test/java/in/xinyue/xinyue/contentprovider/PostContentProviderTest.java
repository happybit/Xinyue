package in.xinyue.xinyue.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import junit.framework.Assert;

import org.apache.http.auth.AUTH;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowContentResolver;

import in.xinyue.xinyue.BuildConfig;
import in.xinyue.xinyue.database.PostReaderContract;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PostContentProviderTest {

    private static final Uri CONTENT_URI = Uri.parse("content://in.xinyue.xinyue.provider/posts");

    private ContentResolver contentResolver;
    private ShadowContentResolver scr;
    private PostContentProvider provider;

    @Before
    public void setUp() {
        contentResolver = ShadowApplication.getInstance().getContentResolver();
        scr = Shadows.shadowOf(contentResolver);
        provider = new PostContentProvider();
        provider.onCreate();
        ShadowContentResolver.registerProvider(CONTENT_URI.toString(), provider);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkInsertWhenNoPostID_shouldThrowIllegalArgumentException() {
        ContentValues testValues = new ContentValues();
        //testValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, "1");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, "post name");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, "rings");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, "http://test.cover.com/image");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, "20150504");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, "http://test.com");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, "test content");

        scr.insert(CONTENT_URI, testValues);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkInsertWhenNoTitle_shouldThrowIllegalArgumentException() {
        ContentValues testValues = new ContentValues();
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, "1");
        //testValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, "post name");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, "rings");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, "http://test.cover.com/image");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, "20150504");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, "http://test.com");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, "test content");

        scr.insert(CONTENT_URI, testValues);
    }

    @Test
    public void checkInsertWhenNoCategory_shouldFillCategoryAsAll() {
        ContentValues testValues = new ContentValues();
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, "1");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, "post name");
        //testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, "rings");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, "http://test.cover.com/image");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, "20150504");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, "http://test.com");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, "test content");

        scr.insert(CONTENT_URI, testValues);
        Cursor result = scr.query(CONTENT_URI, null, null, null, null);
        result.moveToFirst();
        Assert.assertEquals("all", result.getString(result.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY)));

    }

    @Test (expected = IllegalArgumentException.class)
    public void checkInsertWhenNoCover_shouldThrowIllegalArgumentException() {
        ContentValues testValues = new ContentValues();
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, "1");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, "post name");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, "rings");
        //testValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, "http://test.cover.com/image");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, "20150504");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, "http://test.com");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, "test content");

        scr.insert(CONTENT_URI, testValues);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkInsertWhenNoCreatedDate_shouldThrowIllegalArgumentException() {
        ContentValues testValues = new ContentValues();
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, "1");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, "post name");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, "rings");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, "http://test.cover.com/image");
        //testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, "20150504");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, "http://test.com");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, "test content");

        scr.insert(CONTENT_URI, testValues);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkInsertWhenNoLink_shouldThrowIllegalArgumentException() {
        ContentValues testValues = new ContentValues();
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, "1");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, "post name");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, "rings");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, "http://test.cover.com/image");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, "20150504");
        //testValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, "http://test.com");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, "test content");

        scr.insert(CONTENT_URI, testValues);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkInsertWhenNoContent_shouldThrowIllegalArgumentException() {
        ContentValues testValues = new ContentValues();
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, "1");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, "post name");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, "rings");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, "http://test.cover.com/image");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, "20150504");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, "http://test.com");
        //testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, "test content");

        scr.insert(CONTENT_URI, testValues);
    }

}

package in.xinyue.xinyue.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import junit.framework.Assert;

import org.apache.http.auth.AUTH;
import org.junit.Before;
import org.junit.Ignore;
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

    @Test
    @Ignore
    public void checkContentProviderUpdate() {

    }

    @Test
    @Ignore
    public void checkContentProviderDelete() {

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

    @Test
    public void checkNormalInsertAndQuery_shouldInsertCorrectly() {
        ContentValues testValues = putContentValues();

        Uri uri = scr.insert(CONTENT_URI, testValues);

        Cursor result = scr.query(CONTENT_URI, null, null, null, null);
        result.moveToFirst();
        assertNormalInsertAndQuery(result);
    }

    @Test (expected = UnsupportedOperationException.class)
    public void checkInsertWithIncorrectUri_shouldThrowUnsupportedOperationException() {
        ContentValues testValues = putContentValues();

        scr.insert(Uri.parse("content://in.xinyue.xinyue.provider/post"), testValues);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkInsertWithNullContentValues_shouldThrowIllegalArgumentException() {
        ContentValues testValues = null;

        scr.insert(CONTENT_URI, testValues);
    }

    @Test
    public void checkInsertDuplicateValues_shouldReturnFirstUri() {
        ContentValues testValues = putContentValues();

        Uri firstUri = scr.insert(CONTENT_URI, testValues);
        Uri secondUri = scr.insert(CONTENT_URI, testValues);
        Assert.assertEquals(Uri.parse("content://in.xinyue.xinyue.provider/posts/1"), secondUri);
    }

    @NonNull
    private ContentValues putContentValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, "1");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, "post name");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, "rings");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, "http://test.cover.com/image");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, "20150504");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, "http://test.com");
        testValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, "test content");
        return testValues;
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkQueryWithInvalidUri_shouldThrowIllegalArgumentException() {
        ContentValues testValues = putContentValues();

        scr.insert(CONTENT_URI, testValues);
        Cursor result = scr.query(Uri.parse("content://in.xinyue.xinyue.provider"), null, null, null, null);
    }

    @Test
    public void checkQuerySingleRow_shouldQueryCorrectly() {
        ContentValues testValues = putContentValues();

        Uri uri = scr.insert(CONTENT_URI, testValues);

        Cursor result = scr.query(Uri.parse("content://in.xinyue.xinyue.provider/posts/1"), null, null, null, null);
        result.moveToFirst();
        assertNormalInsertAndQuery(result);
    }

    @Test
    public void checkQuerySingleUnexistedRow_shouldReturnEmptyCursor() {
        ContentValues testValues = putContentValues();

        Uri uri = scr.insert(CONTENT_URI, testValues);

        Cursor result = scr.query(Uri.parse("content://in.xinyue.xinyue.provider/posts/2"), null, null, null, null);
        // cursor won't be null even if it's empty;
        Assert.assertEquals(0, result.getCount());
    }

    private void assertNormalInsertAndQuery(Cursor result) {
        Assert.assertEquals("1", result.getString(result.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_POST_ID)));
        Assert.assertEquals("post name", result.getString(result.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_TITLE)));
        Assert.assertEquals("rings", result.getString(result.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY)));
        Assert.assertEquals("http://test.cover.com/image", result.getString(result.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_COVER)));
        Assert.assertEquals("20150504", result.getString(result.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE)));
        Assert.assertEquals("http://test.com", result.getString(result.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_LINK)));
        Assert.assertEquals("test content", result.getString(result.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_CONTENT)));
    }

}

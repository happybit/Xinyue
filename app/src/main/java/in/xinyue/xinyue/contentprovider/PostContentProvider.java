package in.xinyue.xinyue.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import in.xinyue.xinyue.database.PostDatabaseHelper;
import in.xinyue.xinyue.database.PostReaderContract.PostTable;

public class PostContentProvider extends ContentProvider {

    // database
    private PostDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    // used for the UriMatcher
    private static final int POSTS = 10;
    private static final int POST_ID = 20;

    private static final String AUTHORITY = "in.xinyue.xinyue.provider";

    private static final String BASE_PATH = "posts";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/posts";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/post";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, BASE_PATH, POSTS);
        sUriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", POST_ID);
    }

    public PostContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != POSTS) {
            throw new UnsupportedOperationException("Unknown URI " + uri);
        }

        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        mDb = mDbHelper.getWritableDatabase();
        return insert(uri, contentValues, mDb);
    }

    public Uri insert(Uri uri, ContentValues values, SQLiteDatabase db) {
        verifyValues(values);

        // Validate the requested uri
        int m = sUriMatcher.match(uri);
        if (m != POSTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // insert the values into a new database row
        String postID = (String) values.get(PostTable.COLUMN_NAME_POST_ID);

        Long rowID = postRowID(db, postID);
        if (rowID == null) {
            long newRowID = mDb.insert(PostTable.TABLE_NAME, null, values);
            if (newRowID >= 0) {
                Uri insertUri = ContentUris.withAppendedId(CONTENT_URI, newRowID);
                getContext().getContentResolver().notifyChange(insertUri, null);
                return insertUri;
            }

            throw new IllegalStateException("Could not insert " +
                    "content values: " + values);
        }

        return ContentUris.withAppendedId(CONTENT_URI, rowID);
    }

    private Long postRowID(SQLiteDatabase db, String postID) {
        Cursor cursor = null;
        Long rowID = null;
        try {
            cursor = db.query(PostTable.TABLE_NAME, null,
                    PostTable.COLUMN_NAME_POST_ID + " = '" + postID + "'",
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                rowID = cursor.getLong(cursor.getColumnIndexOrThrow(PostTable._ID));
            }
        } finally {
                if (cursor != null) {
                    cursor.close();
                }
        }

        return rowID;
    }

    private void verifyValues(ContentValues values) {

        if (!values.containsKey(PostTable.COLUMN_NAME_POST_ID)) {
            throw new IllegalArgumentException("Post ID not specified");
        }

        if (!values.containsKey(PostTable.COLUMN_NAME_TITLE)) {
            throw new IllegalArgumentException("Post title not specified");
        }

        if (!values.containsKey(PostTable.COLUMN_NAME_CATEGORY)) {
            values.put(PostTable.COLUMN_NAME_CATEGORY, "all");
        }

        if (!values.containsKey(PostTable.COLUMN_NAME_LINK)) {
            throw new IllegalArgumentException("Post link not specified");
        }

        if (!values.containsKey(PostTable.COLUMN_NAME_CREATED_DATE)) {
            throw new IllegalArgumentException("Post created date not specified");
        }

        if (!values.containsKey(PostTable.COLUMN_NAME_COVER)) {
            throw new IllegalArgumentException("Post cover not specified");
        }

        if (!values.containsKey(PostTable.COLUMN_NAME_CONTENT)) {
            throw new IllegalArgumentException("Post content not specified");
        }

    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PostDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        mDb = mDbHelper.getWritableDatabase();
        Cursor queryCursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case POSTS:
                queryCursor = mDb.query(PostTable.TABLE_NAME, projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                queryCursor.setNotificationUri(getContext().getContentResolver(), uri);

                break;
            case POST_ID:
                selection = PostTable._ID + " = " + uri.getLastPathSegment();
                queryCursor = mDb.query(PostTable.TABLE_NAME, projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                queryCursor.setNotificationUri(getContext().getContentResolver(), uri);

                break;
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);
        }

        return queryCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}

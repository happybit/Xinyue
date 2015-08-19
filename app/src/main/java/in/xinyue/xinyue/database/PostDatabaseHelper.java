package in.xinyue.xinyue.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pzheng on 7/15/2015.
 */
public class PostDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "posts.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PostReaderContract.PostTable.TABLE_NAME + " (" +
                    PostReaderContract.PostTable._ID + " INTEGER PRIMARY KEY," +
                    PostReaderContract.PostTable.COLUMN_NAME_POST_ID + TEXT_TYPE + COMMA_SEP +
                    PostReaderContract.PostTable.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    PostReaderContract.PostTable.COLUMN_NAME_CATEGORY + TEXT_TYPE + COMMA_SEP +
                    PostReaderContract.PostTable.COLUMN_NAME_COVER + TEXT_TYPE + COMMA_SEP +
                    PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE + TEXT_TYPE + COMMA_SEP +
                    PostReaderContract.PostTable.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    PostReaderContract.PostTable.COLUMN_NAME_CONTENT + TEXT_TYPE +
                    " );";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PostReaderContract.PostTable.TABLE_NAME + ";";

    public PostDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}

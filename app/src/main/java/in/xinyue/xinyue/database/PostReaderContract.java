package in.xinyue.xinyue.database;

import android.provider.BaseColumns;

/**
 * Created by pzheng on 7/15/2015.
 */
public class PostReaderContract {

    public PostReaderContract() {}

    public static abstract class PostTable implements BaseColumns {
        public static final String TABLE_NAME = "post";
        public static final String COLUMN_NAME_POST_ID = "postid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_COVER = "cover";
        public static final String COLUMN_NAME_CREATED_DATE = "createddate";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_CONTENT = "content";
    }

}

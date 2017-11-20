package com.forcetower.uefs.providers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by João Paulo on 20/11/2017.
 */
public class SagresContract {
    private SagresContract() {}

    public static final String CONTENT_AUTHORITY = "com.forcetower.uefs";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_MESSAGES = "messages";

    public static class Entry implements BaseColumns {
        public static final String MESSAGE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.forcetower.messages";
        public static final String CLASS_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.forcetower.classes";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MESSAGES).build();
    }
}

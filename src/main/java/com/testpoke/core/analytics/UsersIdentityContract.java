package com.testpoke.core.analytics;

import android.net.Uri;


final class UsersIdentityContract {

    public static final String AUTHORITY = "com.testpoke.app";

    public static final String BASE = "users";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE);

    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_IDENTIFIER = "identifier";

    public static final String COLUMN_LOGGED = "logged";

    public static final int VALUE_NO_LOGGED = 0
            ;
    public static final int VALUE_LOGGED = 1;

}

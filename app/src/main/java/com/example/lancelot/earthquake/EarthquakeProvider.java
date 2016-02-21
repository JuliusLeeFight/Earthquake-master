package com.example.lancelot.earthquake;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by Julius on 2016/2/21.
 */
public class EarthquakeProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.example.lancelot.earthquake.earthquakeprovider/earthquakes");

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DETAILS = "details";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_LOCATION_LAT = "latitude";
    public static final String KEY_LOCATION_LNG = "longitude";
    public static final String KEY_MAGNITUDE = "magnitude";
    public static final String KEY_LINK = "link";

    EarthquakeDatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        dbHelper = new EarthquakeDatabaseHelper(context, EarthquakeDatabaseHelper.DATABASE_NAME, null, EarthquakeDatabaseHelper.DATABASE_VERSION);

        return true;
    }

    private static final int QUAKES = 1;
    private static final int QUAKE_ID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("content://com.example.lancelot.earthquake.earthquakeprovider", "earthquakes", QUAKES);
        uriMatcher.addURI("content://com.example.lancelot.earthquake.earthquakeprovider", "earthquake/#", QUAKE_ID);

    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case QUAKES:
                return "vnd.android.cursor.dir/vnd.paad.earthquake";
            case QUAKE_ID:
                return "vnd.android.cursor.item/vnd.paad.earthquake";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE);


        switch (uriMatcher.match(uri)) {
            case QUAKE_ID:
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                break;
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = KEY_DATE;
        } else {
            orderBy = sortOrder;
        }

        Cursor cursor = qb.query(database, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        long rowId = database.insert(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, "quake", values);
        if (rowId > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        //为什么不能抛出SQL异常
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case QUAKES:
                count = database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, selection, selectionArgs);
                break;
            case QUAKE_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE,
                        KEY_ID + "="
                        + segment
                        + (!TextUtils.isEmpty(selection)?"AND("
                        + selection + ')':""),selectionArgs);
                break;

        }

        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int count = 0;
        switch (uriMatcher.match(uri)){
            case QUAKES :
                count = database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE,
                        values,selection,selectionArgs);
                break;
            case QUAKE_ID :
                String segment = uri.getPathSegments().get(1);
                count = database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE,
                        values,KEY_ID
                        + "=" + segment
                        + (TextUtils.isEmpty(selection)? "AND("
                        + selection + ')':""),selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    private static class EarthquakeDatabaseHelper extends SQLiteOpenHelper {


        private static final String TAG = "EarthquakeProvider";

        private static final int DATABASE_VERSION = 1;
        private static final String EARTHQUAKE_TABLE = "earthquakes";
        private static final String DATABASE_NAME = "eathquakes.db";
        private static final String DATABASE_CREATE =
                "create table " + EARTHQUAKE_TABLE + " ("
                        + KEY_ID + " integer primary key autoincrement, "
                        + KEY_DATE + " INTEGER, "
                        + KEY_DETAILS + " TEXT, "
                        + KEY_SUMMARY + " TEXT, "
                        + KEY_LOCATION_LAT + " FLOAT, "
                        + KEY_LOCATION_LNG + " FLOAT, "
                        + KEY_MAGNITUDE + " FLOAT, "
                        + KEY_LINK + " TEXT);";

        private SQLiteDatabase earthquakeDB;

        public EarthquakeDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            System.out.print("............................................................"+"+++++++++++++++++++++++++++++++++++++++++++++++++");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS" + EARTHQUAKE_TABLE);
            onCreate(db);
        }
    }
}

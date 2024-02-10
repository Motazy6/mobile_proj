package com.example.mobileproject.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.model.Housing;
import com.example.mobileproject.model.HousingHistory;
import com.example.mobileproject.model.LeaseAgreement;
import com.example.mobileproject.model.Review;
import com.example.mobileproject.model.Task;
import com.example.mobileproject.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SuppressLint("Range")
public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static final int DATABASE_VERSION = 26;

    private static final String DATABASE_NAME = "StudentAccommodationDB";

    private static final String TABLE_USERS = "users";
    private static final String TABLE_HOUSING = "housing";
    private static final String TABLE_LEASE_AGREEMENTS = "lease_agreements";
    private static final String TABLE_REVIEWS = "reviews";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PRICE = "price";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_AMENITIES = "amenities";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_HOUSING_ID = "housing_id";
    private static final String KEY_RATING = "rating";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_LEASE_DURATION = "lease_duration";
    private static final String KEY_AVAILABLE_FROM = "available_from";
    private static final String KEY_UTILITIES_INCLUDED = "utilities_included";
    private static final String KEY_IS_ACTIVE = "is_active";
    private static final String KEY_LEASE_START_DATE = "lease_start_date";
    private static final String KEY_LEASE_END_DATE = "lease_end_date";
    private static final String KEY_MONTHLY_RENT = "monthly_rent";
    private static final String TABLE_HOUSING_HISTORY = "housing_history";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_NOTES = "notes";
    private static final String TABLE_TASKS = "tasks";
    private static final String KEY_TASK_ID = "id";
    private static final String KEY_TASK_DESCRIPTION = "description";
    private static final String KEY_NOTIFICATION_TIME = "notification_time";
    private static final String KEY_IS_DONE = "is_done";

    private static final String CREATE_TABLE_TASKS = "CREATE TABLE "
            + TABLE_TASKS + "(" + KEY_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TASK_DESCRIPTION + " TEXT,"
            + KEY_NOTIFICATION_TIME + " INTEGER,"
            + KEY_IS_DONE + " INTEGER DEFAULT 0" + ")";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT," + KEY_EMAIL + " TEXT,"
            + KEY_PASSWORD + " TEXT," + KEY_PHONE + " TEXT,"
            + KEY_STUDENT_ID + " TEXT" + ")";

    private static final String CREATE_TABLE_HOUSING = "CREATE TABLE "
            + TABLE_HOUSING + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TITLE + " TEXT," + KEY_DESCRIPTION + " TEXT,"
            + KEY_PRICE + " REAL," + KEY_LOCATION + " TEXT,"
            + KEY_AMENITIES + " TEXT," + KEY_LEASE_DURATION + " TEXT,"
            + KEY_AVAILABLE_FROM + " TEXT," + KEY_UTILITIES_INCLUDED + " TEXT" + ")";

    private static final String CREATE_TABLE_LEASE_AGREEMENTS = "CREATE TABLE "
            + TABLE_LEASE_AGREEMENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " INTEGER," + KEY_HOUSING_ID + " INTEGER,"
            + KEY_LEASE_START_DATE + " TEXT," + KEY_LEASE_END_DATE + " TEXT,"
            + KEY_MONTHLY_RENT + " REAL," + KEY_IS_ACTIVE + " INTEGER DEFAULT 1)";

    private static final String CREATE_TABLE_REVIEWS = "CREATE TABLE "
            + TABLE_REVIEWS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_HOUSING_ID + " INTEGER," + KEY_USER_ID + " INTEGER,"
            + KEY_NAME + " TEXT,"
            + KEY_RATING + " INTEGER," + KEY_COMMENT + " TEXT" + ")";

    private static final String CREATE_TABLE_HOUSING_HISTORY = "CREATE TABLE "
            + TABLE_HOUSING_HISTORY + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " INTEGER," + KEY_NAME + " TEXT,"
            + KEY_ADDRESS + " TEXT," + KEY_START_DATE + " TEXT,"
            + KEY_END_DATE + " TEXT," + KEY_NOTES + " TEXT" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_HOUSING);
        db.execSQL(CREATE_TABLE_LEASE_AGREEMENTS);
        db.execSQL(CREATE_TABLE_REVIEWS);
        db.execSQL(CREATE_TABLE_HOUSING_HISTORY);
        db.execSQL(CREATE_TABLE_TASKS);
        addInitialHousingData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOUSING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEASE_AGREEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOUSING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public void addReview(Review review) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_HOUSING_ID, review.getHousingId());
        values.put(KEY_NAME, review.getUserName());
        values.put(KEY_COMMENT, review.getComment());
        values.put(KEY_RATING, review.getRating());
        values.put(KEY_USER_ID, review.getUserId());
        db.insert(TABLE_REVIEWS, null, values);
        db.close();
    }

    public long addLeaseAgreement(LeaseAgreement leaseAgreement) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, leaseAgreement.getUserId());
        values.put(KEY_HOUSING_ID, leaseAgreement.getHousingId());
        values.put(KEY_LEASE_START_DATE, leaseAgreement.getLeaseStartDate());
        values.put(KEY_LEASE_END_DATE, leaseAgreement.getLeaseEndDate());
        values.put(KEY_MONTHLY_RENT, leaseAgreement.getMonthlyRent());

        long leaseAgreementId = db.insert(TABLE_LEASE_AGREEMENTS, null, values);
        db.close();

        return leaseAgreementId;
    }

    private void addInitialHousingData(SQLiteDatabase db) {
        List<Housing> housings = new ArrayList<>();
        housings.add(new Housing(0, "إسكان الريم", "سكن شباب", 1000.0, "بيرزيت منتصف البلدة", "غير مفروش", "12 months", "2023-01-01", "لا"));
        housings.add(new Housing(1, "Housing Title 2", "Description 2", 1200.0, "Location 2", "Amenities 2", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(2, "Housing Title 3", "Description 3", 1200.0, "Location 3", "Amenities 3", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(3, "Housing Title 4", "Description 4", 1200.0, "Location 4", "Amenities 4", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(4, "Housing Title 5", "Description 5", 1200.0, "Location 5", "Amenities 5", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(5, "Housing Title 6", "Description 6", 1200.0, "Location 6", "Amenities 6", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(6, "Housing Title 7", "Description 7", 1200.0, "Location 7", "Amenities 7", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(7, "Housing Title 8", "Description 8", 1200.0, "Location 8", "Amenities 8", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(8, "Housing Title 9", "Description 9", 1200.0, "Location 9", "Amenities 9", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(9, "Housing Title 10", "Description 10", 1200.0, "Location 10", "Amenities 10", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(10, "Housing Title 11", "Description 11", 1200.0, "Location 11", "Amenities 11", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(11, "Housing Title 12", "Description 12", 1200.0, "Location 12", "Amenities 12", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(12, "Housing Title 13", "Description 13", 1200.0, "Location 13", "Amenities 13", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(13, "Housing Title 14", "Description 14", 1200.0, "Location 14", "Amenities 14", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(14, "Housing Title 15", "Description 15", 1200.0, "Location 15", "Amenities 15", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(15, "Housing Title 16", "Description 16", 1200.0, "Location 16", "Amenities 16", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(16, "Housing Title 17", "Description 17", 1200.0, "Location 17", "Amenities 17", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(17, "Housing Title 18", "Description 18", 1200.0, "Location 18", "Amenities 18", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(18, "Housing Title 19", "Description 19", 1200.0, "Location 19", "Amenities 19", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(19, "Housing Title 20", "Description 20", 1200.0, "Location 20", "Amenities 20", "12 months", "2023-02-01", "No"));
        housings.add(new Housing(20, "Housing Title 21", "Description 21", 1200.0, "Location 21", "Amenities 21", "12 months", "2023-02-01", "No"));

        for (Housing housing : housings) {
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, housing.getTitle());
            values.put(KEY_DESCRIPTION, housing.getDescription());
            values.put(KEY_PRICE, housing.getPrice());
            values.put(KEY_LOCATION, housing.getLocation());
            values.put(KEY_AMENITIES, housing.getAmenities());
            values.put(KEY_LEASE_DURATION, housing.getLeaseDuration());
            values.put(KEY_AVAILABLE_FROM, housing.getAvailableFrom());
            values.put(KEY_UTILITIES_INCLUDED, housing.getUtilitiesIncluded());
            db.insert(TABLE_HOUSING, null, values);
        }
    }

    public List<Housing> getAllHousings() {
        List<Housing> housings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HOUSING, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Housing housing = new Housing();
                housing.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                housing.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                housing.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                housing.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
                housing.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
                housing.setAmenities(cursor.getString(cursor.getColumnIndex(KEY_AMENITIES)));
                housing.setLeaseDuration(cursor.getString(cursor.getColumnIndex(KEY_LEASE_DURATION)));
                housing.setAvailableFrom(cursor.getString(cursor.getColumnIndex(KEY_AVAILABLE_FROM)));
                housing.setUtilitiesIncluded(cursor.getString(cursor.getColumnIndex(KEY_UTILITIES_INCLUDED)));
                housings.add(housing);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return housings;
    }

    public List<Review> getReviewsByHousingId(int housingId) {
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = KEY_HOUSING_ID + " = ?";
        String[] selectionArgs = { String.valueOf(housingId) };

        Cursor cursor = db.query(TABLE_REVIEWS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Review review = new Review();
                review.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                review.setHousingId(cursor.getInt(cursor.getColumnIndex(KEY_HOUSING_ID)));
                review.setUserName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                review.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));
                review.setRating(cursor.getInt(cursor.getColumnIndex(KEY_RATING)));
                reviews.add(review);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return reviews;
    }

    public List<Housing> getFilteredHousings(String query) {
        List<Housing> housings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_HOUSING + " WHERE " + KEY_TITLE + " LIKE ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                Housing housing = new Housing();
                housing.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                housing.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                housing.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                housing.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
                housing.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
                housing.setAmenities(cursor.getString(cursor.getColumnIndex(KEY_AMENITIES)));
                housing.setLeaseDuration(cursor.getString(cursor.getColumnIndex(KEY_LEASE_DURATION)));
                housing.setAvailableFrom(cursor.getString(cursor.getColumnIndex(KEY_AVAILABLE_FROM)));
                housing.setUtilitiesIncluded(cursor.getString(cursor.getColumnIndex(KEY_UTILITIES_INCLUDED)));
                housings.add(housing);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return housings;
    }

    public Housing getHousing(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HOUSING, null, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Housing housing = new Housing(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)),
                    cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(KEY_AMENITIES)),
                    cursor.getString(cursor.getColumnIndex(KEY_LEASE_DURATION)),
                    cursor.getString(cursor.getColumnIndex(KEY_AVAILABLE_FROM)),
                    cursor.getString(cursor.getColumnIndex(KEY_UTILITIES_INCLUDED))
            );
            cursor.close();
            return housing;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public long addHousingHistory(HousingHistory housingHistory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, housingHistory.getUserId());
        values.put(KEY_ADDRESS, housingHistory.getAddress());
        values.put(KEY_START_DATE, housingHistory.getStartDate());
        values.put(KEY_END_DATE, housingHistory.getEndDate());
        values.put(KEY_NOTES, housingHistory.getNotes());
        values.put(KEY_NAME, housingHistory.getHousingName());

        long historyId = db.insert(TABLE_HOUSING_HISTORY, null, values);
        db.close();

        return historyId;
    }


    public List<HousingHistory> getAllHousingHistory() {
        List<HousingHistory> housingHistoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_HOUSING_HISTORY;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                int userId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
                String housingName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
                String startDate = cursor.getString(cursor.getColumnIndex(KEY_START_DATE));
                String endDate = cursor.getString(cursor.getColumnIndex(KEY_END_DATE));
                String notes = cursor.getString(cursor.getColumnIndex(KEY_NOTES));

                HousingHistory housingHistory = new HousingHistory(id, userId, housingName, address, startDate, endDate, notes);
                housingHistoryList.add(housingHistory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return housingHistoryList;
    }

    public HousingHistory getHousingHistory(int historyId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HOUSING_HISTORY, new String[] { KEY_ID, KEY_USER_ID, KEY_NAME, KEY_ADDRESS, KEY_START_DATE, KEY_END_DATE, KEY_NOTES },
                KEY_ID + "=?", new String[] { String.valueOf(historyId) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        HousingHistory housingHistory = new HousingHistory(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6));
        cursor.close();
        return housingHistory;
    }

    public int updateHousingHistory(HousingHistory housingHistory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, housingHistory.getUserId());
        values.put(KEY_NAME, housingHistory.getHousingName());
        values.put(KEY_ADDRESS, housingHistory.getAddress());
        values.put(KEY_START_DATE, housingHistory.getStartDate());
        values.put(KEY_END_DATE, housingHistory.getEndDate());
        values.put(KEY_NOTES, housingHistory.getNotes());

        return db.update(TABLE_HOUSING_HISTORY, values, KEY_ID + " = ?", new String[] { String.valueOf(housingHistory.getId()) });
    }

    public void cancelLease(int housingId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_ACTIVE, 0);

        String whereClause = KEY_HOUSING_ID + " = ? AND " + KEY_USER_ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(housingId), String.valueOf(userId) };

        db.update(TABLE_LEASE_AGREEMENTS, values, whereClause, whereArgs);
        db.close();
    }


    public List<Housing> getActiveHousings(int userId) {
        List<Housing> housings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_HOUSING + " WHERE "
                + KEY_ID + " IN (SELECT " + KEY_HOUSING_ID + " FROM " + TABLE_LEASE_AGREEMENTS
                + " WHERE " + KEY_IS_ACTIVE + " = 1 AND " + KEY_USER_ID + " = ?)";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Housing housing = new Housing();
                housing.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                housing.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                housing.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                housing.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
                housing.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
                housing.setAmenities(cursor.getString(cursor.getColumnIndex(KEY_AMENITIES)));
                housing.setLeaseDuration(cursor.getString(cursor.getColumnIndex(KEY_LEASE_DURATION)));
                housing.setAvailableFrom(cursor.getString(cursor.getColumnIndex(KEY_AVAILABLE_FROM)));
                housing.setUtilitiesIncluded(cursor.getString(cursor.getColumnIndex(KEY_UTILITIES_INCLUDED)));
                housings.add(housing);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return housings;
    }

    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TASK_DESCRIPTION, task.getDescription());
        values.put(KEY_NOTIFICATION_TIME, task.getNotificationTime());
        values.put(KEY_IS_DONE, 0);

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }


    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_DONE, task.isDone() ? 1 : 0);

        db.update(TABLE_TASKS, values, KEY_TASK_ID + " = ?", new String[] { String.valueOf(task.getId()) });
        db.close();
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_STUDENT_ID, user.getStudentId());

        db.update(TABLE_USERS, values, KEY_ID + " = ?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public boolean isLeaseAgreementActive(int userId, int housingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_LEASE_AGREEMENTS +
                " WHERE " + KEY_USER_ID + " = ? AND " +
                KEY_HOUSING_ID + " = ? AND " +
                KEY_IS_ACTIVE + " = 1";

        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), String.valueOf(housingId) });
        boolean isActive = false;

        if (cursor != null && cursor.moveToFirst()) {
            isActive = cursor.getInt(0) > 0;
            cursor.close();
        }

        db.close();
        return isActive;
    }


}

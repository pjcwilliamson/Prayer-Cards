package org.williamsonministry.prayercards;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.le.ScanSettings;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String PRAYER_CARD_TABLE = "PRAYER_CARD_TABLE";
    public static final String COLUMN_PRAYER_REQUEST_TEXT = "PRAYER_REQUEST_TEXT";
    public static final String COLUMN_LIST_ORDER = "LIST_ORDER";
    public static final String COLUMN_TAGS = "TAGS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MAX_FREQUENCY = "MAX_FREQUENCY";
    public static final String COLUMN_MULTI_MAX_FREQ = "MULTI_MAX_FREQ";
    public static final String COLUMN_IN_ROTATION = "IN_ROTATION";
    public static final String COLUMN_LAST_SEEN = "LAST_SEEN";
    public static final String COLUMN_VIEWS_REMAINING = "VIEWS_REMAINING";
    public static final String COLUMN_EXPIRY_DATE = "EXPIRY_DATE";
    public static final String COLUMN_ACTIVE = "ACTIVE";
    public static final String COLUMN_DECK_NAME = "DECK_NAME";
    public static final String COLUMN_ALL_TAGS = "ALL_TAGS";
    public static final String COLUMN_MAX_IN_ROTATION = "MAX_IN_ROTATION";
    public static final String COLUMN_ROTATION_POSITION = "ROTATION_POSITION";
    public static final String PRAYER_DECK_TABLE = "PRAYER_DECK_TABLE";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "prayercard.db", null, 2);
    }

    // this is called the first time a database is accessed. There should be code in here to create a new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + PRAYER_CARD_TABLE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LIST_ORDER + " INTEGER, "
                + COLUMN_PRAYER_REQUEST_TEXT + " TEXT, "
                + COLUMN_TAGS + " TEXT, "
                + COLUMN_MAX_FREQUENCY + " INTEGER, "
                + COLUMN_MULTI_MAX_FREQ + " INTEGER, "
                + COLUMN_IN_ROTATION + " BOOL, "
                + COLUMN_LAST_SEEN + " INTEGER, "
                + COLUMN_VIEWS_REMAINING + " INTEGER, "
                + COLUMN_EXPIRY_DATE + " INTEGER, "
                + COLUMN_ACTIVE + " BOOL)";

        db.execSQL(createTableStatement);

        String createDeckTableStatement = "CREATE TABLE " + PRAYER_DECK_TABLE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LIST_ORDER + " INTEGER, "
                + COLUMN_DECK_NAME + " TEXT, "
                + COLUMN_TAGS + " TEXT, "
                + COLUMN_ALL_TAGS + " BOOL, "
                + COLUMN_MAX_IN_ROTATION + " INTEGER, "
                + COLUMN_ROTATION_POSITION + " INTEGER, "
                + COLUMN_ACTIVE + " BOOL)";

        db.execSQL(createDeckTableStatement);

        /*
        Add a default Prayer Deck
         */
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_LIST_ORDER, -1);
        cv.put(COLUMN_DECK_NAME, "Default");
        cv.put(COLUMN_TAGS, "");
        cv.put(COLUMN_ALL_TAGS, false);
        cv.put(COLUMN_MAX_IN_ROTATION, 3);
        cv.put(COLUMN_ROTATION_POSITION, PrayerDeck.END);
        cv.put(COLUMN_ACTIVE, true);

        db.insert(PRAYER_DECK_TABLE, null, cv);
    }

    // this is called if the database version number changes. It prevents previous users apps from breaking when you change the database design
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        if (i < 2) {
            String createDeckTableStatement = "CREATE TABLE " + PRAYER_DECK_TABLE + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_LIST_ORDER + " INTEGER, "
                    + COLUMN_DECK_NAME + " TEXT, "
                    + COLUMN_TAGS + " TEXT, "
                    + COLUMN_ALL_TAGS + " BOOL, "
                    + COLUMN_MAX_IN_ROTATION + " INTEGER, "
                    + COLUMN_ROTATION_POSITION + " INTEGER, "
                    + COLUMN_ACTIVE + " BOOL)";

            db.execSQL(createDeckTableStatement);

            /*
            Add a default Prayer Deck
             */
            ContentValues cv = new ContentValues();

            cv.put(COLUMN_LIST_ORDER, -1);
            cv.put(COLUMN_DECK_NAME, "Default");
            cv.put(COLUMN_TAGS, "");
            cv.put(COLUMN_ALL_TAGS, false);
            cv.put(COLUMN_MAX_IN_ROTATION, 3);
            cv.put(COLUMN_ROTATION_POSITION, PrayerDeck.END);
            cv.put(COLUMN_ACTIVE, true);

            db.insert(PRAYER_DECK_TABLE, null, cv);
        }
    }

    public long addOne(PrayerCard prayerCard) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRAYER_REQUEST_TEXT, prayerCard.getPrayerText());
        cv.put(COLUMN_TAGS, prayerCard.getTags());
        cv.put(COLUMN_MAX_FREQUENCY, prayerCard.getMaxFrequency());
        cv.put(COLUMN_MULTI_MAX_FREQ, prayerCard.getMultipleMaxFreq());
        cv.put(COLUMN_IN_ROTATION, prayerCard.isInRotation());
        cv.put(COLUMN_LAST_SEEN, prayerCard.getLastSeen().getTime());
        cv.put(COLUMN_VIEWS_REMAINING, prayerCard.getViewsRemaining());
        cv.put(COLUMN_EXPIRY_DATE, prayerCard.getExpiryDate().getTime());
        cv.put(COLUMN_ACTIVE, prayerCard.isActive());
        cv.put(COLUMN_LIST_ORDER, prayerCard.getListOrder());

        long insert = db.insert(PRAYER_CARD_TABLE, null, cv);

        db.close(); //@Pete added this code based on below. Who knows if it's helpful, anti-helpful, or irrelevant.

        return insert;
    }

    public ArrayList<PrayerCard> getAll() {
        ArrayList<PrayerCard> returnList = new ArrayList<>();

        //get data from db

        String queryString = "SELECT * FROM " + PRAYER_CARD_TABLE + "\nORDER BY " + COLUMN_LIST_ORDER; //Look here for help with SQL queries:  https://www.w3schools.com/sqL/sql_examples.asp

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {   //true if there is a first entry ie. the db is not empty
            // loop through cursor and create new customer objects for each row and put them in returnList
            do {
                int prayerCardID = cursor.getInt(0);
                int listOrder = cursor.getInt(1);
                String prayerText = cursor.getString(2);
                String tags = cursor.getString(3);
                int maxFreq = cursor.getInt(4);
                int multiMaxFreq = cursor.getInt(5);
                boolean isInRotation = cursor.getInt(6) == 1;
                long lastSeen = cursor.getLong(7);
                int viewsRemaining = cursor.getInt(8);
                long expiryDate = cursor.getLong(9);
                boolean isActive = cursor.getInt(10) == 1;


                PrayerCard newPrayerCard = new PrayerCard(prayerCardID, listOrder, prayerText, tags, maxFreq, multiMaxFreq, isInRotation, new Date(lastSeen), viewsRemaining, new Date(expiryDate), isActive);

                returnList.add(newPrayerCard);


            } while (cursor.moveToNext()); //This while statement is true when there are still new lines
        } else {
            // failure. Add nothing to list.
        }

        //close cursor and db for others to use. Always clean up after yo'self
        cursor.close();
        db.close();

        return returnList;
    }

    public boolean deleteByID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRAYER_CARD_TABLE, COLUMN_ID + " = " + id, null);
        db.close();
        return true;
    }

    public void cardViewed(int id) {
        PrayerCard prayerCard = getCardById(id);
        int views = prayerCard.getViewsRemaining() - 1;
        if (views < 0) {
            views = -1;
        }
        int active = 1;
        if (views == 0) {
            active = 0;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + PRAYER_CARD_TABLE + " SET "
                + COLUMN_VIEWS_REMAINING + " = " + views
                + " WHERE ID = " + id);

        db.execSQL("UPDATE " + PRAYER_CARD_TABLE + " SET "
                + COLUMN_ACTIVE + " = " + active
                + " WHERE ID = " + id);

        db.execSQL("UPDATE " + PRAYER_CARD_TABLE + " SET "
                + COLUMN_LAST_SEEN + " = " + Calendar.getInstance().getTimeInMillis()
                + " WHERE ID = " + id);

        db.close();
    }

    public boolean clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRAYER_CARD_TABLE, null, null);
        db.execSQL("VACUUM");
        db.close();
        return true;
    }

    public ArrayList<PrayerCard> getRotation() {
        ArrayList<PrayerCard> lastSeenOrderList = new ArrayList<>();

        //get data from db

        String queryString = "SELECT * FROM " + PRAYER_CARD_TABLE + "\nORDER BY " + COLUMN_LAST_SEEN; //Look here for help with SQL queries:  https://www.w3schools.com/sqL/sql_examples.asp

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {   //true if there is a first entry ie. the db is not empty
            // loop through cursor and create new customer objects for each row and put them in returnList
            do {
                int prayerCardID = cursor.getInt(0);
                int listOrder = cursor.getInt(1);
                String prayerText = cursor.getString(2);
                String tags = cursor.getString(3);
                int maxFreq = cursor.getInt(4);
                int multiMaxFreq = cursor.getInt(5);
                boolean isInRotation = cursor.getInt(6) == 1;
                long lastSeen = cursor.getLong(7);
                int viewsRemaining = cursor.getInt(8);
                long expiryDate = cursor.getLong(9);
                boolean isActive = cursor.getInt(10) == 1;


                PrayerCard newPrayerCard = new PrayerCard(prayerCardID, listOrder, prayerText, tags, maxFreq, multiMaxFreq, isInRotation, new Date(lastSeen), viewsRemaining, new Date(expiryDate), isActive);

                lastSeenOrderList.add(newPrayerCard);


            } while (cursor.moveToNext()); //This while statement is true when there are still new lines
        } else {
            // failure. Add nothing to list.
        }

        //close cursor and db for others to use. Always clean up after yo'self
        cursor.close();
        db.close();

        ArrayList<PrayerCard> returnList = new ArrayList<PrayerCard>();

        for (int i = 0; i < lastSeenOrderList.size(); i++) {
            if (lastSeenOrderList.get(i).isInRotation() && lastSeenOrderList.get(i).isActive()) {
                returnList.add(lastSeenOrderList.get(i));
            }
        }

        return returnList;
    }

    public PrayerCard editOneReturnPrayerCard(int id, PrayerCard prayerCard) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRAYER_REQUEST_TEXT, prayerCard.getPrayerText());
        cv.put(COLUMN_TAGS, prayerCard.getTags());
        cv.put(COLUMN_MAX_FREQUENCY, prayerCard.getMaxFrequency());
        cv.put(COLUMN_MULTI_MAX_FREQ, prayerCard.getMultipleMaxFreq());
        cv.put(COLUMN_IN_ROTATION, prayerCard.isInRotation());
        //cv.put(COLUMN_LAST_SEEN, prayerCard.getLastSeen().getTime());
        cv.put(COLUMN_VIEWS_REMAINING, prayerCard.getViewsRemaining());
        cv.put(COLUMN_EXPIRY_DATE, prayerCard.getExpiryDate().getTime());
        cv.put(COLUMN_ACTIVE, prayerCard.isActive());
        //cv.put(COLUMN_LIST_ORDER, prayerCard.getListOrder());

        db.update(PRAYER_CARD_TABLE, cv, COLUMN_ID + " = " + id, null);

        String queryString = "SELECT * FROM " + PRAYER_CARD_TABLE + " WHERE " + COLUMN_ID + " = " + id;

        Cursor cursor = db.rawQuery(queryString, null);

        PrayerCard newPrayerCard = null;

        if (cursor.moveToFirst()) {   //true if there is a first entry ie. the db is not empty
            // loop through cursor and create new customer objects for each row and put them in returnList
            do {
                int prayerCardID = cursor.getInt(0);
                int listOrder = cursor.getInt(1);
                String prayerText = cursor.getString(2);
                String tags = cursor.getString(3);
                int maxFreq = cursor.getInt(4);
                int multiMaxFreq = cursor.getInt(5);
                boolean isInRotation = cursor.getInt(6) == 1;
                long lastSeen = cursor.getLong(7);
                int viewsRemaining = cursor.getInt(8);
                long expiryDate = cursor.getLong(9);
                boolean isActive = cursor.getInt(10) == 1;

                newPrayerCard = new PrayerCard(prayerCardID, listOrder, prayerText, tags, maxFreq, multiMaxFreq, isInRotation, new Date(lastSeen), viewsRemaining, new Date(expiryDate), isActive);

            } while (cursor.moveToNext()); //This while statement is true when there are still new lines
        } else {
            // failure. Add nothing to list.
        }

        //close cursor and db for others to use. Always clean up after yo'self
        cursor.close();
        db.close();

        return newPrayerCard;

    }

    /*
    Updates the list order in the DB to match the order of the passed ArrayList
     */
    public void saveOrder(ArrayList<PrayerCard> allPrayerCards) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < allPrayerCards.size(); i++) {
            db.execSQL("UPDATE " + PRAYER_CARD_TABLE + " SET "
                    + COLUMN_LIST_ORDER + " = " + i
                    + " WHERE ID = " + allPrayerCards.get(i).getId());
        }

        db.close();
    }

    public void makeOneInactive(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ACTIVE, false);

        db.update(PRAYER_CARD_TABLE, cv, COLUMN_ID + " = " + id, null);
        db.close();
    }

    public void makeOneActive(int id, boolean resetViews, boolean resetExpiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ACTIVE, true);
        if (resetViews) {
            cv.put(COLUMN_VIEWS_REMAINING, -1);
        }
        if (resetExpiry) {
            cv.put(COLUMN_EXPIRY_DATE, -1);
        }

        db.update(PRAYER_CARD_TABLE, cv, COLUMN_ID + " = " + id, null);
        db.close();
    }

    public void saveAllCards(ArrayList<PrayerCard> allPrayerCards) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(PRAYER_CARD_TABLE, null, null);
        db.execSQL("VACUUM");

        for (int i = 0; i < allPrayerCards.size(); i++) {
            ContentValues cv = new ContentValues();

            cv.put(COLUMN_ID, allPrayerCards.get(i).getId());
            cv.put(COLUMN_LIST_ORDER, allPrayerCards.get(i).getListOrder());
            cv.put(COLUMN_PRAYER_REQUEST_TEXT, allPrayerCards.get(i).getPrayerText());
            cv.put(COLUMN_TAGS, allPrayerCards.get(i).getTags());
            cv.put(COLUMN_MAX_FREQUENCY, allPrayerCards.get(i).getMaxFrequency());
            cv.put(COLUMN_MULTI_MAX_FREQ, allPrayerCards.get(i).getMultipleMaxFreq());
            cv.put(COLUMN_IN_ROTATION, allPrayerCards.get(i).isInRotation());
            cv.put(COLUMN_LAST_SEEN, allPrayerCards.get(i).getLastSeen().getTime());
            cv.put(COLUMN_VIEWS_REMAINING, allPrayerCards.get(i).getViewsRemaining());
            cv.put(COLUMN_EXPIRY_DATE, allPrayerCards.get(i).getExpiryDate().getTime());
            cv.put(COLUMN_ACTIVE, allPrayerCards.get(i).isActive());

            db.insert(PRAYER_CARD_TABLE, null, cv);
        }

        db.close();
    }

    public PrayerCard getCardById(int mId) {
        //get data from db

        String queryString = "SELECT * FROM " + PRAYER_CARD_TABLE + " WHERE " + COLUMN_ID + " = " + mId; //Look here for help with SQL queries:  https://www.w3schools.com/sqL/sql_examples.asp

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        PrayerCard newPrayerCard = null;

        if (cursor.moveToFirst()) {   //true if there is a first entry ie. the db is not empty
            // loop through cursor and create new customer objects for each row and put them in returnList
            do {
                int prayerCardID = cursor.getInt(0);
                int listOrder = cursor.getInt(1);
                String prayerText = cursor.getString(2);
                String tags = cursor.getString(3);
                int maxFreq = cursor.getInt(4);
                int multiMaxFreq = cursor.getInt(5);
                boolean isInRotation = cursor.getInt(6) == 1;
                long lastSeen = cursor.getLong(7);
                int viewsRemaining = cursor.getInt(8);
                long expiryDate = cursor.getLong(9);
                boolean isActive = cursor.getInt(10) == 1;


                newPrayerCard = new PrayerCard(prayerCardID, listOrder, prayerText, tags, maxFreq, multiMaxFreq, isInRotation, new Date(lastSeen), viewsRemaining, new Date(expiryDate), isActive);

            } while (cursor.moveToNext()); //This while statement is true when there are still new lines
        } else {
            // failure. Add nothing to list.
        }

        //close cursor and db for others to use. Always clean up after yo'self
        cursor.close();
        db.close();

        return newPrayerCard;
    }


    /*
    This method returns all the Decks in order, with the inactivated ones at the bottom.
     */
    public ArrayList<PrayerDeck> getAllDecks() {
        ArrayList<PrayerDeck> returnList = new ArrayList<>();
        ArrayList<PrayerDeck> inactiveList = new ArrayList<>();

        //get data from db

        String queryString = "SELECT * FROM " + PRAYER_DECK_TABLE + "\nORDER BY " + COLUMN_LIST_ORDER; //Look here for help with SQL queries:  https://www.w3schools.com/sqL/sql_examples.asp

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

//        + COLUMN_ALL_TAGS + " BOOL, "
//                + COLUMN_MAX_IN_ROTATION + " INTEGER, "
//                + COLUMN_ROTATION_POSITION + " INTEGER, "
//                + COLUMN_ACTIVE + " BOOL)";

        if (cursor.moveToFirst()) {   //true if there is a first entry ie. the db is not empty
            // loop through cursor and create new customer objects for each row and put them in returnList
            do {
                int prayerDeckID = cursor.getInt(0);
                int listOrder = cursor.getInt(1);
                String deckName = cursor.getString(2);
                String tags = cursor.getString(3);
                boolean mustHaveAllTags = cursor.getInt(4) == 1;
                int maxInRotation = cursor.getInt(5);
                int rotationPosition = cursor.getInt(6);
                boolean isActive = cursor.getInt(7) == 1;


                PrayerDeck newPrayerDeck = new PrayerDeck(prayerDeckID, listOrder, deckName, tags, mustHaveAllTags, maxInRotation, rotationPosition, isActive);

                if (isActive) {
                    returnList.add(newPrayerDeck);
                } else {
                    inactiveList.add(newPrayerDeck);
                }


            } while (cursor.moveToNext()); //This while statement is true when there are still new lines
        } else {
            // failure. Add nothing to list.
        }

        //close cursor and db for others to use. Always clean up after yo'self
        cursor.close();
        db.close();

        for (int i = 0; i < inactiveList.size(); i++) {
            returnList.add(inactiveList.get(i));
        }

        return returnList;
    }

    public ArrayList<String> getDeckNames() {
        ArrayList<String> arrayListDeckNames = new ArrayList<>();

        String queryString = "SELECT " + COLUMN_DECK_NAME + " FROM " + PRAYER_DECK_TABLE + "\nORDER BY " + COLUMN_LIST_ORDER;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(queryString, null);

        if (c.moveToFirst()) {
            do {
                String deckName = c.getString(0);
                arrayListDeckNames.add(deckName);
            } while (c.moveToNext());
        }

        return arrayListDeckNames;
    }

    public void saveAllDecks(ArrayList<PrayerDeck> allDecks) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(PRAYER_DECK_TABLE, null, null);
        db.execSQL("VACUUM");

        for (int i = 0; i < allDecks.size(); i++) {
            ContentValues cv = new ContentValues();

            cv.put(COLUMN_ID, allDecks.get(i).getId());
            cv.put(COLUMN_LIST_ORDER, allDecks.get(i).getListOrder());
            cv.put(COLUMN_DECK_NAME, allDecks.get(i).getPrayerPlanName());
            cv.put(COLUMN_MAX_IN_ROTATION, allDecks.get(i).getMaxCardsInRotation());
            cv.put(COLUMN_TAGS, allDecks.get(i).getTags());
            cv.put(COLUMN_ALL_TAGS, allDecks.get(i).isMustHaveAllTags());
            cv.put(COLUMN_ROTATION_POSITION, allDecks.get(i).getRotationPosition());
            cv.put(COLUMN_ACTIVE, allDecks.get(i).isActive());

            db.insert(PRAYER_DECK_TABLE, null, cv);
        }

        db.close();
    }

    public long addOneDeck(PrayerDeck newPrayerDeck) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DECK_NAME, newPrayerDeck.getPrayerPlanName());
        cv.put(COLUMN_TAGS, newPrayerDeck.getTags());
        cv.put(COLUMN_MAX_IN_ROTATION, newPrayerDeck.getMaxCardsInRotation());
        cv.put(COLUMN_ALL_TAGS, newPrayerDeck.isMustHaveAllTags());
        cv.put(COLUMN_ROTATION_POSITION, newPrayerDeck.getRotationPosition());
        cv.put(COLUMN_ACTIVE, newPrayerDeck.isActive());
        cv.put(COLUMN_LIST_ORDER, newPrayerDeck.getListOrder());

        long insert = db.insert(PRAYER_DECK_TABLE, null, cv);

        db.close(); //@Pete added this code based on below. Who knows if it's helpful, anti-helpful, or irrelevant.

        return insert;
    }

    public boolean resetDecks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRAYER_DECK_TABLE, null, null);
        db.execSQL("VACUUM");

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_LIST_ORDER, -1);
        cv.put(COLUMN_DECK_NAME, "Default");
        cv.put(COLUMN_TAGS, "");
        cv.put(COLUMN_ALL_TAGS, false);
        cv.put(COLUMN_MAX_IN_ROTATION, 3);
        cv.put(COLUMN_ROTATION_POSITION, PrayerDeck.END);
        cv.put(COLUMN_ACTIVE, true);

        db.insert(PRAYER_DECK_TABLE, null, cv);

        db.close();
        return true;
    }

    public PrayerDeck getDeckByName(String deckParams) {
        //get data from db

        String queryString = "SELECT * FROM " + PRAYER_DECK_TABLE + " WHERE " + COLUMN_DECK_NAME + " = '" + deckParams + "'"; //Look here for help with SQL queries:  https://www.w3schools.com/sqL/sql_examples.asp

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        PrayerDeck newPrayerDeck = null;

        if (cursor.moveToFirst()) {   //true if there is a first entry ie. the db is not empty
            // loop through cursor and create new customer objects for each row and put them in returnList
            do {
                int prayerDeckID = cursor.getInt(0);
                int listOrder = cursor.getInt(1);
                String deckName = cursor.getString(2);
                String tags = cursor.getString(3);
                boolean mustHaveAllTags = cursor.getInt(4) == 1;
                int maxCardsInRotation = cursor.getInt(5);
                int rotationPosition = cursor.getInt(6);
                boolean isActive = cursor.getInt(7) == 1;

                newPrayerDeck = new PrayerDeck(prayerDeckID, listOrder, deckName, tags, mustHaveAllTags, maxCardsInRotation, rotationPosition, isActive);

            } while (cursor.moveToNext()); //This while statement is true when there are still new lines
        } else {
            // failure. Add nothing to list.
        }

        //close cursor and db for others to use. Always clean up after yo'self
        cursor.close();
        db.close();

        return newPrayerDeck;
    }

    public String saveToCSV(OutputStream os) {
        // TODO: 2/19/2022 Make this work better. May have to do checks if external storage exists etc. Got most this code from here:  https://parallelcodes.com/android-export-sqlite-database/, also here: https://stackoverflow.com/questions/31367270/exporting-sqlite-database-to-csv-file-in-android


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;

        try {
            c = db.rawQuery("SELECT * FROM " + PRAYER_CARD_TABLE, null);
            int rowCount = 0;
            int colCount = 0;
//                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                String filename = "MyBackUp.csv";
//                // the name of the file to export with
//                File saveFile = new File(downloadDir, filename);
//                FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            rowCount = c.getCount();
            colCount = c.getColumnCount();
            if (rowCount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowCount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colCount; j++) {
                        String uneditedString = c.getString(j);
                        String newString = Utils.replaceCommasAndNewLines(uneditedString);
                        if (j != colCount - 1)
                            bw.write(newString + ",");
                        else
                            bw.write(newString);
                    }
                    bw.newLine();
                }
                bw.flush();

                return "Exported Successfully.";
            }
        } catch (Exception ex) {
            if (db.isOpen()) {
                db.close();
                return ex.getMessage();
            }

        }

        return "Export failed";
    }

    public String importFromCSV(InputStream is) throws IOException {

        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        ArrayList<String[]> rows = new ArrayList<>();
        String line;

        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] row = line.split(",");
            rows.add(row);
        }

        ArrayList<PrayerCard> importedCards = new ArrayList<>();

        try {
            for (String[] row : rows) {
                int id = Integer.parseInt(row[0]);
                int listOrder = Integer.parseInt(row[1]);
                String prayerText = Utils.putCommasAndNewLinesBackIn(row[2]);
                String tags = Utils.putCommasAndNewLinesBackIn(row[3]);
                int maxFreq = Integer.parseInt(row[4]);
                int multiMaxFreq = Integer.parseInt(row[5]);
                boolean inRotation = Integer.parseInt(row[6]) == 1;
                Date lastSeen = new Date(Long.parseLong(row[7]));
                int viewsRemaining = Integer.parseInt(row[8]);
                Date expiryDate = new Date(Long.parseLong(row[9]));
                boolean isActive = Integer.parseInt(row[10]) == 1;

                PrayerCard prayerCard = new PrayerCard(id, listOrder, prayerText, tags, maxFreq, multiMaxFreq, inRotation, lastSeen, viewsRemaining, expiryDate, isActive);
                importedCards.add(prayerCard);
            }
        } catch (Exception e) {
            return "CSV file incorrectly formatted.\n\nOnly import CSV files created through this app's export function.";
        }

        int size = importedCards.size();
        for (int i = 0; i < size; i++) {
            int oldListOrder = importedCards.get(i).getListOrder();
            importedCards.get(i).setListOrder(oldListOrder - size);
            addOne(importedCards.get(i));
        }

        return size + " Prayer Cards Successfully Imported";
    }
}



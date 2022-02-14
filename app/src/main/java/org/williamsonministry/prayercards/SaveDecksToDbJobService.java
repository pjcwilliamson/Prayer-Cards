package org.williamsonministry.prayercards;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SaveDecksToDbJobService extends IntentService {
    private static final String TAG = "SaveDecksToDbJobService";
    public static final String ALL_PRAYERDECKS_ARRAYLIST_KEY = "allDecks";

    /**
     * @deprecated
     */
    public SaveDecksToDbJobService() {
        super("Saving All Prayer Decks");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (null != intent) {
            Log.d(TAG, "onHandleIntent: Save Started");
            ArrayList<PrayerDeck> allDecks = intent.getParcelableArrayListExtra(ALL_PRAYERDECKS_ARRAYLIST_KEY);
            DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
            dataBaseHelper.saveAllDecks(allDecks);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}

package org.williamsonministry.prayercards;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
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
            SharedPreferences sp4 = getSharedPreferences("SAVE_FINISH", MODE_PRIVATE);
            SharedPreferences.Editor editor4 = sp4.edit();
            editor4.putBoolean("SAVE_FINISH", false);
            editor4.apply();
            ArrayList<PrayerDeck> allDecks = intent.getParcelableArrayListExtra(ALL_PRAYERDECKS_ARRAYLIST_KEY);
            DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
            dataBaseHelper.saveAllDecks(allDecks);
            // TODO: 4/20/2022 Delete this deck testing code
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            editor4.putBoolean("SAVE_FINISH", true);
            editor4.apply();
            Log.d(TAG, "onHandleIntent: Save Finished");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}

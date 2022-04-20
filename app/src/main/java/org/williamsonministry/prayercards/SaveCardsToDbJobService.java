package org.williamsonministry.prayercards;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SaveCardsToDbJobService extends IntentService {
    private static final String TAG = "SaveCardsToDbJobService";
    public static final String ALL_PRAYERCARDS_ARRAYLIST_KEY = "allCards";

    /**
     * @deprecated
     */
    public SaveCardsToDbJobService() {
        super("Saving All Prayer Cards");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (null != intent) {
            Log.d(TAG, "onHandleIntent: Save Started");
            SharedPreferences sp4 = getSharedPreferences("SAVE_FINISH", MODE_PRIVATE);
            SharedPreferences.Editor editor4 = sp4.edit();
            editor4.putBoolean("SAVE_FINISH", false);
            editor4.apply();
            ArrayList<PrayerCard> allCards = intent.getParcelableArrayListExtra(ALL_PRAYERCARDS_ARRAYLIST_KEY);
            DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
            dataBaseHelper.saveAllCards(allCards);
            editor4.putBoolean("SAVE_FINISH", true);
            editor4.apply();
            Log.d(TAG, "onHandleIntent: Save Ended");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}

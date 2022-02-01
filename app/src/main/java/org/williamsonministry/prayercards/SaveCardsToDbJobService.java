package org.williamsonministry.prayercards;

import android.app.IntentService;
import android.content.Intent;
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
            ArrayList<PrayerCard> allCards = intent.getParcelableArrayListExtra(ALL_PRAYERCARDS_ARRAYLIST_KEY);
            DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
            dataBaseHelper.saveAllCards(allCards);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}

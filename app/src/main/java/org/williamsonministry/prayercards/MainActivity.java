package org.williamsonministry.prayercards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.williamsonministry.prayercards.DeckSwipe.DECK_PARAMS;

public class MainActivity extends AppCompatActivity {
    public static final String EDIT_STARTUP = "editStartup";

//    Using this YouTube video to figure out ViewPager https://www.youtube.com/watch?v=7aLCWbe6Awk

    private Button btnPrayerStart;
    private ImageButton btnEditCards, btnNewCard, btnEditDecks, btnInfo;
    private Spinner spSelectDeck;
    private ArrayAdapter<String> deckArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        SharedPreferences sp3 = getSharedPreferences("FIRST_OPEN", MODE_PRIVATE);
        boolean hasBeenOpened = sp3.getBoolean("FIRST_OPEN", false);
        if (!hasBeenOpened) {
            AlertDialog aDHelp = new AlertDialog.Builder(MainActivity.this).create();
            aDHelp.setTitle("Info");
            aDHelp.setMessage("This is an app to help you keep track of things you want to be praying for.\n\n"+
                    "With this app you can create digital prayer cards. Some things you may want to pray for every time you pray, other things" +
                    " you may want to be praying for more occasionally. Sometimes you may want to pray for something a few times " +
                    "over a few weeks, such as when someone gives you a prayer request. This app can accommodate however you want to be praying!\n\n" +
                    "Start making prayer cards by pressing 'Add New Card' and pray through your cards by pressing 'Pray Now'.\n\n" +
                    "Made in 2021 by Pete Williamson.\n\nPlease ask any questions or report any bugs at williamsonapps@outlook.com");
            aDHelp.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    aDHelp.dismiss();
                }
            });
            aDHelp.show();
        }
        SharedPreferences.Editor editor3 = sp3.edit();
        editor3.putBoolean("FIRST_OPEN", true);
        editor3.apply();

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog aDHelp = new AlertDialog.Builder(MainActivity.this).create();
                aDHelp.setTitle("Info");
                aDHelp.setMessage("This is an app to help you keep track of things you want to be praying for.\n\n"+
                        "With this app you can create digital prayer cards. Some things you may want to pray for every time you pray, other things" +
                        " you may want to be praying for more occasionally. Sometimes you may want to pray for something a few times " +
                        "over a few weeks, such as when someone gives you a prayer request. This app can accommodate however you want to be praying!\n\n" +
                        "Start making prayer cards by pressing 'Add New Card' and pray through your cards by pressing 'Pray Now'.\n\n" +
                        "Made in 2021 by Pete Williamson.\n\nPlease ask any questions or report any bugs at williamsonapps@outlook.com");
                aDHelp.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        aDHelp.dismiss();
                    }
                });
                aDHelp.show();
            }
        });

        btnEditDecks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditDecks.class);
                startActivity(intent);
            }
        });

        btnPrayerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deckName = spSelectDeck.getSelectedItem().toString();
                Intent intent = new Intent(MainActivity.this,DeckSwipe.class);
                intent.putExtra(DECK_PARAMS, deckName);
                startActivity(intent);
            }
        });

        btnNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditCards.class);
                intent.putExtra(EDIT_STARTUP, "add");
                startActivity(intent);
            }
        });

        btnEditCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditCards.class);
                startActivity(intent);
            }
        });

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

        ArrayList<String> deckNameArrayList = dataBaseHelper.getDeckNames();

        SharedPreferences sp = getSharedPreferences("LAST_PRAYER_PLAN", Context.MODE_PRIVATE);

        String lastDeckName = sp.getString("LAST_PRAYER_PLAN","CJkjd&ijv33dcsaDRF$#vH%$435");

        int lastPlanOrder;

        if (lastDeckName.equals("CJkjd&ijv33dcsaDRF$#vH%$435")) {
            lastPlanOrder = 0;
        }   else    {
            PrayerDeck lastDeckSelected = dataBaseHelper.getDeckByName(lastDeckName);
            if (null != lastDeckSelected) {
                lastPlanOrder = lastDeckSelected.getListOrder();
            } else  {
                lastPlanOrder = 0;
            }
        }

        String[] deckNameArray = deckNameArrayList.toArray(new String[0]);

        deckArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, deckNameArray);
        spSelectDeck.setAdapter(deckArrayAdapter);
        spSelectDeck.setSelection(lastPlanOrder);

        spSelectDeck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = sp.edit();
                String prevDeckName = adapterView.getSelectedItem().toString();
                editor.putString("LAST_PRAYER_PLAN", prevDeckName);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initViews() {
        btnPrayerStart = findViewById(R.id.btnStartPrayer);
        btnNewCard = findViewById(R.id.btnNewCard);
        btnEditCards = findViewById(R.id.btnEditCards);
        btnEditDecks = findViewById(R.id.btnEditDecks);
        spSelectDeck = findViewById(R.id.spDecks);
        btnInfo = findViewById(R.id.btnInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

        ArrayList<String> deckNameArrayList = dataBaseHelper.getDeckNames();

        SharedPreferences sp = getSharedPreferences("LAST_PRAYER_PLAN", Context.MODE_PRIVATE);

        String lastDeckName = sp.getString("LAST_PRAYER_PLAN","CJkjd&ijv33dcsaDRF$#vH%$435");

        int lastPlanOrder;

        if (lastDeckName.equals("CJkjd&ijv33dcsaDRF$#vH%$435")) {
            lastPlanOrder = 0;
        }   else    {
            PrayerDeck lastDeckSelected = dataBaseHelper.getDeckByName(lastDeckName);
            if (null != lastDeckSelected) {
                lastPlanOrder = lastDeckSelected.getListOrder();
            } else  {
                lastPlanOrder = 0;
            }
        }

        String[] deckNameArray = deckNameArrayList.toArray(new String[0]);

        deckArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, deckNameArray);
        spSelectDeck.setAdapter(deckArrayAdapter);
        spSelectDeck.setSelection(lastPlanOrder);
    }
}
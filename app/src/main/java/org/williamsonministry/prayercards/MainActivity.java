package org.williamsonministry.prayercards;

import static org.williamsonministry.prayercards.DeckSwipe.DECK_PARAMS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    public static final String EDIT_STARTUP = "editStartup";
    private static final String TAG = "MainActivity";

//    Using this YouTube video to figure out ViewPager https://www.youtube.com/watch?v=7aLCWbe6Awk

    private Button btnPrayerStart;
    private ImageButton btnEditCards, btnNewCard, btnEditDecks, btnInfo, btnAnswered;
    private Spinner spSelectDeck;
    private ArrayAdapter<String> deckArrayAdapter;
    private boolean isSaveFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started");
        
        initViews();

        String infoText = "This is an app to help you keep track of things you want to be praying for.\n\n" +
                "With this app you can create digital prayer cards. Some things you may want to pray for every time you pray, other things" +
                " you may want to be praying for more occasionally. Sometimes you may want to pray for something a few times " +
                "over a few weeks, such as when someone gives you a prayer request. You can also mark prayers as 'answered'." +
                " This app can accommodate however you want to be praying!\n\n" +
                "Start making prayer cards by pressing 'Add New Card' and pray through your cards by pressing 'Pray Now'.\n\n" +
                "v1.5.10 - Made in 2021 by Pete Williamson.\n\nPlease ask any questions or report any bugs at williamsonapps@outlook.com";

        SharedPreferences sp3 = getSharedPreferences("FIRST_OPEN", MODE_PRIVATE);
        boolean hasBeenOpened = sp3.getBoolean("FIRST_OPEN", false);
        if (!hasBeenOpened) {
            AlertDialog aDHelp = new AlertDialog.Builder(MainActivity.this).create();
            aDHelp.setTitle("Info");
            aDHelp.setMessage(infoText);
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

        SharedPreferences sp4 = getSharedPreferences("SAVE_FINISH", MODE_PRIVATE);
        SharedPreferences.Editor editor4 = sp4.edit();
        editor4.putBoolean("SAVE_FINISH", true);
        editor4.apply();

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog aDHelp = new AlertDialog.Builder(MainActivity.this).create();
                aDHelp.setTitle("Info");
                aDHelp.setMessage(infoText);
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
                if (checkIfSaveFinished()) {
                    Intent intent = new Intent(MainActivity.this, EditDecks.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to open", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrayerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkIfSaveFinished()) {
                    String deckName = spSelectDeck.getSelectedItem().toString();
                    Intent intent = new Intent(MainActivity.this, DeckSwipe.class);
                    intent.putExtra(DECK_PARAMS, deckName);
                    startActivity(intent);
                } else  {
                    Toast.makeText(MainActivity.this, "Failed to open", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkIfSaveFinished()) {
                    Intent intent = new Intent(MainActivity.this, EditCards.class);
                    intent.putExtra(EDIT_STARTUP, "add");
                    startActivity(intent);
                } else  {
                    Toast.makeText(MainActivity.this, "Failed to open", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEditCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkIfSaveFinished()) {
                    Intent intent = new Intent(MainActivity.this, EditCards.class);
                    startActivity(intent);
                } else  {
                    Toast.makeText(MainActivity.this, "Failed to open", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAnswered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkIfSaveFinished())  {
                    Intent intent = new Intent(MainActivity.this, EditCards.class);
                    intent.putExtra(EDIT_STARTUP, "answered");
                    startActivity(intent);
                }   else    {
                    Toast.makeText(MainActivity.this, "Failed to open", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

        ArrayList<String> deckNameArrayList = dataBaseHelper.getDeckNames();

        SharedPreferences sp = getSharedPreferences("LAST_PRAYER_PLAN", Context.MODE_PRIVATE);

        String lastDeckName = sp.getString("LAST_PRAYER_PLAN", null);

        int lastPlanOrder = 0;

        if (null != lastDeckName) {
            for (int i = 0; i < deckNameArrayList.size(); i++) {
                if (lastDeckName.equals(deckNameArrayList.get(i))) {
                    lastPlanOrder = i;
                }
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

    private boolean checkIfSaveFinished() {
        SharedPreferences sp5 = getSharedPreferences("SAVE_FINISH", MODE_PRIVATE);
        isSaveFinished = sp5.getBoolean("SAVE_FINISH", false);
        if (!isSaveFinished) {
            Toast.makeText(MainActivity.this, "Please Wait...", Toast.LENGTH_SHORT).show();
        }
        while (!sp5.getBoolean("SAVE_FINISH", false)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        isSaveFinished = sp5.getBoolean("SAVE_FINISH", false);
        return isSaveFinished;
    }

    private void initViews() {
        btnPrayerStart = findViewById(R.id.btnStartPrayer);
        btnNewCard = findViewById(R.id.btnNewCard);
        btnEditCards = findViewById(R.id.btnEditCards);
        btnEditDecks = findViewById(R.id.btnEditDecks);
        spSelectDeck = findViewById(R.id.spDecks);
        btnInfo = findViewById(R.id.btnInfo);
        btnAnswered = findViewById(R.id.btnAnswered);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: Resumed");
        super.onResume();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

        ArrayList<String> deckNameArrayList = dataBaseHelper.getDeckNames();

        SharedPreferences sp = getSharedPreferences("LAST_PRAYER_PLAN", Context.MODE_PRIVATE);

        String lastDeckName = sp.getString("LAST_PRAYER_PLAN", null);

        int lastPlanOrder = 0;

        if (null != lastDeckName) {
            for (int i = 0; i < deckNameArrayList.size(); i++) {
                if (lastDeckName.equals(deckNameArrayList.get(i))) {
                    lastPlanOrder = i;
                }
            }
        }

        String[] deckNameArray = deckNameArrayList.toArray(new String[0]);

        deckArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, deckNameArray);
        spSelectDeck.setAdapter(deckArrayAdapter);
        spSelectDeck.setSelection(lastPlanOrder);
    }
}
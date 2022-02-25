package org.williamsonministry.prayercards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static org.williamsonministry.prayercards.MainActivity.EDIT_STARTUP;
import static org.williamsonministry.prayercards.PrayerCard.ALWAYS;
import static org.williamsonministry.prayercards.PrayerCard.UNUSED;

public class EditCards extends AppCompatActivity implements OnStartDragListener {
    private static final int CREATE_FILE_CVS = 801;
    private static final int SHARE_CVS_FILE = 802;
    private static final int WRITE_PERMISSION_CODE = 101;

    private RecyclerView editCardsRecView;
    private CardRecViewAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private ImageButton btnOpenFilter, btnDeleteAll, btnAdd, btnExportImport;
    private CheckBox cbFilterAlways, cbFilterRotation, cbFilterRegSched, cbAllTags;
    private EditText etFilterTags, etNameSearch;
    private Button btnApplyFilter, btnResetFilter;
    private ConstraintLayout layoutFilter;

    public CardRecViewAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_CVS && resultCode == Activity.RESULT_OK) {

            Uri uri = data.getData();
            try {
                OutputStream os = getContentResolver().openOutputStream(uri);
                DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
                String result = dataBaseHelper.saveToCSV(os);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/csv");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivityForResult(Intent.createChooser(shareIntent, "Backup"), SHARE_CVS_FILE);

                Toast.makeText(EditCards.this, result, Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cards);

        /*
        Code below can be enabled to test the swipe instruction
         */
//        SharedPreferences sp2 = this.getSharedPreferences("SWIPED", MODE_PRIVATE);
//        SharedPreferences.Editor editor2 = sp2.edit();
//        editor2.putBoolean("SWIPED", false);
//        editor2.apply();

        initViews();

        adapter = new CardRecViewAdapter(this,this);
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

        editCardsRecView.setAdapter(adapter);
        editCardsRecView.setLayoutManager(new LinearLayoutManager(this));

        final ArrayList<PrayerCard> allCardsNewListOrder = dataBaseHelper.getAll();
        for (int i = 0; i < allCardsNewListOrder.size(); i++) {
            allCardsNewListOrder.get(i).setListOrder(i);
        }

        /*
        Inactivate any cards past their expiry
         */
        for (int i = 0; i < allCardsNewListOrder.size(); i++)    {
            if (allCardsNewListOrder.get(i).getExpiryDate().getTime() < Calendar.getInstance().getTimeInMillis() && allCardsNewListOrder.get(i).getExpiryDate().getTime() != UNUSED)    {
                allCardsNewListOrder.get(i).setActive(false);
            }
        }

        adapter.setCards(allCardsNewListOrder);

        btnOpenFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutFilter.getVisibility() == View.GONE)  {
                    layoutFilter.setVisibility(View.VISIBLE);
                } else if (layoutFilter.getVisibility() == View.VISIBLE)   {
                    layoutFilter.setVisibility(View.GONE);
                }
            }
        });

        btnAdd.setOnClickListener(new CardEditOrAddDialog(CardEditOrAddDialog.ADD,this, null, UNUSED));

        btnExportImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2/21/2022 Add a proper import/export UI
                exportData();
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(EditCards.this).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Are you sure?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(EditCards.this);
                        boolean success = dataBaseHelper.clearAll();
                        adapter.setCards(dataBaseHelper.getAll());
                        adapter.getAllPrayerCards().clear();
                        alertDialog.dismiss();

                        if (success)    {
                            Toast.makeText(EditCards.this, "Prayer Cards Cleared", Toast.LENGTH_SHORT).show();
                        }   else    {
                            Toast.makeText(EditCards.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setCards(applyFilter());
                layoutFilter.setVisibility(View.GONE);
            }
        });

        btnResetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cbFilterAlways.setChecked(true);
                cbFilterRotation.setChecked(true);
                cbFilterRegSched.setChecked(true);
                etFilterTags.setText("");
                etNameSearch.setText("");
                cbAllTags.setChecked(false);
                adapter.setCards(applyFilter());
                layoutFilter.setVisibility(View.GONE);
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(editCardsRecView);

        Intent intent = getIntent();
        if (intent.getStringExtra(EDIT_STARTUP) != null){
            if (intent.getStringExtra(EDIT_STARTUP).equals("add")) {
                btnAdd.performClick();
            }
        }
    }

    private void exportData() {
        // TODO: 2/21/2022 Manage permission better - maybe provide rationale?
        if (ContextCompat.checkSelfPermission(EditCards.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditCards.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, "MyBackUp.csv");
            startActivityForResult(intent, CREATE_FILE_CVS);
        }
    }

    /*Reads all the filter options, and returns a deck of cards based on the filter*/
    protected ArrayList<PrayerCard> applyFilter() {
        //DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        //ArrayList<PrayerCard> deckToDrawFilteredDeckFrom = dataBaseHelper.getAll();
        ArrayList<PrayerCard> filteredDeck = new ArrayList<>();
        for (int i = 0; i < adapter.getAllPrayerCards().size(); i++)    {
            filteredDeck.add(adapter.getAllPrayerCards().get(i));
        }
        ArrayList<Integer> remove = new ArrayList<>();
        if (!cbFilterAlways.isChecked()) {
            for (int i = filteredDeck.size()-1; i >= 0; i--)    {
                if (filteredDeck.get(i).getMaxFrequency() == ALWAYS)  {
                    remove.add(i);
                }
            }
            for (int i : remove) {
                filteredDeck.remove(i);
            }
            remove.clear();
        }
        if (!cbFilterRotation.isChecked()) {
            for (int i = filteredDeck.size()-1; i >= 0; i--)    {
                if (filteredDeck.get(i).isInRotation())  {
                    remove.add(i);
                }
            }
            for (int i : remove) {
                filteredDeck.remove(i);
            }
            remove.clear();
        }
        if (!cbFilterRegSched.isChecked()) {
            for (int i = filteredDeck.size()-1; i >= 0; i--)    {
                if (filteredDeck.get(i).getMaxFrequency() > 0)  {
                    remove.add(i);
                }
            }
            for (int i : remove) {
                filteredDeck.remove(i);
            }
            remove.clear();
        }
        if (!etFilterTags.getText().toString().equals(""))  {
            ArrayList<String> filterTags = Utils.commaStringToArraylist(etFilterTags.getText().toString());
            if (cbAllTags.isChecked())  {
                    for (int i = filteredDeck.size()-1; i >= 0; i--)   {
                        ArrayList<String> cardTags = Utils.commaStringToArraylist(filteredDeck.get(i).getTags());
                        int numberOfMatchedTags = 0;
                        for (String s: filterTags)  {
                            int instance = 0;   //To avoid having the same tag multiple times counting as having multiple tags
                            for (int j = 0; j < cardTags.size(); j++) {
                                if (cardTags.get(j).equalsIgnoreCase(s)) {
                                    if (instance == 0) {
                                        numberOfMatchedTags++;
                                    }
                                    instance++;
                                }
                            }
                        }
                        if (numberOfMatchedTags < filterTags.size())   {
                            remove.add(i);
                        }
                    }
                for (int i : remove) {
                    filteredDeck.remove(i);
                }
                remove.clear();
            }   else    {
                for (int i = filteredDeck.size()-1; i >= 0; i--)   {
                    ArrayList<String> cardTags = Utils.commaStringToArraylist(filteredDeck.get(i).getTags());
                    int numberOfMatchedTags = 0;
                    for (String s: filterTags)  {
                        for (int j = 0; j < cardTags.size(); j++) {
                            if (cardTags.get(j).equalsIgnoreCase(s)) {
                                numberOfMatchedTags++;
                            }
                        }
                    }
                    if (numberOfMatchedTags == 0)   {
                        remove.add(i);
                    }
                }
                for (int i : remove) {
                    filteredDeck.remove(i);
                }
                remove.clear();
            }
        }

        if (!etNameSearch.getText().toString().equals(""))  {
            CharSequence nameSearch = etNameSearch.getText().toString().toLowerCase().trim();
            for (int i = filteredDeck.size()-1; i >= 0; i--)    {
                if (!filteredDeck.get(i).getPrayerText().toLowerCase().contains(nameSearch))  {
                    remove.add(i);
                }
            }
            for (int i : remove) {
                filteredDeck.remove(i);
            }
            remove.clear();
        }

        return filteredDeck;
    }

    private void initViews() {
        editCardsRecView = findViewById(R.id.editCardsRecyclerView);
        btnOpenFilter = findViewById(R.id.btnOpenFilter);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnAdd = findViewById(R.id.btnAdd);
        btnExportImport = findViewById(R.id.btnExportImport);
        cbFilterAlways = findViewById(R.id.cbFilterAlways);
        cbFilterRotation = findViewById(R.id.cbFilterRotation);
        cbFilterRegSched = findViewById(R.id.cbFilterRegSched);
        cbAllTags = findViewById(R.id.cbAllTags);
        etFilterTags = findViewById(R.id.etFilterTags);
        etNameSearch = findViewById(R.id.etNameSearch);
        btnApplyFilter = findViewById(R.id.btnFilter);
        btnResetFilter = findViewById(R.id.btnResetFilter);
        layoutFilter = findViewById(R.id.layoutFilter);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_PERMISSION_CODE)   {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportData();
            } else  {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
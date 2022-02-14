package org.williamsonministry.prayercards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static org.williamsonministry.prayercards.MainActivity.EDIT_STARTUP;
import static org.williamsonministry.prayercards.PrayerCard.ALWAYS;
import static org.williamsonministry.prayercards.PrayerCard.UNUSED;

public class EditCards extends AppCompatActivity implements OnStartDragListener {

    private RecyclerView editCardsRecView;
    private CardRecViewAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private ImageButton btnOpenFilter, btnDeleteAll, btnAdd;
    private CheckBox cbFilterAlways, cbFilterRotation, cbFilterRegSched, cbAllTags;
    private EditText etFilterTags, etNameSearch;
    private Button btnApplyFilter, btnResetFilter;
    private ConstraintLayout layoutFilter;

    public CardRecViewAdapter getAdapter() {
        return adapter;
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
}
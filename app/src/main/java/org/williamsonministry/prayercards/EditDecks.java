package org.williamsonministry.prayercards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import static org.williamsonministry.prayercards.PrayerCard.UNUSED;

public class EditDecks extends AppCompatActivity implements OnStartDragListener {
    private ImageButton btnAdd, btnDeleteAll;
    private RecyclerView decksRecyclerView;
    private DeckRecViewAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_decks);

        initViews();

        adapter = new DeckRecViewAdapter(this, this);
        decksRecyclerView.setAdapter(adapter);
        decksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new DeckEditOrAddDialog(DeckEditOrAddDialog.ADD,this, null, UNUSED));

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(EditDecks.this).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Are you sure you want to delete all custom Prayer Plans and reset to the default Prayer Plan?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(EditDecks.this);
                        boolean success = dataBaseHelper.resetDecks();
                        adapter.setAllDecks(dataBaseHelper.getAllDecks());
                        alertDialog.dismiss();
                        adapter.notifyDataSetChanged();

                        if (success)    {
                            Toast.makeText(EditDecks.this, "Prayer Plans Reset", Toast.LENGTH_SHORT).show();
                        }   else    {
                            Toast.makeText(EditDecks.this, "Error", Toast.LENGTH_SHORT).show();
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

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(decksRecyclerView);
    }

    private void initViews() {
        btnAdd = findViewById(R.id.btnAdd);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        decksRecyclerView = findViewById(R.id.editDecksRecyclerView);
    }

    public DeckRecViewAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
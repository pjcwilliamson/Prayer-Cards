package org.williamsonministry.prayercards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.LogRecord;

import static org.williamsonministry.prayercards.PrayerCard.UNUSED;

public class DeckRecViewAdapter extends RecyclerView.Adapter<DeckRecViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<PrayerDeck> allDecks;
    private final Context mContext;
    private final OnStartDragListener mDragStartListener;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;

    public DeckRecViewAdapter(Context mContext, OnStartDragListener dragStartListener) {
        this.mContext = mContext;
        this.mDragStartListener = dragStartListener;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        allDecks = dataBaseHelper.getAllDecks();
    }

    @NonNull
    @Override
    public DeckRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_card_card, parent, false);
        return new DeckRecViewAdapter.ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtPrayerRequest.setText(allDecks.get(holder.getAdapterPosition()).getPrayerPlanName());

        holder.btnInActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allDecks.size() == 1)   {
                    final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("VERBOTEN");
                    alertDialog.setMessage("You cannot delete the last Prayer Plan");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Oh, okay then.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alertDialog.show();
                } else  {
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Do you want to delete this Prayer Plan?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        int x = -1;
//                        int Id = currentPrayerCards.get(holder.getAdapterPosition()).getId();
//                        for (int j = 0; j < allPrayerCards.size(); j++)   {
//                            if (allPrayerCards.get(j).getId() == Id)    {
//                                x = j;
//                            }
//                        }
//                        allPrayerCards.remove(x);
//                        for (int k = 0; k < allPrayerCards.size(); k++) {
//                            allPrayerCards.get(k).setListOrder(k);
//                        }
                        allDecks.remove(holder.getAdapterPosition());
                        Toast.makeText(mContext, "Prayer Plan Deleted", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
            }}
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeckEditOrAddDialog editDialog = new DeckEditOrAddDialog(DeckEditOrAddDialog.EDIT ,mContext, allDecks.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                editDialog.onClick(holder.btnEdit);
            }
        });

        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
//                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN)  {
//                    mDragStartListener.onStartDrag(holder);
//                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return allDecks.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(allDecks, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(allDecks, i, i - 1);
            }
        }

        resetListOrder();

        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    public void resetListOrder() {
        for (int i = 0; i < allDecks.size(); i++)   {
            allDecks.get(i).setListOrder(i);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtPrayerRequest;
        private final Button btnEdit;
        private final Button btnInActivate;
        private final CardView parent;
        private final ImageView handleView;
        private final Button btnRestore;
        private final Button btnDeleteForever;
        private final ConstraintLayout layoutDataCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutDataCard = itemView.findViewById(R.id.layoutDataCard);
            parent = itemView.findViewById(R.id.cardViewParent);
            txtPrayerRequest = itemView.findViewById(R.id.txtPrayerRequest);
            btnEdit = itemView.findViewById(R.id.btnEditCard);
            btnInActivate = itemView.findViewById(R.id.btnInactivateCard);
            handleView = itemView.findViewById(R.id.imgDragHandle);
            btnRestore = itemView.findViewById(R.id.btnRestore);
            btnDeleteForever = itemView.findViewById(R.id.btnDeleteForever);

        }
    }

    public ArrayList<PrayerDeck> getAllDecks() {
        return allDecks;
    }
    public void setAllDecks(ArrayList<PrayerDeck> allDecks) {
        this.allDecks = allDecks;
    }
}



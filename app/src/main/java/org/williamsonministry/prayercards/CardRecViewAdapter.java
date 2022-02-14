package org.williamsonministry.prayercards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.williamsonministry.prayercards.PrayerCard.UNUSED;
import static org.williamsonministry.prayercards.SaveCardsToDbJobService.ALL_PRAYERCARDS_ARRAYLIST_KEY;

public class CardRecViewAdapter extends RecyclerView.Adapter<CardRecViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<PrayerCard> allPrayerCards = new ArrayList<>();
    private ArrayList<PrayerCard> currentPrayerCards = new ArrayList<>();
    private final Context mContext;
    private final OnStartDragListener mDragStartListener;
    private int positionFirstInactive;


    public CardRecViewAdapter(Context mContext, OnStartDragListener dragStartListener) {
        this.mContext = mContext;
        this.mDragStartListener = dragStartListener;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        allPrayerCards = dataBaseHelper.getAll();
    }


    public int getPositionFirstInactive() {
        return positionFirstInactive;
    }

    public void setPositionFirstInactive(int positionFirstInactive) {
        this.positionFirstInactive = positionFirstInactive;
    }

    public ArrayList<PrayerCard> getAllPrayerCards() {
        return allPrayerCards;
    }

    public void setAllPrayerCards(ArrayList<PrayerCard> allPrayerCards) {
        this.allPrayerCards = allPrayerCards;
    }

    public void setCurrentPrayerCards(ArrayList<PrayerCard> currentPrayerCards) {
        this.currentPrayerCards = currentPrayerCards;
    }

    public ArrayList<PrayerCard> getCurrentPrayerCards() {
        return currentPrayerCards;
    }

    @NonNull
    @Override
    public CardRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_card_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final CardRecViewAdapter.ViewHolder holder, final int position) {

        if (!currentPrayerCards.get(holder.getAdapterPosition()).isActive()) {
            holder.btnInActivate.setVisibility(View.GONE);
            holder.handleView.setVisibility(View.GONE);
            holder.btnDeleteForever.setVisibility(View.VISIBLE);
            holder.btnRestore.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.parent.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorLightGrey));
        } else {
            holder.btnInActivate.setVisibility(View.VISIBLE);
            holder.handleView.setVisibility(View.VISIBLE);
            holder.btnDeleteForever.setVisibility(View.GONE);
            holder.btnRestore.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.parent.setCardBackgroundColor(mContext.getResources().getColor(android.R.color.white));
        }

        holder.txtPrayerRequest.setText(currentPrayerCards.get(holder.getAdapterPosition()).getPrayerText());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardEditOrAddDialog editDialog = new CardEditOrAddDialog(CardEditOrAddDialog.EDIT, mContext, currentPrayerCards.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                editDialog.onClick(holder.btnEdit);
            }
        });

        holder.btnInActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Do you want to inactivate this Prayer Card or delete it forever?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete Forever", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int x = -1;
                        int Id = currentPrayerCards.get(holder.getAdapterPosition()).getId();
                        for (int j = 0; j < allPrayerCards.size(); j++) {
                            if (allPrayerCards.get(j).getId() == Id) {
                                x = j;
                            }
                        }
                        allPrayerCards.remove(x);
                        for (int k = 0; k < allPrayerCards.size(); k++) {
                            allPrayerCards.get(k).setListOrder(k);
                        }
                        currentPrayerCards.remove(holder.getAdapterPosition());
                        Toast.makeText(mContext, "Prayer Card Deleted", Toast.LENGTH_SHORT).show();
                        setCards(currentPrayerCards);
                        notifyDataSetChanged();
                        asyncSave();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Inactivate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        currentPrayerCards.get(holder.getAdapterPosition()).setActive(false);
                        int Id = currentPrayerCards.get(holder.getAdapterPosition()).getId();

                        for (int j = 0; j < allPrayerCards.size(); j++) {
                            if (allPrayerCards.get(j).getId() == Id) {
                                allPrayerCards.get(j).setActive(false);
                            }
                        }
                        setCards(currentPrayerCards);
                        notifyDataSetChanged();
                        asyncSave();
                    }
                });
                alertDialog.show();
            }
        });

        holder.btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Do you want to restore this Prayer Card?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Restore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int Id = currentPrayerCards.get(holder.getAdapterPosition()).getId();
                        for (int j = 0; j < allPrayerCards.size(); j++) {
                            if (allPrayerCards.get(j).getId() == Id) {
                                allPrayerCards.get(j).setActive(true);
                            }
                        }
                        if (currentPrayerCards.get(holder.getAdapterPosition()).getViewsRemaining() == 0) {
                            for (int j = 0; j < allPrayerCards.size(); j++) {
                                if (allPrayerCards.get(j).getId() == Id) {
                                    allPrayerCards.get(j).setViewsRemaining(-1);
                                }
                            }
                        }
                        if (currentPrayerCards.get(holder.getAdapterPosition()).getExpiryDate().getTime() < Calendar.getInstance().getTimeInMillis()) {
                            for (int j = 0; j < allPrayerCards.size(); j++) {
                                if (allPrayerCards.get(j).getId() == Id) {
                                    allPrayerCards.get(j).setExpiryDate(new Date(UNUSED));
                                }
                            }
                        }

                        setCards(((EditCards) mContext).applyFilter());
                        notifyDataSetChanged();
                        asyncSave();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alertDialog.show();
            }
        });

        holder.btnDeleteForever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Are you sure you want to delete this Prayer Card forever?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete Forever", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int x = -1;
                        int Id = currentPrayerCards.get(holder.getAdapterPosition()).getId();
                        for (int j = 0; j < allPrayerCards.size(); j++) {
                            if (allPrayerCards.get(j).getId() == Id) {
                                x = j;
                            }
                        }
                        allPrayerCards.remove(x);
                        for (int k = 0; k < allPrayerCards.size(); k++) {
                            allPrayerCards.get(k).setListOrder(k);
                        }
                        currentPrayerCards.remove(holder.getAdapterPosition());
                        Toast.makeText(mContext, "Prayer Card Deleted", Toast.LENGTH_SHORT).show();
                        setCards(currentPrayerCards);
                        notifyDataSetChanged();
                        asyncSave();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
            }
        });

        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    public void asyncSave() {
        Intent intent = new Intent(mContext, SaveCardsToDbJobService.class);
        intent.putParcelableArrayListExtra(ALL_PRAYERCARDS_ARRAYLIST_KEY, allPrayerCards);
        mContext.startService(intent);
    }

    @Override
    public int getItemCount() {
        return currentPrayerCards.size();
    }

    public void setCards(ArrayList<PrayerCard> prayerCards) {
        positionFirstInactive = UNUSED;
        List<Integer> toRemove = new ArrayList<>();
        ArrayList<PrayerCard> toAdd = new ArrayList<>();
        for (int i = 0; i < prayerCards.size(); i++) {
            if (!prayerCards.get(i).isActive()) {
                PrayerCard pc = prayerCards.get(i);
                toRemove.add(i);
                toAdd.add(pc);

            }
        }

        for (int j = toRemove.size() - 1; j >= 0; j--) {
            int x = toRemove.get(j);
            prayerCards.remove(x);
        }

        positionFirstInactive = prayerCards.size();

        prayerCards.addAll(toAdd);
        this.currentPrayerCards = prayerCards;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtPrayerRequest;
        private final Button btnEdit;
        private final Button btnInActivate;
        private final CardView parent;
        private final ImageView handleView;
        private final Button btnRestore;
        private final Button btnDeleteForever;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.cardViewParent);
            txtPrayerRequest = itemView.findViewById(R.id.txtPrayerRequest);
            btnEdit = itemView.findViewById(R.id.btnEditCard);
            btnInActivate = itemView.findViewById(R.id.btnInactivateCard);
            handleView = itemView.findViewById(R.id.imgDragHandle);
            btnRestore = itemView.findViewById(R.id.btnRestore);
            btnDeleteForever = itemView.findViewById(R.id.btnDeleteForever);

        }
    }

    @Override
    //This is from https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf
    public boolean onItemMove(int fromPosition, int toPosition, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//         The code of what actually happens when a item is dragged.

        if (toPosition >= positionFirstInactive && positionFirstInactive != UNUSED) {
            notifyDataSetChanged();
            asyncSave();
            return false;
        } else {

            int fromId = currentPrayerCards.get(fromPosition).getId();
            int toId = currentPrayerCards.get(toPosition).getId();
            int fromListOrder = -1;
            int toListOrder = -1;

            for (int i = 0; i < allPrayerCards.size(); i++) {
                if (allPrayerCards.get(i).getId() == fromId) {
                    fromListOrder = allPrayerCards.get(i).getListOrder();
                }
                if (allPrayerCards.get(i).getId() == toId) {
                    toListOrder = allPrayerCards.get(i).getListOrder();
                }
            }

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(currentPrayerCards, i, i + 1);
                }
                for (int i = fromListOrder; i < toListOrder; i++) {
                    allPrayerCards.get(i).setListOrder(i + 1);
                    allPrayerCards.get(i + 1).setListOrder(i);
                    Collections.swap(allPrayerCards, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(currentPrayerCards, i, i - 1);
                }
                for (int i = fromListOrder; i > toListOrder; i--) {
                    allPrayerCards.get(i).setListOrder(i - 1);
                    allPrayerCards.get(i - 1).setListOrder(i);
                    Collections.swap(allPrayerCards, i, i - 1);
                }
            }

            notifyItemMoved(fromPosition, toPosition);
            asyncSave();

            return true;
        }
    }
}

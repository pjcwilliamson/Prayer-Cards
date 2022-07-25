package org.williamsonministry.prayercards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class DeckEditOrAddDialog implements View.OnClickListener {

    private final int dialogType;
    private final Context mContext;
    private PrayerDeck prayerDeck;
    private final int position;
    public static final int EDIT = 1;
    public static final int ADD = 0;

    private EditText etDeckName, etTags, etRotNumber;
    private CheckBox cbAllRot, cbTags, cbAllTags;
    private RadioGroup rgRotationPosition;
    private Button btnConfirm, btnCloseDialog;
    private ImageButton btnHelp;
    private ConstraintLayout layoutTags;
    private TextView txtDialogTitle;

    public DeckEditOrAddDialog(int dialogType, Context mContext, @Nullable PrayerDeck prayerDeck, int position) {
        this.dialogType = dialogType;
        this.mContext = mContext;
        if (prayerDeck != null){this.prayerDeck = prayerDeck;}
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        View editDialogView = LayoutInflater.from(mContext).inflate(R.layout.deck_edit_layout_dialog, null, false);

        initViews(editDialogView);

        if (dialogType == ADD)  {
            btnConfirm.setText("Add");
            txtDialogTitle.setText("Add Prayer Plan");
        }

        /*
        Set the info of the Prayer Deck if editing
         */
        if (dialogType == EDIT) {
            btnConfirm.setText("Save Changes");
            etDeckName.setText(prayerDeck.getPrayerPlanName());
            txtDialogTitle.setText("Edit Prayer Plan");
            etTags.setText(prayerDeck.getTags());
            if (!prayerDeck.getTags().equals("")) {
                layoutTags.setVisibility(View.VISIBLE);
                cbTags.setChecked(true);
            }
            if (prayerDeck.getMaxCardsInRotation() == PrayerDeck.ALL_ROTATION_CARDS)   {
                cbAllRot.setChecked(true);
                etRotNumber.setEnabled(false);
                etRotNumber.setInputType(InputType.TYPE_NULL);
            }   else    {
                etRotNumber.setText(String.valueOf(prayerDeck.getMaxCardsInRotation()));
            }
            if (prayerDeck.isMustHaveAllTags()){
                cbAllTags.setChecked(true);
            }
            switch (prayerDeck.getRotationPosition()) {
                case PrayerDeck.START:
                    rgRotationPosition.check(R.id.rbStart);
                    break;
                case PrayerDeck.MIXED:
                    rgRotationPosition.check(R.id.rbMixed);
                    break;
                default:
                    rgRotationPosition.check(R.id.rbEnd);
                    break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(editDialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog aDHelp = new androidx.appcompat.app.AlertDialog.Builder(mContext).create();
                aDHelp.setTitle("Info");
                aDHelp.setMessage("Making different Prayer Plans means you can pray in different ways at different times.\n\n"+
                        "You can set how many cards from the rotation list will appear each time you pray, or choose to include them all every time. You can also choose whether the rotation cards appear at the start, end, or mixed throughout the prayer cards.\n\n" +
                        "You can also make this Prayer Plan only include cards with the tags you choose (eg. if you only want to pray through a particular subset of Prayer Cards). You can also set whether the Prayer Cards need to have all the tags you list, or just one of them.");
                aDHelp.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        aDHelp.dismiss();
                    }
                });
                aDHelp.show();
            }
        });

        cbTags.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    layoutTags.setVisibility(View.VISIBLE);
                }else{
                    layoutTags.setVisibility(View.GONE);
                }
            }
        });

        cbAllRot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)  {
                    etRotNumber.setText("");
                    etRotNumber.setEnabled(false);
                    etRotNumber.setInputType(InputType.TYPE_NULL);
                }   else    {
                    etRotNumber.setEnabled(true);
                    etRotNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);

                int finalRotPosition;
                switch (rgRotationPosition.getCheckedRadioButtonId())   {
                    case R.id.rbStart:
                        finalRotPosition = PrayerDeck.START;
                        break;
                    case R.id.rbMixed:
                        finalRotPosition = PrayerDeck.MIXED;
                        break;
                    default:
                        finalRotPosition = PrayerDeck.END;
                        break;
                }

                int finalMaxRot;
                if (cbAllRot.isChecked())   {
                    finalMaxRot = PrayerDeck.ALL_ROTATION_CARDS;
                }   else if (etRotNumber.getText().toString().equals(""))   {
                    finalMaxRot = 0;
                }   else {
                    finalMaxRot = Integer.parseInt(etRotNumber.getText().toString());
                }

                // TODO: 7/25/2022 Add IncludesAnswered/Unanswered when button set up
                PrayerDeck newPrayerDeck = new PrayerDeck(-1,-1,etDeckName.getText().toString(),etTags.getText().toString(),cbAllTags.isChecked(),finalMaxRot,finalRotPosition,true);

                /*
                Check if the prayerdeck name is already in use
                 */
                ArrayList<PrayerDeck> listForNames = ((EditDecks) mContext).getAdapter().getAllDecks();
                boolean nameAlreadyExists = false;
                for (int i = 0; i < listForNames.size(); i++)   {
                    if (dialogType == ADD || (dialogType == EDIT && listForNames.get(i).getId() != prayerDeck.getId()))  {
                        if (listForNames.get(i).getPrayerPlanName().toLowerCase().replaceAll("\\s+", "").equals(newPrayerDeck.getPrayerPlanName().toLowerCase().replaceAll("\\s+", "")))    {
                            nameAlreadyExists = true;
                        }
                    }
                }
                if (nameAlreadyExists)  {
                        Toast.makeText(mContext, "Please pick a unique name for your Prayer Plan", Toast.LENGTH_LONG).show();
                } else {

                    if (dialogType == ADD) {
                        long newID;
                        newID = dataBaseHelper.addOneDeck(newPrayerDeck);
                        newPrayerDeck.setId((int) newID);
                        ((EditDecks) mContext).getAdapter().getAllDecks().add(0, newPrayerDeck);
                        ((EditDecks) mContext).getAdapter().resetListOrder();

                        if (newID > -1) {
                            Toast.makeText(mContext, "Prayer Plan added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                        }

//                    etDeckName.setText("");
//                    etTags.setText("");
//                    etRotNumber.setText("");
//                    rgRotationPosition.check(R.id.rbEnd);
//                    cbAllRot.setChecked(false);
//                    cbTags.setChecked(false);
//                    cbAllTags.setChecked(false);
//                    layoutTags.setVisibility(View.GONE);
                        ((EditDecks) mContext).getAdapter().notifyDataSetChanged();
                        alertDialog.cancel();
                    }

                    if (dialogType == EDIT) {
                        newPrayerDeck.setListOrder(prayerDeck.getListOrder());
                        newPrayerDeck.setId(prayerDeck.getId());
                        ((EditDecks) mContext).getAdapter().getAllDecks().set(position, newPrayerDeck);
                        Toast.makeText(mContext, "Prayer Plan edited successfully", Toast.LENGTH_SHORT).show();
                        ((EditDecks) mContext).getAdapter().notifyDataSetChanged();
                        ((EditDecks) mContext).getAdapter().asyncSave();
                        alertDialog.cancel();
                    }
                }
            }
        });
    }

    private void initViews(View editDialogView) {
        etDeckName = editDialogView.findViewById(R.id.etDeckName);
        etTags = editDialogView.findViewById(R.id.etTags);
        etRotNumber = editDialogView.findViewById(R.id.etRotCards);
        cbAllRot = editDialogView.findViewById(R.id.cbAllRot);
        cbTags = editDialogView.findViewById(R.id.cbTags);
        cbAllTags = editDialogView.findViewById(R.id.cbAllTags);
        rgRotationPosition = editDialogView.findViewById(R.id.rgRotationPosition);
        btnConfirm = editDialogView.findViewById(R.id.btnConfirm);
        btnCloseDialog = editDialogView.findViewById(R.id.btnCloseDialog);
        layoutTags = editDialogView.findViewById(R.id.layoutTags);
        txtDialogTitle = editDialogView.findViewById(R.id.txtDialogTitle);
        btnHelp = editDialogView.findViewById(R.id.btnFreqHelp);
    }
}

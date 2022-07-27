package org.williamsonministry.prayercards;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.williamsonministry.prayercards.PrayerCard.ALWAYS;
import static org.williamsonministry.prayercards.PrayerCard.DAILY;
import static org.williamsonministry.prayercards.PrayerCard.MONTHLY;
import static org.williamsonministry.prayercards.PrayerCard.UNUSED;
import static org.williamsonministry.prayercards.PrayerCard.WEEKLY;

public class CardEditOrAddDialog implements View.OnClickListener {
    private final int dialogType;
    private final Context mContext;
    private PrayerCard prayerCard;
    private final int position;
    public static final int EDIT = 1;
    public static final int ADD = 0;
    public static final int FRAGMENTEDIT = 2;

    private EditText etPrayerReq;
    private EditText etViewsLimit;
    private EditText etMultiMaxFreq;
    private EditText etTags;
    private Spinner spMaxFreq;
    private RadioGroup radioGroup;
    private Button btnOpenAdvanced;
    private Button btnCloseAdvanced;
    private Button btnConfirm;
    private ImageButton btnFreqHelp;
    private CheckBox cbLimitViews;
    private ConstraintLayout layoutRegSched;
    private ConstraintLayout layoutAdvanced;
    private TextView txtViews;
    private TextView txtDialogTitle;
    private TextView txtExpiryDate;
    private Button btnCloseDialog;
    private CheckBox cbExpiryDate;
    private DatePickerDialog datePickerDialog;
    private Date expiryDate;

    public CardEditOrAddDialog(int dialogType, Context mContext, @Nullable PrayerCard prayerCard, int position) {
        this.dialogType = dialogType;
        this.mContext = mContext;
        if (prayerCard != null) {
            this.prayerCard = prayerCard;
        }
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        View editDialogView = LayoutInflater.from(mContext).inflate(R.layout.edit_layout_dialog, null, false);

        initViews(editDialogView);

        if (dialogType == ADD) {
            btnConfirm.setText("Add");
            txtViews.setText("views");
            txtDialogTitle.setText("Add Prayer Card");
        }

        if (dialogType != ADD) {
            btnConfirm.setText("Save Changes");
            txtViews.setText("more views");
            txtDialogTitle.setText("Edit Card");
            etPrayerReq.setText(prayerCard.getPrayerText());
            etTags.setText(prayerCard.getTags());
            if (prayerCard.getMaxFrequency() == ALWAYS) {
                radioGroup.check(R.id.rbAlways);
            } else if (prayerCard.getMaxFrequency() == UNUSED) {
                radioGroup.check(R.id.rbRotation);
            } else {
                radioGroup.check(R.id.rbRegSched);
                layoutRegSched.setVisibility(View.VISIBLE);
                etMultiMaxFreq.setText(String.valueOf(prayerCard.getMultipleMaxFreq()));
                if (prayerCard.getMaxFrequency() == DAILY) {
                    spMaxFreq.setSelection(0);
                } else if (prayerCard.getMaxFrequency() == WEEKLY) {
                    spMaxFreq.setSelection(1);
                } else if (prayerCard.getMaxFrequency() == MONTHLY) {
                    spMaxFreq.setSelection(2);
                }
            }

            if (dialogType == EDIT) {
                if (prayerCard.getViewsRemaining() != -1) {
                    etViewsLimit.setText(String.valueOf(prayerCard.getViewsRemaining()));
                    cbLimitViews.setChecked(true);
                }
            }

            if (dialogType == FRAGMENTEDIT) {
                DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
                int id = prayerCard.getId();
                int viewsRemaining = dataBaseHelper.getCardById(id).getViewsRemaining();
                if (viewsRemaining != -1) {
                    etViewsLimit.setText(String.valueOf(viewsRemaining));
                    cbLimitViews.setChecked(true);
                }
            }

            //Set the expiry date of the prayer card to the dialog
            if (prayerCard.getExpiryDate().getTime() != UNUSED) {
                cbExpiryDate.setChecked(true);
                Calendar tempCal = Calendar.getInstance();
                tempCal.setTimeInMillis(prayerCard.getExpiryDate().getTime());
                int day = tempCal.get(Calendar.DAY_OF_MONTH);
                int month = tempCal.get(Calendar.MONTH);
                int year = tempCal.get(Calendar.YEAR);
                txtExpiryDate.setText(makeDateString(day, month, year));
                txtExpiryDate.setVisibility(View.VISIBLE);
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

        btnOpenAdvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutAdvanced.setVisibility(View.VISIBLE);
                btnOpenAdvanced.setVisibility(View.GONE);
            }
        });

        btnCloseAdvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutAdvanced.setVisibility(View.GONE);
                btnOpenAdvanced.setVisibility(View.VISIBLE);
                if (dialogType == ADD) {
                    radioGroup.check(R.id.rbAlways);
                    etMultiMaxFreq.setText("");
                    etTags.setText("");
                    etViewsLimit.setText("");
                    spMaxFreq.setSelection(0);
                    cbLimitViews.setChecked(false);
                    layoutAdvanced.setVisibility(View.GONE);
                    btnOpenAdvanced.setVisibility(View.VISIBLE);
                    cbExpiryDate.setChecked(false);
                    expiryDate = new Date(UNUSED);
                }
            }
        });

        btnFreqHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog aDHelp = new AlertDialog.Builder(mContext).create();
                aDHelp.setTitle("Help");
                aDHelp.setMessage("Choose 'Always' for the card to always appear.\n\n" +
                        "Choose 'On a regular schedule' for the card to appear after a certain amount of time passes (eg. every three days).\n\n" +
                        "Choose 'In a rotation' for the card to be added to the rotation list. You can set the number of rotation " +
                        "cards that appear in your Prayer Plans. Each time you pray, this number of cards will be added from the " +
                        "rotation list. When the end of the rotation list is reached, it will choose cards from the beginning again.");
                aDHelp.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        aDHelp.dismiss();
                    }
                });
                aDHelp.show();
            }
        });

        cbLimitViews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cbLimitViews.setTextColor(mContext.getResources().getColor(R.color.black));
                    txtViews.setTextColor(mContext.getResources().getColor(R.color.black));
                    etViewsLimit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etViewsLimit.setEnabled(true);
                } else {
                    cbLimitViews.setTextColor(mContext.getResources().getColor(R.color.colorLightGrey));
                    txtViews.setTextColor(mContext.getResources().getColor(R.color.colorLightGrey));
                    etViewsLimit.setEnabled(false);
                    etViewsLimit.setInputType(InputType.TYPE_NULL);
                    etViewsLimit.setText("");

                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbRegSched) {
                    layoutRegSched.setVisibility(View.VISIBLE);
                } else {
                    layoutRegSched.setVisibility(View.GONE);
                }
            }
        });

        cbExpiryDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cbExpiryDate.setTextColor(mContext.getResources().getColor(R.color.black));
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    String date = makeDateString(day, month, year);
                    expiryDate = cal.getTime();
                    txtExpiryDate.setText(date);
                    txtExpiryDate.setVisibility(View.VISIBLE);
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            String date = makeDateString(day, month, year);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, day, 23, 59, 59);
                            expiryDate = calendar.getTime();
                            txtExpiryDate.setText(date);
                            txtExpiryDate.setVisibility(View.VISIBLE);
                        }
                    };
                    datePickerDialog = new DatePickerDialog(mContext, android.app.AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);
                    datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                    datePickerDialog.show();
                } else {
                    cbExpiryDate.setTextColor(mContext.getResources().getColor(R.color.colorLightGrey));
                    txtExpiryDate.setVisibility(View.GONE);
                    expiryDate = new Date(UNUSED);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHelper db = new DataBaseHelper(mContext);

                int maxFrequency = UNUSED;  //default is unused, so -1

                if (radioGroup.getCheckedRadioButtonId() == R.id.rbAlways) {
                    maxFrequency = ALWAYS;
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.rbRotation) {
                    maxFrequency = UNUSED;
                } else {
                    switch (spMaxFreq.getSelectedItem().toString()) {
                        case "days.":
                            maxFrequency = DAILY;
                            break;
                        case "weeks.":
                            maxFrequency = WEEKLY;
                            break;
                        case "months.":
                            maxFrequency = MONTHLY;
                            break;
                        default:
                            break;
                    }
                }


                int multiMaxFreq;
                try {       //make sure its an integer. Could be null?
                    multiMaxFreq = Integer.parseInt(etMultiMaxFreq.getText().toString());
                } catch (Exception e) {
                    multiMaxFreq = -1;
                }

                int viewsRemaining = -1;
                if (cbLimitViews.isChecked()) {
                    try {       //make sure its an integer. Could be null?
                        viewsRemaining = Integer.parseInt(etViewsLimit.getText().toString());
                    } catch (Exception e) {
                        viewsRemaining = -1;
                    }
                }

                if (dialogType == EDIT) {
                    PrayerCard editedPrayerCard = new PrayerCard(prayerCard.getId(), prayerCard.getListOrder(), etPrayerReq.getText().toString(), etTags.getText().toString(), maxFrequency, multiMaxFreq, radioGroup.getCheckedRadioButtonId() == R.id.rbRotation, prayerCard.getLastSeen(), viewsRemaining, expiryDate, true, prayerCard.isAnswered());
                    ((EditCards) mContext).getAdapter().getCurrentPrayerCards().set(position, editedPrayerCard);
                    for (int i = 0; i < ((EditCards) mContext).getAdapter().getAllPrayerCards().size(); i++) {
                        if (((EditCards) mContext).getAdapter().getAllPrayerCards().get(i).getId() == prayerCard.getId()) {
                            int listOrder2 = ((EditCards) mContext).getAdapter().getAllPrayerCards().get(i).getListOrder();
                            ((EditCards) mContext).getAdapter().getAllPrayerCards().set(i, editedPrayerCard);
                            ((EditCards) mContext).getAdapter().getAllPrayerCards().get(i).setListOrder(listOrder2);
                        }
                    }
                    ((EditCards) mContext).getAdapter().notifyDataSetChanged();
                    ((EditCards) mContext).getAdapter().asyncSave();
                    alertDialog.dismiss();
                }

                if (dialogType == FRAGMENTEDIT) {
                    PrayerCard editedPrayerCard = new PrayerCard(prayerCard.getId(), prayerCard.getListOrder(), etPrayerReq.getText().toString(), etTags.getText().toString(), maxFrequency, multiMaxFreq, radioGroup.getCheckedRadioButtonId() == R.id.rbRotation, prayerCard.getLastSeen(), viewsRemaining, expiryDate, true, prayerCard.isAnswered());

                    DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
                    dataBaseHelper.editOneReturnPrayerCard(prayerCard.getId(), editedPrayerCard);

                    ((DeckSwipe) mContext).getAdapter().getThisDeck().set(position, editedPrayerCard);
                    ((DeckSwipe) mContext).getAdapter().notifyDataSetChanged();
                    alertDialog.dismiss();
                }

                if (dialogType == ADD) {
                    int size;

                    if (prayerCard != null) {
                        size = ((DeckSwipe) mContext).getAdapter().getThisDeck().size();
                    } else {
                        size = ((EditCards) mContext).getAdapter().getAllPrayerCards().size();
                    }

                    PrayerCard newPrayerCard = new PrayerCard(-1, size, etPrayerReq.getText().toString(), etTags.getText().toString(), maxFrequency, multiMaxFreq, radioGroup.getCheckedRadioButtonId() == R.id.rbRotation, new Date(0), viewsRemaining, expiryDate, true);
                    long newID = db.addOne(newPrayerCard);
                    newPrayerCard.setId((int) newID);
                    if (prayerCard != null) {
                        ((DeckSwipe) mContext).getAdapter().getThisDeck().set(size - 1, newPrayerCard);
                        PrayerCard addCard = new PrayerCard(-100, -1, "", "", ALWAYS, -1, false, new Date(0), 1, new Date(0), true);
                        ((DeckSwipe) mContext).getAdapter().getThisDeck().add(addCard);
                        ((DeckSwipe) mContext).getAdapter().notifyDataSetChanged();
                        alertDialog.dismiss();
                    } else {
                        ((EditCards) mContext).getAdapter().getAllPrayerCards().add(0, newPrayerCard);
                        for (int i = 0; i < ((EditCards) mContext).getAdapter().getAllPrayerCards().size(); i++) {
                            ((EditCards) mContext).getAdapter().getAllPrayerCards().get(i).setListOrder(i);
                        }
                        db.saveOrder(((EditCards) mContext).getAdapter().getAllPrayerCards());
                        ArrayList<PrayerCard> newCurrentCards = ((EditCards) mContext).getAdapter().getCurrentPrayerCards();
                        newCurrentCards.add(0, newPrayerCard);
                        ((EditCards) mContext).getAdapter().setCards(newCurrentCards);
                    }

                    if (newID > -1) {
                        Toast.makeText(mContext, "Prayer Card added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                    }

                    etPrayerReq.setText("");

                    if (prayerCard == null) {
                        ((EditCards) mContext).getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private String makeDateString(int day, int month, int year) {
        String monthString = "Jan";
        switch (month) {
            case 0:
                monthString = "Jan";
                break;
            case 1:
                monthString = "Feb";
                break;
            case 2:
                monthString = "Mar";
                break;
            case 3:
                monthString = "Apr";
                break;
            case 4:
                monthString = "May";
                break;
            case 5:
                monthString = "Jun";
                break;
            case 6:
                monthString = "Jul";
                break;
            case 7:
                monthString = "Aug";
                break;
            case 8:
                monthString = "Sep";
                break;
            case 9:
                monthString = "Oct";
                break;
            case 10:
                monthString = "Nov";
                break;
            case 11:
                monthString = "Dec";
                break;
            default:
                break;
        }
        return monthString + " " + day + ", " + year;
    }

    private void initViews(View editDialogView) {
        etPrayerReq = editDialogView.findViewById(R.id.etPrayerReq);
        etViewsLimit = editDialogView.findViewById(R.id.etViewLimit);
        etMultiMaxFreq = editDialogView.findViewById(R.id.etMultiMaxFreq);
        etTags = editDialogView.findViewById(R.id.etTags);
        spMaxFreq = editDialogView.findViewById(R.id.spMaxFreq);
        radioGroup = editDialogView.findViewById(R.id.radioGroup);
        btnOpenAdvanced = editDialogView.findViewById(R.id.btnOpenOptional);
        btnCloseAdvanced = editDialogView.findViewById(R.id.btnCloseOptional);
        btnConfirm = editDialogView.findViewById(R.id.btnConfirm);
        btnFreqHelp = editDialogView.findViewById(R.id.btnFreqHelp);
        cbLimitViews = editDialogView.findViewById(R.id.cbLimitViews);
        layoutRegSched = editDialogView.findViewById(R.id.layoutRegSched);
        layoutAdvanced = editDialogView.findViewById(R.id.layoutAdvanced);
        txtViews = editDialogView.findViewById(R.id.txtViews);
        txtDialogTitle = editDialogView.findViewById(R.id.txtDialogTitle);
        txtExpiryDate = editDialogView.findViewById(R.id.txtExpiryDate);
        btnCloseDialog = editDialogView.findViewById(R.id.btnCloseDialog);
        cbExpiryDate = editDialogView.findViewById(R.id.cbExpiryDate);
    }
}

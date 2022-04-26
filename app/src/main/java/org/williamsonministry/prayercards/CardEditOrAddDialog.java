package org.williamsonministry.prayercards;

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
    private Button btnCloseDialog;
    private CheckBox cbExpiryDate;
    private Spinner spMonth;
    private Spinner spDay;
    private Spinner spYear;

    public CardEditOrAddDialog(int dialogType, Context mContext, @Nullable PrayerCard prayerCard, int position) {
        this.dialogType = dialogType;
        this.mContext = mContext;
        if (prayerCard != null){this.prayerCard = prayerCard;}
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        View editDialogView = LayoutInflater.from(mContext).inflate(R.layout.edit_layout_dialog, null, false);

        initViews(editDialogView);

        if (dialogType == ADD)  {
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
                if (viewsRemaining != -1)   {
                    etViewsLimit.setText(String.valueOf(viewsRemaining));
                    cbLimitViews.setChecked(true);
                }
            }

            //Set the expiry date of the prayer card to the dialog
            if (prayerCard.getExpiryDate().getTime() != UNUSED) {
                cbExpiryDate.setChecked(true);

                ArrayAdapter<String> dayDataAdapter;
                String[] arrayDays31 = mContext.getResources().getStringArray(R.array.dates_31);
                String[] arrayDays30 = mContext.getResources().getStringArray(R.array.dates_30);
                String[] arrayDays29 = mContext.getResources().getStringArray(R.array.dates_29);
                String[] arrayDays28 = mContext.getResources().getStringArray(R.array.dates_28);

                Calendar tempCal = Calendar.getInstance();
                tempCal.setTimeInMillis(prayerCard.getExpiryDate().getTime());
                int day = tempCal.get(Calendar.DAY_OF_MONTH);
                int monthInt = tempCal.get(Calendar.MONTH);
                int year = tempCal.get(Calendar.YEAR);
                if (monthInt == 0 || monthInt == 2 || monthInt == 4 || monthInt == 6 || monthInt == 7 || monthInt == 9 || monthInt == 11) {
                    dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays31);
                    spDay.setAdapter(dayDataAdapter);
                }
                if (monthInt == 3 || monthInt == 5 || monthInt == 8 || monthInt == 10) {
                    dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays30);
                    spDay.setAdapter(dayDataAdapter);
                }
                if (monthInt == 1) {
                    if (year == 2024 || year == 2028) {
                        dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays29);
                        spDay.setAdapter(dayDataAdapter);
                    } else {
                        dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays28);
                        spDay.setAdapter(dayDataAdapter);
                    }
                }

                spDay.setSelection(day - 1);
                spMonth.setSelection(monthInt);
                spYear.setSelection(year - 2021);
                spDay.setVisibility(View.VISIBLE);
                spMonth.setVisibility(View.VISIBLE);
                spYear.setVisibility(View.VISIBLE);
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
                if (dialogType == ADD)  {
                    radioGroup.check(R.id.rbAlways);
                    etMultiMaxFreq.setText("");
                    etTags.setText("");
                    etViewsLimit.setText("");
                    spMaxFreq.setSelection(0);
                    cbLimitViews.setChecked(false);
                    layoutAdvanced.setVisibility(View.GONE);
                    btnOpenAdvanced.setVisibility(View.VISIBLE);
                    cbExpiryDate.setChecked(false);
                    spMonth.setSelection(0);
                    spDay.setSelection(0);
                    spYear.setSelection(0);
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
                if (b){
                    cbLimitViews.setTextColor(mContext.getResources().getColor(R.color.black));
                    txtViews.setTextColor(mContext.getResources().getColor(R.color.black));
                    etViewsLimit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etViewsLimit.setEnabled(true);
                } else  {
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
                if (i==R.id.rbRegSched){
                    layoutRegSched.setVisibility(View.VISIBLE);
                } else  {
                    layoutRegSched.setVisibility(View.GONE);
                }
            }
        });

        cbExpiryDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    cbExpiryDate.setTextColor(mContext.getResources().getColor(R.color.black));
                    spDay.setVisibility(View.VISIBLE);
                    spMonth.setVisibility(View.VISIBLE);
                    spYear.setVisibility(View.VISIBLE);

                } else  {
                    cbExpiryDate.setTextColor(mContext.getResources().getColor(R.color.colorLightGrey));
                    spDay.setVisibility(View.GONE);
                    spMonth.setVisibility(View.GONE);
                    spYear.setVisibility(View.GONE);
                }
            }
        });

        //Set the number of available days on the days spinner based on the month (and year for Feb)
        spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            ArrayAdapter<String> dayDataAdapter;
            final String[] arrayDays31 = mContext.getResources().getStringArray(R.array.dates_31);
            final String[] arrayDays30 = mContext.getResources().getStringArray(R.array.dates_30);
            final String[] arrayDays29 = mContext.getResources().getStringArray(R.array.dates_29);
            final String[] arrayDays28 = mContext.getResources().getStringArray(R.array.dates_28);

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String month = String.valueOf(adapterView.getItemAtPosition(i));
                int day = Integer.parseInt(spDay.getSelectedItem().toString());
                if (month.equals("Jan") || month.equals("Mar") || month.equals("May") || month.equals("Jul") || month.equals("Aug") || month.equals("Oct") || month.equals("Dec")) {
                    dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays31);
                    spDay.setAdapter(dayDataAdapter);
                    spDay.setSelection(day-1);
                }
                if (month.equals("Apr") || month.equals("Jun") || month.equals("Sep") || month.equals("Nov"))   {
                    dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays30);
                    spDay.setAdapter(dayDataAdapter);
                    if (day < 31) {spDay.setSelection(day-1);} else {spDay.setSelection(29);}
                }
                if (month.equals("Feb"))    {
                    if (String.valueOf(spYear.getSelectedItem()).equals("2024") || String.valueOf(spYear.getSelectedItem()).equals("2028"))   {
                        dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays29);
                        spDay.setAdapter(dayDataAdapter);
                        if (day < 30) {spDay.setSelection(day-1);} else {spDay.setSelection(28);}
                    }   else   {
                        dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays28);
                        spDay.setAdapter(dayDataAdapter);
                        if (day < 29) {spDay.setSelection(day-1);} else {spDay.setSelection(27);}
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        }); //This might help: https://mkyong.com/android/android-spinner-drop-down-list-example/#:~:text=Attach%20a%20listener%20on%20Spinner,%20fire%20when%20user,Eclipse%203.7,%20and%20tested%20with%20Android%202.3.3.%201.

        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            ArrayAdapter<String> dayDataAdapter;
            final String[] arrayDays29 = mContext.getResources().getStringArray(R.array.dates_29);
            final String[] arrayDays28 = mContext.getResources().getStringArray(R.array.dates_28);

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String month = String.valueOf(spMonth.getSelectedItem());
                String year = String.valueOf(adapterView.getItemAtPosition(i));
                int day = Integer.parseInt(spDay.getSelectedItem().toString());
                if (month.equals("Feb")) {
                    if (year.equals("2024") || year.equals("2028")) {
                        dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays29);
                        spDay.setAdapter(dayDataAdapter);
                        if (day < 30) {spDay.setSelection(day-1);} else {spDay.setSelection(28);}
                    } else  {
                        dayDataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, arrayDays28);
                        spDay.setAdapter(dayDataAdapter);
                        if (day < 29) {spDay.setSelection(day-1);} else {spDay.setSelection(27);}
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHelper db = new DataBaseHelper(mContext);

                int maxFrequency = UNUSED;  //default is unused, so -1

                if (radioGroup.getCheckedRadioButtonId() == R.id.rbAlways)   {
                    maxFrequency = ALWAYS;
                }   else if (radioGroup.getCheckedRadioButtonId() == R.id.rbRotation) {
                    maxFrequency = UNUSED;
                }   else    {
                    switch (spMaxFreq.getSelectedItem().toString()){
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
                } catch (Exception e)   {
                    multiMaxFreq = -1;
                }

                int viewsRemaining = -1;
                if (cbLimitViews.isChecked())   {
                    try {       //make sure its an integer. Could be null?
                        viewsRemaining = Integer.parseInt(etViewsLimit.getText().toString());
                    } catch (Exception e)   {
                        viewsRemaining = -1;
                    }
                }

                Date expiryDate = dateFromSpinners(spMonth.getSelectedItem().toString(), Integer.parseInt(spDay.getSelectedItem().toString()), Integer.parseInt(spYear.getSelectedItem().toString()), cbExpiryDate.isChecked());

                if (dialogType == EDIT){
                    PrayerCard editedPrayerCard = new PrayerCard(prayerCard.getId(), prayerCard.getListOrder(), etPrayerReq.getText().toString(), etTags.getText().toString(), maxFrequency, multiMaxFreq, radioGroup.getCheckedRadioButtonId() == R.id.rbRotation, prayerCard.getLastSeen() , viewsRemaining, expiryDate,true);
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

                if (dialogType == FRAGMENTEDIT){
                    PrayerCard editedPrayerCard = new PrayerCard(prayerCard.getId(), prayerCard.getListOrder(), etPrayerReq.getText().toString(), etTags.getText().toString(), maxFrequency, multiMaxFreq, radioGroup.getCheckedRadioButtonId() == R.id.rbRotation, prayerCard.getLastSeen() , viewsRemaining, expiryDate,true);

                    DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
                    dataBaseHelper.editOneReturnPrayerCard(prayerCard.getId(),editedPrayerCard);

                    ((DeckSwipe) mContext).getAdapter().getThisDeck().set(position, editedPrayerCard);
                    ((DeckSwipe) mContext).getAdapter().notifyDataSetChanged();
                    alertDialog.dismiss();
                }

                if (dialogType == ADD)  {
                    int size;

                    if (prayerCard != null) {
                        size = ((DeckSwipe) mContext).getAdapter().getThisDeck().size();
                    }   else    {
                        size = ((EditCards) mContext).getAdapter().getAllPrayerCards().size();
                    }

                    PrayerCard newPrayerCard = new PrayerCard(-1, size, etPrayerReq.getText().toString(), etTags.getText().toString(), maxFrequency, multiMaxFreq, radioGroup.getCheckedRadioButtonId() == R.id.rbRotation, new Date(0) , viewsRemaining, expiryDate,true);
                    long newID = db.addOne(newPrayerCard);
                    newPrayerCard.setId((int) newID);
                    if (prayerCard != null)  {
                        ((DeckSwipe) mContext).getAdapter().getThisDeck().set(size-1, newPrayerCard);
                        PrayerCard addCard = new PrayerCard(-100,-1,"", "",ALWAYS,-1,false,new Date(0),1,new Date(0),true);
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

                    if (newID > -1)    {
                        Toast.makeText(mContext, "Prayer Card added successfully", Toast.LENGTH_SHORT).show();
                    }   else    {
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

    private Date dateFromSpinners(String month, int day, int year, boolean hasExpiry) {

        int monthInt = -1;

        switch (month)  {
            case "Jan":
                monthInt = 0;
                break;
            case "Feb":
                monthInt = 1;
                break;
            case "Mar":
                monthInt = 2;
                break;
            case "Apr":
                monthInt = 3;
                break;
            case "May":
                monthInt = 4;
                break;
            case "Jun":
                monthInt = 5;
                break;
            case "Jul":
                monthInt = 6;
                break;
            case "Aug":
                monthInt = 7;
                break;
            case "Sep":
                monthInt = 8;
                break;
            case "Oct":
                monthInt = 9;
                break;
            case "Nov":
                monthInt = 10;
                break;
            case "Dec":
                monthInt = 11;
                break;
            default:
                break;
        }

        Calendar calendar = Calendar.getInstance();

        calendar.set(year, monthInt, day, 23, 59,59);

        Date date;

        if (hasExpiry)  {
            date = calendar.getTime();
        } else {
            date = new Date(UNUSED);
        }

        return date;

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
        btnCloseDialog = editDialogView.findViewById(R.id.btnCloseDialog);
        cbExpiryDate = editDialogView.findViewById(R.id.cbExpiryDate);
        spMonth = editDialogView.findViewById(R.id.spMonth);
        spDay = editDialogView.findViewById(R.id.spDay);
        spYear = editDialogView.findViewById(R.id.spYear);
    }


}

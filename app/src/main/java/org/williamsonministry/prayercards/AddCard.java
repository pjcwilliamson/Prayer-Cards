package org.williamsonministry.prayercards;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static org.williamsonministry.prayercards.PrayerCard.ALWAYS;
import static org.williamsonministry.prayercards.PrayerCard.DAILY;
import static org.williamsonministry.prayercards.PrayerCard.MONTHLY;
import static org.williamsonministry.prayercards.PrayerCard.UNUSED;
import static org.williamsonministry.prayercards.PrayerCard.WEEKLY;

public class AddCard extends AppCompatActivity {

    private EditText etPrayerRequest, etTags, etMultiMaxFreq, etViewLimit;
    private Spinner spFreq, spMonth, spDay, spYear;
    private ConstraintLayout layoutRegSched, layoutAdvanced, layoutDateSpinners;
    private Button btnAdd, btnOpenAdvanced, btnCloseAdvanced;
    private RadioButton rbAlways, rbSchedule, rbRotation;
    private RadioGroup radioGroup;
    private CheckBox cbLimitViews, cbExpiryDate;
    private TextView txtViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        initViews();

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

        cbLimitViews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    cbLimitViews.setTextColor(getResources().getColor(R.color.black));
                    txtViews.setTextColor(getResources().getColor(R.color.black));
                    etViewLimit.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else  {
                    cbLimitViews.setTextColor(getResources().getColor(R.color.colorLightGrey));
                    txtViews.setTextColor(getResources().getColor(R.color.colorLightGrey));
                    etViewLimit.setInputType(InputType.TYPE_NULL);
                    etViewLimit.setText("");
                }
            }
        });

        cbExpiryDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    cbExpiryDate.setTextColor(getResources().getColor(R.color.black));
                    layoutDateSpinners.setVisibility(View.VISIBLE);
                } else  {
                    cbExpiryDate.setTextColor(getResources().getColor(R.color.colorLightGrey));
                    layoutDateSpinners.setVisibility(View.GONE);
                }
            }
        });

        spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            ArrayAdapter<String> dayDataAdapter;
            final String[] arrayDays31 = getResources().getStringArray(R.array.dates_31);
            final String[] arrayDays30 = getResources().getStringArray(R.array.dates_30);
            final String[] arrayDays29 = getResources().getStringArray(R.array.dates_29);
            final String[] arrayDays28 = getResources().getStringArray(R.array.dates_28);

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String month = String.valueOf(adapterView.getItemAtPosition(i));
                if (month.equals("Jan") || month.equals("Mar") || month.equals("May") || month.equals("Jul") || month.equals("Aug") || month.equals("Oct") || month.equals("Dec")) {
                    dayDataAdapter = new ArrayAdapter<String>(AddCard.this, android.R.layout.simple_spinner_dropdown_item, arrayDays31);
                    spDay.setAdapter(dayDataAdapter);
                }
                if (month.equals("Apr") || month.equals("Jun") || month.equals("Sep") || month.equals("Nov"))   {
                    dayDataAdapter = new ArrayAdapter<String>(AddCard.this, android.R.layout.simple_spinner_dropdown_item, arrayDays30);
                    spDay.setAdapter(dayDataAdapter);
                }
                if (month.equals("Feb"))    {
                    if (String.valueOf(spYear.getSelectedItem()).equals("2024") || String.valueOf(spYear.getSelectedItem()).equals("2028"))   {
                        dayDataAdapter = new ArrayAdapter<String>(AddCard.this, android.R.layout.simple_spinner_dropdown_item, arrayDays29);
                        spDay.setAdapter(dayDataAdapter);
                    }   else   {
                        dayDataAdapter = new ArrayAdapter<String>(AddCard.this, android.R.layout.simple_spinner_dropdown_item, arrayDays28);
                        spDay.setAdapter(dayDataAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        }); //This might help: https://mkyong.com/android/android-spinner-drop-down-list-example/#:~:text=Attach%20a%20listener%20on%20Spinner,%20fire%20when%20user,Eclipse%203.7,%20and%20tested%20with%20Android%202.3.3.%201.

        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            ArrayAdapter<String> dayDataAdapter;
            final String[] arrayDays29 = getResources().getStringArray(R.array.dates_29);
            final String[] arrayDays28 = getResources().getStringArray(R.array.dates_28);

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String month = String.valueOf(spMonth.getSelectedItem());
                String year = String.valueOf(adapterView.getItemAtPosition(i));
                if (month.equals("Feb")) {
                    if (year.equals("2024") || year.equals("2028")) {
                        dayDataAdapter = new ArrayAdapter<String>(AddCard.this, android.R.layout.simple_spinner_dropdown_item, arrayDays29);
                        spDay.setAdapter(dayDataAdapter);
                    } else  {
                        dayDataAdapter = new ArrayAdapter<String>(AddCard.this, android.R.layout.simple_spinner_dropdown_item, arrayDays28);
                        spDay.setAdapter(dayDataAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCard();
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
                radioGroup.check(R.id.rbAlways);
                etMultiMaxFreq.setText("");
                etTags.setText("");
                etViewLimit.setText("");
                spFreq.setSelection(0);
                cbLimitViews.setChecked(false);
                layoutAdvanced.setVisibility(View.GONE);
                btnOpenAdvanced.setVisibility(View.VISIBLE);
                cbExpiryDate.setChecked(false);
                spMonth.setSelection(0);
                spDay.setSelection(0);
                spYear.setSelection(0);
            }
        });

    }

    private void addNewCard() {
        int maxFrequency = UNUSED;  //default is unused, so -1


        if (rbAlways.isChecked())   {
            maxFrequency = ALWAYS;
        }   else if (rbRotation.isChecked()) {
            maxFrequency = UNUSED;
        }   else    {
                switch (spFreq.getSelectedItem().toString()){
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
                viewsRemaining = Integer.parseInt(etViewLimit.getText().toString());
            } catch (Exception e)   {
                viewsRemaining = -1;
            }
        }

        Date expiryDate = dateFromSpinners(spMonth.getSelectedItem().toString(), Integer.parseInt(spDay.getSelectedItem().toString()), Integer.parseInt(spYear.getSelectedItem().toString()), cbExpiryDate.isChecked());

        PrayerCard newPrayerCard = new PrayerCard(-1, -1, etPrayerRequest.getText().toString(), etTags.getText().toString(), maxFrequency, multiMaxFreq, rbRotation.isChecked(), new Date(Long.parseLong("0")) , viewsRemaining, expiryDate, true);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(AddCard.this);
        long success = dataBaseHelper.addOne(newPrayerCard);

        //boolean success = Utils.getInstance().addPrayerCard(newPrayerCard);
        if (success > -1)    {
            Toast.makeText(this, "Prayer Card added successfully", Toast.LENGTH_SHORT).show();
        }   else    {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        etPrayerRequest.setText("");
    }

    public static Date dateFromSpinners(String month, int day, int year, boolean hasExpiry) {

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

    private void initViews() {
        etPrayerRequest = findViewById(R.id.etPrayerReq);
        etTags = findViewById(R.id.etTags);
        etMultiMaxFreq = findViewById(R.id.etMultiMaxFreq);
        etViewLimit = findViewById(R.id.etViewLimit);
        spFreq = findViewById(R.id.spMaxFreq);
        spMonth = findViewById(R.id.spMonth);
        spDay = findViewById(R.id.spDay);
        spYear = findViewById(R.id.spYear);
        layoutRegSched = findViewById(R.id.layoutRegSched);
        layoutDateSpinners = findViewById(R.id.layoutDateSpinners);
        btnAdd = findViewById(R.id.btnAdd);
        rbAlways = findViewById(R.id.rbAlways);
        rbSchedule = findViewById(R.id.rbRegSched);
        rbRotation = findViewById(R.id.rbRotation);
        radioGroup = findViewById(R.id.radioGroup);
        cbLimitViews = findViewById(R.id.cbLimitViews);
        cbExpiryDate = findViewById(R.id.cbExpiryDate);
        txtViews = findViewById(R.id.txtViews);
        btnOpenAdvanced = findViewById(R.id.btnOpenOptional);
        layoutAdvanced = findViewById(R.id.layoutAdvanced);
        btnCloseAdvanced = findViewById(R.id.btnCloseOptional);
    }
}
package org.williamsonministry.prayercards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.Date;

import static org.williamsonministry.prayercards.PrayerCard.ALWAYS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayCardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayCardsFragment extends Fragment {

    // TOD0: Rename parameter arguments, choose names that match <-- pregiven comment
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER <-- pregiven comment
    private static final String ARG_PRAYER = "prayer";
    private static final String ARG_TAGS = "tags";
    private static final String ARG_ID = "id";
    private static final String ARG_POSITION = "position";
    private static final String ARG_ANSWERED = "answered";

    // TOD0: Rename and change types of parameters <-- pregiven comment
    private String mPrayerRequest, mTags;
    private int mId, position;
    private boolean isAnswered;

    private TextView prayerTextView, tagsTextView, txtSwipeInstr;

    private ImageButton btnEditThisCard, btnDeleteThisCard, btnAddNewHere, btnThisPrayerAnswered, btnThisPrayerNotAnswered;
    private ConstraintLayout layoutAddNew, card;

    public TextView getTxtSwipeInstr() {
        return txtSwipeInstr;
    }

    public DisplayCardsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mPrayerRequest Parameter 1.
     * @param mTags Parameter 2.
     * @param mId Parameter 3
     * @param position Parameter 4
     * @param isAnswered Parameter 5
     * @return A new instance of fragment DisplayCardsFragment.
     */
    // TOD0: Rename and change types and number of parameters
    public static DisplayCardsFragment newInstance(String mPrayerRequest, String mTags, int mId, int position, boolean isAnswered) {
        DisplayCardsFragment fragment = new DisplayCardsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRAYER, mPrayerRequest);
        args.putString(ARG_TAGS, mTags);
        args.putInt(ARG_ID, mId);
        args.putInt(ARG_POSITION, position);
        args.putBoolean(ARG_ANSWERED, isAnswered);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPrayerRequest = getArguments().getString(ARG_PRAYER);
            mTags = getArguments().getString(ARG_TAGS);
            mId = getArguments().getInt(ARG_ID);
            position = getArguments().getInt(ARG_POSITION);
            isAnswered = getArguments().getBoolean(ARG_ANSWERED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_display_cards, container, false);

        prayerTextView = v.findViewById(R.id.prayerText);
        prayerTextView.setText(mPrayerRequest);
        tagsTextView = v.findViewById(R.id.tagsText);
        tagsTextView.setText(mTags);
        btnEditThisCard = v.findViewById(R.id.btnEditThisCard);
        btnDeleteThisCard = v.findViewById(R.id.btnDeleteThisCard);
        txtSwipeInstr = v.findViewById(R.id.txtSwipeInstr);
        layoutAddNew = v.findViewById(R.id.layoutAddCard);
        btnAddNewHere = v.findViewById(R.id.btnAddHere);
        btnThisPrayerAnswered = v.findViewById(R.id.btnThisPrayerAnswered);
        btnThisPrayerNotAnswered = v.findViewById(R.id.btnThisPrayerNotAnswered);
        card = v.findViewById(R.id.card);

        final Context mContext = getContext();
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());

        if (mId == -100) {
            btnEditThisCard.setVisibility(View.GONE);
            btnDeleteThisCard.setVisibility(View.GONE);
            layoutAddNew.setVisibility(View.VISIBLE);
        }

        if (isAnswered) {
            card.setBackground(mContext.getResources().getDrawable(R.drawable.border_answered));
            btnThisPrayerAnswered.setVisibility(View.GONE);
            btnThisPrayerNotAnswered.setVisibility(View.VISIBLE);
        }

        /*
        This if clause checks if the user has ever swiped. If they have never swiped then a timer is set to show the swiping instructions.
         */
        if (position == 0  && mId != -100)   {
                assert mContext != null;
                SharedPreferences sp = mContext.getSharedPreferences("SWIPED",Context.MODE_PRIVATE);
                boolean hasBeenSwiped = sp.getBoolean("SWIPED", false);
                if (!hasBeenSwiped) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!sp.getBoolean("SWIPED", false)) {
                                txtSwipeInstr.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 5000);
                }
        }

        /*
        To add a card while you're swiping
         */
        btnAddNewHere.setOnClickListener(new CardEditOrAddDialog(CardEditOrAddDialog.ADD, mContext, new PrayerCard(-100,-1,"", "",ALWAYS,-1,false,new Date(0),1,new Date(0),true), position));

        /*
        EditOrAddDialog with FRAGMENTEDIT tag will run an edit dialog which will edit the current card both on the ViewPager and the database
         */
        btnEditThisCard.setOnClickListener(new CardEditOrAddDialog(CardEditOrAddDialog.FRAGMENTEDIT, mContext, dataBaseHelper.getCardById(mId), position));

        /*
        Code to delete/inactivate the card both in the current ViewPager and in the database
         */
        btnDeleteThisCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert mContext != null;
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Do you want to inactivate this Prayer Card or delete it forever?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete Forever", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dataBaseHelper.deleteByID(mId);
                        ((DeckSwipe) mContext).getAdapter().getThisDeck().remove(position);
                        ((DeckSwipe) mContext).getAdapter().notifyDataSetChanged();
                        Toast.makeText(mContext, "Prayer Card Deleted", Toast.LENGTH_SHORT).show();
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
                        dataBaseHelper.makeOneInactive(mId);
                        ((DeckSwipe) mContext).getAdapter().getThisDeck().remove(position);
                        ((DeckSwipe) mContext).getAdapter().notifyDataSetChanged();
                        Toast.makeText(mContext, "Prayer Card Inactivated", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();
            }
        });

        return v;
    }
}
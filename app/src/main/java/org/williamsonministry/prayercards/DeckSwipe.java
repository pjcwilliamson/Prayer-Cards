package org.williamsonministry.prayercards;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class DeckSwipe extends AppCompatActivity {

    public static final String DECK_PARAMS = "deckParams";
    private ViewPager viewPager;
    private FragmentCollectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("topPOSITION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("topPOSITION", 0);
        editor.apply();

        Intent intent = getIntent();
        String deckParams = null;
        if (null != intent) {
            deckParams = intent.getStringExtra(DECK_PARAMS);
        }

        setContentView(R.layout.activity_deck_swipe);
        viewPager = findViewById(R.id.deckSwipeViewPager);
        adapter = new FragmentCollectionAdapter(getSupportFragmentManager(), DeckSwipe.this, deckParams);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageSelected(int position) {
                SharedPreferences sp = getSharedPreferences("topPOSITION", Context.MODE_PRIVATE);
                int topPosition = sp.getInt("topPOSITION", 0);
                if (position > topPosition) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("topPOSITION", position);
                    editor.apply();
                    if (position != 0 && position != getAdapter().getThisDeck().size()-1)  {
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(DeckSwipe.this);
                        dataBaseHelper.cardViewed(adapter.getThisDeck().get(position).getId());
                    }
                }
                SharedPreferences sp2 = getSharedPreferences("SWIPED", MODE_PRIVATE);
                boolean hasBeenSwiped = sp2.getBoolean("SWIPED", false);
                if (!hasBeenSwiped && position == 1) {
                    SharedPreferences.Editor editor2 = sp2.edit();
                    editor2.putBoolean("SWIPED", true);
                    editor2.apply();
                }

                /*
                Makes it so that the swipe instruction on the first fragment disappears when you get to the second fragment... just in case you swipe back!
                 */
                if (position == 1)  {
                    DisplayCardsFragment a = (DisplayCardsFragment) adapter.instantiateItem(viewPager, 0);
                    TextView b = a.getTxtSwipeInstr();
                    if (null != b) {b.setVisibility(View.GONE);}
                }
            }
        });
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public FragmentCollectionAdapter getAdapter() {
        return adapter;
    }
}
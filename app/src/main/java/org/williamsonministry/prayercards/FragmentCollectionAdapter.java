package org.williamsonministry.prayercards;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.williamsonministry.prayercards.PrayerCard.ALWAYS;
import static org.williamsonministry.prayercards.PrayerCard.DAILY;
import static org.williamsonministry.prayercards.PrayerCard.MONTHLY;
import static org.williamsonministry.prayercards.PrayerCard.UNUSED;
import static org.williamsonministry.prayercards.PrayerCard.WEEKLY;

public class FragmentCollectionAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;
    private final String deckParams;
    public static final long DAY_IN_MS = Long.parseLong("86400000");
    public static final long WEEK_IN_MS = Long.parseLong("604800000");
    public static final long MONTH_IN_MS = Long.parseLong("2678400000");
    private ArrayList<PrayerCard> thisDeck = new ArrayList<PrayerCard>();
    private final PrayerDeck activePrayerDeck;

    public FragmentCollectionAdapter(FragmentManager fm, Context mContext, String deckParams) {
        super(fm);
        this.mContext = mContext;
        this.deckParams = deckParams;
        activePrayerDeck = initDeckParams(deckParams);
        thisDeck.clear();
        thisDeck = selectDeck();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public DisplayCardsFragment getItem(int i) {

        ArrayList<String> tagsArrayList = Utils.commaStringToArraylist(thisDeck.get(i).getTags());
        String stringTags = String.join("\n", tagsArrayList);
        DisplayCardsFragment fragment = DisplayCardsFragment.newInstance(thisDeck.get(i).getPrayerText(), stringTags, thisDeck.get(i).getId(), i);
        return fragment;
    }

    private PrayerDeck initDeckParams(String deckParams) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        PrayerDeck returnPrayerDeck = dataBaseHelper.getDeckByName(deckParams);
        return returnPrayerDeck;

    }

    private ArrayList<PrayerCard> selectDeck() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        ArrayList<PrayerCard> fullPrayers = dataBaseHelper.getAll();

        /*
        Inactivate any cards past their expiry, and save that to the db
        */
        for (int i = 0; i < fullPrayers.size(); i++)    {
            if (fullPrayers.get(i).getExpiryDate().getTime() < Calendar.getInstance().getTimeInMillis() && fullPrayers.get(i).getExpiryDate().getTime() != UNUSED)    {
                fullPrayers.get(i).setActive(false);
                dataBaseHelper.editOneReturnPrayerCard(fullPrayers.get(i).getId(),fullPrayers.get(i));
            }
        }

        ArrayList<PrayerCard> rotation = dataBaseHelper.getRotation();
        ArrayList<PrayerCard> tagFilteredPrayers = filterByTags(fullPrayers);
        ArrayList<PrayerCard> tagFilteredRotation = filterByTags(rotation);

        ArrayList<PrayerCard> prunedRotation = new ArrayList<PrayerCard>();

        if (activePrayerDeck.getMaxCardsInRotation() == PrayerDeck.ALL_ROTATION_CARDS) {
            prunedRotation = tagFilteredRotation;
        } else {
            if (tagFilteredRotation.size() >= activePrayerDeck.getMaxCardsInRotation()) {
                for (int i = 0; i < activePrayerDeck.getMaxCardsInRotation(); i++) {
                    prunedRotation.add(tagFilteredRotation.get(i));
                }
            } else {
                prunedRotation = tagFilteredRotation;
            }
        }

        ArrayList<PrayerCard> currentDeck = new ArrayList<PrayerCard>();

        //long timeLastSeen = Calendar.getInstance().getTime() - fullPrayers.get(i).getLastSeen().getTime();
        long timeNow = Calendar.getInstance().getTimeInMillis();
        Calendar todaysCalendar = Calendar.getInstance();
        int todaysDayOfMonth = todaysCalendar.get(Calendar.DATE);
        int todaysDayOfYear = todaysCalendar.get(Calendar.DAY_OF_YEAR);
        int todaysMonth = todaysCalendar.get(Calendar.MONTH);
        int todaysYear = todaysCalendar.get(Calendar.YEAR);

        //eg. Calendar.getInstance().get(Calendar.MONTH);  <--- or something similar

        for (int i = 0; i < tagFilteredPrayers.size(); i++)   {
            long timeSinceLastSeen = timeNow - tagFilteredPrayers.get(i).getLastSeen().getTime();


            Calendar calLastSeen = Calendar.getInstance();
            calLastSeen.setTimeInMillis(tagFilteredPrayers.get(i).getLastSeen().getTime());
            int yearLastSeen = calLastSeen.get(Calendar.YEAR);
            int dayOfMonthLastSeenMax28 = calLastSeen.get(Calendar.DATE);
            if (dayOfMonthLastSeenMax28 > 28)   {
                dayOfMonthLastSeenMax28 = 28;
            }
            int monthLastSeen = calLastSeen.get(Calendar.MONTH);
            int dayOfYearLastSeen = calLastSeen.get(Calendar.DAY_OF_YEAR);
            int daysSinceLastSeen;
            int monthsSinceLastSeen;
            if (todaysYear == yearLastSeen) {
                daysSinceLastSeen = todaysDayOfYear - dayOfYearLastSeen;
                monthsSinceLastSeen = todaysMonth - monthLastSeen;
            }   else    {
                int yearsDiff = todaysYear - yearLastSeen;
                int extraDays = 365*(yearsDiff - 1);
                int extraMonths = 12*(yearsDiff - 1);
                daysSinceLastSeen = extraDays + todaysDayOfYear + (365-dayOfYearLastSeen);
                monthsSinceLastSeen = extraMonths + todaysMonth + (12-monthLastSeen);
            }

            if (!tagFilteredPrayers.get(i).isActive())  {
                //Do not add if not active
            }   else if (tagFilteredPrayers.get(i).getMaxFrequency() == ALWAYS){
                currentDeck.add(tagFilteredPrayers.get(i));
                //dataBaseHelper.cardViewed(fullPrayers.get(i));
            }
//            else if (rot1 != null)  {
//                if (fullPrayers.get(i).getId() == rot1.getId()) {
//                    //dataBaseHelper.cardViewed(fullPrayers.get(i));
//                }
//            }   else if (rot2 != null)  {
//                if (fullPrayers.get(i).getId() == rot2.getId()) {
//                    //dataBaseHelper.cardViewed(fullPrayers.get(i));
//                }
//            }
            else if (tagFilteredPrayers.get(i).getMaxFrequency() == DAILY) {
                if (daysSinceLastSeen >= tagFilteredPrayers.get(i).getMultipleMaxFreq()) {
                    currentDeck.add(tagFilteredPrayers.get(i));
                    //dataBaseHelper.cardViewed(fullPrayers.get(i));
                }
            }   else if (tagFilteredPrayers.get(i).getMaxFrequency() == WEEKLY) {
                if (daysSinceLastSeen >= tagFilteredPrayers.get(i).getMultipleMaxFreq()*7) {
                    currentDeck.add(tagFilteredPrayers.get(i));
                    //dataBaseHelper.cardViewed(fullPrayers.get(i));
                }
            }   else if (tagFilteredPrayers.get(i).getMaxFrequency() == MONTHLY) {
                if (monthsSinceLastSeen > tagFilteredPrayers.get(i).getMultipleMaxFreq()) {
                    currentDeck.add(tagFilteredPrayers.get(i));
                    //dataBaseHelper.cardViewed(fullPrayers.get(i));
                }   else if (monthsSinceLastSeen == tagFilteredPrayers.get(i).getMultipleMaxFreq() && todaysDayOfMonth >= dayOfMonthLastSeenMax28)    {
                    currentDeck.add(tagFilteredPrayers.get(i));
                    //dataBaseHelper.cardViewed(fullPrayers.get(i));
                }
            }
        }

        switch (activePrayerDeck.getRotationPosition()) {
            case PrayerDeck.END:
                currentDeck.addAll(prunedRotation);
                break;
            case PrayerDeck.START:
                Collections.reverse(currentDeck);
                currentDeck.addAll(prunedRotation);
                Collections.reverse(currentDeck);
                break;
            case PrayerDeck.MIXED:
                currentDeck.addAll(prunedRotation);
                Collections.sort(currentDeck);
                break;
            default:
                break;
        }

        if (currentDeck.size() != 0)    {
            dataBaseHelper.cardViewed(currentDeck.get(0).getId());
        }

        PrayerCard addCard = new PrayerCard(-100,-1,"", "",ALWAYS,-1,false,new Date(0),1,new Date(0),true);
        currentDeck.add(addCard);

        return currentDeck;
    }

    /*
    Filter an inputted arraylist according to tag rules
    */
    private ArrayList<PrayerCard> filterByTags(ArrayList<PrayerCard> fullPrayers) {
        ArrayList<PrayerCard> tagFilteredPrayers = new ArrayList<PrayerCard>();
        if (!activePrayerDeck.getTags().equals("")){
            ArrayList<String> arrayListDeckTags = Utils.commaStringToArraylist(activePrayerDeck.getTags().toLowerCase());
            if (activePrayerDeck.isMustHaveAllTags())   {
                for (int i=0; i < fullPrayers.size(); i++)  {
                    ArrayList<String> arrayListCardTags = Utils.commaStringToArraylist(fullPrayers.get(i).getTags().toLowerCase());
                    if (arrayListCardTags.containsAll(arrayListDeckTags))   {
                        tagFilteredPrayers.add(fullPrayers.get(i));
                    }
                }
            } else  {
                for (int i=0; i < fullPrayers.size(); i++)  {
                    ArrayList<String> arrayListCardTags = Utils.commaStringToArraylist(fullPrayers.get(i).getTags().toLowerCase());
                    ArrayList<Integer> listofIDs = new ArrayList<Integer>();
                    for (String s: arrayListDeckTags)   {
                        if (arrayListCardTags.contains(s) && !listofIDs.contains(fullPrayers.get(i).getId()))  {
                            listofIDs.add(fullPrayers.get(i).getId());
                            tagFilteredPrayers.add(fullPrayers.get(i));
                        }
                    }
                }
            }
        }   else    {tagFilteredPrayers = fullPrayers;}
        return tagFilteredPrayers;
    }

    @Override
    public int getCount() {
//        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
//        ArrayList<PrayerCard> fullPrayers = dataBaseHelper.getAll();
        return thisDeck.size();
    }

    public ArrayList<PrayerCard> getThisDeck() {
        return thisDeck;
    }

    public void setThisDeck(ArrayList<PrayerCard> thisDeck) {
        this.thisDeck = thisDeck;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}

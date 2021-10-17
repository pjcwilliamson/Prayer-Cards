package org.williamsonministry.prayercards;

import java.util.ArrayList;
import java.util.Arrays;

public class Utils {

    public static ArrayList<String> commaStringToArraylist(String tags)   {
        String[] strParts = tags.split("\\s*,\\s*");

        return new ArrayList<String>( Arrays.asList(strParts) );
    }

//    OLD UTILS CLASS BEFORE DB
//    private static Utils instance;
//
//    private static ArrayList<PrayerCard> prayerCards;
//
//    private Utils() {
//        if (null == prayerCards) {
//            prayerCards = new ArrayList<PrayerCard>();
//        }
//    }
//
//    public static Utils getInstance() {
//        if (null != instance)   {
//            return instance;
//        } else {
//            instance = new Utils();
//            return instance;
//        }
//    }
//
//    public static ArrayList<PrayerCard> getPrayerCards() {
//        return prayerCards;
//    }
//
//    public static boolean addPrayerCard(PrayerCard prayerCard)   {
//        boolean success = prayerCards.add(prayerCard);
//        return success;
//    }
}

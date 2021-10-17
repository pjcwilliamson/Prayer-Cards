package org.williamsonministry.prayercards;

import java.util.ArrayList;
import java.util.Date;

public class PrayerCard implements Comparable<PrayerCard> {
    private int id;
    private int listOrder;
    private String prayerText;
    private String tags;
    private int maxFrequency;  // 0 is every time (no max), 1 is daily, 2 is weekly, 3 is monthly, 4 is annually, -1 is unused
    private int multipleMaxFreq;   // how many of frequency, eg. if maxFrequency == 1, and multipleMaxFreq == 3, then prayer would display at most every 3 days. -1 for unused
    private boolean isInRotation;
    private Date lastSeen;    //Date, time when card last seen
    private int viewsRemaining; //number of times yet to view card. -1 for infinite.
    private Date expiryDate;
    private boolean isActive;

    public static final int ALWAYS = 0;
    public static final int UNUSED = -1;
    public static final int DAILY = 1;
    public static final int WEEKLY = 2;
    public static final int MONTHLY = 3;

    public PrayerCard(int id, int listOrder, String prayerText, String tags, int maxFrequency, int multipleMaxFreq, boolean isInRotation, Date date, int viewsRemaining, Date expiryDate, boolean isActive) {
        this.id = id;
        this.listOrder = listOrder;
        this.prayerText = prayerText;
        this.tags = tags;
        this.maxFrequency = maxFrequency;
        this.multipleMaxFreq = multipleMaxFreq;
        this.isInRotation = isInRotation;
        this.lastSeen = date;
        this.viewsRemaining = viewsRemaining;
        this.expiryDate = expiryDate;
        this.isActive = isActive;
    }

    @Override
    public int compareTo(PrayerCard prayerCard) {
        return (Integer.compare(this.getListOrder(), prayerCard.getListOrder()));
    }

    @Override
    public String toString() {
        return "PrayerCard{" +
                "id=" + id +
                ", listOrder=" + listOrder +
                ", prayerText='" + prayerText + '\'' +
                ", tags='" + tags + '\'' +
                ", maxFrequency=" + maxFrequency +
                ", multipleMaxFreq=" + multipleMaxFreq +
                ", isInRotation=" + isInRotation +
                ", lastSeen=" + lastSeen +
                ", viewsRemaining=" + viewsRemaining +
                ", expiryDate=" + expiryDate +
                ", isActive=" + isActive +
                '}';
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getViewsRemaining() {
        return viewsRemaining;
    }

    public void setViewsRemaining(int viewsRemaining) {
        this.viewsRemaining = viewsRemaining;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListOrder() {
        return listOrder;
    }

    public void setListOrder(int listOrder) {
        this.listOrder = listOrder;
    }

    public String getPrayerText() {
        return prayerText;
    }

    public void setPrayerText(String prayerText) {
        this.prayerText = prayerText;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getMaxFrequency() {
        return maxFrequency;
    }

    public void setMaxFrequency(int maxFrequency) {
        this.maxFrequency = maxFrequency;
    }

    public int getMultipleMaxFreq() {
        return multipleMaxFreq;
    }

    public void setMultipleMaxFreq(int multipleMaxFreq) {
        this.multipleMaxFreq = multipleMaxFreq;
    }

    public boolean isInRotation() {
        return isInRotation;
    }

    public void setInRotation(boolean inRotation) {
        isInRotation = inRotation;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }
}

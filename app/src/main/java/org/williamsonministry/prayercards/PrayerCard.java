package org.williamsonministry.prayercards;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class PrayerCard implements Comparable<PrayerCard>, Parcelable {
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
    private boolean isAnswered;

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
        isAnswered = false;
    }

    public PrayerCard(int id, int listOrder, String prayerText, String tags, int maxFrequency, int multipleMaxFreq, boolean isInRotation, Date lastSeen, int viewsRemaining, Date expiryDate, boolean isActive, boolean isAnswered) {
        this.id = id;
        this.listOrder = listOrder;
        this.prayerText = prayerText;
        this.tags = tags;
        this.maxFrequency = maxFrequency;
        this.multipleMaxFreq = multipleMaxFreq;
        this.isInRotation = isInRotation;
        this.lastSeen = lastSeen;
        this.viewsRemaining = viewsRemaining;
        this.expiryDate = expiryDate;
        this.isActive = isActive;
        this.isAnswered = isAnswered;
    }

    protected PrayerCard(Parcel in) {
        id = in.readInt();
        listOrder = in.readInt();
        prayerText = in.readString();
        tags = in.readString();
        maxFrequency = in.readInt();
        multipleMaxFreq = in.readInt();
        isInRotation = in.readByte() != 0;
        lastSeen = new Date(in.readLong());
        viewsRemaining = in.readInt();
        expiryDate = new Date (in.readLong());
        isActive = in.readByte() != 0;
        isAnswered = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(listOrder);
        dest.writeString(prayerText);
        dest.writeString(tags);
        dest.writeInt(maxFrequency);
        dest.writeInt(multipleMaxFreq);
        dest.writeByte((byte) (isInRotation ? 1 : 0));
        dest.writeLong(lastSeen.getTime());
        dest.writeInt(viewsRemaining);
        dest.writeLong(expiryDate.getTime());
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeByte((byte) (isAnswered ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PrayerCard> CREATOR = new Creator<PrayerCard>() {
        @Override
        public PrayerCard createFromParcel(Parcel in) {
            return new PrayerCard(in);
        }

        @Override
        public PrayerCard[] newArray(int size) {
            return new PrayerCard[size];
        }
    };

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
                ", isAnswered=" + isAnswered +
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

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }
}

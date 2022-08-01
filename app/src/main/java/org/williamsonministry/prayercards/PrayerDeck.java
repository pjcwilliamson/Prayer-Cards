package org.williamsonministry.prayercards;

import android.os.Parcel;
import android.os.Parcelable;

public class PrayerDeck implements Parcelable {
    private int id;
    private int listOrder;
    private String prayerPlanName;
    private String tags;
    private boolean mustHaveAllTags;
    private int maxCardsInRotation;
    private int rotationPosition;
    private boolean isActive;
    private boolean includeAnswered;
    private boolean includeUnanswered;

    public static final int START = 0;
    public static final int END = 1;
    public static final int MIXED = -1;
    public static final int ALL_ROTATION_CARDS = -1;

    public PrayerDeck(int id, int listOrder, String prayerPlanName, String tags, boolean mustHaveAllTags, int maxCardsInRotation, int rotationPosition, boolean isActive) {
        this.id = id;
        this.listOrder = listOrder;
        this.prayerPlanName = prayerPlanName;
        this.tags = tags;
        this.mustHaveAllTags = mustHaveAllTags;
        this.maxCardsInRotation = maxCardsInRotation;
        this.rotationPosition = rotationPosition;
        this.isActive = isActive;
        includeAnswered = false;
        includeUnanswered = true;
    }

    public PrayerDeck(int id, int listOrder, String prayerPlanName, String tags, boolean mustHaveAllTags, int maxCardsInRotation, int rotationPosition, boolean isActive, boolean includeAnswered, boolean includeUnanswered) {
        this.id = id;
        this.listOrder = listOrder;
        this.prayerPlanName = prayerPlanName;
        this.tags = tags;
        this.mustHaveAllTags = mustHaveAllTags;
        this.maxCardsInRotation = maxCardsInRotation;
        this.rotationPosition = rotationPosition;
        this.isActive = isActive;
        this.includeAnswered = includeAnswered;
        this.includeUnanswered = includeUnanswered;
    }

    protected PrayerDeck(Parcel in) {
        id = in.readInt();
        listOrder = in.readInt();
        prayerPlanName = in.readString();
        tags = in.readString();
        mustHaveAllTags = in.readByte() != 0;
        maxCardsInRotation = in.readInt();
        rotationPosition = in.readInt();
        isActive = in.readByte() != 0;
        includeAnswered = in.readByte() != 0;
        includeUnanswered = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(listOrder);
        dest.writeString(prayerPlanName);
        dest.writeString(tags);
        dest.writeByte((byte) (mustHaveAllTags ? 1 : 0));
        dest.writeInt(maxCardsInRotation);
        dest.writeInt(rotationPosition);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeByte((byte) (includeAnswered ? 1 : 0));
        dest.writeByte((byte) (includeUnanswered ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PrayerDeck> CREATOR = new Creator<PrayerDeck>() {
        @Override
        public PrayerDeck createFromParcel(Parcel in) {
            return new PrayerDeck(in);
        }

        @Override
        public PrayerDeck[] newArray(int size) {
            return new PrayerDeck[size];
        }
    };

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

    public String getPrayerPlanName() {
        return prayerPlanName;
    }

    public void setPrayerPlanName(String prayerPlanName) {
        this.prayerPlanName = prayerPlanName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isMustHaveAllTags() {
        return mustHaveAllTags;
    }

    public void setMustHaveAllTags(boolean mustHaveAllTags) {
        this.mustHaveAllTags = mustHaveAllTags;
    }

    public int getMaxCardsInRotation() {
        return maxCardsInRotation;
    }

    public void setMaxCardsInRotation(int maxCardsInRotation) {
        this.maxCardsInRotation = maxCardsInRotation;
    }

    public int getRotationPosition() {
        return rotationPosition;
    }

    public void setRotationPosition(int rotationPosition) {
        this.rotationPosition = rotationPosition;
    }

    public boolean isIncludeAnswered() {
        return includeAnswered;
    }

    public void setIncludeAnswered(boolean includeAnswered) {
        this.includeAnswered = includeAnswered;
    }

    public boolean isIncludeUnanswered() {
        return includeUnanswered;
    }

    public void setIncludeUnanswered(boolean includeUnanswered) {
        this.includeUnanswered = includeUnanswered;
    }
}
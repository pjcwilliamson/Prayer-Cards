package org.williamsonministry.prayercards;

public class PrayerDeck {
    private int id;
    private int listOrder;
    private String prayerPlanName;
    private String tags;
    private boolean mustHaveAllTags;
    private int maxCardsInRotation;
    private int rotationPosition;
    private boolean isActive;

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
}
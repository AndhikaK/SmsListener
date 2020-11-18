package com.example.explore;

public class PlaceItem {
    private String mXid;
    private String mImagePreview;
    private String mPlaceName;
    private int mPlaceRating;


    public PlaceItem(String mXid, String mImagePreview, String mPlaceName, int mPlaceRating) {
        this.mXid = mXid;
        this.mImagePreview = mImagePreview;
        this.mPlaceName = mPlaceName;
        this.mPlaceRating = mPlaceRating;
    }

    public String getmXid() {
        return mXid;
    }

    public String getmImagePreview() {
        return mImagePreview;
    }

    public String getmPlaceName() {
        return mPlaceName;
    }

    public int getmPlaceRating() {
        return mPlaceRating;
    }
}

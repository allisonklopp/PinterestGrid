package com.aklopp.pinterestgrid;

import android.graphics.Bitmap;

/**
 * Object representing a Pin.
 * Created by Allison on 6/2/2015.
 */
public class PinItem {
    private Bitmap image;
    private String title;

    /**
     * Constructor
     * @param image
     * @param title
     */
    public PinItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

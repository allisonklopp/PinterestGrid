package com.aklopp.pinterestgrid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Adapter for the grid view containing PinItems.
 * Created by Allison on 6/2/2015.
 */
public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private RelativeLayout mFocusedView;

    public GridViewAdapter(Context context, int layoutResourceId, RelativeLayout focusedView) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        mFocusedView = focusedView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }
        final PinItem currentItem = (PinItem)getItem(position);

        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        image.setImageBitmap(currentItem.getImage());

        // If user clicks on this item in the grid, show a larger image.
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView)mFocusedView.findViewById(R.id.focused_pin)).setImageBitmap(currentItem.getImage());
                ((TextView)mFocusedView.findViewById(R.id.focused_pin_description)).setText(currentItem.getTitle());
                mFocusedView.setVisibility(View.VISIBLE);

                mFocusedView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFocusedView.setVisibility(View.INVISIBLE); // tap on the overlay to close it
                    }
                });
            }
        });

        return convertView;
    }
}

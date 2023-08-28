package com.oilpalm3f.mainapp.collectioncenter;

import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.uihelper.expandablecheckbox.viewholders.CheckableChildViewHolder;

/**
 * Created by siva on 05/04/17.
 */

//No Use
public class MultiCheckChildViewHolder extends CheckableChildViewHolder {

    private CheckedTextView childCheckedTextView;

    public MultiCheckChildViewHolder(View itemView) {
        super(itemView);
        childCheckedTextView =
                (CheckedTextView) itemView.findViewById(R.id.list_item_multicheck_artist_name);
    }

    @Override
    public Checkable getCheckable() {
        return childCheckedTextView;
    }

    public void setChildItemName(String childItemName) {
        childCheckedTextView.setText(childItemName);
    }
}


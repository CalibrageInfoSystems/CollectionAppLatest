package com.oilpalm3f.mainapp.collectioncenter;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.MainGroupFarmerModel;
import com.oilpalm3f.mainapp.uihelper.expandablecheckbox.models.ExpandableGroup;
import com.oilpalm3f.mainapp.uihelper.expandablecheckbox.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * Created by siva on 06/04/17.
 */

//No Use
public class MainFarmerGroupHolderView extends GroupViewHolder {

    private TextView farmerInfo;
    private ImageView arrow;
    private ImageView icon;

    public MainFarmerGroupHolderView(View itemView) {
        super(itemView);
        farmerInfo = (TextView) itemView.findViewById(R.id.list_item_farmer_name);
        arrow = (ImageView) itemView.findViewById(R.id.list_item_farmer_arrow);
        icon = (ImageView) itemView.findViewById(R.id.list_item_farmer_icon);
    }

    public void setFarmerTitle(ExpandableGroup farmer) {
        if (farmer instanceof MainGroupFarmerModel) {
            farmerInfo.setText(farmer.getTitle());
        }

    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

}

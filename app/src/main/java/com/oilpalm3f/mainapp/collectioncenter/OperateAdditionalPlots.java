package com.oilpalm3f.mainapp.collectioncenter;


import android.graphics.Rect;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.areaextension.FarmerPlotDetailsAdapter;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.dbmodels.PlotDetailsObj;

import java.util.ArrayList;
import java.util.List;


// To  Add Additional Plots for Collection
public class OperateAdditionalPlots extends DialogFragment implements FarmerPlotDetailsAdapter.ClickListener {

    private static final String LOG_TAG = OperateAdditionalPlots.class.getName();

    private List<PlotDetailsObj> plotDetailsObjArrayList = new ArrayList<>();
    private RecyclerView rvplotlist;
    private FarmerPlotDetailsAdapter farmerplotDetailsLVAdapter;
    private CCDataAccessHandler ccDataAccessHandler = null;
    private BasicFarmerDetails basicFarmerDetails;
    private TextView selectedPlotsTxt;
    private LinearLayout selectedPlotsLayout;
    private Button cancelBtn, addBtn;
    private List<String> selectedPlotIds;
    private String selectedPlotIdsStr;
    private addedPlotCodesListener addedPlotCodesListener;

    public OperateAdditionalPlots() {
        // Required empty public constructor
    }

    public static OperateAdditionalPlots newInstance(BasicFarmerDetails basicFarmerDetails, List<String> selectedPlotIds) {
        OperateAdditionalPlots fragment = new OperateAdditionalPlots();
        Bundle args = new Bundle();
        args.putSerializable("basicFarmerDetails", basicFarmerDetails);
        args.putString("selectedPlotIds", TextUtils.join(",", selectedPlotIds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operate_additional_plots, container);
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        view.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

        basicFarmerDetails = (BasicFarmerDetails) getArguments().getSerializable("basicFarmerDetails");

        selectedPlotIdsStr = getArguments().getString("selectedPlotIds");

//        selectedPlotIds = new ArrayList<String>(Arrays.asList(selectedPlotIdsStr.split(",")));

        ccDataAccessHandler = new CCDataAccessHandler(getActivity());
        rvplotlist = (RecyclerView) view.findViewById(R.id.lv_farmerplotdetails);
        selectedPlotsLayout = (LinearLayout) view.findViewById(R.id.selectedPlotsLayout);
        selectedPlotsTxt = (TextView) view.findViewById(R.id.selectedPlotsTxt);
        cancelBtn  = (Button) view.findViewById(R.id.cancelBtn);
        addBtn  = (Button) view.findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (farmerplotDetailsLVAdapter != null && farmerplotDetailsLVAdapter.getSelectedItemCount() > 0) {
                    Log.v(LOG_TAG, "@@@ let's go next "+farmerplotDetailsLVAdapter.getSelectedItemCount());
                    List plotCodes = new ArrayList();
                    for (int i = 0; i < farmerplotDetailsLVAdapter.getSelectedItems().size(); i++) {
                        Log.v(LOG_TAG, "@@@ let's go next " + farmerplotDetailsLVAdapter.getSelectedItems().get(i));
                        plotCodes.add(plotDetailsObjArrayList.get(i).getPlotID());
                    }
//                    selectedPlotIds.addAll(plotCodes);

                    addedPlotCodesListener.addedPlotCodes(plotCodes);
                }
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        bindPlotData();

        return view;
    }

    private void bindPlotData() {
        plotDetailsObjArrayList = ccDataAccessHandler.getPlotDetails(basicFarmerDetails.getFarmerCode().trim());


        if (plotDetailsObjArrayList != null && plotDetailsObjArrayList.size() > 0) {
            farmerplotDetailsLVAdapter = new FarmerPlotDetailsAdapter(getActivity(), plotDetailsObjArrayList, R.layout.small_plots_view);
            farmerplotDetailsLVAdapter.setOnClickListener(this);
            rvplotlist.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvplotlist.setAdapter(farmerplotDetailsLVAdapter);
            farmerplotDetailsLVAdapter.clearSelection();
            farmerplotDetailsLVAdapter.selectedItems.clear();
        }
    }

    @Override
    public void onItemClicked(int position) {
        toggleSelection(position);
        String plotCodes = getPlotCodesToDisplay();
        if (!TextUtils.isEmpty(plotCodes)) {
            selectedPlotsLayout.setVisibility(View.VISIBLE);
            selectedPlotsTxt.setText(getPlotCodesToDisplay());
        } else {
            selectedPlotsLayout.setVisibility(View.GONE);
        }
    }

    private void toggleSelection(int position) {
        Log.v(LOG_TAG, "@@@ item clicked and the position is 111 check "+position);
        farmerplotDetailsLVAdapter.toggleSelection(position);
        String plotCodes = getPlotCodesToDisplay();
        if (!TextUtils.isEmpty(plotCodes)) {
            selectedPlotsLayout.setVisibility(View.VISIBLE);
            selectedPlotsTxt.setText(getPlotCodesToDisplay());
        } else {
            selectedPlotsLayout.setVisibility(View.GONE);
        }
    }

    public String getPlotCodesToDisplay() {
        List plotCodes = new ArrayList();
        if (null != farmerplotDetailsLVAdapter.getSelectedItems() && !farmerplotDetailsLVAdapter.getSelectedItems().isEmpty()) {
            List<Integer> selectedPos = farmerplotDetailsLVAdapter.getSelectedItems();
            for (int i = 0; i < selectedPos.size(); i++) {
                Log.v(LOG_TAG, "@@@ let's go next " + selectedPos.get(i));
                plotCodes.add(plotDetailsObjArrayList.get(selectedPos.get(i)).getPlotID());
            }
        }
        return TextUtils.join(", ", plotCodes);
    }

    public void setAddedPlotCodesListener(OperateAdditionalPlots.addedPlotCodesListener addedPlotCodesListener) {
        this.addedPlotCodesListener = addedPlotCodesListener;
    }

    public interface addedPlotCodesListener {
        void addedPlotCodes(List<String> plotCodes);
    }


}

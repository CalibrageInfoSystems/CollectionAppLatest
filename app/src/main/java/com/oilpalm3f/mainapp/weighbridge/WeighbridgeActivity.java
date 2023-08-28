package com.oilpalm3f.mainapp.weighbridge;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;

import java.util.ArrayList;

import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTRE11;

//Not Using
public class WeighbridgeActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage, tokenNotxt, netWeightTxt;
    private CollectionCenter selectedCollectionCenter;
    ArrayList<CollectionCenter1>collectionCenter1ArrayList=new ArrayList<>();

    private int typeSelected;
    public static final int REQUEST_CAM_PERMISSIONS = 1;
    public static final int OWN_WEIGHBRIDGE = 24;
    public static final int PRIVATE_WEIGHBRIDGE = 25;
    public static final int NO_WEIGHBRIDGE = 26;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weighbridge);
        Toolbar toolbar = findViewById(R.id.toolbar);
        collectionCenterName = (TextView) findViewById(R.id.collection_center_name);
        collectionCenterCode = (TextView) findViewById(R.id.collection_center_code);
        collectionCenterVillage = (TextView) findViewById(R.id.collection_center_village);
        tokenNotxt=(TextView)findViewById(R.id.tokenNoText);

        // setSupportActionBar(toolbar);


        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);
        typeSelected = selectedCollectionCenter.getWeighBridgeTypeId();
        collectionCenter1ArrayList= (ArrayList<CollectionCenter1>) DataManager.getInstance().getDataFromManager(COLLECTION_CENTRE11);

        if (typeSelected == OWN_WEIGHBRIDGE) {
            toolbar.setTitle(R.string.new_collection_own);

        } else if (typeSelected == PRIVATE_WEIGHBRIDGE) {
            toolbar.setTitle(getString(R.string.new_collection_priv));

        } else {
            toolbar.setTitle(R.string.new_collection_no_weighbridge);

        }
        getSupportFragmentManager().addOnBackStackChangedListener(this);


        if (savedInstanceState == null)

            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();
        else
            onBackStackChanged();
        collectionCenterName.setText(selectedCollectionCenter.getName());
        collectionCenterCode.setText(selectedCollectionCenter.getCode());
        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());
       // tokenNotxt.setText(""+collectionCenter1ArrayList.get(0).getTokenNo());
        tokenNotxt.setText(""+ CommonConstants.TokenNumber);

    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            TerminalFragment terminal = (TerminalFragment) getSupportFragmentManager().findFragmentByTag("terminal");
            if (terminal != null)
                terminal.status("USB device detected");

        }
        super.onNewIntent(intent);

    }
}
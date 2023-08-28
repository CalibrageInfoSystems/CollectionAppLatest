package com.oilpalm3f.mainapp.collectioncenter;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.uihelper.InteractiveScrollView;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */

//Not Using
public class WeighbridgePrivate extends Fragment {
    private static final String LOG_TAG = WeighbridgeCC.class.getName();
    private static final int CAMERA_REQUEST = 1888;
    public static final int REQUEST_CAM_PERMISSIONS = 1;
    private android.widget.Spinner sourceOfWaterSpin;
    private android.widget.EditText sourceEdit;
    private ActionBar actionBar;
    private ImageView scrollBottomIndicator;
    private InteractiveScrollView interactiveScrollView;
    private Bitmap userImageData;
    private byte[] imageData = null;
    private File fileToUpload;
    private ImageView slipImage, slipIcon;
    private Button generateReceipt;
    private EditText name_of_weighbridge_centre, date_time_of_weighing, vehicle_no, vehicleDriver, grossWt, tareWt, netWt,  no_of_bunches, no_of_bunches_rejected, rmrks, name_of_grader;
    private String weighbridge_centre_name, weighing_date_time, vehicleNumber, vehicleDriverName, grossWeight, tareWeight, netWeight,  bunchesNumber, rejectedBunches, remarks, graderName;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public WeighbridgePrivate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weighbridge_private, container, false);;
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(activity.getResources().getString(R.string.ffb_details));

        name_of_weighbridge_centre = (EditText) rootView.findViewById(R.id.weighbridge_centre_name);
        date_time_of_weighing = (EditText) rootView.findViewById(R.id.weighing_date_time);
        vehicle_no = (EditText) rootView.findViewById(R.id.vehicle_number);
        vehicleDriver = (EditText) rootView.findViewById(R.id.vehicle_driver);
        grossWt = (EditText) rootView.findViewById(R.id.gross_weight);
        tareWt = (EditText) rootView.findViewById(R.id.tare_weight);
        netWt = (EditText) rootView.findViewById(R.id.net_weight);
        no_of_bunches = (EditText) rootView.findViewById(R.id.number_of_bunches);
        no_of_bunches_rejected = (EditText) rootView.findViewById(R.id.number_of_bunches_rejected);
        rmrks = (EditText) rootView.findViewById(R.id.remarks);
        name_of_grader = (EditText) rootView.findViewById(R.id.grader_name);

        slipImage = (ImageView) rootView.findViewById(R.id.slip_image);
        slipIcon = (ImageView) rootView.findViewById(R.id.slip_icon);

        generateReceipt = (Button) rootView.findViewById(R.id.generateReceipt);


        scrollBottomIndicator = (ImageView) rootView.findViewById(R.id.bottomScroll);
        interactiveScrollView = (InteractiveScrollView) rootView.findViewById(R.id.scrollView);
        scrollBottomIndicator.setVisibility(View.VISIBLE);
        scrollBottomIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactiveScrollView.post(new Runnable() {

                    @Override
                    public void run() {
                        interactiveScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        interactiveScrollView.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                scrollBottomIndicator.setVisibility(View.GONE);
            }
        });

        interactiveScrollView.setOnTopReachedListener(new InteractiveScrollView.OnTopReachedListener() {
            @Override
            public void onTopReached() {
            }
        });

        interactiveScrollView.setOnScrollingListener(new InteractiveScrollView.OnScrollingListener() {
            @Override
            public void onScrolling() {
                android.util.Log.d(LOG_TAG, "onScrolling: ");
                scrollBottomIndicator.setVisibility(View.VISIBLE);
            }
        });

        slipImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA))) {
                    android.util.Log.v(LOG_TAG, "Location Permissions Not Granted");
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_CAM_PERMISSIONS
                    );
                } else {
                    Intent Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(Camera, CAMERA_REQUEST);
                }
            }
        });

        generateReceipt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                if (validate()){

                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            onCaptureImageResult(data);
        }
    }

    public boolean validate() {
        PrintReceipt PR = new PrintReceipt();
        Bundle args = new Bundle();
        weighbridge_centre_name = name_of_weighbridge_centre.getText().toString();
        weighing_date_time = date_time_of_weighing.getText().toString();
        vehicleNumber = vehicle_no.getText().toString();
        vehicleDriverName = vehicleDriver.getText().toString();
        grossWeight = grossWt.getText().toString();
        tareWeight = tareWt.getText().toString();
        netWeight = netWt.getText().toString();
        bunchesNumber = no_of_bunches.getText().toString();
        rejectedBunches = no_of_bunches_rejected.getText().toString();
        remarks = rmrks.getText().toString();
        graderName = name_of_grader.getText().toString();
        char[] arr = vehicleNumber.toCharArray();

        if(TextUtils.isEmpty(weighbridge_centre_name)) {
            Toast.makeText(getActivity(), "Enter Weighbridge Centre Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(weighing_date_time)) {
            Toast.makeText(getActivity(), "Enter Weighing Date and Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(vehicleNumber)) {
            Toast.makeText(getActivity(), "Enter Vehicle Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(arr.length!=10){
            Toast.makeText(getActivity(), "Invalid Vehicle Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        String state_code = String.valueOf(arr[0])+String.valueOf(arr[1]);
        String district_1 = String.valueOf(arr[2]);
        String district_2 = String.valueOf(arr[3]);
        String uniqNum1 = String.valueOf(arr[4]);
        String uniqNum2 = String.valueOf(arr[5]);
        String uniqNum3 = String.valueOf(arr[6]);
        String uniqNum4 = String.valueOf(arr[7]);
        String uniqNum5 =  String.valueOf(arr[8]);
        String uniqNum6 = String.valueOf(arr[9]);

        if(!ArrayUtils.contains(CommonUtils.stateCode,state_code.toUpperCase())
                ||!CommonUtils.numeric.contains(district_1)||!CommonUtils.numeric.contains(district_2)
                ||!CommonUtils.alphabet.contains(uniqNum1.toUpperCase())||!CommonUtils.alphabet.contains(uniqNum2.toUpperCase())
                ||!CommonUtils.numeric.contains(uniqNum3)||!CommonUtils.numeric.contains(uniqNum4)||!CommonUtils.numeric.contains(uniqNum5)||!CommonUtils.numeric.contains(uniqNum6)){
            Toast.makeText(getActivity(), "Invalid Vehicle Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(vehicleDriverName)) {
            Toast.makeText(getActivity(), "Enter Vehicle Driver Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(grossWeight)) {
            Toast.makeText(getActivity(), "Enter Gross Weight", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(tareWeight)) {
            Toast.makeText(getActivity(), "Enter Tare Weight", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(netWeight)) {
            Toast.makeText(getActivity(), "Enter Net Weight", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(bunchesNumber)) {
            Toast.makeText(getActivity(), "Enter Number of Bunches", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(rejectedBunches)) {
            Toast.makeText(getActivity(), "Enter Number of Rejected Bunches", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(graderName)) {
            Toast.makeText(getActivity(), "Enter Grader Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            args.putString("weighbridge_centre_name",weighbridge_centre_name);
            args.putString("weighing_date_time",weighing_date_time);
            args.putString("vehicle_number", vehicleNumber.toUpperCase());
            args.putString("vehicleDriverName", vehicleDriverName);
            args.putString("grossWeight", grossWeight);
            args.putString("tareWeight", tareWeight);
            args.putString("netWeight", netWeight);
            args.putString("bunchesNumber", bunchesNumber);
            args.putString("rejectedBunches", rejectedBunches);
            args.putString("remarks", remarks);
            args.putString("graderName", graderName);
            args.putString("weighbridge_centre", "Weighbridge Centre");

            PR.setArguments(args);
            String backStateName = PR.getClass().getName();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ftransaction = fm.beginTransaction();
            ftransaction.replace(android.R.id.content, PR).commit();
            ftransaction.addToBackStack(backStateName);
            return true;
        }
    }


    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void onCaptureImageResult(Intent data) {
        userImageData = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        userImageData.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        imageData = bytes.toByteArray();
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        fileToUpload = destination;
        FileOutputStream fo;

        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ExifInterface exif = new ExifInterface(fileToUpload.getName());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            int rotation = 0;
            if (orientation == 6) rotation = 90;
            else if (orientation == 3) rotation = 180;
            else if (orientation == 8) rotation = 270;

            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                System.out.println("++++++++++++++++++++++++++++");

                // Rotate Bitmap
                Bitmap rotated = Bitmap.createBitmap(userImageData, 0, 0, userImageData.getWidth(), userImageData.getHeight(), matrix, true);

                userImageData.recycle();
                userImageData = rotated;
                rotated = null;
            }
        } catch (Exception e) {
        }

        if (imageData != null) {
            slipIcon.setVisibility(View.GONE);
        }
        slipImage.setImageBitmap(userImageData);
        slipImage.invalidate();
    }

}

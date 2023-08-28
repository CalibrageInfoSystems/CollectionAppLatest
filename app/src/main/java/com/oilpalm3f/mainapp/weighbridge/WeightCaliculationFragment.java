package com.oilpalm3f.mainapp.weighbridge;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.oilpalm3f.mainapp.BuildConfig;
import com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles.CollectionWithOutPlot;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.CCDataAccessHandler;
import com.oilpalm3f.mainapp.collectioncenter.FarmersDetailsScreen;
import com.oilpalm3f.mainapp.collectioncenter.PrintReceipt;
import com.oilpalm3f.mainapp.collectioncenter.WeighbridgeCC;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Collection;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.common.FiscalDate;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.utils.ImageUtility;
import com.oilpalm3f.mainapp.utils.UiUtils;
import com.oilpalm3f.mainapp.weighbridge.CollectionCenter1;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTRE11;


//Not Using
public class WeightCaliculationFragment extends Fragment {
    EditText gross_weight, tare_weight, net_weight, number_of_bunches, number_of_bunches_rejected, number_of_bunches_accepted, remarks,
            grader_name;
    TextView netWeightTxt;
    ImageView slipImage, slipIcon;
    Button generateReceipt;
    View view;
    String grossWeight, tareWeight, netWeight;
    private int typeSelected;
    public static final int REQUEST_CAM_PERMISSIONS = 1;
    public static final int OWN_WEIGHBRIDGE = 24;
    public static final int PRIVATE_WEIGHBRIDGE = 25;
    public static final int NO_WEIGHBRIDGE = 26;
    private static final int CAMERA_REQUEST = 1888;
    private CollectionCenter selectedCollectionCenter;
    private int rejectedBunches_converted, totalBunches_converted;

    ArrayList<CollectionCenter1> collectionCenter1ArrayList = new ArrayList<>();
    private double GrossWeightD = 0.0, TareWeightD = 0.0;
    private static final String LOG_TAG = WeighbridgeCC.class.getName();
    private String ColFarmerWithOutplot;
    private String collectionId = "", totalBunches;
    private DataAccessHandler dataAccessHandler;
    private CCDataAccessHandler ccDataAccessHandler;
    private String days = "";
    public int financialYear;
    private Bitmap currentBitmap = null;
    private String vehicleNumber, vehicleDriverName, operatorName, bunchesNumber, rejectedBunches, remarksString, graderName;
    private String mCurrentPhotoPath;
    Calendar myCalendar = Calendar.getInstance();
    Calendar myCalendar1 = Calendar.getInstance();
    Calendar weighingCalendar = Calendar.getInstance();
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public WeightCaliculationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_weight_caliculation, container, false);
        initViews();
        return view;
    }

    public void initViews() {
        dataAccessHandler = new DataAccessHandler(getActivity());
        ccDataAccessHandler = new CCDataAccessHandler(getActivity());

        gross_weight = view.findViewById(R.id.gross_weight);
        tare_weight = view.findViewById(R.id.tare_weight);
        netWeightTxt = view.findViewById(R.id.netWeightTxt);
        slipImage = view.findViewById(R.id.slip_image);
        slipIcon = (ImageView) view.findViewById(R.id.slip_icon);
        number_of_bunches = view.findViewById(R.id.number_of_bunches);
        number_of_bunches_rejected = view.findViewById(R.id.number_of_bunches_rejected);
        number_of_bunches_accepted = view.findViewById(R.id.number_of_bunches_accepted);
        remarks=view.findViewById(R.id.remarks);
        grader_name=view.findViewById(R.id.grader_name);

        generateReceipt = view.findViewById(R.id.generateReceipt);
        net_weight = view.findViewById(R.id.net_weight);
        ColFarmerWithOutplot = FarmersDetailsScreen.firstthree;
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Weight Caliculation");



        final Calendar calendar = Calendar.getInstance();
        final FiscalDate fiscalDate = new FiscalDate(calendar);
        financialYear = fiscalDate.getFiscalYear();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String currentdate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_3);
            String financalDate = "01/04/" + String.valueOf(financialYear);
            Date date1 = dateFormat.parse(currentdate);
            Date date2 = dateFormat.parse(financalDate);
            long diff = date1.getTime() - date2.getTime();
            String noOfDays = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
            days = StringUtils.leftPad(noOfDays, 3, "0");
            Log.v(LOG_TAG, "days -->" + days);

        } catch (Exception e) {
            e.printStackTrace();
        }
        setViews();
    }

    private void setViews() {
        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);
        typeSelected = selectedCollectionCenter.getWeighBridgeTypeId();

        //collectionCenter1=(CollectionCenter1) DataManager.getInstance().getDataFromManager(COLLECTION_CENTRE11);

        collectionCenter1ArrayList = (ArrayList<CollectionCenter1>) DataManager.getInstance().getDataFromManager(COLLECTION_CENTRE11);
        gross_weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                grossWeight = gross_weight.getText().toString();
                if (typeSelected == PRIVATE_WEIGHBRIDGE || typeSelected == NO_WEIGHBRIDGE) {
                    validateDoubles();
                }

                if (TextUtils.isEmpty(grossWeight) || !validateDoubles()) {
                    tare_weight.setText("");
                    netWeightTxt.setText("");
                }
                enableNetWeight();
            }
        });

        tare_weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tareWeight = tare_weight.getText().toString();
                if (!validateDoubles()) {
                    if (!TextUtils.isEmpty(grossWeight)) {
                        tare_weight.setText(tareWeight.substring(0, tareWeight.length() - 1));
                        tare_weight.setSelection(tareWeight.length());
                    }
                }
                enableNetWeight();
            }
        });

        netWeightTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (typeSelected == PRIVATE_WEIGHBRIDGE || typeSelected == NO_WEIGHBRIDGE) {
//                    String netWeight = netWt.getText().toString();
//                    if (!TextUtils.isEmpty(netWeight) && TextUtils.isEmpty(tareWeight) && TextUtils.isEmpty(grossWeight)) {
//                        tareWt.setEnabled(false);
//                        grossWt.setEnabled(false);
//                    } else {
//                        tareWt.setEnabled(true);
//                        grossWt.setEnabled(true);
//                    }
//                }
            }
        });

        number_of_bunches.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                totalBunches = number_of_bunches.getText().toString();
                if (TextUtils.isEmpty(totalBunches) || !validateBunchValues()) {
                    number_of_bunches_rejected.setText("");
                    number_of_bunches_accepted.setText("");
                }
            }
        });

        number_of_bunches_rejected.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rejectedBunches = number_of_bunches_rejected.getText().toString();
                if (!validateBunchValues()) {
                    if (!TextUtils.isEmpty(totalBunches)) {
                        number_of_bunches_rejected.setText(rejectedBunches.substring(0, rejectedBunches.length() - 1));
                        number_of_bunches_rejected.setSelection(rejectedBunches.length());
                    }
                }
            }
        });


        if (ColFarmerWithOutplot.equalsIgnoreCase("CCFARMERW") || ColFarmerWithOutplot.equalsIgnoreCase("FFBFARMER")) {
            collectionId = ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL,
                    ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days);
        } else {
            collectionId = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL,
                    ccDataAccessHandler.TABLE_COLLECTION, days);
        }

        slipImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA))) {
                    Log.v(LOG_TAG, "Location Permissions Not Granted");
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_CAM_PERMISSIONS
                    );
                } else {
//                    Intent Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(Camera, CAMERA_REQUEST);
                    dispatchTakePictureIntent(CAMERA_REQUEST);
                }
            }
        });


        generateReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                injectData();
                if (typeSelected == OWN_WEIGHBRIDGE && !validationsInOwWbCase(true)) {
                    return;
                } else if (typeSelected == PRIVATE_WEIGHBRIDGE && !validationsInPrivateWbCase()) {
                    return;
                } else if (!validationsInNoWbCase()) {
                    return;
                }
                gotoPreviewScreen();
            }
        });


    }

    public boolean validateDoubles() {
        if (!TextUtils.isEmpty(grossWeight) && !grossWeight.equalsIgnoreCase(".") && !TextUtils.isEmpty(tareWeight) && !tareWeight.equalsIgnoreCase(".")) {
            try {
                GrossWeightD = Double.parseDouble(grossWeight);
                TareWeightD = Double.parseDouble(tareWeight);
                if (GrossWeightD >= TareWeightD) {
                    getNetWeight(GrossWeightD, TareWeightD);
                } else {
                    UiUtils.showCustomToastMessage("Tare weight  should not exceed gross weight", getActivity(), 1);
                    return false;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        } else if (!TextUtils.isEmpty(grossWeight) && !grossWeight.equalsIgnoreCase(".")) {
            try {
                GrossWeightD = Double.parseDouble(grossWeight);
                getNetWeight(GrossWeightD, 0.0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        } else if (!TextUtils.isEmpty(tareWeight) && !tareWeight.equalsIgnoreCase(".")) {
            try {
                TareWeightD = Double.parseDouble(tareWeight);
                getNetWeight(TareWeightD, 0.0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        }
        return true;
    }

    public boolean validateBunchValues() {
        if (!TextUtils.isEmpty(rejectedBunches) && !rejectedBunches.equalsIgnoreCase(".") && !TextUtils.isEmpty(totalBunches) && !totalBunches.equalsIgnoreCase(".")) {
            try {
                rejectedBunches_converted = Integer.parseInt(rejectedBunches);
                totalBunches_converted = Integer.parseInt(totalBunches);
                if (totalBunches_converted >= rejectedBunches_converted) {
                    getAreaLeft(totalBunches_converted, rejectedBunches_converted);
                } else {
                    UiUtils.showCustomToastMessage("Rejected bunches should not exceed total bunches", getActivity(), 1);
                    return false;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        } else if (!TextUtils.isEmpty(rejectedBunches) && !TextUtils.isEmpty(totalBunches) && !totalBunches.equalsIgnoreCase(".")) {
            Log.v(LOG_TAG, "@@@ check 1 " + totalBunches);
            totalBunches_converted = Integer.parseInt(totalBunches);
            getAreaLeft(totalBunches_converted, 0);
        }
        return true;
    }
    public double getAreaLeft(final int plotAreaDouble, final int totalAreaDouble) {
        double diff = plotAreaDouble - totalAreaDouble;
        number_of_bunches_accepted.setText("" + diff);
        return diff;
    }


    public double getNetWeight(final Double plotAreaDouble, final Double totalAreaDouble) {
        double diff = plotAreaDouble - totalAreaDouble;
        netWeightTxt.setVisibility(View.VISIBLE);
        netWeightTxt.setText("" + diff);
        return diff;
    }

    public void enableNetWeight() {


        net_weight.setEnabled(true);
        net_weight.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);


    }

    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void gotoPreviewScreen() {
        PrintReceipt printReceipt = new PrintReceipt();
        if (ColFarmerWithOutplot.equalsIgnoreCase("CCFARMERW") || ColFarmerWithOutplot.equalsIgnoreCase("FFBFARMER")) {
            bindCollectionFarmerWOPData();
            Bundle dataBundle = new Bundle();
            dataBundle.putParcelable("collection_data", bindCollectionFarmerWOPData());
            printReceipt.setArguments(dataBundle);
        } else {
            bindData();
            Bundle dataBundle = new Bundle();
            dataBundle.putParcelable("collection_data", bindData());
            printReceipt.setArguments(dataBundle);
        }

        String backStateName = printReceipt.getClass().getName();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ftransaction = fm.beginTransaction();
        ftransaction.replace(android.R.id.content, printReceipt).commit();
        ftransaction.addToBackStack(backStateName);
    }

    public void injectData() {
        vehicleNumber = collectionCenter1ArrayList.get(0).getVechileNo();
        vehicleDriverName = collectionCenter1ArrayList.get(0).getDriverName();
        grossWeight = gross_weight.getText().toString();
        tareWeight = tare_weight.getText().toString();
        if (netWeightTxt.getVisibility() == View.VISIBLE) {
            netWeight = netWeightTxt.getText().toString();
        } else {
            netWeight = net_weight.getText().toString();
        }
        operatorName = collectionCenter1ArrayList.get(0).getNameOfOperator();

        bunchesNumber = number_of_bunches.getText().toString();
        rejectedBunches = number_of_bunches_rejected.getText().toString();

        remarksString = remarks.getText().toString();
        graderName = grader_name.getText().toString();
//        dateAndTimeStr = dateTimeEdit.getText().toString();
//        postedDateAndTimeStr = postDateTimeEdit.getText().toString();
    }

    public boolean validationsInNoWbCase() {


        if (currentBitmap == null) {
            UiUtils.showCustomToastMessage("Please Capture Photo", getActivity(), 1);
            return false;
        }


        return true;
    }

    public boolean validationsInOwWbCase(boolean applyValidation) {
//


        if (applyValidation && currentBitmap == null) {
            UiUtils.showCustomToastMessage("Please Capture Photo", getActivity(), 1);
            return false;
        }


        return true;
    }

    public boolean validationsInPrivateWbCase() {


        return validationsInOwWbCase(true);
    }


    public CollectionWithOutPlot bindCollectionFarmerWOPData() {
        CollectionWithOutPlot wbWOPCollection = new CollectionWithOutPlot();

        wbWOPCollection.setWeighingDate(collectionCenter1ArrayList.get(0).getDateAndTime());
        wbWOPCollection.setCollectioncentercode(selectedCollectionCenter.getCode());
        wbWOPCollection.setCode(ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(),
                ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days));
        wbWOPCollection.setVehiclenumber(vehicleNumber.toUpperCase());
        wbWOPCollection.setDrivername(vehicleDriverName);
        wbWOPCollection.setGrossweight(CommonUtils.isValidDouble(grossWeight) ? Double.parseDouble(grossWeight) : 0);
        wbWOPCollection.setNetweight(CommonUtils.isValidDouble(netWeight) ? Double.parseDouble(netWeight) : 0);
        wbWOPCollection.setTareweight(CommonUtils.isValidDouble(tareWeight) ? Double.parseDouble(tareWeight) : 0);
        wbWOPCollection.setOperatorname(operatorName);
        wbWOPCollection.setRecieptGeneratedDate(collectionCenter1ArrayList.get(0).getPostingDate());
        if (!TextUtils.isEmpty(bunchesNumber) && bunchesNumber.length() > 0) {
            wbWOPCollection.setTotalbunches(Integer.parseInt(bunchesNumber));
        }
        if (!TextUtils.isEmpty(rejectedBunches) && rejectedBunches.length() > 0) {
            wbWOPCollection.setRejectedbunches(Integer.parseInt(rejectedBunches));
        }
        if (typeSelected == PRIVATE_WEIGHBRIDGE || typeSelected == NO_WEIGHBRIDGE) {
            if (!collectionCenter1ArrayList.get(0).getWbId().equals("0")) {

                wbWOPCollection.setWeighbridgecenterid(Integer.parseInt(collectionCenter1ArrayList.get(0).getWbId()));
            } else {
                wbWOPCollection.setWeighbridgecenterid(null);
            }
        } else {
            wbWOPCollection.setWeighbridgecenterid(null);
        }
        wbWOPCollection.setRemarks(remarksString);
        wbWOPCollection.setGradername(graderName);
        wbWOPCollection.setComments(collectionCenter1ArrayList.get(0).getComments());
        wbWOPCollection.setRecieptlocation(mCurrentPhotoPath);
        wbWOPCollection.setRecieptextension(".jpg");
       // wbWOPCollection.setTokenNo(collectionCenter1ArrayList.get(0).getTokenNo());
        return wbWOPCollection;
    }

    public Collection bindData() {
        Collection wbCollection = new Collection();
        wbCollection.setWeighingDate(collectionCenter1ArrayList.get(0).getDateAndTime());
        wbCollection.setCollectioncentercode(selectedCollectionCenter.getCode());
        wbCollection.setCode(ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, days));
        wbCollection.setVehiclenumber(vehicleNumber.toUpperCase());
        wbCollection.setDrivername(vehicleDriverName);
        wbCollection.setGrossweight(CommonUtils.isValidDouble(grossWeight) ? Double.parseDouble(grossWeight) : 0);
        wbCollection.setNetweight(CommonUtils.isValidDouble(netWeight) ? Double.parseDouble(netWeight) : 0);
        wbCollection.setTareweight(CommonUtils.isValidDouble(tareWeight) ? Double.parseDouble(tareWeight) : 0);
        wbCollection.setOperatorname(operatorName);
        wbCollection.setRecieptGeneratedDate(collectionCenter1ArrayList.get(0).getPostingDate());
        if (!TextUtils.isEmpty(bunchesNumber) && bunchesNumber.length() > 0) {
            wbCollection.setTotalbunches(Integer.parseInt(bunchesNumber));
        }
        if (!TextUtils.isEmpty(rejectedBunches) && rejectedBunches.length() > 0) {
            wbCollection.setRejectedbunches(Integer.parseInt(rejectedBunches));
        }
        if (typeSelected == PRIVATE_WEIGHBRIDGE || typeSelected == NO_WEIGHBRIDGE) {
            if (!collectionCenter1ArrayList.get(0).getWbId().equals("0")) {

                wbCollection.setWeighbridgecenterid(Integer.parseInt(collectionCenter1ArrayList.get(0).getWbId()));
            } else {
                wbCollection.setWeighbridgecenterid(null);
            }
        } else {
            wbCollection.setWeighbridgecenterid(null);
        }
        wbCollection.setRemarks(remarksString);
        wbCollection.setGradername(graderName);
        wbCollection.setComments(collectionCenter1ArrayList.get(0).getComments());
        wbCollection.setRecieptlocation(mCurrentPhotoPath);
        wbCollection.setRecieptextension(".jpg");
        wbCollection.setTokenNo(collectionCenter1ArrayList.get(0).getTokenNo());
        return wbCollection;
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
//            mCurrentPhotoPath = null;
        }

    }

    private void setPic() {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        int targetW = slipImage.getWidth();
        int targetH = slipImage.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        bitmap = ImageUtility.rotatePicture(90, bitmap);

        currentBitmap = bitmap;
        slipImage.setImageBitmap(bitmap);

//		/* Associate the Bitmap to the ImageView */
//        if (null != rotatedBitmap) {
//            String convertedImage = CommonUtils.getBase64String(rotatedBitmap);
//            Log.v(LOG_TAG, "@@@ converted image "+convertedImage.length());
//            slipImage.setImageBitmap(rotatedBitmap);
//            currentBitmap = rotatedBitmap;
//        } else {
//            currentBitmap = bitmap;
//            slipImage.setImageBitmap(bitmap);
//        }

        slipImage.setVisibility(View.VISIBLE);
        slipIcon.setVisibility(View.GONE);
        slipImage.invalidate();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "@@@ check on resume called");
        if (!TextUtils.isEmpty(mCurrentPhotoPath) && !TextUtils.isEmpty(collectionId) && null != slipImage) {
            loadImageFromStorage(mCurrentPhotoPath);
            slipImage.invalidate();
        }
    }

    private void loadImageFromStorage(String path) {
        try {
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            b = ImageUtility.rotatePicture(90, b);
            slipImage.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

        } // switch
    }


    private File createImageFile() throws IOException {
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDirectory = new File(root + "/3F_Pictures");
        File pictureDirectory = new File(root + "/3F_Pictures/" + "CollectionPhotos");

        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }

        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }

        File finalFile = new File(pictureDirectory, collectionId + CommonConstants.JPEG_FILE_SUFFIX);
        return finalFile;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch (actionCode) {
            case CAMERA_REQUEST:
                File f = null;
                mCurrentPhotoPath = null;
                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch
        startActivityForResult(takePictureIntent, actionCode);
    }


}
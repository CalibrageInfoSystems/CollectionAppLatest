package com.oilpalm3f.mainapp.common;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.oilpalm3f.mainapp.areacalculator.LocationService;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Config;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.DatabaseKeys;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.oilpalm3f.mainapp.common.CommonConstants.COUNT_OF_TREES;


//Commonly Used method will be written here
public class CommonUtils {


    public static final int REQUEST_CAM_PERMISSIONS = 1;
    public static final int FROM_CAMERA = 1;
    public static final int FROM_GALLERY = 2;
    public static boolean isNotSyncScreen = false;
    /**
     * validating email pattern
     */
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    //Validate VehicleNumber Pattern
    public static final Pattern VEHICLE_NUMBER_PATTERN = Pattern.compile("^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$");

    public static final int PERMISSION_CODE = 100;
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{6,20})";
    public static FragmentActivity currentActivity;
    public static DecimalFormat twoDForm = new DecimalFormat("#.##");
    public static boolean isFarmerDetailsAdded = false;
    public static boolean isPlotDetailsAdded = false;
    public static String[] stateCode = {"AN", "AP", "AR", "AS", "BR", "CG", "CH", "DD", "DL", "DN", "GA", "GJ", "HR", "HP", "JH", "JK", "KA", "KL", "LD", "MH", "ML", "MN", "MP", "MZ", "NL", "OD", "PB", "PY", "RJ", "SK", "TN", "TR", "TS", "UK", "UP", "WB"};
    public static String numeric = "1234567890";
    public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Pattern pattern = null;
    static Matcher matcher;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String LOG_TAG = CommonUtils.class.getName();

    public static boolean isValidVehicleNumber(String vehicleNumber) {
        return VEHICLE_NUMBER_PATTERN.matcher(vehicleNumber).matches();
    }

    /**
     * Checking the given emailId is valid or not
     *
     * @param email --> user emailId
     * @return ---> true if valid else false
     */
    public static boolean isValidEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }




    public static String getConsignmentFileRootPath() {
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDirectory = new File(root + "/Consignment_Files");
        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }
        return rootDirectory.getAbsolutePath() + File.separator;
    }
    /**
     * Validate password with regular expression
     *
     * @param password password for validation
     * @return true valid password, false invalid password
     */


    public static boolean passwordValidate(final String password, Context context) {
        /*
         * Pattern pattern = null; Matcher matcher;
		 */
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        if (matcher.matches() == false)
            Toast.makeText(context, "Password Must contain: Minimum 6 characters and atleast one number.", Toast.LENGTH_SHORT).show();
        return matcher.matches();

    }

    /**
     * Checking the Internet connection is available or not
     *
     * @param context
     * @return true if available else false
     */
    public static boolean isNetworkAvailable(final Context context) {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();

            Log.d(CommonUtils.class.getSimpleName(),"---> IS NET AVAILABLE  :"+exitValue);
            return (exitValue == 0);
        }
        catch (IOException e)          {
            Log.d(CommonUtils.class.getSimpleName(),"---> IS NET AVAILABLE error :"+e.getLocalizedMessage());
            e.printStackTrace(); }
        catch (InterruptedException e) {
            Log.d(CommonUtils.class.getSimpleName(),"---> IS NET AVAILABLE error :"+e.getLocalizedMessage());
            e.printStackTrace(); }

        return false;

//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (null != connectivityManager) {
//            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
//        }
//
//        return false;
    }

    //Show Toast Method
    public static void showToast(final String message, final Context context) {
        ApplicationThread.uiPost(LOG_TAG, "toast", new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static boolean isBlueToothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Deleting a particular directory from sdcard
     *
     * @param path ---> file path
     * @return ---> true is successfully deleted else false
     */
    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) deleteDirectory(files[i]);
                else files[i].delete();
            }
        }
        return true;
    }

    //Hides Keyboard
    public static void hideKeyPad(Context context, EditText editField) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editField.getWindowToken(), 0);
    }

    /**
     * Checking SD card is available or not in mobile
     *
     * @param context
     * @return --> true if available else false
     */
    public static boolean isSDcardAvailable(Context context) {
        boolean isAvailable = false;
        boolean available = Environment.getExternalStorageState().toString().equalsIgnoreCase(Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().toString().equalsIgnoreCase(Environment.MEDIA_CHECKING) ||
                Environment.getExternalStorageState().toString().equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY);
        if (!available) {
            isAvailable = false;
        } else {
            isAvailable = true;
        }
        return isAvailable;
    }

    public static boolean isValidMobile(String phone2, EditText tv) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", "  ")) {
            if (phone2.length() < 10 || phone2.length() > 10) {
                check = false;
                tv.setError(Html.fromHtml("<font color='red'>" + "Please specify a valid contact number" + "</font>"));
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    /**
     * Checking a string contains an integer or not
     *
     * @param s --> Input string
     * @return true is string contains number else false.
     */
    public static boolean isInteger(final String s) {
        return Pattern.matches("^\\d*$", s);
    }

    /**
     * Getting the device id
     *
//     * @param context --> Current context
     * @return --> Device IMEI number as string
     */
/*    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String strDeviceIMEI = telephonyManager.getDeviceId();
        if (strDeviceIMEI != null) {
            return strDeviceIMEI;
        } else {
            return "";
        }
    }*/

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    public static String encodeFileToBase64Binary(File file)
            throws IOException {

        byte[] bytes = loadFile(file);

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * Getting the device information(android version, Device model,Device manufacturer)
     *
     * @return ---> Device information in a string formate
     */
    public static String getDeviceExtraInfo() {
        return "" + Build.VERSION.RELEASE + "/n" + Build.MANUFACTURER + " - " + Build.MODEL + "/n";
    }

    /**
     * Getting the current date and time with comma separated.
     *
     * @return if both are required returns date and time with comma separated else if only time required returns time else only date else default date and time.
     */

    public static String getcurrentDateTime(final String format) {
        Calendar c = Calendar.getInstance();
        String date;
        SimpleDateFormat Objdateformat = new SimpleDateFormat(format, Locale.US);
        date = Objdateformat.format(c.getTime());
        return date;
    }

    //Get Current Data & Time
    public static String getCollectionCurrentDateTime(final String format) {
        Calendar c = Calendar.getInstance();
        String date;
    SimpleDateFormat Objdateformat = new SimpleDateFormat(format, Locale.US);
        date = Objdateformat.format(c.getTime());
        return date;
    }

    /**
     * Replacing empty spaces with %20
     *
     * @param url ---> Input url
     * @return ----> encoded url
     */
    public static String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getRealPathFromURI(Uri uri, Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static void getAddressFromLocation(final Context context, final double latitude, final double longitude, ApplicationThread.OnComplete onComplete) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            String result = null;
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLocality()).append("\n");
                result = sb.toString();
                onComplete.execute(true, result, null);
            }
        } catch (IOException e) {
            Log.e("", "Unable connect to Geocoder", e);
            onComplete.execute(false, null, null);
        }
    }

    public static boolean spinnerSelect(Spinner spinner, int position, Context context) {
        if (position == 0 || position <= 0) {
            Toast.makeText(context, "Please select " + spinner, Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    public static boolean spinnerPositinCondition(String spinner, int minimum, int maximum, Context context) {
        if (minimum >= maximum) {
            Toast.makeText(context, "Please select " + spinner, Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    public static String[] getGenericArrayValues(String type, int size) {
        String[] items = new String[size + 1];
        items[0] = "Select";
        for (int i = 1; i <= size; i++) {

            if (type.length() == 0) {
                if (i == size)
                    items[i] = "" + (i - 1) + "+";
                else {
                    items[i] = "" + (i - 1);
                }

            } else {
                items[i] = "" + ((i - 1) + 1980);
            }
        }
        return items;
    }

    public static String[] getGenericAfterArrayValues(int startSize, int size) {
        String[] items = new String[size - startSize];
        Log.i("", "Item size: " + items.length);
        for (int i = 0; i <= size - 1; i++) {

            if (i >= startSize) {

                if (i == size - 1)
                    items[i - startSize] = "" + (i) + "+";
                else {
                    items[i - startSize] = "" + (i);
                }

            } else {
//                items[i]="Extra";
            }
        }
        return items;
    }

    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    //Maps Data
    public static String[] fromMap(LinkedHashMap<String, String> inputMap, String type) {
        Collection c = inputMap.values();
        Iterator itr = c.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
        toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        return toMap;
    }

    //Makes an Array
    public static String[] arrayFromPair(LinkedHashMap<String, Pair> inputMap, String type) {
        Collection c = inputMap.values();
        Iterator itr = c.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
        toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            Pair valuePair = (Pair) itr.next();
            toMap[iCount] = valuePair.second.toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            Pair valuePair = (Pair) itr.next();
            toMap[iCount] = valuePair.second.toString();
            iCount++;
        }
        return toMap;
    }

    public static double getPercentage(double n, double total) {
        double proportion;
        proportion = ((n - total) / (n + total)) * 100;
        return Math.abs(Math.round(proportion));
    }

    public static String[] removeDuplicates(LinkedHashMap<String, String> inputMap, String type) {
        Collection c = inputMap.values();
        Iterator itr = c.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
        toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itr.next().toString();
            iCount++;
        }

        List<String> stringList = new ArrayList<String>();
        for (int i = 0; i < toMap.length; i++) {
            if (!stringList.contains(toMap[i].toString()))
                stringList.add(toMap[i].toString());
        }
        toMap = stringList.toArray(new String[stringList.size()]);

        return toMap;
    }

    public static String[] fromMapAll(LinkedHashMap<String, String> inputMap, String type) {
        Collection c = inputMap.values();
        Collection cc = inputMap.keySet();
        Iterator itr = c.iterator();
        Iterator itrcc = cc.iterator();
        int size = inputMap.size() + 1;
        String[] toMap = new String[size];
        toMap[0] = "-- Select " + type + " --";
        int iCount = 1;
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itrcc.next().toString() + "-" + itr.next().toString();
            iCount++;
        }
        while (iCount < size && itr.hasNext()) {
            toMap[iCount] = itrcc.next().toString() + "-" + itr.next().toString();
            iCount++;
        }

        return toMap;
    }

    public static String formattedTime() {
        DateFormat stringTime = new SimpleDateFormat("hh:mm a");
        Date currentDate = new Date(System.currentTimeMillis());
        return stringTime.format(currentDate);
    }

//    public  static  String[] sortStringArray(String[] sort){
//
//        String[] jobType_array = sort;
//        Arrays.sort(jobType_array);
//        jobType_array[0] = "Select";
//
//        return  jobType_array;
//
//    }

    //Parse to Integer
    public static int parseIntValue(String inputValue) {
        if (!TextUtils.isEmpty(inputValue) && TextUtils.isDigitsOnly(inputValue)) {
            try {
                return Integer.parseInt(inputValue);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return 0;
    }

    public static String convertToDays(String inputStr) {
        int resultInt = 0;
        String resultStr = inputStr.replaceAll("\\D+", "");
        if (inputStr.contains("Week") || inputStr.contains("Weeks")) {
            resultInt = parseIntValue(resultStr) * 7;
            resultStr = String.valueOf(resultInt);
        } else if (inputStr.contains("Month") || inputStr.contains("Months")) {
            resultInt = parseIntValue(resultStr) * 30;
            resultStr = String.valueOf(resultInt);
        }
        return resultStr;
    }

    public static String convertToWeeks(String inputStr) {

        if (inputStr.equals("7")) {
            inputStr = "1 Week";
        } else if (inputStr.contains("15")) {
            inputStr = "15 Days";
        } else if (inputStr.contains("21")) {
            inputStr = "3 Weeks";
        } else if (inputStr.contains("30")) {
            inputStr = "1 Month";
        } else if (inputStr.contains("60")) {
            inputStr = "2 Months";
        } else if (inputStr.contains("90")) {
            inputStr = "3 Months";
        } else {
            inputStr = "0 Day";
        }
        return inputStr;
    }

    public static String getCleanDate(String dateToFormate) {
        if (!dateToFormate.equalsIgnoreCase("")) {
            long dateValue = Long.parseLong(dateToFormate.replaceAll("[^0-9]", ""));
            Date date = new Date(dateValue);
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
            String dateText = df2.format(date);
            return dateText;
        }
        return dateToFormate;
    }

    public static int getIndex(Set<? extends String> set, String value) {
        int result = 0;
        for (String entry : set) {
            Log.d("", "entry value" + entry + "   " + value);
            if (entry.equalsIgnoreCase(value)) {
                return result;
            }
            result++;
        }
        return -1;
    }

    public static int getIndexFromValue(Collection<String> set, String value) {
        int result = 0;
        for (String entry : set) {
            Log.d("", "entry value" + entry + "   " + value);
            if (entry.equals(value)) {
                return result;
            }
            result++;
        }
        return -1;
    }

    public static int getIndexFromArray(String[] arrayValues, String value) {
        int result = 0;
        for (String entry : arrayValues) {
            Log.d("", "entry value" + entry + "   " + value);
            if (entry.equalsIgnoreCase(value)) {
                return result;
            }
            result++;
        }
        return -1;
    }

    public static String getKeyFromValue(LinkedHashMap<String, String> map, String value) {
        String toReturnvalue = "";
        for (Map.Entry entry : map.entrySet()) {
            if (value.equalsIgnoreCase(entry.getValue().toString())) {
                toReturnvalue = (String) entry.getKey();
                break; //breaking because its one to one map
            }
        }
        return toReturnvalue;
    }

    public static String removeLastComma(String str) {
        if (str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static String replaceNewLines(String inputStr) {

        if (null != inputStr && inputStr.length() > 0) {
            return inputStr.replaceAll("\\\\n", "");
        }
        return "";
    }

    /**
     * Converting given string into date format
     *
     * @param dateStr          ---> Given date
     * @param _dateFormatteStr ----> Given date format
     * @return ----> converted date   (if any exception occurred returns null)
     */
    public static Date convertStringToDate(String dateStr, String _dateFormatteStr) {
        Date _convertedDate = null;
        SimpleDateFormat _dateFormate = new SimpleDateFormat("" + _dateFormatteStr);
        try {
            _convertedDate = _dateFormate.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return _convertedDate;
    }

    public static boolean checkOnlyPalmDataExistance(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        if (CommonConstants.screenFrom.equalsIgnoreCase("addReg")) {
            if (dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().marketSurveyDataCheck(CommonConstants.FARMER_CODE))
                    || dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().getPlotExistanceInAnyPalmDetailsQuery(CommonConstants.PLOT_CODE))) {
                return true;
            }
        } else if (dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().getPlotExistanceInAnyPalmDetailsQuery(CommonConstants.PLOT_CODE))) {
            return true;
        }

        return false;
    }

    //Generates a Serial Number
    public static String serialNumber(int number, int stringLength) {
        int numberOfDigits = String.valueOf(number).length();
        int numberOfLeadingZeroes = stringLength - numberOfDigits;
        StringBuilder sb = new StringBuilder();
        if (numberOfLeadingZeroes > 0) {
            for (int i = 0; i < numberOfLeadingZeroes; i++) {
                sb.append("0");
            }
        }
        sb.append(number);
        return sb.toString();
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    //We are calling this method to check the permission status
    public static boolean isPermissionAllowed(final Context context, final String permission) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(context, permission);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //To check if all permissions are allowed
    public static boolean areAllPermissionsAllowed(final Context context, final String[] permissions) {
        boolean isAllPermissionsGranted = false;
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                isAllPermissionsGranted = true;
            }
        }
        return isAllPermissionsGranted;
    }

    //Requesting permission
    public static void requestPermission(final FragmentActivity context, final String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
    }
    //And finally ask for the permission
        ActivityCompat.requestPermissions(context, new String[]{permission}, PERMISSION_CODE);
    }

    //For TabletID
    public static String getIMEInumberID(final Context context){

        String deviceId;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = mTelephony.getDeviceId();
        }

         //return deviceId;

       return "351558072968326";//Roja
       //return "351558072736715";//Live Rajshekar 205
       //return "351558072434071";//Nikhil
  //     return "351558072360896";//Arun
       //return "6a37ea2216b3d210";//Annavaram Ratala client testing user
       //return "c4d79bf3ed309943";//Collectionwithoutplot not synced
   //  return telephonyManager.getDeviceId();

    }

    //Validation for Empty Spinner
    public static boolean isEmptySpinner(final Spinner inputSpinner) {
        if (null == inputSpinner) return true;
        if (inputSpinner.getSelectedItemPosition() == -1 || inputSpinner.getSelectedItemPosition() == 0) {
            return true;
        }
        return false;
    }

    public static byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    //Calculates the Distance
    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K' || unit == 'k') {
            dist = dist * 1.609344;
        } else if (unit == 'N' || unit == 'n') {
            dist = dist * 0.8684;
        } else if (unit == 'M' || unit == 'm') {
            dist = dist * 1609.344;
        }
        return Double.parseDouble(twoDForm.format(dist));
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }



    public static void hideSoftKeyboard(final AppCompatActivity appCompatActivity) {
        if (appCompatActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) appCompatActivity.getSystemService(appCompatActivity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(appCompatActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    //To get the App Version
    public static String getAppVersion(final Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "version error " + e.getMessage());
        }
        return pInfo.versionName;
    }

    public static double getOnlyTwoDecimals(final Double inputValue) {
        return Math.round(100 * (inputValue)) / (double) 100;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }


    //Maps the Data
    public static LinkedHashMap<String, Object> toMap(JSONObject object) throws JSONException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    //Json Array to List
    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static byte[] base64ToByte(String imageString) throws Exception {
        return Base64.decode(imageString, Base64.DEFAULT);
    }

    public static List<File> deleteImages(File root, List<File> fileList) {
        File listFile[] = root.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory() && (listFile[i].getName().equalsIgnoreCase("DCIM") || listFile[i].getName().equalsIgnoreCase("Camera"))) {
                    fileList.add(listFile[i]);
                    deleteImages(listFile[i], fileList);
                } else {
                    if (listFile[i].getName().endsWith(".png")
                            || listFile[i].getName().endsWith(".jpg")
                            || listFile[i].getName().endsWith(".jpeg")
                            || listFile[i].getName().endsWith(".gif")) {
                        fileList.add(listFile[i]);
                        listFile[i].delete();
                    }
                }


            }
        }
        return fileList;
    }

    //Copies the File
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    //Coverts file to gzip File

    public static void gzipFile(File sourceFile, String destinaton_zip_filepath, final ApplicationThread.OnComplete<String> onComplete) {

        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(destinaton_zip_filepath);

            GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);

            FileInputStream fileInput = new FileInputStream(sourceFile);

            int bytes_read;

            while ((bytes_read = fileInput.read(buffer)) > 0) {
                gzipOuputStream.write(buffer, 0, bytes_read);
            }

            fileInput.close();

            gzipOuputStream.finish();
            gzipOuputStream.close();

            System.out.println("The file was compressed successfully!");
            onComplete.execute(true, "success", "success");
        } catch (IOException ex) {
            ex.printStackTrace();
            onComplete.execute(false, "failed", "failed");
        }
    }

    public static void createZipFile(final String path, final File file, final ApplicationThread.OnComplete<String> onComplete) {
        try {
            int BUFFER = 2048;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(path);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            Log.v("Compress", "Adding: " + file);
            FileInputStream fi = new FileInputStream(file);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(file.getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            out.close();
            onComplete.execute(true, "success", "success");
        } catch (Exception e) {
            e.printStackTrace();
            onComplete.execute(false, "failed", "failed");
        }
    }

    public static void profilePic(final FragmentActivity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, FROM_CAMERA);
    }

    public static void deleteInCompleteData(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        LinkedHashMap<String, List<String>> inCompleteRecords = CommonUtils.getZombiRecords(dataAccessHandler);
        Log.v(LOG_TAG, "@@@@ incomplete data count " + CommonUtils.getZombiRecords(dataAccessHandler).size());
        if (null != inCompleteRecords && !inCompleteRecords.isEmpty()) {
            Set<String> tableNames = inCompleteRecords.keySet();
            for (String tableName : tableNames) {
                List<String> dataList = inCompleteRecords.get(tableName);
                String plotIds = TextUtils.join(",", dataList);
                if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_MARKETSURVEYDETAILS)) {
                    String deleteQuery = String.format(Queries.getInstance().deleteInCompleteMarketSurveyData(), tableName, plotIds);
                    Log.v(LOG_TAG, "@@@ farmercode ids to delete " + plotIds);
                    Log.v(LOG_TAG, "@@@ farmercode ids to delete query " + deleteQuery);
                    dataAccessHandler.executeRawQuery(deleteQuery);
                } else {
                    String deleteQuery = String.format(Queries.getInstance().deleteInCompleteData(), tableName, plotIds);
                    Log.v(LOG_TAG, "@@@ plot ids to delete " + plotIds);
                    Log.v(LOG_TAG, "@@@ plot ids to delete query " + deleteQuery);
                    dataAccessHandler.executeRawQuery(deleteQuery);
                }
            }
        }

    }

    public static LinkedHashMap<String, List<String>> getZombiRecords(final DataAccessHandler dataAccessHandler) {
        LinkedHashMap<String, List<String>> zombiRecord = new LinkedHashMap<>();
        List<String> transactionsData = DatabaseKeys.transactionTables;
        transactionsData.add(DatabaseKeys.TABLE_PICTURE_REPORTING);
        Log.v(LOG_TAG, "@@@ tables list to delete " + transactionsData);
        for (String tableName : transactionsData) {
            if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_MARKETSURVEYDETAILS)) {
                List<String> zombiData = dataAccessHandler.getZombiData(String.format(Queries.getInstance().queryToGetIncompleteMarketSurveyData()));
                if (null != zombiData && !zombiData.isEmpty()) {
                    zombiRecord.put(tableName, zombiData);
                }
            } else {
                List<String> zombiData = dataAccessHandler.getZombiData(String.format(Queries.getInstance().queryToFindJunkData(), tableName));
                if (null != zombiData && !zombiData.isEmpty()) {
                    zombiRecord.put(tableName, zombiData);
                }
            }
        }
        return zombiRecord;
    }


//Copies File from the Path
    public static void copyFile(final Context context) {
        try {
            String dataDir = context.getApplicationInfo().dataDir;

            final String dbfile = "/sdcard/3f_" + CommonConstants.TAB_ID + "_" + System.nanoTime();

            File dir = new File(dataDir + "/databases");
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().equals("3foilpalm.sqlite")) {
                    try {
                        copy(file, new File(dbfile));
                    } catch (Exception e) {
                        android.util.Log.e(LOG_TAG, "", e);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            android.util.Log.w("Settings Backup", e);
        }

    }

    public static boolean isLocationPermissionGranted(final Context context) {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= 29
                || (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
                && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED));
    }

    //Starts Location Service
    public static void startLocationService(Context context, BroadcastReceiver mLbsMessageReceiver) {
        if (mLbsMessageReceiver != null) {
            LocalBroadcastManager.getInstance(context).registerReceiver(mLbsMessageReceiver, new IntentFilter(LocationService.ACTION_LOCATION_UPDATED));
        }
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(LocationService.ACTION_START);
        context.startService(intent);
    }

    //To get Address By Location
    public static void getAddressByLocation(final Context context, final double latitude, final double longitude, final boolean onlyVillage, final ApplicationThread.OnComplete onComplete) {
        Log.d(LOG_TAG, "### in getAddressByLocation");
        ApplicationThread.nuiPost(LOG_TAG, "get zipcode", new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder coder = new Geocoder(context, Locale.getDefault());
                    List<Address> addrList = coder.getFromLocation(latitude, longitude, 1);
                    if (!addrList.isEmpty()) {
                        Address addr = addrList.get(0);
                        String countryCode = addr.getCountryCode();
                        String postalCode = addr.getPostalCode();
                        String locality = addr.getLocality();
                        String area = addr.getAdminArea();

                        String add1 = addr.getAddressLine(0);
                        String add2 = addr.getAddressLine(1);

                        StringBuilder addressBuilder = new StringBuilder();
                        if (!TextUtils.isEmpty(add1)) {
                            addressBuilder.append("House/Door No: " + add1);
                        }

                        if (!TextUtils.isEmpty(add2)) {
                            addressBuilder.append(", \n");
                            addressBuilder.append(add2);
                        }

                        addressBuilder.append(", \n");
                        addressBuilder.append(area);
                        addressBuilder.append(", \n");
                        addressBuilder.append(locality);
                        addressBuilder.append(", \n");
                        addressBuilder.append("Pincode: " + postalCode);


                        Log.d(LOG_TAG, "### def zipcode:" + postalCode + "/" + countryCode + "/" + area + "/" + locality);
                        if (onlyVillage) {
//                            CollectionCenterHomeScreen.receivedAddr = true;
                            onComplete.execute(true, addr.getLocality() + "-" + addr.getSubLocality(), addressBuilder.toString());
                        } else {
                            onComplete.execute(true, postalCode, addressBuilder.toString());
                        }

                    } else {
                        onComplete.execute(false, null, null);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Fail to lookup zipcode");
                    onComplete.execute(false, null, null);
                }
            }
        });
    }

    //Stops Location Service
    public static void stopLocationService(Context context, BroadcastReceiver mLbsMessageReceiver) {
        if (mLbsMessageReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mLbsMessageReceiver);
        }

        if (isServiceRunning(context, LocationService.class)) {
            Intent intent = new Intent(context, LocationService.class);
            context.stopService(intent);
        }
    }

    //To check whether the service is running or not
    public static boolean isServiceRunning(Context context, Class<?> clazz) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (clazz.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Validate the Double Values
    public static boolean isValidDouble(String doubleValue) {
        if (!TextUtils.isEmpty(doubleValue) && doubleValue.length() > 0 && !doubleValue.equalsIgnoreCase(".")) {
            return true;
        }
        return false;
    }

    public static void takeScreenShot(final FragmentActivity activity) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = activity.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            openScreenshot(imageFile, activity);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    public static void openScreenshot(File imageFile, final FragmentActivity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        activity.startActivity(intent);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Converting given date into required format
     *
     * @param _date          --> Date to convert
     * @param requiredFormat ----> Format required
     * @return final date string
     */
    public String convertDateToString(Date _date, String requiredFormat) {
        String _finalDateStr = "";
        SimpleDateFormat _dateFormatter = new SimpleDateFormat(requiredFormat);
        try {
            _finalDateStr = _dateFormatter.format(_date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _finalDateStr;
    }

    public void getActivityName(final ApplicationThread.OnComplete oncomplete, final Context context) {
        ApplicationThread.bgndPost("", "Registering Device...", new Runnable() {
            @Override
            public void run() {
                String[] activePackages;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                    activePackages = getActivePackages(context);
                } else {
                    activePackages = getActivePackagesCompat(context);
                }
                if (activePackages != null) {
                    for (String activePackage : activePackages) {
                        if (activePackage.equals("com.google.android.calendar")) {
                            //Calendar app is launched, do something
                        }
                    }
                }
            }
        });
    }

    public String[] getActivePackagesCompat(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        final ComponentName componentName = taskInfo.get(0).topActivity;
        final String[] activePackages = new String[1];
        activePackages[0] = componentName.getPackageName();
        return activePackages;
    }

//    public void createPDF(String content, String fileName) {
//        Document doc = new Document();
//        String outpath = Environment.getExternalStorageDirectory() + "/" +fileName;
//        try {
//            PdfWriter.getInstance(doc, new FileOutputStream(outpath));
//            doc.open();
//            doc.add(new Paragraph(content));
//            doc.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//    }

    public String[] getActivePackages(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final Set<String> activePackages = new HashSet<>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }

    private String formatDouble(double d) {
        if (d % 1.0 == 0) {
            return new DecimalFormat("#").format(d);
        } else {
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.FLOOR);
            return df.format(d);
        }
    }

    //Binds the Image of the Farmer
    public static String getImageUrl(final BasicFarmerDetails basicFarmerDetails) {
        String url = Config.image_url + basicFarmerDetails.getPhotoLocation() + "/" + basicFarmerDetails.getPhotoName() +basicFarmerDetails.getFileExtension();
        return url.replace('\\', '/');
    }

    public static String getBase64String(final Bitmap bitmap) {
        String base64string = "";
        try {
            //calculate how many bytes our image consists of.
            int bytes = bitmap.getByteCount();
            //or we can calculate bytes this way. Use a different value than 4 if you don't use 32bit images.
            //int bytes = b.getWidth()*b.getHeight()*4;
            ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
            bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
            byte[] array = buffer.array(); //Get the underlying array containing the data.
            base64string = Base64.encodeToString(array, Base64.DEFAULT);
        } catch (Exception e) {
            base64string = "";
        }
        return base64string;
    }


    //Avoids Duplicates in ArrayList
    public static List<String> ignoreDuplicatedInArrayList(List<String> inputList) {
        if (null != inputList && !inputList.isEmpty()) {
            Set<String> hs = new HashSet<>();
            hs.addAll(inputList);
            inputList.clear();
            inputList.addAll(hs);
        }
        if (null == inputList) {
            inputList = new ArrayList<>();
        }
        return inputList;
    }

}

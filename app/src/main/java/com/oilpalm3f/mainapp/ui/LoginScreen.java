package com.oilpalm3f.mainapp.ui;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oilpalm3f.mainapp.FaLogTracking.FalogService;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.CollectionCenterHomeScreen;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Palm3FoilDatabase;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.RefreshSyncActivity;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.dbmodels.UserDetails;
import com.oilpalm3f.mainapp.dbmodels.UserSync;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;
import com.oilpalm3f.mainapp.utils.SharedPrefManager;
import com.oilpalm3f.mainapp.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.USER_DETAILS;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.USER_VILLAGES;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;


//Login Screen
public class LoginScreen extends AppCompatActivity {

    public static final String LOG_TAG = LoginScreen.class.getName();

    private EditText userID;
    private EditText passwordEdit;
    private Button signInBtn;
    private String userId;
    private String password;
    private TextView imeiNumberTxt, versionnumbertxt, dbVersionTxt;
    FloatingActionButton sync;
    DataAccessHandler dataAccessHandler;
    LocationManager lm;


    //Initializing the Class & UI
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        this.signInBtn = (Button) findViewById(R.id.signInBtn);
        this.passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        this.userID = (EditText) findViewById(R.id.userID);
        this.sync=(FloatingActionButton) findViewById(R.id.sync);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.login_screen);
        setSupportActionBar(toolbar);
        CommonUtils.currentActivity = this;

        SharedPrefManager.set_isDataSyncRunning(false);

        versionnumbertxt  = (TextView) findViewById(R.id.versionnumbertxt);
        imeiNumberTxt = (TextView) findViewById(R.id.imeiNumberTxt);
        dbVersionTxt = (TextView) findViewById(R.id.dbVersiontxt);
        imeiNumberTxt.setText(CommonUtils.getIMEInumberID(this));
        versionnumbertxt.setText(CommonUtils.getAppVersion(this));
        dbVersionTxt.setText(""+Palm3FoilDatabase.DATA_VERSION);

        dataAccessHandler = new DataAccessHandler(LoginScreen.this);


        //Getting User Details
        final UserDetails userDetails = dataAccessHandler.getUserDetails(CommonUtils.getIMEInumberID(this));

        if (null != userDetails) {
            userID.setText(userDetails.getUserName());
            passwordEdit.setText(userDetails.getPassword());


            List userVillages = dataAccessHandler.getSingleListData(Queries.getInstance().getUserVillages(userDetails.getId()));
            DataManager.getInstance().addData(USER_DETAILS, userDetails);
            if (!userVillages.isEmpty()) {
                DataManager.getInstance().addData(USER_VILLAGES, userVillages);
            }
            CommonConstants.USER_ID = userDetails.getId();
            CommonConstants.USER_RoleID = userDetails.getRoleId();
            //Generate Tablet Id
            CommonConstants.TABLET_ID = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTabIDNew(CommonUtils.getIMEInumberID(LoginScreen.this)));
            CommonConstants.TAB_ID = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTabId(CommonUtils.getIMEInumberID(LoginScreen.this)));

            Log.d("TABLET_ID", CommonConstants.TABLET_ID + "");
            Log.d("TAB_ID",  CommonConstants.TAB_ID + "");
            Log.d("USER_RoleID",  CommonConstants.USER_RoleID + "");


            imeiNumberTxt.setText(CommonUtils.getIMEInumberID(this)+" ("+CommonConstants.TAB_ID+")");


            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startService(new Intent(this, FalogService.class));
            }else {

            }


        } else {
            UiUtils.showCustomToastMessage("User not existed", LoginScreen.this, 1);
        }

        masterSync();
        transactionSync();

        //Sign In Button On Click Listener
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userId = userID.getText().toString();
                password = passwordEdit.getText().toString();
                if (validateField()) {
                    CommonUtils.hideKeyPad(LoginScreen.this, passwordEdit);
                    if (null != userDetails) {
                        startActivity(new Intent(LoginScreen.this, CollectionCenterHomeScreen.class));
                        finish();
                    } else {
                        UiUtils.showCustomToastMessage("User not existed", LoginScreen.this, 1);
                    }
                }
            }
        });


        //Master Sync Button On Click Listerner
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                masterSync();
            }
        });


      }

      //Perform Master Sync Method
   public void masterSync(){

       if (CommonUtils.isNetworkAvailable(LoginScreen.this)) {
           DataSyncHelper.performMasterSync(this, false, new ApplicationThread.OnComplete() {
               @Override
               public void execute(boolean success, Object result, String msg) {
                   ProgressBar.hideProgressBar();
                   if (success) {
                       palm3FoilDatabase.insertErrorLogs(LOG_TAG,"masterSync", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                       if (!msg.equalsIgnoreCase("Sync is up-to-date")) {
                           UiUtils.showCustomToastMessage("Data synced successfully", LoginScreen.this, 0);
                           palm3FoilDatabase.insertErrorLogs(LOG_TAG,"masterSync",CommonConstants.TAB_ID,"result.toString()",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                           //palm3FoilDatabase.insertErrorLogs(LOG_TAG,"masterSync","result.toString()",msg);
                           //Toast.makeText(LoginScreen.this, "Data synced successfully", Toast.LENGTH_SHORT).show();
                           List<UserSync> userSyncList;
                          // userSyncList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfMasterSync());
                           userSyncList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfSync(CommonConstants.USER_ID));

                           if(userSyncList.size()==0){
                               Log.v("@@@MM","mas");

                               if(Integer.parseInt(CommonConstants.USER_ID) != 12345)
                               {
                                   addUserMasSyncDetails();

                               }
                           }
                           else {
                               dataAccessHandler.updateMasterSync();
                           }


                       } else {
                           ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(LoginScreen.this, "You have updated data", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                   } else {
                       Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                       //palm3FoilDatabase.insertErrorLogs(LOG_TAG,"masterSync","result.toString()",msg);
                       palm3FoilDatabase.insertErrorLogs(LOG_TAG,"masterSync",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                       ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(LoginScreen.this, "Master sync failed. Please try again", Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               }
           });
       } else {
           UiUtils.showCustomToastMessage("Please check network connection", LoginScreen.this, 1);
       }


   }

   //Perform Transaction Sync
   public void transactionSync(){
       if (CommonUtils.isNetworkAvailable(LoginScreen.this)) {
           CommonUtils.isNotSyncScreen = true;
           DataSyncHelper.performCollectionCenterTransactionsSync(LoginScreen.this, new ApplicationThread.OnComplete() {
               @Override
               public void execute(boolean success, final Object result, String msg) {
                   if (success) {
                       palm3FoilDatabase.insertErrorLogs(LOG_TAG,"transactionSync", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                       ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                           @Override
                           public void run() {
                               CommonUtils.isNotSyncScreen = false;
                               UiUtils.showCustomToastMessage("Successfully data sent to server", LoginScreen.this, 0);
                               // dataAccessHandler.updateUserSync();

                           }
                       });
                   } else {
                       palm3FoilDatabase.insertErrorLogs(LOG_TAG,"transactionSync", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                       ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                           @Override
                           public void run() {
                               CommonUtils.isNotSyncScreen = false;
                               UiUtils.showCustomToastMessage("Sync Failed due to"+result, LoginScreen.this, 1);
                           }
                       });
                   }
               }

           });

       } else {
           UiUtils.showCustomToastMessage("Please check network connection", LoginScreen.this, 1);
       }


   }

   //Insert Usersync Details for Master Sync
    public void addUserMasSyncDetails(){
        UserSync userSync;

        SimpleDateFormat simpledatefrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = simpledatefrmt.format(new Date());

        userSync = new UserSync();
        userSync.setUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setApp(""+"3fCropCollection");
        userSync.setDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setMasterSync(1);
        userSync.setTransactionSync(0);
        userSync.setResetData(0);
        userSync.setIsActive(1);
        userSync.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setServerUpdatedStatus(0);
        long resul=  dataAccessHandler.addUserSync(userSync);
        if(resul>-1){
            Log.v("@@@MM","Success");
        }

    }

    //Validations
    private boolean validateField() {
        if (TextUtils.isEmpty(userId)) {
            Toasty.error(this, "Please enter user id", Toast.LENGTH_SHORT).show();
            userID.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toasty.error(this, "Please enter password", Toast.LENGTH_SHORT).show();
            passwordEdit.requestFocus();
            return false;
        }
        return true;
    }

}

package com.oilpalm3f.mainapp.weighbridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.common.CommonConstants;

public class SettingsActivity extends AppCompatActivity {
    EditText baaudrateT, databindsT;
    Spinner stopBindsT, paarityT;
    Button btn;
    String[] stopbits = {"stopbits", "StopBits.OnePointFive", "StopBits.None", "StopBits.One", "StopBits.Two"};
    String[] paarityValues = {"parity", "Parity.Even", "Parity.Mark", "Parity.None", "Parity.Odd", "Parity.Space"};
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("collection", AppCompatActivity.MODE_PRIVATE);

        baaudrateT = findViewById(R.id.baaudrateT);
        databindsT = findViewById(R.id.databindsT);
        stopBindsT = findViewById(R.id.stopBindsT);
        paarityT = findViewById(R.id.paarityT);
        btn = findViewById(R.id.btn);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stopbits);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopBindsT.setAdapter(aa);

        ArrayAdapter a1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paarityValues);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paarityT.setAdapter(a1);

        stopBindsT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (stopBindsT.getSelectedItemPosition() != 0) {
                    if (stopBindsT.getSelectedItem().toString().equals("StopBits.OnePointFive")) {
                        CommonConstants.stopbits = 3;
                        Log.v("@@@","stopbits"+CommonConstants.stopbits);


                    } else if (stopBindsT.getSelectedItem().toString().equals("StopBits.None")) {
                        CommonConstants.stopbits = 0;
                        Log.v("@@@","stopbits"+CommonConstants.stopbits);

                    } else if (stopBindsT.getSelectedItem().toString().equals("StopBits.One")) {
                        CommonConstants.stopbits = 1;
                        Log.v("@@@","stopbits"+CommonConstants.stopbits);

                    } else if (stopBindsT.getSelectedItem().toString().equals("StopBits.Two")) {
                        CommonConstants.stopbits = 2;
                        Log.v("@@@","stopbits"+CommonConstants.stopbits);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        paarityT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (paarityT.getSelectedItemPosition() != 0) {
                    if (paarityT.getSelectedItem().toString().equals("Parity.Even")) {
                        CommonConstants.parity = 2;
                        Log.v("@@@","parity"+CommonConstants.parity);
                    } else if (paarityT.getSelectedItem().toString().equals("Parity.Mark")) {
                        CommonConstants.parity = 3;
                        Log.v("@@@","parity"+CommonConstants.parity);

                    } else if (paarityT.getSelectedItem().toString().equals("Parity.None")) {
                        CommonConstants.parity = 0;
                        Log.v("@@@","parity"+CommonConstants.parity);

                    } else if (paarityT.getSelectedItem().toString().equals("Parity.Odd")) {
                        CommonConstants.parity = 1;
                        Log.v("@@@","parity"+CommonConstants.parity);

                    } else if (paarityT.getSelectedItem().toString().equals("Parity.Space")) {
                        CommonConstants.parity = 4;
                        Log.v("@@@","parity"+CommonConstants.parity);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(baaudrateT.getText().toString())) {
                    Toast.makeText(SettingsActivity.this, "Please give Baudrate value", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(databindsT.getText().toString())) {
                    Toast.makeText(SettingsActivity.this, "Please give DataBind value", Toast.LENGTH_LONG).show();

                } else if (paarityT.getSelectedItemPosition() == 0) {
                    Toast.makeText(SettingsActivity.this, "Please select Parity value", Toast.LENGTH_LONG).show();

                } else if (stopBindsT.getSelectedItemPosition() == 0) {
                    Toast.makeText(SettingsActivity.this, "Please select StopBinds value", Toast.LENGTH_LONG).show();

                } else {
                    CommonConstants.baudrate = baaudrateT.getText().toString();
                    Log.v("@@@","baudrate"+CommonConstants.baudrate);

                    CommonConstants.databits = Integer.parseInt(databindsT.getText().toString());
                    Log.v("@@@","databind"+CommonConstants.databits);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String br=String.valueOf(CommonConstants.baudrate);
                    editor.putString("baudrate", br);

                    editor.commit();


                    startActivity(new Intent(SettingsActivity.this, New_WeighbridgeActivity.class));
                }
            }
        });

    }
}
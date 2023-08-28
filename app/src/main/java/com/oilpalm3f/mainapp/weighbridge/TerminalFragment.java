package com.oilpalm3f.mainapp.weighbridge;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.oilpalm3f.mainapp.BuildConfig;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.NewWeighbridgeCC;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.weighbridge.driver.UsbSerialDriver;
import com.oilpalm3f.mainapp.weighbridge.driver.UsbSerialPort;
import com.oilpalm3f.mainapp.weighbridge.driver.UsbSerialProber;
import com.oilpalm3f.mainapp.weighbridge.util.SerialInputOutputManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class TerminalFragment extends Fragment {

    private enum UsbPermission {Unknown, Requested, Granted, Denied}

    ;
    Button asciiBtn, hexaBtn;

    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    private static final int WRITE_WAIT_MILLIS = 2000;
    private static final int READ_WAIT_MILLIS = 2000;

    private int deviceId, portNum, baudRate;
    private boolean withIoManager;
    TextView okText;
    private BroadcastReceiver broadcastReceiver;
    private Handler mainLooper;
    private TextView receiveText;
    private ControlLines controlLines;
    Button nextBtn;
    TextView baaudrateTv,changebr;
    private SerialInputOutputManager usbIoManager;
    private UsbSerialPort usbSerialPort;
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private boolean connected = false;
    Boolean isAscii = true;
    Boolean isReceive_ASCII = false;
    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
        public void onRunError(Exception exc) {
            // Log.d(ChatRoom.TAG, "Runner stopped.");
        }

        public void onNewData(final byte[] bArr) {
            getActivity().runOnUiThread(() -> {
                updateReceivedData1(bArr);
                Log.d("RECEIVED", new String(bArr));
            });
        }
    };
    private SerialInputOutputManager mSerialIoManager;
    public PrefManager prefManager;


    public TerminalFragment() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(INTENT_ACTION_GRANT_USB)) {
                    usbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            ? UsbPermission.Granted : UsbPermission.Denied;
                    connect();
                }
            }
        };
        mainLooper = new Handler(Looper.getMainLooper());
    }

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefManager = new PrefManager(getContext());

        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceId = getArguments().getInt("device");
        portNum = getArguments().getInt("port");
        baudRate = getArguments().getInt("baud");
        withIoManager = getArguments().getBoolean("withIoManager");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));

        if (usbPermission == UsbPermission.Unknown || usbPermission == UsbPermission.Granted)
            mainLooper.post(this::connect);
    }

    @Override
    public void onPause() {
        if (connected) {
            status("disconnected");
            disconnect();
        }
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        TextView sendText = view.findViewById(R.id.send_text);
        View sendBtn = view.findViewById(R.id.send_btn);
        baaudrateTv=view.findViewById(R.id.baaudrate);
        nextBtn = view.findViewById(R.id.nextBtn);
        okText = view.findViewById(R.id.okText);
        changebr=view.findViewById(R.id.changebr);
        baaudrateTv.setText("baud rate : "+CommonConstants.baudrate);
        changebr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =getActivity().getSharedPreferences("collection",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(getContext(),SettingsActivity.class));

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String textViewStr = receiveText.getText().toString();
                if (CommonConstants.baudrate.equals("9600")) {
                    if (textViewStr.contains("±")) {

                        textViewStr = textViewStr.replaceAll("±", "1");
                        Log.v("@@@H", textViewStr);


                    }
                    if (textViewStr.contains("²")) {
                        textViewStr = textViewStr.replaceAll("²", "2");
                        Log.v("@@@H", textViewStr);


                    }
                    if (textViewStr.contains("´")) {

                        textViewStr = textViewStr.replaceAll("´", "4");
                        Log.v("@@@H", textViewStr);
                    }
                    if (textViewStr.contains("·")) {

                        textViewStr = textViewStr.replaceAll("·", "7");
                        Log.v("@@@H", textViewStr);

                    }

                    if (textViewStr.contains("¸")) {

                        textViewStr = textViewStr.replaceAll("¸", "8");
                        Log.v("@@@H", textViewStr);

                    }


                    String data = textViewStr;
                    //Regular expression to digits
                    String regex = "([0-9]+)";


                    //Creating a pattern object
                    Pattern pattern = Pattern.compile(regex);
                    // Pattern pattern = Pattern.compile("(\\d{1})");

                    //Creating a Matcher object
                    Matcher matcher = pattern.matcher(data);
                    while (matcher.find()) {
                        String s = matcher.group();

                        if (!s.equals("0") && !s.equals("00") && !s.equals("40")) {
                            Log.v("@@@K", matcher.group() + " first   ");
                            okText.setText(matcher.group() + " ");
                            if (CommonConstants.flowFrom.equalsIgnoreCase("Gross_Weight")) {
                                NewWeighbridgeCC.gross_weight.setText(okText.getText());
                            }
                            if (CommonConstants.flowFrom.equalsIgnoreCase("Tare_Weight")) {
                                tareWeighCaliculation.tare_weight.setText(okText.getText());

                            }


                        }
                        if (s.length() == 4 || s.length() == 3 || s.length() == 5) {
                            String result = removeLeadingZeroes(matcher.group());
                            Log.v("@@@K", result + " second  ");

                            okText.setText(result + " ");
                            if (CommonConstants.flowFrom.equalsIgnoreCase("Gross_Weight")) {
                                NewWeighbridgeCC.gross_weight.setText(okText.getText());
                            }
                            if (CommonConstants.flowFrom.equalsIgnoreCase("Tare_Weight")) {
                                tareWeighCaliculation.tare_weight.setText(okText.getText());

                            }


                        }


                    }
                } else {

                    String data = textViewStr;
                    //Regular expression to digits
                    String regex = "([0-9]+)";


                    //Creating a pattern object
                    Pattern pattern = Pattern.compile(regex);
                    // Pattern pattern = Pattern.compile("(\\d{1})");

                    //Creating a Matcher object
                    Matcher matcher = pattern.matcher(data);
                    while (matcher.find()) {
                        String s = matcher.group();

                        if (!s.equals("0")) {
                            String result = removeLeadingZeroes(matcher.group());
                            Log.v("@@@K", result + " second  ");

                            okText.setText(result + " ");
                            if (CommonConstants.flowFrom.equalsIgnoreCase("Gross_Weight")) {
                                NewWeighbridgeCC.gross_weight.setText(okText.getText());
                            }
                            if (CommonConstants.flowFrom.equalsIgnoreCase("Tare_Weight")) {
                                tareWeighCaliculation.tare_weight.setText(okText.getText());

                            }
                        }


                    }

                }


            }
        });

        sendBtn.setOnClickListener(v -> send(sendText.getText().toString()));
        View receiveBtn = view.findViewById(R.id.receive_btn);
        controlLines = new ControlLines(view);
        if (withIoManager) {
            receiveBtn.setVisibility(View.GONE);
        } else {
            receiveBtn.setOnClickListener(v -> read());
        }

        setFilter();

        return view;

    }

    public static String removeLeadingZeroes(String str) {
        String strPattern = "^0+(?!$)";
        str = str.replaceAll(strPattern, "");
        return str;
    }


    private void setFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbPermission.Unknown.toString());
        intentFilter.addAction(UsbPermission.Granted.toString());
        intentFilter.addAction(UsbPermission.Denied.toString());
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_terminal, menu);
    }

    public void updateReceivedData() {
        byte[] bArr = receiveText.getText().toString().getBytes();
        //  byte[] bArr = new byte[8192];

        int[] iArr = new int[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            iArr[i] = bArr[i] & 255;
        }
        String str = new String(iArr, 0, bArr.length);
        if (this.isAscii.booleanValue()) {
            if (!this.isReceive_ASCII.booleanValue()) {
                String format = DateFormat.getDateTimeInstance().format(new Date());
                this.isReceive_ASCII = true;
                if (this.receiveText.length() != 0) {
                    TextView textView = this.receiveText;
                    // appendColoredText(textView, "\n[" + format + "] ASCII:\n", -7829368);
                } else {
                    TextView textView2 = this.receiveText;
                    //   appendColoredText(textView2, "[" + format + "] ASCII:\n", -7829368);
                }
            }
            this.receiveText.append(str);
        } else {
            if (this.isReceive_ASCII.booleanValue()) {
                String format2 = DateFormat.getDateTimeInstance().format(new Date());
                this.isReceive_ASCII = false;
                if (this.receiveText.length() != 0) {
                    TextView textView3 = this.receiveText;
                    appendColoredText(textView3, "\n[" + format2 + "] HEX:\n", -7829368);
                } else {
                    TextView textView4 = this.receiveText;
                    appendColoredText(textView4, "[" + format2 + "] HEX:\n", -7829368);
                }
            }
            this.receiveText.append(asciiToHex(str));
        }
        this.receiveText.setFocusable(false);


    }

    public void updateReceivedData1(byte[] bArr) {
        // byte[] bArr = receiveText.getText().toString().getBytes();
        //  byte[] bArr = new byte[8192];
        if (bArr != null) {
            if (bArr.length > 0) {
                int[] iArr = new int[bArr.length];
                for (int i = 0; i < bArr.length; i++) {
                    iArr[i] = bArr[i] & 255;
                }
                String str = new String(iArr, 0, bArr.length);
                if (this.isAscii.booleanValue()) {
                    if (!this.isReceive_ASCII.booleanValue()) {
                        String format = DateFormat.getDateTimeInstance().format(new Date());
                        this.isReceive_ASCII = true;
                        if (this.receiveText.length() != 0) {
                            TextView textView = this.receiveText;
                            appendColoredText(textView, "\n[" + format + "] ASCII:\n", -7829368);
                        } else {
                            TextView textView2 = this.receiveText;
                            appendColoredText(textView2, "[" + format + "] ASCII:\n", -7829368);
                        }
                    }
                    this.receiveText.append(str);
                } else {
                    if (this.isReceive_ASCII.booleanValue()) {
                        String format2 = DateFormat.getDateTimeInstance().format(new Date());
                        this.isReceive_ASCII = false;
                        if (this.receiveText.length() != 0) {
                            TextView textView3 = this.receiveText;
                            appendColoredText(textView3, "\n[" + format2 + "] HEX:\n", -7829368);
                        } else {
                            TextView textView4 = this.receiveText;
                            appendColoredText(textView4, "[" + format2 + "] HEX:\n", -7829368);
                        }
                    }
                    this.receiveText.append(asciiToHex(str));
                }
            }
        }
        this.receiveText.setFocusable(false);
    }


    private static String asciiToHex(String str) {
        char[] charArray = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char hexString : charArray) {
            try {
                String hexString2 = Integer.toHexString(hexString);
                if (hexString2.length() == 1) {
                    sb.append("0");
                    sb.append(hexString2);
                    sb.append(" ");
                } else {
                    sb.append(hexString2);
                    sb.append(" ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void appendColoredText(TextView textView, String str, int i) {
        int length = textView.getText().length();
        textView.append(str);
        ((Spannable) textView.getText()).setSpan(new ForegroundColorSpan(i), length, textView.getText().length(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear) {
            receiveText.setText("");
            return true;
        } else if (id == R.id.send_break) {
            if (!connected) {
                Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    usbSerialPort.setBreak(true);
                    Thread.sleep(100); // should show progress bar instead of blocking UI thread
                    usbSerialPort.setBreak(false);
                    SpannableStringBuilder spn = new SpannableStringBuilder();
                    spn.append("send <break>\n");
                    spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    receiveText.append(spn);
                } catch (UnsupportedOperationException ignored) {
                    Toast.makeText(getActivity(), "BREAK not supported", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "BREAK failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Serial
     */
//    @Override
//    public void onNewData(byte[] data) {
//        mainLooper.post(() -> {
//            //receive(data);
//            updateReceivedData1(data);
//        });
//    }


//    @Override
//    public void onRunError(Exception e) {
//        mainLooper.post(() -> {
//            status("connection lost: " + e.getMessage());
//            disconnect();
//        });
//    }

    /*
     * Serial + UI
     */
    private void connect() {
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        if (usbManager != null) {
            if (usbManager.getDeviceList() != null) {
                for (UsbDevice v : usbManager.getDeviceList().values()) {
                    if (v.getDeviceId() == deviceId)
                        device = v;
                }
                if (device == null) {
                    status("connection failed: device not found");
                    return;
                }
                try {
                    UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
                    if (driver == null) {
                        driver = CustomProber.getCustomProber().probeDevice(device);
                    }
                    if (driver == null) {
                        status("connection failed: no driver for device");
                        return;
                    }
                    if (driver.getPorts().size() < portNum) {
                        status("connection failed: not enough ports at device");
                        return;
                    }
                    usbSerialPort = driver.getPorts().get(portNum);
                    UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());

                    if (usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.getDevice())) {
                        usbPermission = UsbPermission.Requested;
                        PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                        usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
                        return;
                    }

                    if (usbConnection == null) {
                        if (!usbManager.hasPermission(driver.getDevice()))
                            status("connection failed: permission denied");
                        else
                            status("connection failed: open failed");
                        return;
                    }

                    try {
                        usbSerialPort.open(usbConnection);
                        usbSerialPort.setParameters(Integer.parseInt(CommonConstants.baudrate), prefManager.getDataBits(),
                                prefManager.getStopBits(), prefManager.getParity());
                        startIoManager();
//            if (withIoManager) {
//                usbIoManager = new SerialInputOutputManager(usbSerialPort, mListener);
//                Executors.newSingleThreadExecutor().submit(usbIoManager);
//            }
                        //  status("connected");
                        connected = true;
                        controlLines.start();
                    } catch (Exception e) {
                        status("connection failed: " + e.getMessage());
                        disconnect();
                    }
                } catch (Exception e) {
                    status("connection failed: " + e.getMessage());
                    disconnect();
                }
                //  }
            }
        } else {
            disconnect();
        }

    }


    private void disconnect() {
        connected = false;
        controlLines.stop();
        if (usbIoManager != null)
            usbIoManager.stop();
        usbIoManager = null;
        try {
            usbSerialPort.close();
        } catch (IOException ignored) {
        }
        usbSerialPort = null;
    }


    private void startIoManager() {
        if (usbSerialPort != null) {
            Log.i(TAG, "Starting io manager ..");
            this.mSerialIoManager = new SerialInputOutputManager(usbSerialPort, this.mListener);
            Executors.newSingleThreadExecutor().submit(this.mSerialIoManager);
        }
    }

    private void send(String str) {
        if (!connected) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            byte[] data = (str + '\n').getBytes();
            SpannableStringBuilder spn = new SpannableStringBuilder();
            spn.append("send " + data.length + " bytes\n");
            spn.append(HexDump.dumpHexString(data) + "\n");
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);
            usbSerialPort.write(data, WRITE_WAIT_MILLIS);
        } catch (Exception e) {
            // onRunError(e);
        }
    }

    private void read() {
        if (!connected) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            byte[] buffer = new byte[8192];
            int len = usbSerialPort.read(buffer, READ_WAIT_MILLIS);
            receive(Arrays.copyOf(buffer, len));
        } catch (IOException e) {
            // when using read with timeout, USB bulkTransfer returns -1 on timeout _and_ errors
            // like connection loss, so there is typically no exception thrown here on error
            status("connection lost: " + e.getMessage());
            disconnect();
        }
    }

    private void receive(byte[] data) {
        SpannableStringBuilder spn = new SpannableStringBuilder();
        spn.append("receive " + data.length + " bytes\n");
        if (data.length > 0)
            spn.append(HexDump.dumpHexString(data) + "\n");
        receiveText.append(spn);
    }

    public void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    class ControlLines {
        private static final int refreshInterval = 200; // msec

        private Runnable runnable;
        private ToggleButton rtsBtn, ctsBtn, dtrBtn, dsrBtn, cdBtn, riBtn;

        ControlLines(View view) {
            runnable = this::run; // w/o explicit Runnable, a new lambda would be created on each postDelayed, which would not be found again by removeCallbacks

            rtsBtn = view.findViewById(R.id.controlLineRts);
            ctsBtn = view.findViewById(R.id.controlLineCts);
            dtrBtn = view.findViewById(R.id.controlLineDtr);
            dsrBtn = view.findViewById(R.id.controlLineDsr);
            cdBtn = view.findViewById(R.id.controlLineCd);
            riBtn = view.findViewById(R.id.controlLineRi);
            rtsBtn.setOnClickListener(this::toggle);
            dtrBtn.setOnClickListener(this::toggle);
        }

        private void toggle(View v) {
            ToggleButton btn = (ToggleButton) v;
            if (!connected) {
                btn.setChecked(!btn.isChecked());
                Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            String ctrl = "";
            try {
                if (btn.equals(rtsBtn)) {
                    ctrl = "RTS";
                    usbSerialPort.setRTS(btn.isChecked());
                }
                if (btn.equals(dtrBtn)) {
                    ctrl = "DTR";
                    usbSerialPort.setDTR(btn.isChecked());
                }
            } catch (IOException e) {
                status("set" + ctrl + "() failed: " + e.getMessage());
            }
        }

        private void run() {
            if (!connected)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getControlLines();
                rtsBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.RTS));
                ctsBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.CTS));
                dtrBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.DTR));
                dsrBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.DSR));
                cdBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.CD));
                riBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.RI));
                mainLooper.postDelayed(runnable, refreshInterval);
            } catch (IOException e) {
                status("getControlLines() failed: " + e.getMessage() + " -> stopped control line refresh");
            }
        }

        void start() {
            if (!connected)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getSupportedControlLines();
                if (!controlLines.contains(UsbSerialPort.ControlLine.RTS))
                    rtsBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.CTS))
                    ctsBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.DTR))
                    dtrBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.DSR))
                    dsrBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.CD))
                    cdBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.RI))
                    riBtn.setVisibility(View.INVISIBLE);
                run();
            } catch (IOException e) {
                Toast.makeText(getActivity(), "getSupportedControlLines() failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        void stop() {
            mainLooper.removeCallbacks(runnable);
            rtsBtn.setChecked(false);
            ctsBtn.setChecked(false);
            dtrBtn.setChecked(false);
            dsrBtn.setChecked(false);
            cdBtn.setChecked(false);
            riBtn.setChecked(false);
        }
    }
}

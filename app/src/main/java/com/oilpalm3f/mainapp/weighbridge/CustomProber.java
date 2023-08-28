package com.oilpalm3f.mainapp.weighbridge;


import com.oilpalm3f.mainapp.weighbridge.driver.CdcAcmSerialDriver;
import com.oilpalm3f.mainapp.weighbridge.driver.ProbeTable;
import com.oilpalm3f.mainapp.weighbridge.driver.UsbSerialProber;

public class CustomProber {

    public static UsbSerialProber getCustomProber() {
        ProbeTable customTable = new ProbeTable();
        customTable.addProduct(0x16d0, 0x087e, CdcAcmSerialDriver.class); // e.g. Digispark CDC
        return new UsbSerialProber(customTable);
    }

}


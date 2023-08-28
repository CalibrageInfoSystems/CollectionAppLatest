package com.oilpalm3f.mainapp.printer;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.collectioncenter.onPrinterType;
import com.oilpalm3f.mainapp.datasync.RefreshSyncActivity;

/**
 * Created by siva on 07/03/17.
 */

//To choose the Printer Type

public class PrinterChooserFragment  extends DialogFragment {

    public static final int USB_PRINTER = 0;
    public static final int BLUETOOTH_PRINTER = 1;

    private onPrinterType printerType;

    //public  Dialog dialog;

    public void setPrinterType(onPrinterType printerType) {
        this.printerType = printerType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chooser_layout, viewGroup);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        view.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

        RelativeLayout type1 = (RelativeLayout) view.findViewById(R.id.layout_type_1);
        type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                printerType.onPrinterTypeSelected(USB_PRINTER);
            }
        });

        RelativeLayout type2 = (RelativeLayout) view.findViewById(R.id.layout_type_2);
        type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                printerType.onPrinterTypeSelected(BLUETOOTH_PRINTER);
            }
        });

        return view;
    }
}

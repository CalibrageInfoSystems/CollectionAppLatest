package com.oilpalm3f.mainapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.uihelper.ChromeFloatingCirclesDrawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;

import static com.oilpalm3f.mainapp.utils.ImageUtility.calculateInSampleSize;

public class UiUtils {

    public static final String LOG_TAG = UiUtils.class.getName();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
 /*
 * If backgrount type 0(Zero) = Green
 *                    1        = Red*/

    //Shows Custom Toast Message
    public static void showCustomToastMessage(final String message, final Context context, final int backgroundColorType) {
        ApplicationThread.uiPost(LOG_TAG, "show custom toast", new Runnable() {
            @Override
            public void run() {

                if (null == context)
                    return;

                LayoutInflater inflater = LayoutInflater.from(context);
                View toastRoot = inflater.inflate(R.layout.custom_toast, null);
                TextView messageToDisplay = (TextView) toastRoot.findViewById(R.id.toast_message);
                messageToDisplay.setBackground(context.getDrawable(backgroundColorType == 0 ? R.drawable.toast_msg_green : R.drawable.toast_bg));
                messageToDisplay.setText(message);
                Toast toast = new Toast(context);
                // Set layout to toast
                toast.setView(toastRoot);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void saveReceiptAsPdf(final String filePath, final View contentToRender, final ApplicationThread.OnComplete onComplete) {
        ApplicationThread.bgndPost(LOG_TAG, "", new Runnable() {
            @Override
            public void run() {
                String folderToSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/3F_OilPalm_Files/Receipts";

                File filesDirectory = new File(folderToSave);
                if (!filesDirectory.exists()) {
                    filesDirectory.mkdirs();
                }

                PdfDocument document = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    document = new PdfDocument();
                }

                int pageNumber = 1;
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(contentToRender.getWidth(),
                        contentToRender.getHeight(), pageNumber).create();

                PdfDocument.Page page = document.startPage(pageInfo);
                contentToRender.draw(page.getCanvas());

                document.finishPage(page);
                File outputFile = new File(folderToSave + "/" + filePath);
                try {
                    OutputStream out = new FileOutputStream(outputFile);
                    document.writeTo(out);
                    document.close();
                    out.close();
                    onComplete.execute(true, outputFile.getName(), "file saved success fully");
                    Log.v(LOG_TAG, "file saved success fully at " + outputFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    onComplete.execute(true, "failed to save file", "failed to save file due to " + e.getMessage());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View rootView, final View view, boolean reveal, final AlertDialog dialog) {
        int w = view.getWidth();
        int h = view.getHeight();
        float maxRadius = (float) Math.sqrt(w * w / 4 + h * h / 4);

        if (reveal) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view,
                    w / 2, h / 2, 0, maxRadius);
            view.setVisibility(View.VISIBLE);
            revealAnimator.start();
        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, maxRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });

            anim.start();
        }
    }

    public static ArrayAdapter createAdapter(Context mContext,LinkedHashMap<String, String> dataMap,String spinnerType){
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(dataMap, spinnerType));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return spinnerArrayAdapter;
    }


    public static void show(Context context,String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setMessage("Data syncing in background is running wait ...")
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //Button bq = builder.getButton(DialogInterface.BUTTON_NEGATIVE);
//        bq.setBackgroundColor(Color.BLUE);
        AlertDialog alert = builder.create();
        alert.setTitle(context.getString(R.string.sync));
        alert.setIcon(R.drawable.ic_sync);
        alert.show();


    }
    public static void backGroundSyncDilogue(Context context)
    {
        show(context, context.getString(R.string.sync));

    }
    public static void decodeFile(String mCurrentPhotoPath, File finalFile) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(mCurrentPhotoPath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            android.util.Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                android.util.Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                android.util.Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                android.util.Log.d("EXIF", "Exif: " + orientation);
            }
            // matrix.postRotate(90);
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(finalFile);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


}

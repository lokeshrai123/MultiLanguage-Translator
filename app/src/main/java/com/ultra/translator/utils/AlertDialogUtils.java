package com.ultra.translator.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ultra.translator.R;


/**
 * Created by BHM on 6/30/2016.
 */
public class AlertDialogUtils {

    public static void showAlertDialog (final Activity ac, String title, String message, final String ok, String cancel,
                                        boolean cancelable, final Idelegate callback) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ac);

        if (!title.equals(new String()))
            alertDialog.setTitle(title);


        alertDialog.setMessage(message);
        alertDialog.setCancelable(cancelable);


        alertDialog.setIcon(R.mipmap.ic_launcher);

        alertDialog.setPositiveButton(ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.callBack("ok", 0);
                        }
                    }
                });

        if (!cancel.equals("")) {
            alertDialog.setNegativeButton(cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (callback != null) {
                                callback.callBack("cancel", 0);
                            }
                        }
                    });
        }


        alertDialog.show();
    }

    public static void showAlertDialog (final Activity ac, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ac);


        alertDialog.setMessage(message);


        alertDialog.setIcon(R.mipmap.ic_launcher);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        alertDialog.show();
    }

    public static void showAlertDialogWithEdittext (final Activity ac, String title, String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ac);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        final EditText etInput = new EditText(ac);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        etInput.setLayoutParams(lp);
        alertDialog.setView(etInput);


        alertDialog.setIcon(R.mipmap.ic_launcher);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String sInput = etInput.getText()+"";

                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();


    }

}

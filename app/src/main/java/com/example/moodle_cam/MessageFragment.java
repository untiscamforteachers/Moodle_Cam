package com.example.moodle_cam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;



public class MessageFragment extends AppCompatDialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //view
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_layout, null);

        // Button Listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        MainActivity.instance.toaster();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                MainActivity.instance.stack();
                                Log.i("TAG","started Stack Mode");
                            }
                        }).start();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Log.i("TAG","Canceld Dialog");
                        break;
                }

            }
        };


        //alert Dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Wichtige Info zum Modus:")
                .setView(v)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel,listener)
                .create();
    }
}

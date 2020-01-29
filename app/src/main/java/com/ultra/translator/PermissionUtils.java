/*
 * Copyright (c) 2016 Metin Kale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ultra.translator;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;


/**
 * Created by metin on 14.12.2015.
 */
public class PermissionUtils {

    private static PermissionUtils mInstance;
    public boolean pCalendar;
    public boolean pCamera;
    public boolean pStorage;
    public boolean pLocation;
    public boolean pNotPolicy;

    private PermissionUtils(Context c) {
        checkPermissions(c);
    }

    public static PermissionUtils get(Context c) {
        if (mInstance == null) {
            mInstance = new PermissionUtils(c);
        }
        return mInstance;
    }

    private void checkPermissions(Context c) {
        pCalendar = ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
        pCamera = ContextCompat.checkSelfPermission(c, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        pStorage = ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        pLocation = ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            pNotPolicy = nm.isNotificationPolicyAccessGranted();
            //Crashlytics.setBool("pNotPolicy", pLocation);
        } else
            pNotPolicy = true;

        /*Crashlytics.setBool("pCalendar", pCalendar);
        Crashlytics.setBool("pCamera", pCamera);
        Crashlytics.setBool("pStorage", pStorage);
        Crashlytics.setBool("pLocation", pLocation);*/

    }



    public void needNotificationPolicy(final Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && act.isDestroyed())
            return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        NotificationManager nm = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
        pNotPolicy = nm.isNotificationPolicyAccessGranted();
        if (!pNotPolicy) {
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            PackageManager packageManager = act.getPackageManager();
            if (intent.resolveActivity(packageManager) != null) {
                act.startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 0);
            }
        }
    }

    public void needCamera(final Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && act.isDestroyed())
            return;

        if (!pCamera) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);

            builder
                    .setTitle("Camera Permission")
                    .setMessage("To use 3D-Compass, you need to allow Camera access. Otherwise you will only be able to use the 2D-Compass.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.CAMERA}, 0);
                }
            });

            builder.show();

        }
    }


    public void needLocation(final Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && act.isDestroyed())
            return;

        if (!pLocation) {

            /*AlertDialog.Builder builder = new AlertDialog.Builder(act);

            builder.setTitle(R.string.permissionLocationTitle).setMessage(R.string.permissionLocationText).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
            });


            builder.show();*/

            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        }
    }


    public void needCalendar(final Activity act, boolean force) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && act.isDestroyed())
            return;

        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.WRITE_CALENDAR}, 0);

    }

    public boolean checkIsGrantAllPermissions (Context c) {
        boolean pCamera = ContextCompat.checkSelfPermission(c, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean pStorage = ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (pCamera && pStorage)
            return true;
        else
            return false;

    }


    public void requestAllPermissions (final Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && act.isDestroyed())
            return;


        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
    }



    public void needStorage(final Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && act.isDestroyed())
            return;

        if (!pStorage) {

            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        }
    }

    public void onRequestPermissionResult(String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
                case Manifest.permission.CAMERA:
                    pCamera = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    break;
                case Manifest.permission.WRITE_CALENDAR:
                    pCalendar = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    //if (!pCalendar)
                        //Prefs.setCalendar("-1");
                    break;
                case Manifest.permission.ACCESS_NOTIFICATION_POLICY:
                    pNotPolicy = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    break;
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    pLocation = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    pStorage = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    break;
            }
        }
    }

}


/*
 * Copyright 2014 Thomas Hoffmann
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
package de.j4velin.mediaprioritymode;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 23 && getPackageManager()
                .checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // notification listener is only required on API 21
        // AND on API 22 on Samsung devices -.-
        boolean notificationListenerRequired = Build.VERSION.SDK_INT == 21 ||
                (Build.VERSION.SDK_INT == 22 &&
                        Build.MANUFACTURER.toLowerCase().contains("samsung"));
        if (notificationListenerRequired &&
                !getSharedPreferences("listener_setting", Context.MODE_MULTI_PROCESS)
                        .getBoolean("listenerEnabled", false)) {
            findViewById(R.id.launchericon).setVisibility(View.GONE);
            findViewById(R.id.listenerwarning).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                }
            });

            // Samsung has implement a silent mode and a priority mode, which aren't the same
            // the notification listener is only required to detect the priority mode
            if (Build.MANUFACTURER.toLowerCase().contains("samsung"))
                findViewById(R.id.wontdoanything).setVisibility(View.GONE);
        } else {
            findViewById(R.id.listenerwarning).setVisibility(View.GONE);
            findViewById(R.id.launchericon).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean showLauncherIcon = ((CheckBox) findViewById(R.id.launchericon)).isChecked();
        if (!showLauncherIcon) {
            getPackageManager().setComponentEnabledSetting(getComponentName(),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }
}

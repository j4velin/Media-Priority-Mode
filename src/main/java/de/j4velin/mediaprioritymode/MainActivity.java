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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // no need for the listener before Lollipop
        boolean listenerEnabled = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ||
                getSharedPreferences("listener_setting", Context.MODE_MULTI_PROCESS)
                        .getBoolean("listenerEnabled", false);
        if (listenerEnabled) {
            findViewById(R.id.listenerwarning).setVisibility(View.GONE);
            findViewById(R.id.launchericon).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.launchericon).setVisibility(View.GONE);
            findViewById(R.id.listenerwarning).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    startActivity(
                            new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });
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

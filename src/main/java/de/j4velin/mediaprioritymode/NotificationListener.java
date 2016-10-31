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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NotificationListener extends NotificationListenerService {

    @Override
    public IBinder onBind(final Intent mIntent) {
        if (BuildConfig.DEBUG) Logger.log("NotificationListener::onBind");
        getSharedPreferences("listener_setting", Context.MODE_PRIVATE).edit()
                .putBoolean("listenerEnabled", true).apply();
        return super.onBind(mIntent);
    }

    @Override
    public boolean onUnbind(final Intent mIntent) {
        if (BuildConfig.DEBUG) Logger.log("NotificationListener::onUnbind");
        getSharedPreferences("listener_setting", Context.MODE_PRIVATE).edit()
                .putBoolean("listenerEnabled", false).apply();
        return super.onUnbind(mIntent);
    }

    @Override
    public void onInterruptionFilterChanged(final int interruptionFilter) {
        boolean inPriority =
                interruptionFilter != NotificationListenerService.INTERRUPTION_FILTER_ALL;
        SharedPreferences prefs = getSharedPreferences("audio_setting", Context.MODE_PRIVATE);
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (BuildConfig.DEBUG) Logger.log("onInterruptionFilterChanged " + interruptionFilter);
        if (inPriority) {
            int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (BuildConfig.DEBUG) Logger.log(
                    "NotificationListener - in priority mode, current volume: " + currentVolume);
            if (currentVolume > 0) {
                prefs.edit().putInt("media_volume", currentVolume).apply();
                if (BuildConfig.DEBUG)
                    Logger.log("NotificationListener - changing STREAM_MUSIC volume to 0");
                am.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
        } else {
            if (BuildConfig.DEBUG) Logger.log(
                    "NotificationListener - changing STREAM_MUSIC volume to : " +
                            prefs.getInt("media_volume", 128));
            am.setStreamVolume(AudioManager.STREAM_MUSIC, prefs.getInt("media_volume", 128), 0);
        }
    }
}

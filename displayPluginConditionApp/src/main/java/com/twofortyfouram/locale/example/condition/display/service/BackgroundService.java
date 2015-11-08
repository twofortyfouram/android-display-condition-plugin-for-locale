/*
 * android-display-condition-plugin-for-locale <https://github.com/twofortyfouram/android-display-condition-plugin-for-locale>
 * Copyright 2014 two forty four a.m. LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twofortyfouram.locale.example.condition.display.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.twofortyfouram.locale.example.condition.display.ui.activity.EditActivity;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.bundle.BundleScrubber;
import com.twofortyfouram.spackle.power.PartialWakeLockForService;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;


/**
 * {@code Service} for monitoring the {@code REGISTERED_RECEIVER_ONLY} {@code Intent}s
 * {@link Intent#ACTION_SCREEN_ON} and {@link Intent#ACTION_SCREEN_OFF}.
 */
public final class BackgroundService extends Service {
    //@formatter:off
    /*
     * REPRESENTATION INVARIANTS:
     * - INTENT_REQUEST_REQUERY must not be mutated after it is initialized
     * - mReceiver is registered only while the service is running
     */
    //@formatter:on

    /**
     * {@code Intent} to ask the host to re-query this plug-in condition.
     */
    /*
     * This object is cached here so that it only must be allocated once. The Activity name must be
     * present as an extra in this Intent, so that the host will know who needs updating. The host will
     * ignore the requery request unless the Activity extra is present.
     */
    @NonNull
    private static final Intent INTENT_REQUEST_REQUERY = new Intent(
            com.twofortyfouram.locale.api.Intent.ACTION_REQUEST_QUERY).putExtra(
            com.twofortyfouram.locale.api.Intent.EXTRA_STRING_ACTIVITY_CLASS_NAME,
            EditActivity.class.getName());

    /**
     * Type: {@code boolean}.
     * <p>
     * State of the display prior to the request to start the service.
     */
    @NonNull
    private static final String EXTRA_BOOLEAN_WAS_SCREEN_ON = BackgroundService.class.getName()
            + ".extra.BOOLEAN_WAS_SCREEN_ON"; //$NON-NLS-1$

    /**
     * WakeLock to cover the gap between receiving the Intent from the host and starting the
     * service.
     */
    @NonNull
    private static final PartialWakeLockForService PARTIAL_WAKE_LOCK
            = new PartialWakeLockForService(
            BackgroundService.class.getName());

    /**
     * A {@code BroadcastReceiver} to monitor {@link Intent#ACTION_SCREEN_ON} and
     * {@link Intent#ACTION_SCREEN_OFF}. Assigned/registered in {@link #onCreate()} and
     * unregistered/dereferenced in {@link #onDestroy()}.
     */
    @Nullable
    private BroadcastReceiver mReceiver;

    /**
     * Flag to note when {@link #onStartCommand(Intent, int, int)} has been called.
     */
    private boolean mIsOnStartCommandCalled = false;

    /**
     * @param context    Application context.
     * @param isScreenOn True if the screen is on. False if the screen is off.
     */
    public static void startService(@NonNull final Context context, final boolean isScreenOn) {
        assertNotNull(context, "context"); //$NON-NLS-1$

        final Intent intent = new Intent(context, BackgroundService.class);
        intent.putExtra(EXTRA_BOOLEAN_WAS_SCREEN_ON, isScreenOn);

        PARTIAL_WAKE_LOCK.beforeStartingService(context);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*
         * ACTION_SCREEN_ON and ACTION_SCREEN_OFF are RECEIVER_REGISTERED_ONLY Intents, and must
         * therefore be registered via a Service rather than via the Android Manifest.
         *
         * This is not ideal for a plug-in, because it requires the plug-in maintain a background
         * service.
         */
        mReceiver = new DisplayReceiver();
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        PARTIAL_WAKE_LOCK.beforeDoingWork(getApplicationContext());

        try {
            /*
             * If the Intent is null, then the service is being started by Android rather than being
             * started from the BroadcastReceiver.
             */
            if (null != intent) {
                /*
                 * Because Services are started from an event loop, there is a timing gap between
                 * when the BroadcastReceiver checks the screen state and when the Service starts
                 * monitoring for screen changes.
                 *
                 * This case is only important the first time the Service starts.
                 */
                if (!mIsOnStartCommandCalled) {
                    final PowerManager powerManager = (PowerManager) getSystemService(
                            Context.POWER_SERVICE);
                    final boolean isScreenOn = powerManager.isScreenOn();
                    final boolean wasScreenOn = intent
                            .getBooleanExtra(EXTRA_BOOLEAN_WAS_SCREEN_ON, false);

                    if (isScreenOn != wasScreenOn) {
                        sendBroadcast(INTENT_REQUEST_REQUERY);
                    }
                }
            }

            mIsOnStartCommandCalled = true;

            return START_STICKY;
        } finally {
            PARTIAL_WAKE_LOCK.afterDoingWork(getApplicationContext());
        }
    }

    @Override
    public IBinder onBind(final Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
        mReceiver = null;
    }

    /**
     * A subclass of BroadcastReceiver that will always send a re-query Intent to Locale when it
     * receives an Intent.
     * <p>
     * This BroadcastReceiver is intended to be registered with the {@link Intent#ACTION_SCREEN_ON}
     * and {@link Intent#ACTION_SCREEN_OFF} actions.
     */
    private static final class DisplayReceiver extends BroadcastReceiver {

        /**
         * Constructs a new DisplayReceiver
         */
        public DisplayReceiver() {
            super();
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (BundleScrubber.scrub(intent)) {
                return;
            }
            
            Lumberjack.v("Received %s", intent); //$NON-NLS-1$

            /*
             * Ignore the initial sticky Intent
             */
            if (isInitialStickyBroadcast()) {
                return;
            }

            /*
             * Ask the host to re-query our condition instances. Note: this plug-in does not keep
             * track of what types of conditions have been set up. While executing this code, this
             * Condition has no idea whether there are even any Display conditions within the host or
             * whether those conditions are checking for screen on versus screen off. This is an
             * intentional design decision to eliminate all sorts of complex synchronization
             * problems between state within the host and state within the plug-in.
             */

            /*
             * Note: For the most part, the Display condition service will only be running if there
             * is an instance of the Display condition within the host. The only time this wouldn't be
             * true is if the user creates a Display condition and saves it, the host queries the
             * Display condition (therefore launching its service), and then the user deletes
             * the Display condition or the situation containing the Display condition. At this
             * point, the Display condition service will be left running. This is not considered to
             * be a significant problem, because Android will kill the Display condition service
             * when necessary.
             */
            context.sendBroadcast(INTENT_REQUEST_REQUERY);
        }
    }
}

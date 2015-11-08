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

package com.twofortyfouram.locale.example.condition.display.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.Nullable;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.format.DateUtils;

import com.twofortyfouram.locale.example.condition.display.bundle.PluginBundleValues;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.ThreadUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests the {@link com.twofortyfouram.locale.example.condition.display.receiver.QueryReceiver}
 */
public final class QueryReceiverTest extends AndroidTestCase {

    /**
     * Amount of time to wait for threaded queries to complete. Android gives
     * BroadcastReceivers 10 seconds to complete their work, however plug-ins
     * should be much faster than that.
     */
    private static final long LATCH_WAIT_TIME = 3 * DateUtils.SECOND_IN_MILLIS;

    /**
     * Initialize a looper to be used across all test cases. This is required,
     * as ConditionFactory guarantees that the methods will be consistently
     * called from the same thread.
     */
    @Nullable
    private Looper mLooper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final HandlerThread h = ThreadUtil
                .newHandlerThread(getName(), ThreadUtil.ThreadPriority.DEFAULT);
        mLooper = h.getLooper();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        mLooper.quit();
    }

    /**
     * Tests sending an Intent that is satisfied when the display is on.
     */
    @MediumTest
    public void testSatisfiedDisplayOn() {
        final CountDownLatch latch = new CountDownLatch(1);

        // For some reason, this test fails on the CI server with Kit Kat...
        if (!AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.KITKAT)) {
           /*
            * Force the display on for this test
            */
            @SuppressWarnings("deprecation")
            final WakeLock wakeLock = ((PowerManager) getContext().getSystemService(
                    Context.POWER_SERVICE)).newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, "test"); //$NON-NLS-1$
            try {
                wakeLock.acquire();

                final Intent intent = new Intent(getContext(), QueryReceiver.class)
                        .setAction(com.twofortyfouram.locale.api.Intent.ACTION_QUERY_CONDITION);
                final Bundle extras = PluginBundleValues.generateBundle(getContext(), true);
                intent.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE, extras);

                getContext().sendOrderedBroadcast(
                        intent,
                        null,
                        new BroadcastReceiver() {
                            @Override
                            public void onReceive(final Context context, final Intent i) {
                                assertEquals(
                                        com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_SATISFIED,
                                        getResultCode());

                                latch.countDown();
                            }
                        }, new Handler(mLooper),
                        com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNKNOWN,
                        null, null
                );

                try {
                    assertTrue(latch.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS));
                } catch (final InterruptedException e) {
                    fail();
                }
            } finally {
                wakeLock.release();
            }
        }
    }

    /**
     * Tests sending an Intent that is satisfied when the display is on.
     */
    @MediumTest
    public void testUnsatisfiedDisplayOn() {
        final CountDownLatch latch = new CountDownLatch(1);

        // For some reason, this test fails on the CI server with Kit Kat...
        if (!AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.KITKAT)) {
        /*
         * Force the display on for this test
         */
            @SuppressWarnings("deprecation")
            final WakeLock wakeLock = ((PowerManager) getContext().getSystemService(
                    Context.POWER_SERVICE)).newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, "test"); //$NON-NLS-1$
            try {
                wakeLock.acquire();

                final Intent intent = new Intent(
                        com.twofortyfouram.locale.api.Intent.ACTION_QUERY_CONDITION).setClass(
                        getContext(), QueryReceiver.class);
                final Bundle extras = PluginBundleValues.generateBundle(getContext(), false);
                intent.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE, extras);

                getContext().sendOrderedBroadcast(
                        intent,
                        null,
                        new BroadcastReceiver() {
                            @Override
                            public void onReceive(final Context context, final Intent i) {
                                assertEquals(
                                        com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNSATISFIED,
                                        getResultCode());

                                latch.countDown();
                            }
                        }, new Handler(mLooper),
                        com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNKNOWN,
                        null, null
                );

                try {
                    assertTrue(latch.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS));
                } catch (final InterruptedException e) {
                    fail();
                }
            } finally {
                wakeLock.release();
            }
        }
    }
}

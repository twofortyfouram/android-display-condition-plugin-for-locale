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

package com.twofortyfouram.locale.example.condition.display.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.twofortyfouram.locale.example.condition.display.R;
import com.twofortyfouram.locale.example.condition.display.bundle.PluginBundleValues;
import com.twofortyfouram.spackle.ResourceUtil;
import com.twofortyfouram.test.ui.activity.ActivityTestUtil;

/**
 * Tests the {@link EditActivity}.
 */
public final class EditActivityTest extends ActivityInstrumentationTestCase2<EditActivity> {

    /**
     * Context of the target application. This is initialized in
     * {@link #setUp()}.
     */
    private Context mTargetContext;

    /**
     * Instrumentation for the test. This is initialized in {@link #setUp()}.
     */
    private Instrumentation mInstrumentation;

    /**
     * Constructor for the test class; required by Android.
     */
    public EditActivityTest() {
        super(EditActivity.class);
    }

    /**
     * Setup that executes before every test case
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mInstrumentation = getInstrumentation();
        mTargetContext = mInstrumentation.getTargetContext();

        /*
         * Perform test case specific initialization. This is required to be set
         * up here because setActivityIntent has no effect inside a method
         * annotated with @UiThreadTest
         */
        if ("testNewConditionWithoutSelection".equals(getName())) { //$NON-NLS-1$
            setActivityIntent(new Intent(com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION)
                    .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BREADCRUMB,
                            "Locale > Edit Situation")); //$NON-NLS-1$
        } else if ("testNewConditionSaveOff".equals(getName())) { //$NON-NLS-1$
            setActivityIntent(new Intent(com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION)
                    .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BREADCRUMB,
                            "Locale > Edit Situation")); //$NON-NLS-1$
        } else if ("testNewConditionSaveOn".equals(getName())) { //$NON-NLS-1$
            setActivityIntent(new Intent(com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION)
                    .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BREADCRUMB,
                            "Locale > Edit Situation")); //$NON-NLS-1$
        } else if ("testEditOldConditionOff".equals(getName())) { //$NON-NLS-1$
            final Bundle bundle = PluginBundleValues.generateBundle(mTargetContext, false);

            setActivityIntent(new Intent(com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION)
                            .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BREADCRUMB,
                                    "Locale > Edit Situation") //$NON-NLS-1$
                            .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE,
                                    bundle)
                            .putExtra(
                                    com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB,
                                    "Off"));//$NON-NLS-1$
        } else if ("testEditOldConditionOn".equals(getName())) { //$NON-NLS-1$
            final Bundle bundle = PluginBundleValues.generateBundle(mTargetContext, true);

            setActivityIntent(new Intent(com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION)
                    .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BREADCRUMB,
                            "Locale > Edit Situation")//$NON-NLS-1$
                    .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE,
                            bundle)
                    .putExtra(
                            com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB,
                            "On"));//$NON-NLS-1$
        } else if ("testMissingBreadcrumb".equals(getName())) { //$NON-NLS-1$
            setActivityIntent(
                    new Intent(com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION));
        } else if ("testBadBundleMissingExtra".equals(getName())) { //$NON-NLS-1$
            final Bundle bundle = new Bundle();

            setActivityIntent(new Intent(com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION)
                    .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BREADCRUMB,
                            "Locale > Edit Situation")
                    .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE,
                            bundle)); //$NON-NLS-1$
        } else if ("testBadBundleWrongType".equals(getName())) { //$NON-NLS-1$
            final Bundle bundle = new Bundle();
            bundle.putString(PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE, "test"); //$NON-NLS-1$

            setActivityIntent(new Intent(com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION)
                    .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BREADCRUMB,
                            "Locale > Edit Situation")
                    .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE,
                            bundle)); //$NON-NLS-1$
        }
    }

    /**
     * Verifies the Activity class name hasn't been accidentally changed.
     */
    @SmallTest
    public static void testActivityName() {
        /*
         * NOTE: This test is expected to fail initially when you are adapting
         * this example to your own plug-in. Once you've settled on a name for
         * your Activity, go ahead and update this test case.
         *
         * The goal of this test case is to prevent accidental renaming of the
         * Activity. Once a plug-in is published to the app store, the Activity
         * shouldn't be renamed because that will break the plug-in for users
         * who had the old version of the plug-in. If you ever find yourself
         * really needing to rename the Activity after the plug-in has been
         * published, take a look at using an activity-alias entry in the
         * Android Manifest.
         */

        assertEquals(
                "com.twofortyfouram.locale.example.condition.display.ui.activity.EditActivity",
                EditActivity.class.getName()); //$NON-NLS-1$
    }

    /**
     * Tests creation of a new condition, that the UI is initialized to the
     * right state, and that nothing is saved.
     */
    @MediumTest
    @UiThreadTest
    public void testNewCondition_cancel_result() throws Throwable {
        final Activity activity = getActivity();

        assertSelectedPositionAutoSync(AdapterView.INVALID_POSITION);

        activity.finish();

        assertEquals(Activity.RESULT_CANCELED, ActivityTestUtil.getActivityResultCode(activity));
    }

    /**
     * Tests creation of a new condition, that the UI is initialized to the
     * right state, and that changes are properly saved
     */
    @MediumTest
    @UiThreadTest
    public void testNewConditionSaveOff() throws Throwable {
        assertSelectedPositionAutoSync(AdapterView.INVALID_POSITION);

        setSelectedPositionAutoSync(R.string.list_off);

        assertActivityResultAutoSync(false);
    }

    @MediumTest
    @UiThreadTest
    public void testNewConditionSaveOn() throws Throwable {
        assertSelectedPositionAutoSync(AdapterView.INVALID_POSITION);

        setSelectedPositionAutoSync(R.string.list_on);

        assertActivityResultAutoSync(true);
    }

    @MediumTest
    @UiThreadTest
    public void testEditOldConditionOff() throws Throwable {
        /*
         * It is necessary to call this manually; the test case won't call
         * onPostCreate() for us :-(
         */
        getActivity().onPostCreateWithPreviousResult(
                getActivity().getIntent().getBundleExtra(
                        com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE),
                getActivity().getIntent().getStringExtra(
                        com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB));

        assertSelectedPositionAutoSync(R.string.list_off);

        getActivity().finish();
        assertEquals(Activity.RESULT_CANCELED,
                ActivityTestUtil.getActivityResultCode(getActivity()));
    }

    @MediumTest
    @UiThreadTest
    public void testEditOldConditionOn() throws Throwable {
        /*
         * It is necessary to call this manually; the test case won't call
         * onPostCreate() for us :-(
         */
        getActivity().onPostCreateWithPreviousResult(
                getActivity().getIntent().getBundleExtra(
                        com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE),
                getActivity().getIntent().getStringExtra(
                        com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB));

        assertSelectedPositionAutoSync(R.string.list_on);

        getActivity().finish();
        assertEquals(Activity.RESULT_CANCELED,
                ActivityTestUtil.getActivityResultCode(getActivity()));
    }

    @MediumTest
    @UiThreadTest
    public void testMissingBreadcrumb() {
        final Activity activity = getActivity();

        assertEquals(mTargetContext.getString(R.string.plugin_name), activity.getTitle());
    }

    @MediumTest
    @UiThreadTest
    public void testBadBundleMissingExtra() throws Throwable {
        final Activity activity = getActivity();

        assertSelectedPositionAutoSync(AdapterView.INVALID_POSITION);

        activity.finish();
        assertEquals(Activity.RESULT_CANCELED, ActivityTestUtil.getActivityResultCode(activity));
    }

    @MediumTest
    @UiThreadTest
    public void testBadBundleWrongType() throws Throwable {
        final Activity activity = getActivity();

        assertSelectedPositionAutoSync(AdapterView.INVALID_POSITION);

        activity.finish();
        assertEquals(Activity.RESULT_CANCELED, ActivityTestUtil.getActivityResultCode(activity));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private CharSequence getTitleHoneycomb() {
        return getActivity().getActionBar().getSubtitle();
    }

    /**
     * Asserts the Activity result contains the expected values for the given
     * display state.
     *
     * @param isDisplayOn True if the display is on, false if the display is
     *                    off.
     */
    private void assertActivityResultAutoSync(final boolean isDisplayOn) throws Throwable {
        final Activity activity = getActivity();

        final Runnable runnable = new Runnable() {
            public void run() {
                /*
                 * Verify finishing with text entry is saved
                 */
                activity.finish();

                assertEquals(Activity.RESULT_OK, ActivityTestUtil.getActivityResultCode(activity));

                final Intent result = ActivityTestUtil.getActivityResultData(activity);
                assertNotNull(result);

                final Bundle extras = result.getExtras();
                assertNotNull(extras);
                assertEquals(
                        String.format(
                                "Extras should only contain %s and %s but actually contain %s",
                                com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE,
                                com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB,
                                extras.keySet()), 2, extras.keySet() //$NON-NLS-1$
                                .size());

                assertFalse(TextUtils.isEmpty(extras
                        .getString(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB)));
                // BundleTestHelper.assertSerializable(extras);

                final Bundle pluginBundle = extras
                        .getBundle(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE);
                assertNotNull(pluginBundle);

                /*
                 * The following are tests specific to this plug-in's bundle
                 */
                assertTrue(PluginBundleValues.isBundleValid(pluginBundle));
                assertEquals(isDisplayOn,
                        pluginBundle.getBoolean(PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE));
            }
        };

        autoSyncRunnable(runnable);
    }

    /**
     * Asserts provided item is selected in the list.
     *
     * @param position one of {@link R.string#list_off},
     *                 {@link R.string#list_on}, or {@link AdapterView#INVALID_POSITION}.
     */
    private void assertSelectedPositionAutoSync(final int position) throws Throwable {
        final int actualPosition;
        if (AdapterView.INVALID_POSITION == position) {
            actualPosition = AdapterView.INVALID_POSITION;
        } else {
            actualPosition = ResourceUtil.getPositionForIdInArray(mTargetContext,
                    R.array.display_states, position);
        }

        final Runnable runnable = new Runnable() {
            final Activity mActivity = getActivity();

            public void run() {
                assertEquals(actualPosition,
                        ((ListView) mActivity.findViewById(android.R.id.list))
                                .getCheckedItemPosition());
            }
        };

        autoSyncRunnable(runnable);
    }

    /**
     * Sets the given position in the list.
     *
     * @param position one of {@link R.string#list_off},
     *                 {@link R.string#list_on}, or {@link AdapterView#INVALID_POSITION}.
     */
    private void setSelectedPositionAutoSync(final int position) throws Throwable {
        final int actualPosition;
        if (AdapterView.INVALID_POSITION == position) {
            actualPosition = AdapterView.INVALID_POSITION;
        } else {
            actualPosition = ResourceUtil.getPositionForIdInArray(mTargetContext,
                    R.array.display_states, position);
        }

        final Runnable runnable = new Runnable() {
            public void run() {
                final ListView listView = (ListView) getActivity().findViewById(android.R.id.list);
                listView.setItemChecked(actualPosition, true);
            }
        };

        autoSyncRunnable(runnable);
    }

    /**
     * Executes a runnable on the main thread. This method works even if the
     * current thread is already the main thread.
     *
     * @param runnable to execute.
     */
    protected final void autoSyncRunnable(final Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            getInstrumentation().runOnMainSync(runnable);
            getInstrumentation().waitForIdleSync();
        }
    }
}

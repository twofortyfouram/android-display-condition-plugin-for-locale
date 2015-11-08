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
package com.twofortyfouram.locale.example.condition.display.bundle;

import android.os.Bundle;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.twofortyfouram.spackle.AppBuildInfo;

/**
 * Tests {@link PluginBundleValues}.
 */
public final class PluginBundleValuesTest extends AndroidTestCase {

    @SmallTest
    public static void testExtraConstants() {
        /*
         * NOTE: This test is expected to fail initially when you are adapting this example to your
         * own plug-in. Once you've settled on constant names for your Intent extras, go ahead and
         * update this test case.
         *
         * The goal of this test case is to prevent accidental renaming of the Intent extras. If the
         * extra is intentionally changed, then this unit test needs to be intentionally updated.
         */
        assertEquals(
                "com.twofortyfouram.locale.example.condition.display.extra.BOOLEAN_STATE",
                PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE); //$NON-NLS-1$
        assertEquals(
                "com.twofortyfouram.locale.example.condition.display.extra.INT_VERSION_CODE",
                PluginBundleValues.BUNDLE_EXTRA_INT_VERSION_CODE); //$NON-NLS-1$
    }

    @SmallTest
    public void testGenerateBundle_on() {
        final Bundle bundle = PluginBundleValues.generateBundle(getContext(), true);
        assertNotNull(bundle);

        assertEquals(2, bundle.keySet().size());

        assertTrue(bundle.getBoolean(PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE));
        assertEquals(AppBuildInfo.getVersionCode(getContext()),
                bundle.getInt(PluginBundleValues.BUNDLE_EXTRA_INT_VERSION_CODE));
    }

    @SmallTest
    public void testGenerateBundle_off() {
        final Bundle bundle = PluginBundleValues.generateBundle(getContext(), false);
        assertNotNull(bundle);

        assertEquals(2, bundle.keySet().size());

        assertFalse(bundle.getBoolean(PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE));
        assertEquals(AppBuildInfo.getVersionCode(getContext()),
                bundle.getInt(PluginBundleValues.BUNDLE_EXTRA_INT_VERSION_CODE));
    }

    @SmallTest
    public static void testVerifyBundle_correct() {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE, true);
        bundle.putInt(PluginBundleValues.BUNDLE_EXTRA_INT_VERSION_CODE, 0);
        assertTrue(PluginBundleValues.isBundleValid(bundle));

        bundle.putBoolean(PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE, false);
        bundle.putInt(PluginBundleValues.BUNDLE_EXTRA_INT_VERSION_CODE, 1);
        assertTrue(PluginBundleValues.isBundleValid(bundle));
    }

    @SmallTest
    public static void testVerifyBundle_null() {
        assertFalse(PluginBundleValues.isBundleValid(null));
    }

    @SmallTest
    public static void testVerifyBundle_missing_extra() {
        assertFalse(PluginBundleValues.isBundleValid(new Bundle()));
    }

    @SmallTest
    public static void testVerifyBundle_extra_items() {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE, true);
        bundle.putInt(PluginBundleValues.BUNDLE_EXTRA_INT_VERSION_CODE, 0);
        bundle.putString("test", "test"); //$NON-NLS-1$//$NON-NLS-2$
        assertFalse(PluginBundleValues.isBundleValid(bundle));
    }

    @SmallTest
    public static void testVerifyBundle_wrong_type() {
        {
            final Bundle bundle = new Bundle();
            bundle.putString(PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE, "test"); //$NON-NLS-1$
            bundle.putInt(PluginBundleValues.BUNDLE_EXTRA_INT_VERSION_CODE, 0);
            assertFalse(PluginBundleValues.isBundleValid(bundle));
        }

        {
            final Bundle bundle = new Bundle();
            bundle.putBoolean(PluginBundleValues.BUNDLE_EXTRA_BOOLEAN_STATE, true);
            bundle.putString(PluginBundleValues.BUNDLE_EXTRA_INT_VERSION_CODE,
                    "test"); //$NON-NLS-1$
            assertFalse(PluginBundleValues.isBundleValid(bundle));
        }
    }
}

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

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.twofortyfouram.locale.example.condition.display.R;
import com.twofortyfouram.locale.example.condition.display.bundle.PluginBundleValues;
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractAppCompatPluginActivity;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.ResourceUtil;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class EditActivity extends AbstractAppCompatPluginActivity {

    /**
     * ListView shown in the Activity.
     */
    @Nullable
    private ListView mList = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mList = ((ListView) findViewById(android.R.id.list));
        mList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, getResources()
                .getStringArray(R.array.display_states)
        ));

        /*
         * To help the user keep context, the title shows the host's name and the subtitle
         * shows the plug-in's name.
         */
        CharSequence callingApplicationLabel = null;
        try {
            callingApplicationLabel =
                    getPackageManager().getApplicationLabel(
                            getPackageManager().getApplicationInfo(getCallingPackage(),
                                    0));
        } catch (final PackageManager.NameNotFoundException e) {
            Lumberjack.e("Calling package couldn't be found%s", e); //$NON-NLS-1$
        }
        if (null != callingApplicationLabel) {
            setTitle(callingApplicationLabel);
        }

        getSupportActionBar().setSubtitle(R.string.plugin_name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean isBundleValid(@NonNull Bundle bundle) {
        return PluginBundleValues.isBundleValid(bundle);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull final Bundle previousBundle,
            @NonNull final String previousBlurb) {
        if (PluginBundleValues.isBundleValid(previousBundle)) {
            final boolean isDisplayOn = PluginBundleValues.isDisplayOn(previousBundle);
            final int position = ResourceUtil.getPositionForIdInArray(getApplicationContext(),
                    R.array.display_states, isDisplayOn ? R.string.list_on : R.string.list_off);
            mList.setItemChecked(position, true);
        }
    }

    @Override
    @Nullable
    public Bundle getResultBundle() {
        Bundle resultBundle = null;

        if (AdapterView.INVALID_POSITION != mList.getCheckedItemPosition()) {
            final int selectedResourceId = ResourceUtil
                    .getResourceIdForPositionInArray(getApplicationContext(),
                            R.array.display_states, mList.getCheckedItemPosition());

            final boolean isDisplayOn;
            if (R.string.list_on == selectedResourceId) {
                isDisplayOn = true;
            } else if (R.string.list_off == selectedResourceId) {
                isDisplayOn = false;
            } else {
                throw new AssertionError();
            }

            resultBundle = PluginBundleValues.generateBundle(getApplicationContext(), isDisplayOn);
        }

        return resultBundle;
    }

    @Override
    @NonNull
    public String getResultBlurb(@NonNull final Bundle bundle) {
        final boolean isDisplayOn = PluginBundleValues.isDisplayOn(bundle);
        if (isDisplayOn) {
            return getString(R.string.blurb_on);
        }

        return getString(R.string.blurb_off);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        else if (R.id.menu_discard_changes == item.getItemId()) {
            // Signal to AbstractAppCompatPluginActivity that the user canceled.
            mIsCancelled = true;
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

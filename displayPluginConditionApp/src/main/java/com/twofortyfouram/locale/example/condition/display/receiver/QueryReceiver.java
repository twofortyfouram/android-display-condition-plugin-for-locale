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

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import com.twofortyfouram.locale.example.condition.display.bundle.PluginBundleValues;
import com.twofortyfouram.locale.example.condition.display.service.BackgroundService;
import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginConditionReceiver;


public final class QueryReceiver extends AbstractPluginConditionReceiver {

    @Override
    protected int getPluginConditionResult(@NonNull final Context context,
            @NonNull final Bundle bundle) {
        int result = com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNKNOWN;

        final boolean isScreenOn = (((PowerManager) context
                .getSystemService(Context.POWER_SERVICE)).isScreenOn());
        final boolean conditionState = PluginBundleValues.isDisplayOn(bundle);

        if (isScreenOn) {
            if (conditionState) {
                result = com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_SATISFIED;
            } else {
                result = com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNSATISFIED;
            }
        } else {
            if (conditionState) {
                result = com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNSATISFIED;
            } else {
                result = com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_SATISFIED;
            }
        }

        BackgroundService.startService(context, isScreenOn);

        return result;
    }

    @Override
    protected boolean isBundleValid(@NonNull final Bundle bundle) {
        return PluginBundleValues.isBundleValid(bundle);
    }

    @Override
    protected boolean isAsync() {
        return false;
    }
}

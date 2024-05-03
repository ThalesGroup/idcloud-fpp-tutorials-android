/*
 * MIT License
 *
 * Copyright (c) 2019 Thales DIS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * IMPORTANT: This source code is intended to serve training information purposes only.
 *            Please make sure to review our IdCloud documentation, including security guidelines.
 */

package com.thales.fpp.gettingstarted;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.gemalto.riskengine.GAHCore;
import com.gemalto.riskengine.GAHCoreConfig;
import com.gemalto.riskengine.GAHErrorCodes;
import com.gemalto.riskengine.GAHGemaltoSignalConfig;
import com.gemalto.riskengine.GAHMetaInformation;
import com.gemalto.riskengine.GAHResponseCallback;

import java.util.Locale;

public class MainActivity extends AbstractMainActivity {

    //region Defines

    /**
     * RiskEngine backend URL
     */
    private static final String FPP_URL = "// PLACEHOLDER: Server URL";
    private static final String TAG = MainActivity.class.getSimpleName();

    //endregion

    //region Life Cycle

    /**
     * Configure and initialise FPP SDK.
     *
     * @return {@code True} if initialisation was successful, else {@code false}.
     */
    @Override
    boolean initSDK() {

        // Setup core config, set other optional params, if required
        GAHCoreConfig coreConfig = new GAHCoreConfig.Builder(this.getApplication(), FPP_URL)
                .build();

        // Gemalto Signal collection is mandatory.
        GAHGemaltoSignalConfig signalConfig = new GAHGemaltoSignalConfig.Builder()
                .build();

        // Pass configuration to core.
        GAHCore.initialize(coreConfig, signalConfig);

        // Start Signal prefetch
        GAHCore.startPrefetchSignals();

        return true;
    }

    @Override
    void endSDK() {
        // It is recommended to call this method during a transaction screen exit
        // where startPrefetchSignals() was called previously.
        GAHCore.stopPrefetchSignals();
    }

    /**
     * Load activity visual components.
     */
    @Override
    void initGUI() {

        // Display SDK version information.
        GAHMetaInformation sdkInfo = GAHCore.getSDKVersionInfo();
        String formattedVersion = String.format(Locale.getDefault(),
                getString(R.string.main_activity_text_sdk_info_values),
                sdkInfo.getName(),
                sdkInfo.getVersion(),
                sdkInfo.getBuild(),
                sdkInfo.isDebug() ? getText(R.string.common_debug) : getText(R.string.common_release));

        TextView versionTextView = findViewById(R.id.main_activity_text_version_values);
        versionTextView.setText(formattedVersion);

        // Add listener to sample button.
        findViewById(R.id.main_activity_button_sample_action)
                .setOnClickListener(view -> onButtonPressedSampleAction());

    }

    //endregion

    //region User Interface

    /**
     * User taped on Sample Action button.
     * Show signal collection
     */
    private void onButtonPressedSampleAction() {
        // Show direct response to UI.
        // Full application will have some sort of dialog fragment / loading indicator.
        Toast.makeText(getApplicationContext(), R.string.common_processing, Toast.LENGTH_SHORT).show();

        // Listen to prefetch status
        GAHCore.requestPrefetchStatus(this::processPrefetchStatusResponse);
    }

    //endregion

    //region Private Helpers

    private void processPrefetchStatusResponse(int statusCode, String statusMsg) {
        // Show return value status.
        Log.d(TAG, "Request prefetch finished with status code: " + statusCode + " and message: " + statusMsg);

        // Make sure that we have all signals prefetched.
        if (statusCode == GAHErrorCodes.PREFETCH_STATUS_OK) {
            // With all signals in place. Request Visit ID.
            GAHCore.requestVisitID(new GAHResponseCallback() {
                @Override
                public void success(String visitID) {
                    processVisitIDResponse(true, visitID);
                }

                @Override
                public void error(int statusCode, String statusMsg) {
                    processVisitIDResponse(false, statusMsg);
                }
            });
        }
    }

    /**
     * Process request visit id response. All transactions should be cleared.
     * To keep Lab simple, we will have unified method instead of two callbacks.
     *
     * @param success {@code True} if request was successful, otherwise {@code False}
     * @param value   On success it contain Visit ID, otherwise error description.
     */
    private void processVisitIDResponse(boolean success, String value) {
        // In this Lab we want to simple display some visual result, so UI thread is also handy.
        runOnUiThread(() -> {
            // Display result on screen.
            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();

            //noinspection StatementWithEmptyBody
            if (success) {
                // Do something with Visit ID.
                // ...
                // ...
            }
        });
    }

    //endregion

}

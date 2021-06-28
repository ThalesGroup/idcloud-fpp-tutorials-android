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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.app.AppCompatActivity;

import com.thales.fpp.gettingstarted.fragments.FragmentMissingPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class taking care of Android dynamic permissions.
 * FPP SDK is handled in MainActivity directly.
 */
public abstract class AbstractMainActivity extends AppCompatActivity {

    //region Defines

    /**
     * TAG for missing permissions fragment. Used by manager to identify overlay.
     */
    private static final String TAG_FRAGMENT_MISSING_PERMISSIONS = "MissingPermissionsId";

    /**
     * SDK Initialisation status. Used to prevent multiple init calls.
     */
    private boolean mSDKInitialised = false;

    //endregion

    //region Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for permissions or display fragment with information.
        if (!checkMandatoryPermissions(true)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, new FragmentMissingPermissions(), null)
                    .addToBackStack(TAG_FRAGMENT_MISSING_PERMISSIONS)
                    .commit();
        }

        // Load SDK version information.
        initGUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // In case we don't have permissions yet, simple wait for another call.
        // FragmentMissingPermissions will take care of that.
        if (!mSDKInitialised && checkMandatoryPermissions(false)) {
            // Hide missing permission fragment if it's present.
            getSupportFragmentManager()
                    .popBackStack(TAG_FRAGMENT_MISSING_PERMISSIONS, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // Initialise SDK
            mSDKInitialised = initSDK();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        endSDK();
    }

    //endregion

    //region Public API

    /**
     * Checks the required runtime permissions.
     *
     * @param askForThem {@code True} if dialog application should request missing permissions, else {@code false}.
     * @return {@code True} if all permissions are present, else {@code false}.
     */
    public boolean checkMandatoryPermissions(boolean askForThem) {
        try {
            // Get list of all permissions defined in app manifest.
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            return checkPermissions(askForThem, info.requestedPermissions);
        } catch (PackageManager.NameNotFoundException exception) {
            // App package must be present.
            throw new IllegalStateException(exception);
        }
    }

    //endregion

    //region Private Helpers

    /**
     * Checks for runtime permission.
     *
     * @param askForThem  {@code True} if missing permission should be requested, else {@code false}.
     * @param permissions List of permissions.
     * @return {@code True} if permissions are present, else {@code false}.
     */
    private boolean checkPermissions(
            boolean askForThem,
            String... permissions
    ) {

        // Old SDK version does not require dynamic permissions.
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        // Update list of permissions based on granted status.
        List<String> permissionsToCheck = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PermissionChecker.PERMISSION_GRANTED) {
                //noinspection StatementWithEmptyBody
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    // Here we can display some description why we need this permission.
                }

                permissionsToCheck.add(permission);
            }
        }

        // Some permissions are not granted. Ask user for them.
        if (!permissionsToCheck.isEmpty() && askForThem) {
            String[] notGrantedArray = permissionsToCheck.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, notGrantedArray, 0);
        }

        return permissionsToCheck.isEmpty();
    }

    //endregion

    //region FPP SDK Methods

    abstract boolean initSDK();

    abstract void endSDK();

    abstract void initGUI();

    //endregion
}

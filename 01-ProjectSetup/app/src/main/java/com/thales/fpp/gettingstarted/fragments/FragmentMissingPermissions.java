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

package com.thales.fpp.gettingstarted.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thales.fpp.gettingstarted.MainActivity;
import com.thales.fpp.gettingstarted.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment to request mandatory runtime permissions.
 */
public class FragmentMissingPermissions extends Fragment {

    //region Life Cycle

    @Nullable
    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View retValue = inflater.inflate(R.layout.fragment_missing_permissions, null);

        // Find Ask For Permissions button and add handler to it.
        retValue.findViewById(R.id.fragment_missing_permissions_button_permissions)
                .setOnClickListener(this::onButtonPressedPermissions);

        return retValue;
    }

    //endregion

    //region User Interface

    private void onButtonPressedPermissions(View view) {
        // MainActivity should always be present.
        // Make sure, that someone did not change activity.
        if (!(getActivity() instanceof MainActivity)) {
            throw new IllegalStateException();
        }

        // Force permission check with user dialog.
        final MainActivity activity = (MainActivity) getActivity();
        activity.checkMandatoryPermissions(true);
    }

    //endregion

}

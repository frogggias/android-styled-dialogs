package com.avast.android.dialogs.iface;

import android.os.Bundle;

/**
 * Created by Tomáš Šůstek on 04.08.16.
 * Dotykačka s.r.o.
 */
public interface INumberPositiveButtonDialogListener {

    void onPositiveEditTextButtonClicked(int requestCode, int value, Bundle data);

}

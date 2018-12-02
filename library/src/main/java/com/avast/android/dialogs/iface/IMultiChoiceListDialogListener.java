package com.avast.android.dialogs.iface;

import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Interface for ListDialogFragment in modes: CHOICE_MODE_MULTIPLE
 * Implement it in Activity or Fragment to react to item selection.
 *
 * @since 2.1.0
 */
public interface IMultiChoiceListDialogListener {

    public void onListItemsSelected(CharSequence[] values, int[] selectedPositions, int requestCode, @Nullable Bundle data);
}

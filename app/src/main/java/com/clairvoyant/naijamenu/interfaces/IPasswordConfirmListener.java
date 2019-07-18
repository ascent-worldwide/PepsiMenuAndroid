package com.clairvoyant.naijamenu.interfaces;

import android.view.View;

public interface IPasswordConfirmListener {

    void onDialogCancel();

    void onDialogSuccess(View view, int position, int updatedMenuVersion);
}

package com.faiz.mahasiswaku.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.faiz.mahasiswaku.model.Mahasiswa;

import java.io.File;

public interface AddUpdateContract {

    interface View {
        Context getBaseContext();
        void startActivityForResult(Intent intent, int requestCode);
        PackageManager getPackageManager();
        File getExternalFilesDir(String type);
        void finish();
        void setActionBarTitle(String title);
        void setButtonText(String text);
        String getNrp();
        String getName();
        String getAddress();
        void setNrp(int nrp);
        void setName(String name);
        void setAddress(String address);
        void setErrorNrp(String error);
        void setErrorName(String error);
        void setErrorAddress(String error);
        void setResult(int result);
        void setImage(File file);
        void setImage(String url);
        void showLoading(Boolean state);
        void showMessage(String message);
        void hideKeyboard();
    }

    interface Presenter {
        void start();
        void onSaveClicked();
        void onResult(int requestCode, int resultCode, Intent data);
        void create(Mahasiswa mahasiswa);
        void update(Mahasiswa mahasiswa);
        void delete();
    }
}

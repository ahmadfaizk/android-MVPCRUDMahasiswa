package com.faiz.mahasiswaku.presenter;

import com.faiz.mahasiswaku.model.Mahasiswa;

import java.util.ArrayList;

public interface MainContract {

    interface View {
        void refreshData(ArrayList<Mahasiswa> list);
        void showLoading(Boolean state);
        void showMessage(String message);
    }

    interface Presenter {
        void onStart();
        void requestData();
        void onResult(int requestCode, int resultCode);
    }
}

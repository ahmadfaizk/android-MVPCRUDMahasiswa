package com.faiz.mahasiswaku.presenter;

import com.faiz.mahasiswaku.api.ApiClient;
import com.faiz.mahasiswaku.api.Sercvices;
import com.faiz.mahasiswaku.api.response.MultipleResponse;
import com.faiz.mahasiswaku.model.Mahasiswa;
import com.faiz.mahasiswaku.view.ui.AddUpdateMahasiswaActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void onStart() {
        requestData();
    }

    @Override
    public void requestData() {
        view.showLoading(true);
        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Sercvices.class).getAllMahasiswa()
                .enqueue(new Callback<MultipleResponse<Mahasiswa>>() {
                    @Override
                    public void onResponse(Call<MultipleResponse<Mahasiswa>> call, Response<MultipleResponse<Mahasiswa>> response) {
                        view.showLoading(false);
                        boolean error = response.body().isError();
                        if (!error) {
                            ArrayList<Mahasiswa> list = response.body().getData();
                            if (list.isEmpty()) {
                                view.showMessage("Data Kosong");
                            }
                            view.refreshData(list);
                        }
                    }

                    @Override
                    public void onFailure(Call<MultipleResponse<Mahasiswa>> call, Throwable t) {
                        view.showLoading(false);
                        t.printStackTrace();
                        view.showMessage(t.getMessage());
                    }
                });
    }

    @Override
    public void onResult(int requestCode, int resultCode) {
        if (requestCode == AddUpdateMahasiswaActivity.REQUEST_ADD) {
            if (resultCode == AddUpdateMahasiswaActivity.RESULT_ADD) {
                requestData();
                view.showMessage("Satu Mahasiswa Berhasil Ditambahkan");
            }
        }
        else if (requestCode == AddUpdateMahasiswaActivity.REQUEST_UPDATE) {
            if (resultCode == AddUpdateMahasiswaActivity.RESULT_UPDATE) {
                requestData();
                view.showMessage("Satu Mahasiswa Berhasil Diubah");
            }
            else if (resultCode == AddUpdateMahasiswaActivity.RESULT_DELETE) {
                requestData();
                view.showMessage("Satu Mahasiswa Berhasil Dihapus");
            }
        }
    }
}

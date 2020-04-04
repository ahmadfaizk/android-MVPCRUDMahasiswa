package com.faiz.mahasiswaku.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faiz.mahasiswaku.view.adapter.MahasiswaAdapter;
import com.faiz.mahasiswaku.R;
import com.faiz.mahasiswaku.api.ApiClient;
import com.faiz.mahasiswaku.api.Sercvices;
import com.faiz.mahasiswaku.api.response.MultipleResponse;
import com.faiz.mahasiswaku.model.Mahasiswa;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvMahasiswa;
    FloatingActionButton fabAdd;
    ProgressBar progressBar;
    MahasiswaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMahasiswa = findViewById(R.id.rv_mahasiswa);
        fabAdd = findViewById(R.id.fab_add);
        progressBar = findViewById(R.id.progressBar);

        rvMahasiswa.setHasFixedSize(true);
        rvMahasiswa.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MahasiswaAdapter();
        rvMahasiswa.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvMahasiswa.addItemDecoration(decoration);
        rvMahasiswa.setItemAnimator(new DefaultItemAnimator());
        refreshData();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddUpdateMahasiswaActivity.class);
            startActivityForResult(intent, AddUpdateMahasiswaActivity.REQUEST_ADD);
        });

        adapter.setOnClickListener(mahasiswa -> {
            Intent intent = new Intent(MainActivity.this, AddUpdateMahasiswaActivity.class);
            intent.putExtra(AddUpdateMahasiswaActivity.EXTRA_MAHASISWA, mahasiswa);
            startActivityForResult(intent, AddUpdateMahasiswaActivity.REQUEST_UPDATE);
        });
    }

    public void refreshData() {
        showLoading(true);
        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Sercvices.class).getAllMahasiswa().enqueue(new Callback<MultipleResponse<Mahasiswa>>() {
            @Override
            public void onResponse(Call<MultipleResponse<Mahasiswa>> call, Response<MultipleResponse<Mahasiswa>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    ArrayList<Mahasiswa> list = response.body().getData();
                    if (list.isEmpty()) {
                        showSnackBar("Data Mahasiswa Kosong");
                    } else {
                        adapter.setListMahasiswa(list);
                    }
                }
            }

            @Override
            public void onFailure(Call<MultipleResponse<Mahasiswa>> call, Throwable t) {
                showLoading(false);
                showSnackBar(t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddUpdateMahasiswaActivity.REQUEST_ADD) {
            if (resultCode == AddUpdateMahasiswaActivity.RESULT_ADD) {
                refreshData();
                showSnackBar("Satu Mahasiswa Berhasil Ditambahkan");
            }
        }
        else if (requestCode == AddUpdateMahasiswaActivity.REQUEST_UPDATE) {
            if (resultCode == AddUpdateMahasiswaActivity.RESULT_UPDATE) {
                refreshData();
                showSnackBar("Satu Mahasiswa Berhasil Diubah");
            }
            else if (resultCode == AddUpdateMahasiswaActivity.RESULT_DELETE) {
                refreshData();
                showSnackBar("Satu Mahasiswa Berhasil Dihapus");
            }
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(rvMahasiswa, message, Snackbar.LENGTH_LONG).show();
    }

    private void showLoading(Boolean state) {
        if (state)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }
}

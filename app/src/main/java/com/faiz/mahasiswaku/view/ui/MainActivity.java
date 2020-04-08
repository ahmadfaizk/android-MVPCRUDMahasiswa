package com.faiz.mahasiswaku.view.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.faiz.mahasiswaku.R;
import com.faiz.mahasiswaku.model.Mahasiswa;
import com.faiz.mahasiswaku.presenter.MainContract;
import com.faiz.mahasiswaku.presenter.MainPresenter;
import com.faiz.mahasiswaku.view.adapter.MahasiswaAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.rv_mahasiswa) RecyclerView rvMahasiswa;
    @BindView(R.id.fab_add) FloatingActionButton fabAdd;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout swipeLayout;
    MahasiswaAdapter adapter;
    MainPresenter presenter;
    ProgressDialog progressDialog;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        presenter = new MainPresenter(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading ...");

        rvMahasiswa.setHasFixedSize(true);
        rvMahasiswa.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MahasiswaAdapter();
        rvMahasiswa.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvMahasiswa.addItemDecoration(decoration);
        presenter.onStart();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddUpdateMahasiswaActivity.class);
            startActivityForResult(intent, AddUpdateMahasiswaActivity.REQUEST_ADD);
        });

        adapter.setOnClickListener(mahasiswa -> {
            Intent intent = new Intent(this, AddUpdateMahasiswaActivity.class);
            intent.putExtra(AddUpdateMahasiswaActivity.EXTRA_MAHASISWA, mahasiswa);
            startActivityForResult(intent, AddUpdateMahasiswaActivity.REQUEST_UPDATE);
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.requestData();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onResult(requestCode, resultCode);
    }

    @Override
    public void refreshData(ArrayList<Mahasiswa> list) {
        adapter.setListMahasiswa(list);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showLoading(@NotNull Boolean state) {
        if (state)
            progressDialog.show();
        else
            progressDialog.dismiss();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rvMahasiswa, message, Snackbar.LENGTH_LONG).show();
    }
}

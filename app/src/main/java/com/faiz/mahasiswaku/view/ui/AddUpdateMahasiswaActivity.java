package com.faiz.mahasiswaku.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.faiz.mahasiswaku.R;
import com.faiz.mahasiswaku.api.ApiClient;
import com.faiz.mahasiswaku.api.Sercvices;
import com.faiz.mahasiswaku.api.response.SingleResponse;
import com.faiz.mahasiswaku.model.Mahasiswa;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddUpdateMahasiswaActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText edtNRP;
    private TextInputEditText edtNama;
    private TextInputEditText edtAlamat;
    private TextInputLayout contNRP;
    private TextInputLayout contNama;
    private TextInputLayout contAlamat;
    private ProgressBar progressBar;
    private Button btnAddUpdate;

    private boolean isUpdate = false;
    private Mahasiswa mahasiswa;

    public static final String EXTRA_MAHASISWA = "extra_mahasiswa";
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;
    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mahasiswa);

        edtNRP = findViewById(R.id.et_nrp);
        edtNama = findViewById(R.id.et_nama);
        edtAlamat = findViewById(R.id.et_alamat);
        contNRP = findViewById(R.id.cont_nrp);
        contNama = findViewById(R.id.cont_nama);
        contAlamat = findViewById(R.id.cont_alamat);
        btnAddUpdate = findViewById(R.id.btn_add);
        progressBar = findViewById(R.id.progressBar);

        mahasiswa = getIntent().getParcelableExtra(EXTRA_MAHASISWA);

        if (mahasiswa != null) {
            isUpdate = true;
        } else {
            mahasiswa = new Mahasiswa();
        }

        String actionBarTitle;
        String btnTitle;

        if (isUpdate) {
            actionBarTitle = "Ubah";
            btnTitle = "Update";

            edtNRP.setText(String.valueOf(mahasiswa.getNrp()));
            edtNama.setText(mahasiswa.getNama());
            edtAlamat.setText(mahasiswa.getAlamat());
        } else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        btnAddUpdate.setText(btnTitle);
        btnAddUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            String nrp = edtNRP.getText().toString().trim();
            String nama = edtNama.getText().toString().trim();
            String alamat = edtAlamat.getText().toString().trim();

            if (nrp.isEmpty()) {
                contNRP.setError("NRP Tidak Boleh Kosong");
                return;
            } else {
                contNRP.setError("");
            }
            if (nama.isEmpty()) {
                contNama.setError("Nama Tidak Boleh Kosong");
                return;
            } else {
                contNama.setError("");
            }
            if (alamat.isEmpty()) {
                contAlamat.setError("Alamat Tidak Boleh Kosong");
                return;
            } else {
                contAlamat.setError("");
            }

            int bil_nrp;
            try {
                bil_nrp = Integer.parseInt(nrp);
            } catch (NumberFormatException e) {
                contNRP.setError("NRP Harus Angka");
                return;
            }

            hideKeyboard();

            if (isUpdate) {
                updateMahasiswa(bil_nrp, nama, alamat);
            } else {
                createMahasiswa(bil_nrp, nama, alamat);
            }
        }
    }

    private void createMahasiswa(int nrp, String nama, String alamat) {
        showLoading(true);
        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Sercvices.class).createMahasiswa(nrp, nama, alamat).enqueue(new Callback<SingleResponse<Mahasiswa>>() {
            @Override
            public void onResponse(Call<SingleResponse<Mahasiswa>> call, Response<SingleResponse<Mahasiswa>> response) {
                boolean error = response.body().isError();
                showLoading(false);
                if (!error) {
                    setResult(RESULT_ADD);
                    finish();
                } else {
                    Toast.makeText(AddUpdateMahasiswaActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SingleResponse<Mahasiswa>> call, Throwable t) {
                t.printStackTrace();
                showMessage(t.getMessage());
                showLoading(false);
            }
        });
    }

    private void updateMahasiswa(int nrp, String nama, String alamat) {
        showLoading(true);
        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Sercvices.class).updateMahasiswa(mahasiswa.getId(), nrp, nama, alamat)
                .enqueue(new Callback<SingleResponse<Mahasiswa>>() {
                    @Override
                    public void onResponse(Call<SingleResponse<Mahasiswa>> call, Response<SingleResponse<Mahasiswa>> response) {
                        showLoading(false);
                        boolean error = response.body().isError();
                        if (!error) {
                            setResult(RESULT_UPDATE);
                            finish();
                        } else {
                            showMessage(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleResponse<Mahasiswa>> call, Throwable t) {
                        showLoading(false);
                        t.printStackTrace();
                        showMessage(t.getMessage());
                    }
                });
    }

    private void deleteMahasiswa() {
        showLoading(true);
        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Sercvices.class).deleteMahasiswa(mahasiswa.getId())
                .enqueue(new Callback<SingleResponse<Mahasiswa>>() {
                    @Override
                    public void onResponse(Call<SingleResponse<Mahasiswa>> call, Response<SingleResponse<Mahasiswa>> response) {
                        showLoading(false);
                        boolean error = response.body().isError();
                        if (!error) {
                            setResult(RESULT_DELETE);
                            finish();
                        } else {
                            showMessage(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleResponse<Mahasiswa>> call, Throwable t) {
                        t.printStackTrace();
                        showMessage(t.getMessage());
                        showLoading(false);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isUpdate) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    public void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;
        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        } else {
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
            dialogTitle = "Hapus Mahasiswa";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isDialogClose) {
                            finish();
                        } else {
                            deleteMahasiswa();
                        }
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLoading(Boolean state) {
        if (state)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

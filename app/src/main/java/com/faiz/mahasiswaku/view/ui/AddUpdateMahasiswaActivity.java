package com.faiz.mahasiswaku.view.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faiz.mahasiswaku.R;
import com.faiz.mahasiswaku.model.Mahasiswa;
import com.faiz.mahasiswaku.presenter.AddUpdateContract;
import com.faiz.mahasiswaku.presenter.AddUpdatePresenter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddUpdateMahasiswaActivity extends AppCompatActivity implements View.OnClickListener, AddUpdateContract.View {

    @BindView(R.id.et_nrp) TextInputEditText edtNRP;
    @BindView(R.id.et_nama) TextInputEditText edtNama;
    @BindView(R.id.et_alamat) TextInputEditText edtAlamat;
    @BindView(R.id.cont_nrp) TextInputLayout contNRP;
    @BindView(R.id.cont_nama) TextInputLayout contNama;
    @BindView(R.id.cont_alamat) TextInputLayout contAlamat;
    @BindView(R.id.imagePhoto) CircleImageView imgFoto;
    @BindView(R.id.btn_add) Button btnAddUpdate;

    private Mahasiswa mahasiswa;
    private Unbinder unbinder;
    private ProgressDialog progressDialog;
    private AddUpdatePresenter presenter;

    public static final String EXTRA_MAHASISWA = "extra_mahasiswa";
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;
    private final int ALERT_DIALOG_CLOSE = 10;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_MEDIA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mahasiswa);
        unbinder = ButterKnife.bind(this);
        mahasiswa = getIntent().getParcelableExtra(EXTRA_MAHASISWA);
        presenter = new AddUpdatePresenter(this, mahasiswa);
        presenter.start();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnAddUpdate.setOnClickListener(this);
        imgFoto.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            presenter.onSaveClicked();
        } else  if (v.getId() == R.id.imagePhoto) {
            openDialogSelectImage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mahasiswa != null) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int ALERT_DIALOG_DELETE = 20;
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
                .setPositiveButton("Ya", (dialog, id) -> {
                    if (isDialogClose) {
                        finish();
                    } else {
                        presenter.delete();
                    }
                })
                .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void openDialogSelectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Gambar dari")
                .setItems(R.array.arr_media, (dialog, which) -> {
                    if (which == 0) {
                        presenter.dispatchTakePictureIntent();
                    } else if (which == 1) {
                        presenter.getImageFromMedia();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onResult(requestCode, resultCode, data);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void setButtonText(String text) {
        btnAddUpdate.setText(text);
    }

    @Override
    public String getNrp() {
        return String.valueOf(edtNRP.getText());
    }

    @Override
    public String getName() {
        return String.valueOf(edtNama.getText());
    }

    @Override
    public String getAddress() {
        return String.valueOf(edtAlamat.getText());
    }

    @Override
    public void setNrp(int nrp) {
        edtNRP.setText(String.valueOf(nrp));
    }

    @Override
    public void setName(String name) {
        edtNama.setText(name);
    }

    @Override
    public void setAddress(String address) {
        edtAlamat.setText(address);
    }

    @Override
    public void setErrorNrp(String error) {
        contNRP.setError(error);
    }

    @Override
    public void setErrorName(String error) {
        contNama.setError(error);
    }

    @Override
    public void setErrorAddress(String error) {
        contAlamat.setError(error);
    }

    @Override
    public void setImage(File file) {
        Glide.with(this)
                .load(file)
                .placeholder(R.drawable.ic_user)
                .into(imgFoto);
    }

    @Override
    public void setImage(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_user)
                .into(imgFoto);
    }

    @Override
    public void showLoading(Boolean state) {
        if (state)
            progressDialog.show();
        else
            progressDialog.dismiss();
    }

    @Override
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}

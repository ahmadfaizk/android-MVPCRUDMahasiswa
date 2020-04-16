package com.faiz.mahasiswaku.presenter;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.faiz.mahasiswaku.api.ApiClient;
import com.faiz.mahasiswaku.api.Sercvices;
import com.faiz.mahasiswaku.api.response.SingleResponse;
import com.faiz.mahasiswaku.model.Error;
import com.faiz.mahasiswaku.model.Mahasiswa;
import com.faiz.mahasiswaku.view.ui.AddUpdateMahasiswaActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

public class AddUpdatePresenter implements AddUpdateContract.Presenter {

    private AddUpdateContract.View view;
    private Mahasiswa mahasiswa;
    private String currentPhotoPath;
    private boolean isUpdate;

    public AddUpdatePresenter(AddUpdateContract.View view, Mahasiswa mahasiswa) {
        this.view = view;
        this.mahasiswa = mahasiswa;
        isUpdate = false;
    }

    @Override
    public void start() {
        if (mahasiswa != null) {
            view.setImage(ApiClient.getImageUrl(mahasiswa.getFoto()));
            view.setNrp(mahasiswa.getNrp());
            view.setName(mahasiswa.getNama());
            view.setAddress(mahasiswa.getAlamat());
            view.setActionBarTitle("Ubah");
            view.setButtonText("Update");
            isUpdate = true;
        }
    }

    @Override
    public void onSaveClicked() {
        boolean ready = true;
        String nrpS = view.getNrp();
        String name = view.getName();
        String address = view.getAddress();

        if (nrpS.isEmpty()) {
            view.setErrorNrp("NRP Tidak boleh kosong");
            ready = false;
        } else {
            view.setErrorNrp("");
        }
        if (name.isEmpty()) {
            view.setErrorName("Nama Tidak boleh kosong");
            ready = false;
        } else {
            view.setErrorName("");
        }
        if (address.isEmpty()) {
            view.setErrorAddress("Alamat Tidak boleh kosong");
            ready = false;
        } else {
            view.setErrorAddress("");
        }

        int nrp = 0;
        try {
            nrp = Integer.parseInt(nrpS);
            view.setErrorNrp("");
        } catch (NumberFormatException e) {
            view.setErrorNrp("Format NRP Salah");
            ready = false;
        }

        view.hideKeyboard();

        if (currentPhotoPath == null && !isUpdate) {
            view.showMessage("Gambar belum dipilih");
            ready = false;
        }

        Mahasiswa mahasiswa = new Mahasiswa(nrp, name, address);

        if (ready) {
            if (isUpdate) {
                update(mahasiswa);
            } else {
                create(mahasiswa);
            }
        }
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddUpdateMahasiswaActivity.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            view.setImage(file);
        }
        else if (requestCode == AddUpdateMahasiswaActivity.REQUEST_IMAGE_MEDIA && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            currentPhotoPath = getRealPathFromUri(uri);
            File file = new File(currentPhotoPath);
            view.setImage(file);
        }
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(view.getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                view.showMessage(ex.getMessage());
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(view.getBaseContext(),
                        "com.faiz.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                view.startActivityForResult(takePictureIntent, AddUpdateMahasiswaActivity.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void getImageFromMedia() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        view.startActivityForResult(intent, AddUpdateMahasiswaActivity.REQUEST_IMAGE_MEDIA);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = view.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String getRealPathFromUri(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(view.getBaseContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public void create(Mahasiswa mahasiswa) {
        view.showLoading(true);
        File photo = new File(currentPhotoPath);
        RequestBody requestBody = RequestBody.create(photo, MediaType.parse("image/*"));
        MultipartBody.Part foto = MultipartBody.Part.createFormData("foto", photo.getName(), requestBody);

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("nrp", createPartFromInt(mahasiswa.getNrp()));
        map.put("nama", createPartFromString(mahasiswa.getNama()));
        map.put("alamat", createPartFromString(mahasiswa.getAlamat()));

        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Sercvices.class)
                .createMahasiswa(foto, map)
                .enqueue(new Callback<SingleResponse<Mahasiswa>>() {
                    @Override
                    public void onResponse(Call<SingleResponse<Mahasiswa>> call, Response<SingleResponse<Mahasiswa>> response) {
                        view.showLoading(false);
                        assert response.body() != null;
                        boolean error = response.body().isError();
                        if (!error) {
                            view.setResult(AddUpdateMahasiswaActivity.RESULT_ADD);
                            view.finish();
                        } else {
                            view.showMessage(response.body().getMessage());
                            setErrorRequest(response.body().getErrors());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleResponse<Mahasiswa>> call, Throwable t) {
                        view.showLoading(false);
                        t.printStackTrace();
                        view.showMessage(t.getMessage());
                    }
                });
    }

    @Override
    public void update(Mahasiswa mahasiswa) {
        view.showLoading(true);
        MultipartBody.Part foto = null;
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/*"));
            foto = MultipartBody.Part.createFormData("foto", file.getName(), requestBody);
        }

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("id", createPartFromInt(this.mahasiswa.getId()));
        map.put("nrp", createPartFromInt(mahasiswa.getNrp()));
        map.put("nama", createPartFromString(mahasiswa.getNama()));
        map.put("alamat", createPartFromString(mahasiswa.getAlamat()));

        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Sercvices.class)
                .updateMahasiswa(foto, map)
                .enqueue(new Callback<SingleResponse<Mahasiswa>>() {
                    @Override
                    public void onResponse(Call<SingleResponse<Mahasiswa>> call, Response<SingleResponse<Mahasiswa>> response) {
                        view.showLoading(false);
                        assert response.body() != null;
                        boolean error = response.body().isError();
                        if (!error) {
                            view.setResult(AddUpdateMahasiswaActivity.RESULT_UPDATE);
                            view.finish();
                        } else {
                            view.showMessage(response.body().getMessage());
                            setErrorRequest(response.body().getErrors());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleResponse<Mahasiswa>> call, Throwable t) {
                        view.showLoading(false);
                        t.printStackTrace();
                        view.showMessage(t.getMessage());
                    }
                });
    }

    private void setErrorRequest(Error error) {
        if (error.getNrp() != null) {
            view.setErrorNrp(error.getNrp().get(0));
        }
        if (error.getNama() != null) {
            view.setErrorName(error.getNama().get(0));
        }
        if (error.getAlamat() != null) {
            view.setErrorAddress(error.getAlamat().get(0));
        }
        if (error.getFoto() != null) {
            view.showMessage(error.getFoto().get(0));
        }
    }

    @Override
    public void delete() {
        view.showLoading(true);
        Retrofit retrofit = ApiClient.getClient();
        retrofit.create(Sercvices.class).deleteMahasiswa(mahasiswa.getId())
                .enqueue(new Callback<SingleResponse<Mahasiswa>>() {
                    @Override
                    public void onResponse(Call<SingleResponse<Mahasiswa>> call, Response<SingleResponse<Mahasiswa>> response) {
                        view.showLoading(false);
                        boolean error = response.body().isError();
                        if (!error) {
                            view.setResult(AddUpdateMahasiswaActivity.RESULT_DELETE);
                            view.finish();
                        } else {
                            view.showMessage(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleResponse<Mahasiswa>> call, Throwable t) {
                        view.showLoading(false);
                        t.printStackTrace();
                        view.showMessage(t.getMessage());
                    }
                });
    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(descriptionString, okhttp3.MultipartBody.FORM);
    }

    private RequestBody createPartFromInt(int descriptionInt) {
        return RequestBody.create(String.valueOf(descriptionInt), MultipartBody.FORM);
    }
}

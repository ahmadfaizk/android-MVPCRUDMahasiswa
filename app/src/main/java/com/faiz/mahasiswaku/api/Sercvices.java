package com.faiz.mahasiswaku.api;

import com.faiz.mahasiswaku.api.response.MultipleResponse;
import com.faiz.mahasiswaku.api.response.SingleResponse;
import com.faiz.mahasiswaku.model.Mahasiswa;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Sercvices {
    @GET("mahasiswa")
    Call<MultipleResponse<Mahasiswa>> getAllMahasiswa();

    @Multipart
    @POST("mahasiswa/create")
    Call<SingleResponse<Mahasiswa>> createMahasiswa(@Part MultipartBody.Part foto,
                                                    @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("mahasiswa/update")
    Call<SingleResponse<Mahasiswa>> updateMahasiswa(@Part MultipartBody.Part foto,
                                                    @PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("mahasiswa/delete")
    Call<SingleResponse<Mahasiswa>> deleteMahasiswa(@Field("id") int id);
}

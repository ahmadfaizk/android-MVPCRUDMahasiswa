package com.faiz.mahasiswaku.api;

import com.faiz.mahasiswaku.api.response.MultipleResponse;
import com.faiz.mahasiswaku.api.response.SingleResponse;
import com.faiz.mahasiswaku.model.Mahasiswa;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Sercvices {
    @GET("mahasiswa")
    Call<MultipleResponse<Mahasiswa>> getAllMahasiswa();

    @FormUrlEncoded
    @POST("mahasiswa/create")
    Call<SingleResponse<Mahasiswa>> createMahasiswa(@Field("nrp") int nrp,
                                                    @Field("nama") String nama,
                                                    @Field("alamat") String alamat);

    @FormUrlEncoded
    @POST("mahasiswa/update")
    Call<SingleResponse<Mahasiswa>> updateMahasiswa(@Field("id") int id,
                                                    @Field("nrp") int nrp,
                                                    @Field("nama") String nama,
                                                    @Field("alamat") String alamat);

    @FormUrlEncoded
    @POST("mahasiswa/delete")
    Call<SingleResponse<Mahasiswa>> deleteMahasiswa(@Field("id") int id);
}

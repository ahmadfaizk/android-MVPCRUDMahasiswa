package com.faiz.mahasiswaku.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Mahasiswa implements Parcelable {
    private int id;
    private int nrp;
    private String nama;
    private String alamat;
    private String foto;

    public Mahasiswa() {
    }

    public Mahasiswa(int nrp, String nama, String alamat) {
        this.nrp = nrp;
        this.nama = nama;
        this.alamat = alamat;
    }

    protected Mahasiswa(Parcel in) {
        id = in.readInt();
        nrp = in.readInt();
        nama = in.readString();
        alamat = in.readString();
        foto = in.readString();
    }

    public static final Creator<Mahasiswa> CREATOR = new Creator<Mahasiswa>() {
        @Override
        public Mahasiswa createFromParcel(Parcel in) {
            return new Mahasiswa(in);
        }

        @Override
        public Mahasiswa[] newArray(int size) {
            return new Mahasiswa[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNrp() {
        return nrp;
    }

    public void setNrp(int nrp) {
        this.nrp = nrp;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(nrp);
        dest.writeString(nama);
        dest.writeString(alamat);
        dest.writeString(foto);
    }
}

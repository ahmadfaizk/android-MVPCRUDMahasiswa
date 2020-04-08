package com.faiz.mahasiswaku.model;

import java.util.ArrayList;

public class Error {
    private ArrayList<String> nrp;
    private ArrayList<String> nama;
    private ArrayList<String> alamat;
    private ArrayList<String> foto;

    public ArrayList<String> getNrp() {
        return nrp;
    }

    public void setNrp(ArrayList<String> nrp) {
        this.nrp = nrp;
    }

    public ArrayList<String> getNama() {
        return nama;
    }

    public void setNama(ArrayList<String> nama) {
        this.nama = nama;
    }

    public ArrayList<String> getAlamat() {
        return alamat;
    }

    public void setAlamat(ArrayList<String> alamat) {
        this.alamat = alamat;
    }

    public ArrayList<String> getFoto() {
        return foto;
    }

    public void setFoto(ArrayList<String> foto) {
        this.foto = foto;
    }
}

package com.example.reporttdm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultKas {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("id_toko")
    @Expose
    private String idToko;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("id_akun_kas")
    @Expose
    private String idAkunKas;
    @SerializedName("nominal")
    @Expose
    private String nominal;
    @SerializedName("keterangan")
    @Expose
    private String keterangan;
    @SerializedName("tanggal")
    @Expose
    private String tanggal;
    @SerializedName("jenis")
    @Expose
    private String jenis;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdToko() {
        return idToko;
    }

    public void setIdToko(String idToko) {
        this.idToko = idToko;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIdAkunKas() {
        return idAkunKas;
    }

    public void setIdAkunKas(String idAkunKas) {
        this.idAkunKas = idAkunKas;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}

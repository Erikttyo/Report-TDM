package com.example.reporttdm.model;

public class ResultPembelianItem{
	private String id;
	private String id_toko;
	private String id_transaksi;
	private String id_barang;
	private String jumlah;
	private String harga_jual;
	private String harga_dasar;
	private String tanggal;
	private String modal;
	private String jual;
	private String tipe;
	private String kode_barang;
	private String nama_barang;
	private String kategori_barang;
	private String stok;

	public ResultPembelianItem(String id, String id_toko, String id_transaksi, String id_barang, String jumlah, String harga_jual, String harga_dasar, String tanggal, String modal, String jual, String tipe, String kode_barang, String nama_barang, String kategori_barang, String stok) {
		this.id = id;
		this.id_toko = id_toko;
		this.id_transaksi = id_transaksi;
		this.id_barang = id_barang;
		this.jumlah = jumlah;
		this.harga_jual = harga_jual;
		this.harga_dasar = harga_dasar;
		this.tanggal = tanggal;
		this.modal = modal;
		this.jual = jual;
		this.tipe = tipe;
		this.kode_barang = kode_barang;
		this.nama_barang = nama_barang;
		this.kategori_barang = kategori_barang;
		this.stok = stok;
	}

	// Getter Methods

	public String getId() {
		return id;
	}

	public String getId_toko() {
		return id_toko;
	}

	public String getId_transaksi() {
		return id_transaksi;
	}

	public String getId_barang() {
		return id_barang;
	}

	public String getJumlah() {
		return jumlah;
	}

	public String getHarga_jual() {
		return harga_jual;
	}

	public String getHarga_dasar() {
		return harga_dasar;
	}

	public String getTanggal() {
		return tanggal;
	}

	public String getModal() {
		return modal;
	}

	public String getJual() {
		return jual;
	}

	public String getTipe() {
		return tipe;
	}

	public String getKode_barang() {
		return kode_barang;
	}

	public String getNama_barang() {
		return nama_barang;
	}

	public String getKategori_barang() {
		return kategori_barang;
	}

	public String getStok() {
		return stok;
	}

	// Setter Methods

	public void setId(String id) {
		this.id = id;
	}

	public void setId_toko(String id_toko) {
		this.id_toko = id_toko;
	}

	public void setId_transaksi(String id_transaksi) {
		this.id_transaksi = id_transaksi;
	}

	public void setId_barang(String id_barang) {
		this.id_barang = id_barang;
	}

	public void setJumlah(String jumlah) {
		this.jumlah = jumlah;
	}

	public void setHarga_jual(String harga_jual) {
		this.harga_jual = harga_jual;
	}

	public void setHarga_dasar(String harga_dasar) {
		this.harga_dasar = harga_dasar;
	}

	public void setTanggal(String tanggal) {
		this.tanggal = tanggal;
	}

	public void setModal(String modal) {
		this.modal = modal;
	}

	public void setJual(String jual) {
		this.jual = jual;
	}

	public void setTipe(String tipe) {
		this.tipe = tipe;
	}

	public void setKode_barang(String kode_barang) {
		this.kode_barang = kode_barang;
	}

	public void setNama_barang(String nama_barang) {
		this.nama_barang = nama_barang;
	}

	public void setKategori_barang(String kategori_barang) {
		this.kategori_barang = kategori_barang;
	}

	public void setStok(String stok) {
		this.stok = stok;
	}
}

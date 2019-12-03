package com.example.reporttdm.service;

import com.example.reporttdm.model.Barang;
import com.example.reporttdm.model.GetBarangResponse;
import com.example.reporttdm.model.GetKategoriResponse;
import com.example.reporttdm.model.GetPelSupResponse;
import com.example.reporttdm.model.GetPembelianResponse;
import com.example.reporttdm.model.GetPenjualanResponse;
import com.example.reporttdm.model.GetPesananResponse;
import com.example.reporttdm.model.GetProfileResponse;
import com.example.reporttdm.model.GetTokoResponse;
import com.example.reporttdm.model.GetTransaksiResponse;
import com.example.reporttdm.model.GetUserResponse;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.LoginResponse;
import com.example.reporttdm.model.Pembelian;
import com.example.reporttdm.model.Pemesanan;
import com.example.reporttdm.model.RegisterResponse;
import com.example.reporttdm.model.UploadImageResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIService {

    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register(@Field("nama") String nama,
                                    @Field("email") String email,
                                    @Field("telephone") String telephone,
                                    @Field("alamat") String alamat,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("registerToko.php")
    Call<InsertResponse> registerToko(@Field("nama") String nama,
                                      @Field("id_user") int id_user,
                                      @Field("telephone") String telephone,
                                      @Field("nama_pemilik") String nama_pemilik,
                                      @Field("lokasi") String lokasi);

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(@Field("email") String email,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST("getUser.php")
    Call<GetProfileResponse> getUser(@Field("id") String id);

    @FormUrlEncoded
    @POST("updateUser.php")
    Call<InsertResponse> updateUser(@Field("id") String id,
                                    @Field("nama") String nama,
                                    @Field("email") String email,
                                    @Field("telephone") String telephone,
                                    @Field("alamat") String alamat,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("showBarang.php")
    Call<GetBarangResponse> showBarang(@Field("id_toko") String id_toko);

    @POST("addPenjualan.php")
    Call<GetPenjualanResponse> addPenjualan(@Query("username") String username,
                                            @Body List<Barang> barangList);

    @FormUrlEncoded
    @POST("addBarang.php")
    Call<InsertResponse> addBarang(@Field("id_toko") String id_toko,
                                   @Field("id_kategori") String id_kategori,
                                   @Field("nama") String nama,
                                   @Field("kode") String kode,
                                   @Field("harga") String harga,
                                   @Field("stok") String stok,
                                   @Field("harga_jual") String harga_jual,
                                   @Field("diskon") String diskon,
                                   @Field("satuan") String satuan,
                                   @Field("image") String image);

    @FormUrlEncoded
    @POST("updateBarang.php")
    Call<InsertResponse> updateBarang(@Field("id") String id,
                                      @Field("id_kategori") String id_kategori,
                                      @Field("nama") String nama,
                                      @Field("kode") String kode,
                                      @Field("harga") String harga,
                                      @Field("stok") String stok,
                                      @Field("harga_jual") String harga_jual,
                                      @Field("diskon") String diskon,
                                      @Field("satuan") String satuan,
                                      @Field("image") String image);

    @FormUrlEncoded
    @POST("showKategori.php")
    Call<GetKategoriResponse> showKategori(@Field("id_toko") String id_toko);

    @FormUrlEncoded
    @POST("addKategori.php")
    Call<InsertResponse> addKategori(@Field("id_toko") String id_toko,
                                     @Field("nama") String nama);

    @POST("addTransaksi.php")
    Call<InsertResponse> addTransaksi(@Body Pembelian pembelian);

    @FormUrlEncoded
    @POST("showListPelSup.php")
    Call<GetPelSupResponse> showListPelSup(@Field("id_toko") String id_toko);

    @FormUrlEncoded
    @POST("addPelSup.php")
    Call<InsertResponse> addPelSup(@Field("id_toko") String id_toko,
                                   @Field("nama") String nama,
                                   @Field("telephone") String telephone,
                                   @Field("alamat") String alamat,
                                   @Field("tipe") String tipe);

    @FormUrlEncoded
    @POST("updatePelsup.php")
    Call<InsertResponse> updatePelsup(@Field("id_pelsup") String id_pelsup,
                                      @Field("nama") String nama,
                                      @Field("telephone") String telephone,
                                      @Field("alamat") String alamat);


    @FormUrlEncoded
    @POST("getToko.php")
    Call<GetTokoResponse> getToko(@Field("id") String id);



    @FormUrlEncoded
    @POST("updateToko.php")
    Call<InsertResponse> updateToko(@Field("id") String id,
                                    @Field("nama") String nama,
                                    @Field("telephone") String telephone,
                                    @Field("nama_pemilik") String nama_pemilik,
                                    @Field("lokasi") String lokasi);

    @FormUrlEncoded
    @POST("showPenjualan.php")
    Call<GetPembelianResponse> showPenjualan(@Field("id_toko") String id_toko);

    @FormUrlEncoded
    @POST("showPembelian.php")
    Call<GetPembelianResponse> showPembelian(@Field("id_toko") String id_toko);

    @FormUrlEncoded
    @POST("showTransaksi.php")
    Call<GetTransaksiResponse> showTransaksi(@Field("id_toko") String id_toko);

    @FormUrlEncoded
    @POST("addUser.php")
    Call<InsertResponse> addUser(@Field("nama") String nama,
                                 @Field("id_toko") String id_toko,
                                 @Field("telephone") String telephone,
                                 @Field("email") String email,
                                 @Field("alamat") String alamat,
                                 @Field("tipe") String tipe,
                                 @Field("password") String password);

    @FormUrlEncoded
    @POST("showUser.php")
    Call<GetUserResponse> showUser(@Field("id_toko") String id_toko);

    @POST("addPesanan.php")
    Call<InsertResponse> addPesanan(@Body Pemesanan pemesanan);

    @FormUrlEncoded
    @POST("showPesanan.php")
    Call<GetPesananResponse> showPesanan(@Field("id_toko") String id_toko);

    @FormUrlEncoded
    @POST("deletePesanan.php")
    Call<InsertResponse> deletePesanan(@Field("id_transaksi") String id_transaksi);



    @FormUrlEncoded
    @POST("deleteBarang.php")
    Call<InsertResponse> deleteBarang(@Field("id_barang") String id_barang);

    @FormUrlEncoded
    @POST("deleteKategori.php")
    Call<InsertResponse>  deleteKategori(@Field("id_kategori") String id_kategori);

    @FormUrlEncoded
    @POST("deletePelSup.php")
    Call<InsertResponse>  deletePelSup(@Field("id_pelsup") String id_pelsup);

    @FormUrlEncoded
    @POST("resetData.php")
    Call<InsertResponse>  resetData(@Field("id_toko") String id_toko);

    @Multipart
    @POST("uploadImage.php")
    Call<UploadImageResponse> uploadImage(@Part MultipartBody.Part fileToUpload);

    @FormUrlEncoded
    @POST("showListTransaksi.php")
    Call<GetBarangResponse> showListTransaksi(@Field("id_toko") String id_toko,
                                              @Field("id_transaksi") String id_transaksi);




}

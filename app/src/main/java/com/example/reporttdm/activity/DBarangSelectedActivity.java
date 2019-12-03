package com.example.reporttdm.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.reporttdm.R;
import com.example.reporttdm.helper.NumberTextWatcherForThousand;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.Barang;
import com.example.reporttdm.model.GetKategoriResponse;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.KategoriBarang;
import com.example.reporttdm.model.UploadImageResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DBarangSelectedActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Toolbar toolbar;
    private TinyDB tinyDB;
    private List<KategoriBarang> kategoriBarangList = new ArrayList<KategoriBarang>();
    private Spinner spinKategori;
    private EditText edtNama, edtStok, edtKode, edtHD, edtHJ, edtDiskon, edtSatuan, edtKet;
    private Button btnSimpan, btnDelete;
    private List<Barang> barangList = new ArrayList<Barang>();
    private String nama,stok,kode,hd,hj,diskon,kategori,id_kat,tipe,id_barang,satuan;
    private List<String> listNama = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;
    private ProgressDialog dialog;
    private ImageView imgPhoto;
    private String imageUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbarang_selected);

        tinyDB = new TinyDB(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Harap tunggu...");

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Barang");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        edtNama = (EditText)findViewById(R.id.edt_nama_dbarangs);
        edtStok = (EditText)findViewById(R.id.edt_stock_dbarangs);
        edtSatuan = (EditText)findViewById(R.id.edt_satuan_dbarangs);
        edtKode = (EditText)findViewById(R.id.edt_kode_dbarangs);
        edtHD = (EditText)findViewById(R.id.edt_hd_dbarangs);
        edtHD.addTextChangedListener(new NumberTextWatcherForThousand(edtHD));
        edtHJ = (EditText)findViewById(R.id.edt_hj_dbarangs);
        edtHJ.addTextChangedListener(new NumberTextWatcherForThousand(edtHJ));
        edtDiskon = (EditText)findViewById(R.id.edt_diskon_dbarangs);
        edtKet = findViewById(R.id.edt_ket_dbarangs);
        spinKategori = (Spinner)findViewById(R.id.spin_kat_dbarangs);
        btnSimpan = (Button)findViewById(R.id.btn_simpan_dbarangs);
        btnDelete = (Button)findViewById(R.id.btn_delete_dbarangs);
        imgPhoto = findViewById(R.id.img_photo_barang);

        if (getIntent().hasExtra("tipe")){
            if ((getIntent().getStringExtra("tipe")).equals("1")){
                tipe = "1";
                edtNama.setText(getIntent().getStringExtra("nama"));
                edtStok.setText(getIntent().getStringExtra("stok"));
                edtKode.setText(getIntent().getStringExtra("kode"));
                edtHD.setText(getIntent().getStringExtra("hd"));
                edtHJ.setText(getIntent().getStringExtra("hj"));
                edtDiskon.setText(getIntent().getStringExtra("diskon"));
                kategori = getIntent().getStringExtra("kategori");
                id_barang = getIntent().getStringExtra("id_barang");
                edtSatuan.setText(getIntent().getStringExtra("satuan"));
                imageUrl = getIntent().getStringExtra("image");
            }
        }else {
            tipe = "0";
        }

        spinKategori.setOnItemSelectedListener(this);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listNama);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinKategori.setAdapter(arrayAdapter);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(edtNama)||isEmpty(edtStok)||isEmpty(edtKode)||isEmpty(edtHD)||
                        isEmpty(edtHJ)||(spinKategori.getSelectedItemPosition()==0)){
                    Toast.makeText(DBarangSelectedActivity.this,"Harap isi semua kolom",Toast.LENGTH_SHORT).show();
                }else if (!(Integer.parseInt(edtStok.getText().toString())<0)){
                    Toast.makeText(DBarangSelectedActivity.this,"Harap tunggu...",Toast.LENGTH_SHORT).show();
                    if (!tipe.equals("1")) {
                        addBarang();
                    }else{
                        updateBarang();
                    }
                }else {
                    Toast.makeText(DBarangSelectedActivity.this,"Stok harus lebih dari 0",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteBarang();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(DBarangSelectedActivity.this);
                builder.setMessage("Apakah anda yakin ingin hapus ?").setPositiveButton("Ya", dialogClickListener)
                        .setNegativeButton("Tidak", dialogClickListener).show();
            }
        });
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImgPicker();
            }
        });

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .error(R.drawable.ic_picture)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(DBarangSelectedActivity.this)
                .load(imageUrl)
                .apply(options)
                .into(imgPhoto);

        getListKategori();
    }

    private void showImgPicker(){
        ImagePicker.create(DBarangSelectedActivity.this)
                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                .includeVideo(false) // Show video on image picker
                .single() // single mode
                .start(); // start image picker activity with request code
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            /*List<Image> images = ImagePicker.getImages(data);*/
            dialog.show();
            //get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);

            final Uri fileUri;
            File mediaFile = new File(image.getPath());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileUri = FileProvider.getUriForFile(DBarangSelectedActivity.this, DBarangSelectedActivity.this.getApplicationContext().getPackageName() + ".my.package.name.provider", mediaFile);
            }else{
                fileUri = Uri.fromFile(mediaFile);
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mediaFile);

            MultipartBody.Part multipartBody =MultipartBody.Part.createFormData("fileToUpload",image.getName(),requestFile);

            //Call<ResponseBody> responseBodyCall = service.addRecord(token, userId, "fileName", multipartBody);
            APIService api = RetrofitHelper.getClient().create(APIService.class);
            Call<UploadImageResponse> call = api.uploadImage(multipartBody);
            call.enqueue(new Callback<UploadImageResponse>() {
                @Override
                public void onResponse(Call<UploadImageResponse> call, Response<UploadImageResponse> response) {
                    if (response.body().getStatus_code().equals("1")) {
                        imgPhoto.setImageURI(fileUri);
                        Toast.makeText(DBarangSelectedActivity.this,"Perubahan gambar sukses",Toast.LENGTH_SHORT).show();
                        System.out.println("Upload berhasil");
                        imageUrl = response.body().getUrl();
                        dialog.dismiss();
                    }else{
                        dialog.dismiss();
                        Toast.makeText(DBarangSelectedActivity.this,"Error : "+response.body().getStatus(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if((spinner.getId() == R.id.spin_kat_dbarangs)&&spinner.getSelectedItemPosition()>0) {
            int s = spinner.getSelectedItemPosition();
            kategori = kategoriBarangList.get(s-1).getId();
            Toast.makeText(DBarangSelectedActivity.this,"Kategori "+kategoriBarangList.get(s-1).getNama(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void getListKategori(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetKategoriResponse> call = api.showKategori(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetKategoriResponse>() {
            @Override
            public void onResponse(Call<GetKategoriResponse> call, Response<GetKategoriResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    System.out.println("masuk kategoori");
                    listNama.clear();
                    kategoriBarangList.addAll(response.body().getResult_kategori());
                    listNama.add("Pilih Kategori");
                    int i = 0;
                    int temp = 0;
                    for (KategoriBarang kb : response.body().getResult_kategori()){
                        if (kb.getId().equals(kategori)){
                            temp = i;
                        }
                        listNama.add(kb.getNama());
                        i++;
                    }
                    arrayAdapter.notifyDataSetChanged();
                    if (tipe.equals("1")) {
                        spinKategori.setSelection(temp + 1);
                    }else{
                        spinKategori.setSelection(0);
                    }
                }
            }
            @Override
            public void onFailure(Call<GetKategoriResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
//                    swipeContainer.setRefreshing(false);
                    Toast.makeText(DBarangSelectedActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addBarang() {
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.addBarang(((User) tinyDB.getObject("user_login", User.class)).getId_toko(), kategori,
                edtNama.getText().toString(), edtKode.getText().toString(), edtHD.getText().toString().replace(",",""),
                edtStok.getText().toString(), edtHJ.getText().toString().replace(",",""),
                edtDiskon.getText().toString().equals("") ? "0":edtDiskon.getText().toString(),
                edtSatuan.getText().toString(),imageUrl);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(DBarangSelectedActivity.this, "Tambah barang berhasil", Toast.LENGTH_SHORT).show();
                    edtNama.setText("");
                    edtDiskon.setText("");
                    edtHD.setText("");
                    edtHJ.setText("");
                    edtKode.setText("");
                    edtStok.setText("");
                    edtSatuan.setText("");
                    spinKategori.setSelection(0);
                    edtKet.setText("");

                }else{
                    Toast.makeText(DBarangSelectedActivity.this, "Tambah barang gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
//                    swipeContainer.setRefreshing(false);
                    Toast.makeText(DBarangSelectedActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateBarang() {
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.updateBarang(id_barang, kategori,
                edtNama.getText().toString(), edtKode.getText().toString(), edtHD.getText().toString().replace(",",""),
                edtStok.getText().toString(), edtHJ.getText().toString().replace(",",""),
                edtDiskon.getText().toString().equals("") ? "0":edtDiskon.getText().toString(),
                edtSatuan.getText().toString(),imageUrl);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(DBarangSelectedActivity.this, "Update barang berhasil", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(DBarangSelectedActivity.this, "Update barang gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
//                    swipeContainer.setRefreshing(false);
                    Toast.makeText(DBarangSelectedActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteBarang() {
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.deleteBarang(id_barang);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(DBarangSelectedActivity.this, "Delete barang berhasil", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }else{
                    dialog.dismiss();
                    Toast.makeText(DBarangSelectedActivity.this, "Delete barang gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(DBarangSelectedActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

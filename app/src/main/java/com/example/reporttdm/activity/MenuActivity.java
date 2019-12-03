package com.example.reporttdm.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.fragment.DatabaseFragment;
import com.example.reporttdm.fragment.LaporanFragment;
import com.example.reporttdm.fragment.PengaturanFragment;
import com.example.reporttdm.fragment.TransaksiFragment;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.User;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ImageView imgProfile;
    private TextView txProfile;
    private TinyDB tinyDB;
    private List<User> listUser = new ArrayList<User>();
    Fragment fragment = null;
    Class fragmentClass = null;
    NavigationView navigationView;
    private String flag = "";
    public static Activity menuActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menuActivity = this;

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        imgProfile = (ImageView) headerView.findViewById(R.id.img_profile_nav);
        txProfile = (TextView) headerView.findViewById(R.id.tx_profile_nav);
        try {
            txProfile.setText(((User)tinyDB.getObject("user_login",User.class)).getNama());
        } catch (Exception e) {
            e.printStackTrace();
        }
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this,ProfileActivity.class));
            }
        });
        txProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this,ProfileActivity.class));
            }
        });

        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        if (getIntent().hasExtra("flag")){
            flag = getIntent().getStringExtra("flag");
        }

        if (flag.equals("")) {
            fragmentClass = DatabaseFragment.class;
            try {
                setTitle("Database");
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            navigationView.setCheckedItem(R.id.nav_1);
        }else if (flag.equals("1")){
            fragmentClass = TransaksiFragment.class;
            try {
                setTitle("Transaksi");
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            navigationView.setCheckedItem(R.id.nav_2);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MenuActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    public void resetTransaksi(){
        fragmentClass = TransaksiFragment.class;
        try {
            setTitle("Transaksi");
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        navigationView.setCheckedItem(R.id.nav_2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        assert mDrawer != null;
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_1) {
            toolbar.setTitle("Database");
            fragmentClass = DatabaseFragment.class;
        }else if(id == R.id.nav_2){
            toolbar.setTitle("Transaksi");
            fragmentClass = TransaksiFragment.class;
        }else if(id == R.id.nav_3){
            toolbar.setTitle("Laporan");
            fragmentClass = LaporanFragment.class;
        }else if(id == R.id.nav_4){
            toolbar.setTitle("Pengaturan");
            fragmentClass = PengaturanFragment.class;
        }else if(id == R.id.nav_5){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            startActivity(new Intent(MenuActivity.this,LoginActivity.class));
                            finish();
                            tinyDB.putObject("user_login",null);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
            builder.setMessage("Apakah anda yakin logout ?").setPositiveButton("Ya", dialogClickListener)
                    .setNegativeButton("Tidak", dialogClickListener).show();
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        item.setChecked(true);
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MenuActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            2);
                } else {
                    Toast.makeText(MenuActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MenuActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            3);
                } else {
                    Toast.makeText(MenuActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(MenuActivity.this,LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(MenuActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}

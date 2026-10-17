package com.example.mylaundry;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtProfileName;
    private TextView txtProfileEmail;
    private TextView txtUsernameProfile;
    private TextView txtEmailProfile;

    private SharedPreferences userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Menghubungkan komponen XML
        ImageButton btnBack =
                findViewById(R.id.btnBackProfile);

        Button btnEdit =
                findViewById(R.id.btnEditProfile);

        Button btnSettings =
                findViewById(R.id.btnSettingsProfile);

        Button btnLogout =
                findViewById(R.id.btnLogoutProfile);

        txtProfileName =
                findViewById(R.id.txtProfileName);

        txtProfileEmail =
                findViewById(R.id.txtProfileEmail);

        txtUsernameProfile =
                findViewById(R.id.txtUsernameProfile);

        txtEmailProfile =
                findViewById(R.id.txtEmailProfile);

        // Mengakses penyimpanan akun dan sesi login
        userSession = getSharedPreferences(
                "UserSession",
                MODE_PRIVATE
        );

        // Menampilkan data akun yang sedang login
        tampilkanDataProfil();

        // Tombol kembali ke Home
        btnBack.setOnClickListener(v -> finish());

        // Tombol Edit Profil
        btnEdit.setOnClickListener(v ->
                Toast.makeText(
                        ProfileActivity.this,
                        "Fitur Edit Profil belum tersedia",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // Tombol Pengaturan
        btnSettings.setOnClickListener(v ->
                Toast.makeText(
                        ProfileActivity.this,
                        "Fitur Pengaturan belum tersedia",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // Tombol Keluar
        btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Keluar dari Akun")
                        .setMessage(
                                "Apakah kamu yakin ingin keluar?"
                        )
                        .setNegativeButton(
                                "Batal",
                                (dialog, which) -> dialog.dismiss()
                        )
                        .setPositiveButton(
                                "Keluar",
                                (dialog, which) -> logout()
                        )
                        .show()
        );
    }

    // Menampilkan data akun yang berhasil login
    private void tampilkanDataProfil() {

        String nama = userSession.getString(
                "loginNama",
                "Nama belum tersedia"
        );

        String email = userSession.getString(
                "loginEmail",
                "Email belum ditambahkan"
        );

        // Menampilkan nama akun
        txtProfileName.setText(nama);
        txtUsernameProfile.setText(nama);

        // Menampilkan email akun
        txtProfileEmail.setText(email);
        txtEmailProfile.setText(email);
    }

    // Mengakhiri sesi tanpa menghapus data registrasi
    private void logout() {

        userSession.edit()
                .remove("isLoggedIn")
                .remove("loginNama")
                .remove("loginEmail")
                .remove("loginAlamat")
                .remove("loginTelepon")
                .apply();

        Toast.makeText(
                ProfileActivity.this,
                "Kamu berhasil keluar dari akun",
                Toast.LENGTH_SHORT
        ).show();

        // Kembali ke halaman Login
        Intent intent = new Intent(
                ProfileActivity.this,
                MainActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
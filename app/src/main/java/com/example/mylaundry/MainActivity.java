package com.example.mylaundry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText txtName, txtPassword;
    private Button btnLogin, btnDaftar;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Mengatur area sistem Android
        View mainView = findViewById(R.id.main);

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                    mainView, (v, insets) -> {

                        Insets systemBars = insets.getInsets(
                                WindowInsetsCompat.Type.systemBars()
                        );

                        v.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                systemBars.bottom
                        );

                        return insets;
                    }
            );
        }

        // Menghubungkan komponen XML
        txtName = findViewById(R.id.txtName);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnDaftar = findViewById(R.id.btnDaftar);

        // Mengakses penyimpanan akun
        preferences = getSharedPreferences(
                "UserSession",
                MODE_PRIVATE
        );

        // Fungsi tombol Login
        btnLogin.setOnClickListener(view -> prosesLogin());

        // Fungsi tombol Daftar
        btnDaftar.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    Register.class
            );

            startActivity(intent);
        });
    }

    // =====================================
    // PROSES LOGIN
    // =====================================
    private void prosesLogin() {

        String namaInput = txtName.getText()
                .toString().trim();

        String passwordInput = txtPassword.getText()
                .toString();

        // Validasi input kosong
        if (namaInput.isEmpty()) {
            txtName.setError("Nama wajib diisi");
            txtName.requestFocus();
            return;
        }

        if (passwordInput.trim().isEmpty()) {
            txtPassword.setError("Password wajib diisi");
            txtPassword.requestFocus();
            return;
        }

        // Mengambil data akun yang terdaftar
        String namaTerdaftar = preferences.getString(
                "nama",
                ""
        );

        String passwordTerdaftar = preferences.getString(
                "password",
                ""
        );

        // Memastikan akun tersedia
        if (namaTerdaftar.isEmpty()
                || passwordTerdaftar.isEmpty()) {

            Toast.makeText(
                    this,
                    "Akun belum terdaftar. Silakan daftar dahulu.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // Memeriksa kecocokan nama dan password
        boolean namaCocok = namaInput.equalsIgnoreCase(
                namaTerdaftar
        );

        boolean passwordCocok = passwordInput.equals(
                passwordTerdaftar
        );

        if (namaCocok && passwordCocok) {

            // Simpan informasi sesi akun yang sedang login
            preferences.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("loginNama", namaTerdaftar)
                    .putString(
                            "loginEmail",
                            preferences.getString("email", "")
                    )
                    .putString(
                            "loginAlamat",
                            preferences.getString("alamat", "")
                    )
                    .putString(
                            "loginTelepon",
                            preferences.getString("telepon", "")
                    )
                    .apply();

            Toast.makeText(
                    this,
                    "Login berhasil! Selamat datang, "
                            + namaTerdaftar + "!",
                    Toast.LENGTH_SHORT
            ).show();

            // Pindah ke halaman Home
            Intent intent = new Intent(
                    MainActivity.this,
                    HomeActivity.class
            );

            startActivity(intent);
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Nama atau password salah!",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
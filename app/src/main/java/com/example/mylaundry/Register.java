package com.example.mylaundry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Register extends AppCompatActivity {

    private EditText nama, email, alamat, nomorHp;
    private EditText password, konfirmasiPassword;
    private Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Mengatur area sistem Android
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

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

        // Menghubungkan komponen XML
        nama = findViewById(R.id.editTextText);
        email = findViewById(R.id.editTextTextEmailAddress);
        alamat = findViewById(R.id.editTextText2);
        nomorHp = findViewById(R.id.editTextPhone);

        password = findViewById(R.id.editTextTextPassword);
        konfirmasiPassword =
                findViewById(R.id.editTextTextPassword2);

        btnDaftar = findViewById(R.id.button);

        // Tombol Daftar
        btnDaftar.setOnClickListener(v -> {

            String namaUser = nama.getText()
                    .toString().trim();

            String emailUser = email.getText()
                    .toString().trim();

            String alamatUser = alamat.getText()
                    .toString().trim();

            String hpUser = nomorHp.getText()
                    .toString().trim();

            String pass = password.getText()
                    .toString();

            String konfirmasi = konfirmasiPassword.getText()
                    .toString();

            // Validasi semua kolom
            if (namaUser.isEmpty()
                    || emailUser.isEmpty()
                    || alamatUser.isEmpty()
                    || hpUser.isEmpty()
                    || pass.trim().isEmpty()
                    || konfirmasi.trim().isEmpty()) {

                Toast.makeText(
                        Register.this,
                        "Semua kolom wajib diisi!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Validasi nama
            if (namaUser.length() < 3) {
                nama.setError("Nama minimal 3 karakter");
                nama.requestFocus();
                return;
            }

            // Validasi email untuk data profil
            if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
                email.setError("Masukkan email yang valid");
                email.requestFocus();
                return;
            }

            // Validasi nomor HP
            if (!hpUser.matches("[0-9]{8,15}")) {
                nomorHp.setError(
                        "Nomor HP harus terdiri dari 8–15 angka"
                );
                nomorHp.requestFocus();
                return;
            }

            // Validasi panjang password
            if (pass.length() < 6) {
                password.setError(
                        "Password minimal 6 karakter"
                );
                password.requestFocus();
                return;
            }

            // Validasi konfirmasi password
            if (!pass.equals(konfirmasi)) {
                konfirmasiPassword.setError(
                        "Konfirmasi password tidak sama"
                );
                konfirmasiPassword.requestFocus();

                Toast.makeText(
                        Register.this,
                        "Password dan konfirmasi harus sama!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Mengakses penyimpanan lokal
            SharedPreferences preferences =
                    getSharedPreferences(
                            "UserSession",
                            MODE_PRIVATE
                    );

            // Cek apakah nama sudah terdaftar
            String namaLama =
                    preferences.getString("nama", "");

            if (namaUser.equalsIgnoreCase(namaLama)) {
                nama.setError("Nama sudah terdaftar");
                nama.requestFocus();

                Toast.makeText(
                        Register.this,
                        "Nama sudah digunakan. Silakan login.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Simpan data pengguna
            preferences.edit()
                    .putString("nama", namaUser)
                    .putString("email", emailUser)
                    .putString("alamat", alamatUser)
                    .putString("telepon", hpUser)
                    .putString("password", pass)
                    .apply();

            // Notifikasi berhasil
            Toast.makeText(
                    Register.this,
                    "Pendaftaran berhasil! Silakan login.",
                    Toast.LENGTH_LONG
            ).show();

            // Kembali ke halaman Login
            finish();
        });
    }
}
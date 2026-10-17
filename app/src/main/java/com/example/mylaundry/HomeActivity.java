package com.example.mylaundry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private TextView txtIdPesananHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // ==============================
        // INISIALISASI ID PESANAN
        // ==============================
        txtIdPesananHome = findViewById(R.id.txtIdPesananHome);

        // ==============================
        // MENGATUR STATUS BAR DAN NAVIGASI
        // ==============================
        View mainView = findViewById(R.id.main);

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                    mainView,
                    (v, insets) -> {
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

        // ==============================
        // MENU LAYANAN
        // ==============================
        aturKlik(R.id.button3, Pesan.class);
        aturKlik(R.id.button2, DaftarPesanan.class);
        aturKlik(R.id.btnTransaksi, Transaksi.class);
        aturKlik(R.id.button6, DaftarPesanan.class);

        // ==============================
        // BOTTOM NAVIGATION
        // ==============================

        // HOME
        View btnHome = findViewById(R.id.btnNavHome);

        if (btnHome != null) {
            btnHome.setOnClickListener(v ->
                    Toast.makeText(
                            HomeActivity.this,
                            "Kamu berada di halaman Home",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        }

        // PESANAN
        aturKlik(R.id.btnNavPesanan, DaftarPesanan.class);

        // QR CODE
        aturKlik(R.id.btnNavQR, QRActivity.class);

        // TRANSAKSI
        aturKlik(R.id.btnNavTransaksi, Transaksi.class);

        // ==============================
        // HALAMAN PROFIL
        // ==============================
        View btnProfil = findViewById(R.id.btnProfil);

        if (btnProfil != null) {
            btnProfil.setOnClickListener(v -> {
                Intent intent = new Intent(
                        HomeActivity.this,
                        ProfileActivity.class
                );
                startActivity(intent);
            });
        }

        // ==============================
        // HALAMAN NOTIFIKASI
        // ==============================
        View btnNotifikasi = findViewById(R.id.btnNotifikasi);

        if (btnNotifikasi != null) {
            btnNotifikasi.setOnClickListener(v -> {
                Intent intent = new Intent(
                        HomeActivity.this,
                        NotifikasiActivity.class
                );
                startActivity(intent);
            });
        }

        // ==============================
        // HALAMAN TRACKING
        // ==============================
        View btnTracking = findViewById(R.id.btnTracking);

        if (btnTracking != null) {
            btnTracking.setOnClickListener(v -> {
                Intent intent = new Intent(
                        HomeActivity.this,
                        TrackingActivity.class
                );
                startActivity(intent);
            });
        }

        // Tampilkan ID pesanan saat halaman dibuka
        tampilkanIdPesananTerbaru();
    }

    // ==============================
    // PERBARUI ID SAAT KEMBALI KE HOME
    // ==============================
    @Override
    protected void onResume() {
        super.onResume();
        tampilkanIdPesananTerbaru();
    }

    // ==============================
    // MENAMPILKAN ID PESANAN TERBARU
    // ==============================
    private void tampilkanIdPesananTerbaru() {

        if (txtIdPesananHome == null) {
            return;
        }

        SharedPreferences preferences =
                getSharedPreferences("DataPesanan", MODE_PRIVATE);

        String json = preferences.getString("daftar", "[]");

        try {
            JSONArray daftarPesanan = new JSONArray(json);

            // Jika belum ada pesanan
            if (daftarPesanan.length() == 0) {
                txtIdPesananHome.setText("Belum ada pesanan");
                return;
            }

            // Pesanan terbaru diasumsikan berada di elemen terakhir
            JSONObject pesananTerbaru =
                    daftarPesanan.getJSONObject(
                            daftarPesanan.length() - 1
                    );

            String idPesanan =
                    pesananTerbaru.optString("id", "").trim();

            if (idPesanan.isEmpty()) {
                txtIdPesananHome.setText("ID tidak tersedia");
            } else {
                txtIdPesananHome.setText("#" + idPesanan);
            }

        } catch (Exception e) {
            txtIdPesananHome.setText("Data pesanan tidak tersedia");
        }
    }

    // ==============================
    // FUNGSI NAVIGASI UMUM
    // ==============================
    private void aturKlik(int idView, Class<?> tujuan) {

        View tombol = findViewById(idView);

        if (tombol != null) {
            tombol.setOnClickListener(v -> {
                Intent intent = new Intent(
                        HomeActivity.this,
                        tujuan
                );
                startActivity(intent);
            });
        }
    }
}
package com.example.mylaundry;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class QRActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;

    private ActivityResultLauncher<ScanOptions> barcodeLauncher;

    private TextView txtHasilScan;
    private TextView txtStatusScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kunci halaman aplikasi ke posisi vertikal
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_qr);

        txtHasilScan = findViewById(R.id.txtHasilScan);
        txtStatusScan = findViewById(R.id.txtStatusScan);

        // Siapkan pemindai QR
        barcodeLauncher = registerForActivityResult(
                new ScanContract(),
                result -> {
                    if (result.getContents() == null) {
                        perbaruiStatus("Pemindaian dibatalkan.");
                        return;
                    }

                    prosesHasilQR(result.getContents());
                }
        );

        // Tombol tutup
        findViewById(R.id.btnTutupQR).setOnClickListener(v -> finish());

        // Periksa izin kamera
        periksaIzinKamera();
    }

    // Periksa izin kamera
    private void periksaIzinKamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA
            );

        } else {
            bukaKamera();
        }
    }

    // Tangani hasil permintaan izin kamera
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                bukaKamera();

            } else {
                perbaruiStatus(
                        "Izin kamera diperlukan untuk scan QR."
                );

                Toast.makeText(
                        this,
                        "Aktifkan izin kamera untuk menggunakan pemindai QR.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    // Buka scanner dalam orientasi vertikal
    private void bukaKamera() {
        ScanOptions options = new ScanOptions();

        options.setPrompt(
                "Arahkan kamera ke QR Code pesanan MyLaundry"
        );

        options.setBeepEnabled(true);

        // Kunci orientasi scanner
        options.setOrientationLocked(true);

        // Hanya membaca QR Code
        options.setDesiredBarcodeFormats(
                ScanOptions.QR_CODE
        );

        barcodeLauncher.launch(options);
    }

    // Proses hasil QR
    private void prosesHasilQR(String dataQR) {

        final String AWALAN_QR = "MYLAUNDRY:";

        if (dataQR == null
                || !dataQR.startsWith(AWALAN_QR)) {

            tampilkanHasil(
                    "QR Code tidak dikenali.\n\n"
                            + "Pastikan QR berasal dari pesanan MyLaundry."
            );
            return;
        }

        String idPesanan = dataQR
                .substring(AWALAN_QR.length())
                .trim();

        if (idPesanan.isEmpty()) {
            tampilkanHasil("ID pesanan tidak valid.");
            return;
        }

        cariPesanan(idPesanan);
    }

    // Cari pesanan berdasarkan ID di penyimpanan lokal
    private void cariPesanan(String idPesanan) {
        try {
            SharedPreferences prefs = getSharedPreferences(
                    "DataPesanan",
                    MODE_PRIVATE
            );

            String json = prefs.getString("daftar", "[]");
            JSONArray daftar = new JSONArray(json);

            for (int i = 0; i < daftar.length(); i++) {

                JSONObject pesanan =
                        daftar.getJSONObject(i);

                String idTersimpan = pesanan
                        .optString("id", "")
                        .trim();

                if (idPesanan.equals(idTersimpan)) {

                    String nama = pesanan.optString("nama", "-");
                    String telepon = pesanan.optString("telepon", "-");
                    String alamat = pesanan.optString("alamat", "-");
                    String layanan = pesanan.optString("layanan", "-");
                    String tanggal = pesanan.optString("tanggal", "-");
                    String berat = pesanan.optString("berat", "-");
                    String metode = pesanan.optString("metode", "-");
                    String catatan = pesanan.optString("catatan", "-");
                    String total = pesanan.optString("total", "0");

                    String hasil =
                            "PESANAN DITEMUKAN\n\n"
                                    + "ID Pesanan: " + idTersimpan + "\n"
                                    + "Nama: " + nama + "\n"
                                    + "Telepon: " + telepon + "\n"
                                    + "Alamat: " + alamat + "\n"
                                    + "Layanan: " + layanan + "\n"
                                    + "Berat: " + berat + "\n"
                                    + "Tanggal: " + tanggal + "\n"
                                    + "Metode: " + metode + "\n"
                                    + "Catatan: " + catatan + "\n"
                                    + "Total: Rp " + total;

                    tampilkanHasil(hasil);
                    return;
                }
            }

            tampilkanHasil(
                    "PESANAN TIDAK DITEMUKAN\n\n"
                            + "ID Pesanan: " + idPesanan + "\n\n"
                            + "Data pesanan tidak ditemukan di perangkat ini."
            );

        } catch (Exception e) {
            tampilkanHasil(
                    "Gagal membaca data pesanan.\n\n"
                            + "Pastikan data pesanan tersimpan dengan benar."
            );
        }
    }

    // Tampilkan hasil pemindaian
    private void tampilkanHasil(String pesan) {

        if (txtHasilScan != null) {
            txtHasilScan.setText(pesan);
        }

        if (txtStatusScan != null) {

            if (pesan.startsWith("PESANAN DITEMUKAN")) {
                txtStatusScan.setText(
                        "✓ Pesanan berhasil ditemukan"
                );

            } else if (pesan.startsWith("PESANAN TIDAK DITEMUKAN")) {
                txtStatusScan.setText(
                        "Pesanan tidak ditemukan"
                );

            } else {
                txtStatusScan.setText(
                        "Hasil pemindaian"
                );
            }
        }

        Toast.makeText(
                this,
                pesan.startsWith("PESANAN DITEMUKAN")
                        ? "Pesanan berhasil ditemukan!"
                        : "Pemindaian selesai.",
                Toast.LENGTH_SHORT
        ).show();
    }

    // Perbarui status pemindaian
    private void perbaruiStatus(String pesan) {
        if (txtStatusScan != null) {
            txtStatusScan.setText(pesan);
        }
    }
}
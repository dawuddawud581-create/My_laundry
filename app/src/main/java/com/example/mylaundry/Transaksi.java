package com.example.mylaundry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaksi extends AppCompatActivity {

    private TextView txtTotalTransaksi;
    private TextView txtNamaLayanan;
    private TextView txtBeratLaundry;
    private TextView txtHargaLaundry;
    private TextView txtSubtotal;
    private TextView txtBiayaLayanan;
    private TextView txtTotalRingkasan;

    private RadioGroup radioMetodePembayaran;
    private EditText edtCatatanTransaksi;
    private Button btnKonfirmasiTransaksi;

    private double totalHarga;
    private double totalBayar;
    private String nama;
    private String telepon;
    private String alamat;
    private String tanggal;
    private String layanan;
    private String catatanCucian;
    private double berat;

    private final NumberFormat rupiah =
            NumberFormat.getCurrencyInstance(
                    new Locale("id", "ID")
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        // Hubungkan komponen XML
        txtTotalTransaksi = findViewById(R.id.txtTotalTransaksi);
        txtNamaLayanan = findViewById(R.id.txtNamaLayanan);
        txtBeratLaundry = findViewById(R.id.txtBeratLaundry);
        txtHargaLaundry = findViewById(R.id.txtHargaLaundry);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtBiayaLayanan = findViewById(R.id.txtBiayaLayanan);
        txtTotalRingkasan = findViewById(R.id.txtTotalRingkasan);

        radioMetodePembayaran =
                findViewById(R.id.radioMetodePembayaran);

        edtCatatanTransaksi =
                findViewById(R.id.edtCatatanTransaksi);

        btnKonfirmasiTransaksi =
                findViewById(R.id.btnKonfirmasiTransaksi);

        // Tombol kembali
        View btnBack = findViewById(R.id.btnBackTransaksi);
        btnBack.setOnClickListener(v -> finish());

        // Terima data dari halaman Pesan
        Intent intent = getIntent();

        nama = intent.getStringExtra("nama");
        telepon = intent.getStringExtra("telepon");
        alamat = intent.getStringExtra("alamat");
        tanggal = intent.getStringExtra("tanggal");
        layanan = intent.getStringExtra("layanan");
        catatanCucian = intent.getStringExtra("catatan");

        berat = intent.getDoubleExtra("berat", 0);

        totalHarga = intent.getDoubleExtra("harga", -1);

        // Hitung ulang jika harga tidak ikut terkirim
        if (totalHarga < 0) {
            double hargaPerKg = 7000;

            if (layanan != null &&
                    layanan.toLowerCase(Locale.ROOT).contains("kilat")) {
                hargaPerKg = 12000;
            } else if (layanan != null &&
                    layanan.toLowerCase(Locale.ROOT).contains("express")) {
                hargaPerKg = 18000;
            }

            totalHarga = berat * hargaPerKg;
        }

        // Biaya layanan Rp0
        double biayaLayanan = 0;
        totalBayar = totalHarga + biayaLayanan;

        // Tampilkan ringkasan
        txtNamaLayanan.setText(
                layanan != null ? layanan : "Laundry"
        );

        txtBeratLaundry.setText(
                String.format(
                        Locale.getDefault(),
                        "Berat: %.2f kg",
                        berat
                )
        );

        txtHargaLaundry.setText(rupiah.format(totalHarga));
        txtSubtotal.setText(rupiah.format(totalHarga));
        txtBiayaLayanan.setText(rupiah.format(biayaLayanan));
        txtTotalRingkasan.setText(rupiah.format(totalBayar));
        txtTotalTransaksi.setText(rupiah.format(totalBayar));

        // Isi catatan dari halaman sebelumnya
        if (catatanCucian != null &&
                !catatanCucian.trim().isEmpty()) {
            edtCatatanTransaksi.setText(catatanCucian);
        }

        // Tombol konfirmasi pembayaran
        btnKonfirmasiTransaksi.setOnClickListener(v -> {

            int metodeId =
                    radioMetodePembayaran.getCheckedRadioButtonId();

            if (metodeId == -1) {
                Toast.makeText(
                        Transaksi.this,
                        "Pilih metode pembayaran terlebih dahulu.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            String metodePembayaran;

            if (metodeId == R.id.radioTunai) {
                metodePembayaran = "Tunai / Bayar di Kasir";
            } else if (metodeId == R.id.radioTransfer) {
                metodePembayaran = "Transfer Bank";
            } else if (metodeId == R.id.radioEwallet) {
                metodePembayaran = "E-Wallet";
            } else {
                Toast.makeText(
                        Transaksi.this,
                        "Metode pembayaran tidak valid.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Ambil catatan terbaru
            String catatanTransaksi =
                    edtCatatanTransaksi.getText().toString().trim();

            // Simpan pesanan sebelum berpindah halaman
            boolean berhasil = simpanPesanan(
                    metodePembayaran,
                    catatanTransaksi
            );

            if (!berhasil) {
                Toast.makeText(
                        Transaksi.this,
                        "Pesanan gagal disimpan. Silakan coba lagi.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            Toast.makeText(
                    Transaksi.this,
                    "Pesanan berhasil dikonfirmasi!",
                    Toast.LENGTH_SHORT
            ).show();

            // Cegah konfirmasi berulang
            btnKonfirmasiTransaksi.setEnabled(false);
            btnKonfirmasiTransaksi.setText(
                    "Transaksi Dikonfirmasi"
            );

            // Buka halaman Daftar Pesanan
            Intent daftarIntent = new Intent(
                    Transaksi.this,
                    DaftarPesanan.class
            );

            startActivity(daftarIntent);
            finish();
        });
    }

    // Menyimpan semua data pesanan
    private boolean simpanPesanan(
            String metodePembayaran,
            String catatanTransaksi) {

        try {
            SharedPreferences prefs =
                    getSharedPreferences(
                            "DataPesanan",
                            MODE_PRIVATE
                    );

            JSONArray daftar = new JSONArray(
                    prefs.getString("daftar", "[]")
            );

            JSONObject pesanan = new JSONObject();

            String idPesanan = "TRX-" +
                    new SimpleDateFormat(
                            "yyMMddHHmmss",
                            Locale.getDefault()
                    ).format(new Date());

            String waktuKonfirmasi =
                    new SimpleDateFormat(
                            "dd-MM-yyyy HH:mm",
                            Locale.getDefault()
                    ).format(new Date());

            pesanan.put("id", idPesanan);
            pesanan.put("nama", aman(nama));
            pesanan.put("telepon", aman(telepon));
            pesanan.put("alamat", aman(alamat));
            pesanan.put("layanan", aman(layanan));
            pesanan.put("berat", berat);
            pesanan.put("tanggal", aman(tanggal));
            pesanan.put("catatan", aman(catatanTransaksi));
            pesanan.put("metode", metodePembayaran);
            pesanan.put("subtotal", totalHarga);
            pesanan.put("total", totalBayar);
            pesanan.put("status", "Dikonfirmasi");
            pesanan.put("waktu", waktuKonfirmasi);

            // Tambahkan pesanan baru
            daftar.put(pesanan);

            // Simpan ke penyimpanan lokal HP
            prefs.edit()
                    .putString("daftar", daftar.toString())
                    .commit();

            // Pastikan penyimpanan berhasil
            return prefs.getString("daftar", "")
                    .equals(daftar.toString());

        } catch (Exception e) {
            return false;
        }
    }

    private String aman(String nilai) {
        return nilai != null ? nilai : "-";
    }
}
package com.example.mylaundry;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Locale;

public class DaftarPesanan extends Activity {

    private LinearLayout container;
    private TextView txtPesananKosong;

    // Warna tema Neon
    private final int WARNA_BACKGROUND = Color.rgb(8, 11, 24);
    private final int WARNA_CARD = Color.rgb(17, 26, 46);
    private final int WARNA_CYAN = Color.rgb(0, 245, 255);
    private final int WARNA_PUTIH = Color.WHITE;
    private final int WARNA_DETAIL = Color.rgb(167, 180, 208);
    private final int WARNA_HIJAU = Color.rgb(57, 255, 136);
    private final int WARNA_GELAP = Color.rgb(8, 11, 24);

    private final NumberFormat rupiah =
            NumberFormat.getCurrencyInstance(
                    new Locale("id", "ID")
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pesanan);

        container = findViewById(R.id.containerDaftarPesanan);
        txtPesananKosong = findViewById(R.id.txtPesananKosong);

        TextView btnKembali =
                findViewById(R.id.btnKembaliDaftar);

        if (btnKembali != null) {
            btnKembali.setOnClickListener(v -> finish());
        }

        txtPesananKosong.setTextColor(WARNA_DETAIL);
        txtPesananKosong.setTextSize(15);
        txtPesananKosong.setGravity(Gravity.CENTER);

        tampilkanPesanan();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (container != null && txtPesananKosong != null) {
            tampilkanPesanan();
        }
    }

    private void tampilkanPesanan() {
        container.removeAllViews();

        SharedPreferences prefs =
                getSharedPreferences(
                        "DataPesanan",
                        MODE_PRIVATE
                );

        String json = prefs.getString("daftar", "[]");

        try {
            JSONArray daftar = new JSONArray(json);

            if (daftar.length() == 0) {
                txtPesananKosong.setText(
                        "Belum ada pesanan yang dikonfirmasi."
                );
                txtPesananKosong.setVisibility(View.VISIBLE);
                return;
            }

            txtPesananKosong.setVisibility(View.GONE);

            // Pesanan terbaru ditampilkan paling atas
            for (int i = daftar.length() - 1; i >= 0; i--) {
                JSONObject pesanan = daftar.getJSONObject(i);
                buatKartuPesanan(pesanan);
            }

        } catch (Exception e) {
            txtPesananKosong.setText(
                    "Data pesanan gagal dibaca."
            );
            txtPesananKosong.setVisibility(View.VISIBLE);
        }
    }

    private void buatKartuPesanan(JSONObject p) {

        String idPesanan = p.optString("id", "").trim();

        // Kartu utama
        LinearLayout kartu = new LinearLayout(this);
        kartu.setOrientation(LinearLayout.VERTICAL);
        kartu.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable background = new GradientDrawable();
        background.setColor(WARNA_CARD);
        background.setCornerRadius(dp(18));
        background.setStroke(dp(1.5f), WARNA_CYAN);

        kartu.setBackground(background);
        kartu.setElevation(dp(6));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, dp(18));
        kartu.setLayoutParams(params);

        // Judul pesanan
        TextView judul = new TextView(this);
        judul.setText("PESANAN #" +
                (idPesanan.isEmpty() ? "-" : idPesanan));
        judul.setTextSize(18);
        judul.setTypeface(null, Typeface.BOLD);
        judul.setTextColor(WARNA_PUTIH);
        judul.setLetterSpacing(0.03f);

        kartu.addView(judul);

        // Status pembayaran
        TextView status = new TextView(this);
        status.setText("✓  Pembayaran Dikonfirmasi");
        status.setTextColor(WARNA_HIJAU);
        status.setTextSize(13);
        status.setTypeface(null, Typeface.BOLD);
        status.setPadding(0, dp(8), 0, dp(14));

        kartu.addView(status);

        // Detail pesanan
        tambahBaris(kartu, "Nama pelanggan",
                p.optString("nama", "-"));

        tambahBaris(kartu, "Nomor telepon",
                p.optString("telepon", "-"));

        tambahBaris(kartu, "Alamat",
                p.optString("alamat", "-"));

        tambahBaris(kartu, "Layanan",
                p.optString("layanan", "-"));

        tambahBaris(kartu, "Berat cucian",
                p.optString("berat", "-") + " kg");

        tambahBaris(kartu, "Tanggal pesanan",
                p.optString("tanggal", "-"));

        tambahBaris(kartu, "Metode pembayaran",
                p.optString("metode", "-"));

        tambahBaris(kartu, "Catatan",
                p.optString("catatan", "-"));

        // Garis pemisah
        View garis = new View(this);
        garis.setBackgroundColor(WARNA_CYAN);

        LinearLayout.LayoutParams garisParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        garisParams.setMargins(0, dp(14), 0, dp(14));
        kartu.addView(garis, garisParams);

        // Total pembayaran
        TextView total = new TextView(this);
        total.setText(
                "TOTAL PEMBAYARAN\n" +
                        rupiah.format(p.optDouble("total", 0))
        );

        total.setTextColor(WARNA_HIJAU);
        total.setTextSize(19);
        total.setTypeface(null, Typeface.BOLD);
        total.setLineSpacing(dp(5), 1f);

        kartu.addView(total);

        // Waktu konfirmasi
        tambahBaris(kartu, "Waktu konfirmasi",
                p.optString("waktu", "-"));

        // Tombol QR Code
        Button btnQR = new Button(this);
        btnQR.setText("▦  LIHAT QR CODE PESANAN");
        btnQR.setTextColor(WARNA_GELAP);
        btnQR.setTextSize(13);
        btnQR.setTypeface(null, Typeface.BOLD);
        btnQR.setAllCaps(false);
        btnQR.setPadding(dp(10), dp(8), dp(10), dp(8));

        GradientDrawable tombolBackground =
                new GradientDrawable();

        tombolBackground.setColor(WARNA_CYAN);
        tombolBackground.setCornerRadius(dp(12));

        btnQR.setBackground(tombolBackground);

        LinearLayout.LayoutParams tombolParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        tombolParams.setMargins(0, dp(18), 0, 0);
        btnQR.setLayoutParams(tombolParams);

        btnQR.setOnClickListener(v -> {
            if (idPesanan.isEmpty()) {
                Toast.makeText(
                        this,
                        "ID pesanan tidak ditemukan.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            tampilkanQR(idPesanan);
        });

        kartu.addView(btnQR);

        // Masukkan kartu ke container
        container.addView(kartu);
    }

    private void tampilkanQR(String idPesanan) {

        try {
            // Isi QR memakai ID pesanan yang unik
            String isiQR = "MYLAUNDRY:" + idPesanan;

            int ukuran = 700;

            Hashtable<EncodeHintType, Object> hints =
                    new Hashtable<>();

            hints.put(
                    EncodeHintType.MARGIN,
                    2
            );

            BitMatrix matrix =
                    new MultiFormatWriter().encode(
                            isiQR,
                            BarcodeFormat.QR_CODE,
                            ukuran,
                            ukuran,
                            hints
                    );

            Bitmap bitmap = Bitmap.createBitmap(
                    ukuran,
                    ukuran,
                    Bitmap.Config.ARGB_8888
            );

            for (int x = 0; x < ukuran; x++) {
                for (int y = 0; y < ukuran; y++) {
                    bitmap.setPixel(
                            x,
                            y,
                            matrix.get(x, y)
                                    ? Color.BLACK
                                    : Color.WHITE
                    );
                }
            }

            // Tampilan dialog QR Code
            LinearLayout layout =
                    new LinearLayout(this);

            layout.setOrientation(
                    LinearLayout.VERTICAL
            );

            layout.setGravity(Gravity.CENTER);
            layout.setPadding(
                    dp(20),
                    dp(20),
                    dp(20),
                    dp(20)
            );

            layout.setBackgroundColor(WARNA_CARD);

            TextView judul = new TextView(this);
            judul.setText("QR CODE PESANAN");
            judul.setTextColor(WARNA_CYAN);
            judul.setTextSize(18);
            judul.setTypeface(null, Typeface.BOLD);
            judul.setGravity(Gravity.CENTER);

            layout.addView(judul);

            TextView nomor = new TextView(this);
            nomor.setText("ID Pesanan: " + idPesanan);
            nomor.setTextColor(WARNA_PUTIH);
            nomor.setTextSize(15);
            nomor.setGravity(Gravity.CENTER);
            nomor.setPadding(0, dp(10), 0, dp(15));

            layout.addView(nomor);

            ImageView gambarQR = new ImageView(this);
            gambarQR.setImageBitmap(bitmap);
            gambarQR.setScaleType(
                    ImageView.ScaleType.FIT_CENTER
            );

            LinearLayout.LayoutParams qrParams =
                    new LinearLayout.LayoutParams(
                            dp(240),
                            dp(240)
                    );

            gambarQR.setLayoutParams(qrParams);
            gambarQR.setBackgroundColor(Color.WHITE);
            gambarQR.setPadding(
                    dp(8),
                    dp(8),
                    dp(8),
                    dp(8)
            );

            layout.addView(gambarQR);

            TextView keterangan = new TextView(this);
            keterangan.setText(
                    "Tunjukkan QR ini untuk identifikasi pesanan."
            );
            keterangan.setTextColor(WARNA_DETAIL);
            keterangan.setTextSize(12);
            keterangan.setGravity(Gravity.CENTER);
            keterangan.setPadding(0, dp(14), 0, 0);

            layout.addView(keterangan);

            AlertDialog dialog =
                    new AlertDialog.Builder(this)
                            .setView(layout)
                            .setPositiveButton(
                                    "Tutup",
                                    (d, which) -> d.dismiss()
                            )
                            .create();

            dialog.show();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(
                        new GradientDrawable()
                );
            }

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Gagal membuat QR Code: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void tambahBaris(
            LinearLayout kartu,
            String label,
            String nilai) {

        LinearLayout baris = new LinearLayout(this);
        baris.setOrientation(LinearLayout.VERTICAL);
        baris.setPadding(0, dp(5), 0, dp(5));

        TextView txtLabel = new TextView(this);
        txtLabel.setText(
                label.toUpperCase(Locale.ROOT)
        );
        txtLabel.setTextColor(WARNA_CYAN);
        txtLabel.setTextSize(10);
        txtLabel.setTypeface(null, Typeface.BOLD);
        txtLabel.setLetterSpacing(0.05f);

        TextView txtNilai = new TextView(this);
        txtNilai.setText(nilai);
        txtNilai.setTextColor(WARNA_DETAIL);
        txtNilai.setTextSize(14);
        txtNilai.setPadding(0, dp(2), 0, 0);
        txtNilai.setSingleLine(false);
        txtNilai.setMaxLines(5);

        baris.addView(txtLabel);
        baris.addView(txtNilai);

        kartu.addView(baris);
    }

    private int dp(float nilai) {
        return (int) (
                nilai * getResources()
                        .getDisplayMetrics().density + 0.5f
        );
    }
}
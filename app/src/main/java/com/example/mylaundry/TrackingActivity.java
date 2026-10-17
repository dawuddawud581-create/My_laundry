package com.example.mylaundry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class TrackingActivity extends AppCompatActivity {

    private TextView txtNomorTracking;
    private TextView txtBadgeTracking;
    private TextView txtPersenTracking;
    private TextView txtDeskripsiTracking;
    private TextView txtEstimasiTracking;

    private TextView waktuTracking1;
    private TextView waktuTracking2;

    private TextView timelineIcon1;
    private TextView timelineIcon2;
    private TextView timelineIcon3;
    private TextView timelineIcon4;
    private TextView timelineIcon5;

    private ProgressBar progressTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        // Hubungkan komponen XML
        ImageButton btnBackTracking =
                findViewById(R.id.btnBackTracking);

        txtNomorTracking =
                findViewById(R.id.txtNomorTracking);

        txtBadgeTracking =
                findViewById(R.id.txtBadgeTracking);

        txtPersenTracking =
                findViewById(R.id.txtPersenTracking);

        txtDeskripsiTracking =
                findViewById(R.id.txtDeskripsiTracking);

        txtEstimasiTracking =
                findViewById(R.id.txtEstimasiTracking);

        progressTracking =
                findViewById(R.id.progressTracking);

        waktuTracking1 =
                findViewById(R.id.waktuTracking1);

        waktuTracking2 =
                findViewById(R.id.waktuTracking2);

        timelineIcon1 =
                findViewById(R.id.timelineIcon1);

        timelineIcon2 =
                findViewById(R.id.timelineIcon2);

        timelineIcon3 =
                findViewById(R.id.timelineIcon3);

        timelineIcon4 =
                findViewById(R.id.timelineIcon4);

        timelineIcon5 =
                findViewById(R.id.timelineIcon5);

        // Tombol kembali
        if (btnBackTracking != null) {
            btnBackTracking.setOnClickListener(v -> finish());
        }

        // Tampilkan ID pesanan
        tampilkanIdPesanan();

        // Status contoh: pakaian sedang dicuci
        tampilkanStatus(40);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Perbarui nomor pesanan ketika halaman dibuka kembali
        tampilkanIdPesanan();
    }

    // =========================================
    // MEMBACA ID PESANAN DARI SHAREDPREFERENCES
    // =========================================
    private void tampilkanIdPesanan() {

        if (txtNomorTracking == null) {
            return;
        }

        SharedPreferences preferences =
                getSharedPreferences(
                        "DataPesanan",
                        MODE_PRIVATE
                );

        String json =
                preferences.getString("daftar", "[]");

        try {
            JSONArray daftarPesanan =
                    new JSONArray(json);

            if (daftarPesanan.length() == 0) {
                txtNomorTracking.setText(
                        "Belum ada pesanan"
                );
                return;
            }

            // Pesanan terbaru disimpan di elemen terakhir
            JSONObject pesananTerbaru =
                    daftarPesanan.getJSONObject(
                            daftarPesanan.length() - 1
                    );

            String idPesanan =
                    pesananTerbaru.optString(
                            "id", ""
                    ).trim();

            if (idPesanan.isEmpty()) {
                txtNomorTracking.setText(
                        "ID tidak tersedia"
                );
            } else {
                txtNomorTracking.setText(
                        "#" + idPesanan
                );
            }

        } catch (Exception e) {
            txtNomorTracking.setText(
                    "Data pesanan tidak tersedia"
            );
        }
    }

    // =========================================
    // MENGATUR STATUS DAN PROGRES TRACKING
    // =========================================
    private void tampilkanStatus(int progres) {

        progres = Math.max(
                0,
                Math.min(progres, 100)
        );

        if (progressTracking != null) {
            progressTracking.setMax(100);
            progressTracking.setProgress(progres);
        }

        if (txtPersenTracking != null) {
            txtPersenTracking.setText(
                    progres + "%"
            );
        }

        // Atur ulang ikon timeline
        aturTahap(timelineIcon1, 1, false);
        aturTahap(timelineIcon2, 2, false);
        aturTahap(timelineIcon3, 3, false);
        aturTahap(timelineIcon4, 4, false);
        aturTahap(timelineIcon5, 5, false);

        // Tahap pertama sudah diterima
        aturTahap(timelineIcon1, 1, true);

        if (waktuTracking1 != null) {
            waktuTracking1.setText(
                    "Pesanan berhasil diterima"
            );
            waktuTracking1.setTextColor(
                    0xFF35D9F5
            );
        }

        if (progres < 20) {

            aturInfo(
                    "Pesanan diterima",
                    "Pesanan kamu sudah diterima dan menunggu proses berikutnya.",
                    "Menunggu proses",
                    "Menunggu proses"
            );

        } else if (progres < 40) {

            aturInfo(
                    "Persiapan",
                    "Pesanan sedang dipersiapkan untuk proses pencucian.",
                    "Dalam proses",
                    "Menunggu pencucian"
            );

        } else if (progres < 60) {

            aturTahap(
                    timelineIcon2, 2, true
            );

            aturInfo(
                    "Sedang dicuci",
                    "Pakaian kamu sedang dalam proses pencucian.",
                    "Dalam proses",
                    "Sedang berlangsung"
            );

        } else if (progres < 80) {

            aturTahap(
                    timelineIcon2, 2, true
            );
            aturTahap(
                    timelineIcon3, 3, true
            );

            aturInfo(
                    "Finishing",
                    "Pakaian sedang disetrika dan dirapikan.",
                    "Hampir selesai",
                    "Pencucian selesai"
            );

        } else if (progres < 100) {

            aturTahap(
                    timelineIcon2, 2, true
            );
            aturTahap(
                    timelineIcon3, 3, true
            );
            aturTahap(
                    timelineIcon4, 4, true
            );

            aturInfo(
                    "Siap diambil",
                    "Laundry kamu sudah siap diambil sesuai jam operasional.",
                    "Sudah siap",
                    "Pencucian selesai"
            );

        } else {

            aturTahap(
                    timelineIcon2, 2, true
            );
            aturTahap(
                    timelineIcon3, 3, true
            );
            aturTahap(
                    timelineIcon4, 4, true
            );
            aturTahap(
                    timelineIcon5, 5, true
            );

            aturInfo(
                    "Selesai",
                    "Pesanan laundry kamu telah selesai.",
                    "Pesanan selesai",
                    "Pencucian selesai"
            );
        }
    }

    // =========================================
    // MEMPERBARUI INFORMASI STATUS
    // =========================================
    private void aturInfo(
            String badge,
            String deskripsi,
            String estimasi,
            String waktu
    ) {

        if (txtBadgeTracking != null) {
            txtBadgeTracking.setText(badge);
        }

        if (txtDeskripsiTracking != null) {
            txtDeskripsiTracking.setText(
                    deskripsi
            );
        }

        if (txtEstimasiTracking != null) {
            txtEstimasiTracking.setText(
                    estimasi
            );
        }

        if (waktuTracking2 != null) {
            waktuTracking2.setText(waktu);
        }
    }

    // =========================================
    // MENGATUR IKON TIMELINE
    // =========================================
    private void aturTahap(
            TextView ikon,
            int nomorTahap,
            boolean selesai
    ) {

        if (ikon == null) {
            return;
        }

        if (selesai) {
            ikon.setText("✓");
            ikon.setTextColor(0xFF35D9F5);
        } else {
            ikon.setText(
                    String.valueOf(nomorTahap)
            );
            ikon.setTextColor(0xFF91A4BC);
        }
    }
}
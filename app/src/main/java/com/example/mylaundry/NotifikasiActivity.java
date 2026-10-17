package com.example.mylaundry;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotifikasiActivity extends AppCompatActivity {

    private TextView txtJumlahNotif;
    private TextView txtRingkasanNotif;
    private TextView btnTandaiDibaca;
    private TextView btnHapusNotif;

    private TextView statusNotif1;
    private TextView statusNotif2;
    private TextView statusNotif3;

    private View cardNotif1;
    private View cardNotif2;
    private View cardNotif3;

    private boolean[] sudahDibaca = {false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifikasi);

        ImageButton btnBackNotif = findViewById(R.id.btnBackNotif);

        txtJumlahNotif = findViewById(R.id.txtJumlahNotif);
        txtRingkasanNotif = findViewById(R.id.txtRingkasanNotif);
        btnTandaiDibaca = findViewById(R.id.btnTandaiDibaca);
        btnHapusNotif = findViewById(R.id.btnHapusNotif);

        cardNotif1 = findViewById(R.id.cardNotif1);
        cardNotif2 = findViewById(R.id.cardNotif2);
        cardNotif3 = findViewById(R.id.cardNotif3);

        statusNotif1 = findViewById(R.id.statusNotif1);
        statusNotif2 = findViewById(R.id.statusNotif2);
        statusNotif3 = findViewById(R.id.statusNotif3);

        btnBackNotif.setOnClickListener(v -> finish());

        // Tandai semua notifikasi sebagai sudah dibaca
        btnTandaiDibaca.setOnClickListener(v -> tandaiSemuaDibaca());

        // Hapus semua notifikasi dari tampilan
        btnHapusNotif.setOnClickListener(v -> hapusSemuaNotifikasi());

        perbaruiRingkasan();
    }

    private void tandaiSemuaDibaca() {
        sudahDibaca[0] = true;
        sudahDibaca[1] = true;
        sudahDibaca[2] = true;

        statusNotif1.setText("✓  SUDAH DIBACA  •  Baru saja");
        statusNotif2.setText("✓  SUDAH DIBACA  •  10 menit lalu");
        statusNotif3.setText("✓  SUDAH DIBACA  •  1 jam lalu");

        statusNotif1.setTextColor(0xFF91A4BC);
        statusNotif2.setTextColor(0xFF91A4BC);
        statusNotif3.setTextColor(0xFF91A4BC);

        perbaruiRingkasan();

        Toast.makeText(
                this,
                "Semua notifikasi sudah dibaca",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void hapusSemuaNotifikasi() {
        cardNotif1.setVisibility(View.GONE);
        cardNotif2.setVisibility(View.GONE);
        cardNotif3.setVisibility(View.GONE);

        btnTandaiDibaca.setVisibility(View.GONE);
        btnHapusNotif.setVisibility(View.GONE);

        sudahDibaca[0] = true;
        sudahDibaca[1] = true;
        sudahDibaca[2] = true;

        txtJumlahNotif.setText("0");
        txtRingkasanNotif.setText("Tidak ada notifikasi saat ini");

        Toast.makeText(
                this,
                "Semua notifikasi dihapus dari tampilan",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void perbaruiRingkasan() {
        int belumDibaca = 0;

        for (boolean dibaca : sudahDibaca) {
            if (!dibaca) {
                belumDibaca++;
            }
        }

        txtJumlahNotif.setText(String.valueOf(belumDibaca));

        if (belumDibaca == 0) {
            txtRingkasanNotif.setText("Semua notifikasi sudah dibaca");
        } else {
            txtRingkasanNotif.setText(
                    "Kamu memiliki " + belumDibaca
                            + " notifikasi belum dibaca"
            );
        }
    }
}
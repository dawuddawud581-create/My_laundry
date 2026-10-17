package com.example.mylaundry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class Pesan extends AppCompatActivity {

    private EditText edtNama;
    private EditText edtTelepon;
    private EditText edtAlamat;
    private EditText edtBerat;
    private EditText edtTanggal;
    private EditText edtCatatan;

    private RadioGroup radioLayanan;
    private TextView txtEstimasi;
    private Button btnBuatPesanan;
    private View btnKembali;

    private static final double HARGA_REGULER = 7000;
    private static final double HARGA_KILAT = 12000;
    private static final double HARGA_EXPRESS = 18000;

    private final NumberFormat formatRupiah =
            NumberFormat.getCurrencyInstance(
                    new Locale("id", "ID")
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan);

        // Menghubungkan komponen XML
        edtNama = findViewById(R.id.edtNamaPelanggan);
        edtTelepon = findViewById(R.id.edtTeleponPelanggan);
        edtAlamat = findViewById(R.id.edtAlamatPelanggan);
        edtBerat = findViewById(R.id.edtBeratCucian);
        edtTanggal = findViewById(R.id.edtTanggalPesanan);
        edtCatatan = findViewById(R.id.edtCatatanCucian);

        radioLayanan = findViewById(R.id.radioJenisLayanan);
        txtEstimasi = findViewById(R.id.txtEstimasiHarga);
        btnBuatPesanan = findViewById(R.id.btnBuatPesanan);
        btnKembali = findViewById(R.id.btnKembaliPesan);

        // Tombol kembali
        btnKembali.setOnClickListener(v -> finish());

        // Pemilih tanggal
        edtTanggal.setFocusable(false);
        edtTanggal.setOnClickListener(v -> tampilkanTanggal());

        // Hitung harga ketika berat berubah
        edtBerat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                hitungEstimasi();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Hitung ulang ketika jenis layanan berubah
        radioLayanan.setOnCheckedChangeListener(
                (group, checkedId) -> hitungEstimasi()
        );

        // Tombol membuat pesanan
        btnBuatPesanan.setOnClickListener(v -> buatPesanan());

        hitungEstimasi();
    }

    private void buatPesanan() {
        String nama = edtNama.getText().toString().trim();
        String telepon = edtTelepon.getText().toString().trim();
        String alamat = edtAlamat.getText().toString().trim();
        String beratText = edtBerat.getText().toString().trim();
        String tanggal = edtTanggal.getText().toString().trim();
        String catatan = edtCatatan.getText().toString().trim();

        // Validasi data wajib
        if (nama.isEmpty()) {
            edtNama.setError("Nama wajib diisi");
            edtNama.requestFocus();
            return;
        }

        if (telepon.isEmpty()) {
            edtTelepon.setError("Nomor telepon wajib diisi");
            edtTelepon.requestFocus();
            return;
        }

        if (telepon.length() < 10) {
            edtTelepon.setError("Nomor telepon minimal 10 digit");
            edtTelepon.requestFocus();
            return;
        }

        if (alamat.isEmpty()) {
            edtAlamat.setError("Alamat wajib diisi");
            edtAlamat.requestFocus();
            return;
        }

        if (beratText.isEmpty()) {
            edtBerat.setError("Berat cucian wajib diisi");
            edtBerat.requestFocus();
            return;
        }

        double berat;

        try {
            berat = Double.parseDouble(
                    beratText.replace(",", ".")
            );
        } catch (NumberFormatException e) {
            edtBerat.setError("Masukkan berat yang valid");
            edtBerat.requestFocus();
            return;
        }

        if (Double.isNaN(berat)
                || Double.isInfinite(berat)
                || berat <= 0
                || berat > 1000) {
            edtBerat.setError(
                    "Berat harus lebih dari 0 hingga maksimal 1000 kg"
            );
            edtBerat.requestFocus();
            return;
        }

        if (tanggal.isEmpty()) {
            edtTanggal.setError("Pilih tanggal pesanan");
            edtTanggal.requestFocus();
            return;
        }

        int layananId = radioLayanan.getCheckedRadioButtonId();

        if (layananId == -1) {
            Toast.makeText(
                    this,
                    "Silakan pilih jenis layanan laundry",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String jenisLayanan;

        if (layananId == R.id.radioReguler) {
            jenisLayanan = "Laundry Reguler";
        } else if (layananId == R.id.radioKilat) {
            jenisLayanan = "Laundry Kilat";
        } else if (layananId == R.id.radioExpress) {
            jenisLayanan = "Laundry Express";
        } else {
            Toast.makeText(
                    this,
                    "Jenis layanan tidak valid",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        double harga = hitungHarga(berat);

        // Kirim data ke halaman Transaksi
        Intent intent = new Intent(Pesan.this, Transaksi.class);

        intent.putExtra("nama", nama);
        intent.putExtra("telepon", telepon);
        intent.putExtra("alamat", alamat);
        intent.putExtra("berat", berat);
        intent.putExtra("tanggal", tanggal);
        intent.putExtra("layanan", jenisLayanan);
        intent.putExtra("harga", harga);
        intent.putExtra("catatan", catatan);

        startActivity(intent);
    }

    private double hargaPerKg() {
        int id = radioLayanan.getCheckedRadioButtonId();

        if (id == R.id.radioKilat) {
            return HARGA_KILAT;
        }

        if (id == R.id.radioExpress) {
            return HARGA_EXPRESS;
        }

        return HARGA_REGULER;
    }

    private double hitungHarga(double berat) {
        return berat * hargaPerKg();
    }

    private void hitungEstimasi() {
        if (edtBerat == null || txtEstimasi == null) {
            return;
        }

        String teksBerat = edtBerat.getText().toString().trim();
        double berat = 0;

        try {
            if (!teksBerat.isEmpty()) {
                berat = Double.parseDouble(
                        teksBerat.replace(",", ".")
                );
            }
        } catch (NumberFormatException ignored) {
            berat = 0;
        }

        double total = 0;

        if (berat > 0
                && berat <= 1000
                && !Double.isInfinite(berat)
                && !Double.isNaN(berat)) {
            total = hitungHarga(berat);
        }

        txtEstimasi.setText(formatRupiah.format(total));
    }

    private void tampilkanTanggal() {
        Calendar kalender = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                Pesan.this,
                (view, year, month, day) -> {
                    String tanggal = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            day,
                            month + 1,
                            year
                    );

                    edtTanggal.setText(tanggal);
                    edtTanggal.setError(null);
                },
                kalender.get(Calendar.YEAR),
                kalender.get(Calendar.MONTH),
                kalender.get(Calendar.DAY_OF_MONTH)
        );

        // Tidak boleh memilih tanggal sebelum hari ini
        dialog.getDatePicker().setMinDate(
                System.currentTimeMillis() - 1000
        );

        dialog.show();
    }
}
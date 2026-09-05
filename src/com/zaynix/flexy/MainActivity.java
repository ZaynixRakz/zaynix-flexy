package com.zaynix.flexy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends Activity {
    private EditText kolomLisensi;
    private Button tombolMasuk;
    
    // ⚠️ GANTI LINK DI BAWAH INI DENGAN LINK RAW GIST KAMU TADI ⚠️
    private final String URL_DATABASE = "https://gist.githubusercontent.com/ZaynixRakz/57ee477607c331d0fc95ab5e8e322ebe/raw/61e9222392a54e4e990ec099613e66c93860a414/licensi.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        
        TextView judul = new TextView(this);
        judul.setText("ZAYNIX FLEXY ONLINE");
        judul.setTextSize(24);
        layout.addView(judul);

        kolomLisensi = new EditText(this);
        kolomLisensi.setHint("Masukkan Kunci Lisensi...");
        layout.addView(kolomLisensi);

        tombolMasuk = new Button(this);
        tombolMasuk.setText("VALIDASI LISENSI");
        layout.addView(tombolMasuk);

        setContentView(layout);

        tombolMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUser = kolomLisensi.getText().toString().trim();
                if (inputUser.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Ketik lisensinya!", Toast.LENGTH_SHORT).show();
                } else {
                    new CekLisensiServer().execute(inputUser);
                }
            }
        });
    }

    private class CekLisensiServer extends AsyncTask<String, Void, String> {
        private ProgressDialog loading;
        private String inputKey;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MainActivity.this);
            loading.setMessage("Memeriksa lisensi Zaynix...");
            loading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            inputKey = params;
            StringBuilder hasil = new StringBuilder();
            try {
                URL url = new URL(URL_DATABASE);
                BufferedReader pembaca = new BufferedReader(new InputStreamReader(url.openStream()));
                String baris;
                while ((baris = pembaca.readLine()) != null) {
                    hasil.append(baris);
                }
                pembaca.close();
                return hasil.toString();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String hasilJson) {
            super.onPostExecute(hasilJson);
            if (loading.isShowing()) { loading.dismiss(); }
            if (hasilJson == null) {
                Toast.makeText(MainActivity.this, "Koneksi Gagal!", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject database = new JSONObject(hasilJson);
                if (database.has(inputKey)) {
                    JSONObject dataUser = database.getJSONObject(inputKey);
                    String status = dataUser.getString("status");
                    String nama = dataUser.getString("user");

                    if (status.equalsIgnoreCase("aktif")) {
                        Toast.makeText(MainActivity.this, "Lisensi Valid! Selamat Datang " + nama, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Lisensi DIBLOKIR!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Lisensi Tidak Terdaftar!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Gagal memproses data kunci!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


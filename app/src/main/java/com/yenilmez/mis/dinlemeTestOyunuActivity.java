package com.yenilmez.mis;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class dinlemeTestOyunuActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    ArrayList<String> yazilan;
    ArrayList<Integer> yazilan_dinleme;
    RadioGroup rG_sikler;
    ImageButton btn_dinle;
    RadioButton rB_secilen_sik, rB_1, rB_2, rB_3, rB_4;
    SQLiteDatabase database = chooseWeekActivity.database;
    String secilenhafta;
    TextView tV_Skor, tV_Rekor;
    int kelimeninYeri, dogru_sik;


    //Konusturma
    TextToSpeech tts;
    private static final int RC_CHECKTTSDATA = 100;
    String dilCifti = "en-tr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinleme_test_oyunu);
        //Konusturma
        Intent checkIntent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, RC_CHECKTTSDATA);
        //Konusturma

        Intent ıntent = getIntent();
        secilenhafta = ıntent.getStringExtra("secilenhafta");
        yazilan = new ArrayList<>();
        yazilan_dinleme = new ArrayList<>();
        rG_sikler = findViewById(R.id.rG_sikler);
        btn_dinle = findViewById(R.id.ibtn_Dinle);
        rB_1 = findViewById(R.id.rbtn_1);
        rB_2 = findViewById(R.id.rbtn_2);
        rB_3 = findViewById(R.id.rbtn_3);
        rB_4 = findViewById(R.id.rbtn_4);
        tV_Skor = findViewById(R.id.tVSkor);
        tV_Rekor = findViewById(R.id.tVRekor);


        cek_Kelimeler();
        kelimeninYeri = kelimeBul();
        sikleri_Ayarla();
        skorRekorCek();

        //İlk kelimeyi söyletme

    }

    public void sik_Secti(View view) {
        int sik_id = rG_sikler.getCheckedRadioButtonId();
        rB_secilen_sik = findViewById(sik_id);
        if (!(rB_secilen_sik.getText().equals(yazilan.get(kelimeninYeri)))) {
            rB_secilen_sik.setBackgroundColor(Color.RED);
            rB_secilen_sik.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rB_secilen_sik.setChecked(false);
                    rB_secilen_sik.setBackgroundResource(0);
                }
            }, 300);

            tV_Skor.setText("Skor : 0");
            TYArttır();

            return;
        }
        rB_secilen_sik.setBackgroundColor(Color.GREEN);
        rB_secilen_sik.postDelayed(new Runnable() {
            @Override
            public void run() {
                rB_secilen_sik.setChecked(false);
                rB_secilen_sik.setBackgroundResource(0);
            }
        }, 500);

        //İSTATİSTİK
        String Skor = tV_Skor.getText().toString();
        String Rekor = tV_Rekor.getText().toString();

        String d[] = Skor.split(":");

        int Skor1 = Integer.parseInt(d[1].trim());
        Skor1++;
        Skor = String.valueOf(Skor1);
        d = Rekor.split(":");
        int Rekor1 = Integer.parseInt(d[1].trim());
        if (Skor1 > Rekor1) Rekor1++;
        Rekor = String.valueOf(Rekor1);

        tV_Skor.setText("Skor : " + Skor);
        tV_Rekor.setText("Rekor : " + Rekor);

        TDArttır();

        //İSTATİSTİK BİTİŞ

        yazilan_dinleme.set(kelimeninYeri, 1);
        dinlemeTestArttır(yazilan.get(kelimeninYeri));
        kelimeninYeri = kelimeBul();
        if (kelimeninYeri == -1) {
            Intent ıntent = new Intent(this, practiceActivity.class);
            ıntent.putExtra("secilenhafta", secilenhafta);
            startActivity(ıntent);
        } else {
            sikleri_Ayarla();
            Konus(yazilan.get(kelimeninYeri), "en-tr");
        }


    }

    public void skorRekorCek() {
        database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM istatistik", null);
            int inHafta1 = cursor.getColumnIndex("TDS");
            int inHafta3 = cursor.getColumnIndex("TDR");
            cursor.moveToFirst();
            tV_Skor.setText("Skor : " + String.valueOf(cursor.getInt(inHafta1)));
            tV_Rekor.setText("Rekor : " + String.valueOf(cursor.getInt(inHafta3)));
        } catch (SQLException e) {
            System.out.println("Hafta Çekerken hata");
        } finally {
            database.close();
        }
    }

    public void TDArttır() {
        database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE istatistik SET TDD=TDD+1");
        database.execSQL("UPDATE istatistik SET TDS=TDS+1");
        database.execSQL("UPDATE istatistik SET TDR=TDR+1 WHERE TDS>TDR");

        database.close();
    }

    public void TYArttır() {
        database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE istatistik SET TDY=TDY+1");
        database.execSQL("UPDATE istatistik SET TDS=0");

        database.close();
    }

    public void dinlemeTestArttır(String kelime) {
        database = dinlemeTestOyunuActivity.this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE " + secilenhafta + " SET dinlemeTest=1 WHERE kelime='" + kelime + "'");
        database.close();
    }

    public void cek_Kelimeler() {
        database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT kelime,dinlemeTest FROM " + secilenhafta, null);
        cursor.moveToFirst();
        int indexKelime = cursor.getColumnIndex("kelime");
        int indexdinlemeTeste = cursor.getColumnIndex("dinlemeTest");
        while (cursor != null) {
            yazilan.add(cursor.getString(indexKelime));
            yazilan_dinleme.add(cursor.getInt(indexdinlemeTeste));
            boolean son = cursor.moveToNext();
            if (!son) break;
        }
    }

    public void sikleri_Ayarla() {

        int yanlis_sik, yanlis_sik1, yanlis_sik2;
        do {
            yanlis_sik = (int) (Math.random() * yazilan_dinleme.size());
        } while (kelimeninYeri == yanlis_sik);

        do {
            yanlis_sik1 = (int) (Math.random() * yazilan_dinleme.size());
        } while (yanlis_sik == yanlis_sik1 || kelimeninYeri == yanlis_sik1);

        do {
            yanlis_sik2 = (int) (Math.random() * yazilan_dinleme.size());
        }
        while (yanlis_sik == yanlis_sik2 || yanlis_sik1 == yanlis_sik2 || kelimeninYeri == yanlis_sik2);

        dogru_sik = (int) (Math.random() * 4 + 1);

        switch (dogru_sik) {
            case 1:
                rB_1.setText(yazilan.get(kelimeninYeri));
                rB_2.setText(yazilan.get(yanlis_sik));
                rB_3.setText(yazilan.get(yanlis_sik1));
                rB_4.setText(yazilan.get(yanlis_sik2));
                break;
            case 2:
                rB_2.setText(yazilan.get(kelimeninYeri));
                rB_1.setText(yazilan.get(yanlis_sik));
                rB_3.setText(yazilan.get(yanlis_sik1));
                rB_4.setText(yazilan.get(yanlis_sik2));
                break;
            case 3:
                rB_3.setText(yazilan.get(kelimeninYeri));
                rB_2.setText(yazilan.get(yanlis_sik));
                rB_1.setText(yazilan.get(yanlis_sik1));
                rB_4.setText(yazilan.get(yanlis_sik2));
                break;
            case 4:
                rB_4.setText(yazilan.get(kelimeninYeri));
                rB_2.setText(yazilan.get(yanlis_sik));
                rB_3.setText(yazilan.get(yanlis_sik1));
                rB_1.setText(yazilan.get(yanlis_sik2));
                break;
        }

    }

    public int kelimeBul() {
        for (int i = 0; i < yazilan_dinleme.size(); i++) {
            if (yazilan_dinleme.get(i) == 0) return i;
        }
        return -1;
    }

    public void btn_DinleMetot(View view) {
        Konus(yazilan.get(kelimeninYeri), "en-tr");
    }

    //Konuşturma
    public void Konus(String neKonusayim, String dilCifti) {
        setLanguage(dilCifti);
        speak(tts, neKonusayim);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (dilCifti.equals("en-tr")) {
                tts.setLanguage(Locale.ENGLISH);
            } else {
                tts.setLanguage(new Locale("tr"));
            }
        } else {
            Toast.makeText(this, "Tts Tanımlama başarısız", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CHECKTTSDATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            }
        } else {
            Intent installIntent = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        }
    }

    public void setLanguage(String dilCifti) {
        if (dilCifti.equals("en-tr")) {
            tts.setLanguage(Locale.ENGLISH);
        } else if (dilCifti.equals("tr-en")) {
            tts.setLanguage(new Locale("tr"));
        }

    }

    public void speak(TextToSpeech tts, String text) {
        if (tts.isSpeaking()) {
            tts.stop();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!text.equals("")) {
                    tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
                } else {
                    Toast.makeText(this, "Konusturma Hatası", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!text.equals("")) {
                    tts.speak(text, TextToSpeech.QUEUE_ADD, null);
                } else {
                    Toast.makeText(this, "Konusturma hatası 3", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
    //Konuşturma Bitiş
}

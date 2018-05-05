package com.yenilmez.mis;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class testOyunuActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<String> yazilan;
    ArrayList<String> yazilan_cevaplar;
    ArrayList<Integer> yazilan_test;
    int cevap = 0, kelimeninYeri;
    SQLiteDatabase database = chooseWeekActivity.database;

    Button btn_sik1, btn_sik2, btn_sik3, btn_sik4;
    TextView tw_anlamlar, tV_Skor, tV_Rekor;
    String secilenhafta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_oyunu);

        yazilan = new ArrayList<>();
        yazilan_cevaplar = new ArrayList<>();
        yazilan_test = new ArrayList<>();
        btn_sik1 = findViewById(R.id.btn_sik1);
        btn_sik1.setOnClickListener(this);
        btn_sik2 = findViewById(R.id.btn_sik2);
        btn_sik2.setOnClickListener(this);
        btn_sik3 = findViewById(R.id.btn_sik3);
        btn_sik3.setOnClickListener(this);
        btn_sik4 = findViewById(R.id.btn_sik4);
        btn_sik4.setOnClickListener(this);
        tw_anlamlar = findViewById(R.id.tw_Anlamlar);
        tV_Skor = findViewById(R.id.tVSkor);
        tV_Rekor = findViewById(R.id.tVRekor);
        Intent ıntent = getIntent();
        secilenhafta = ıntent.getStringExtra("secilenhafta");

        //VERİTABANI

        try {
            chooseWeekActivity.database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
            Cursor cursor = chooseWeekActivity.database.rawQuery("SELECT * FROM " + secilenhafta + " WHERE test=0", null);
            int indexKelime = cursor.getColumnIndex("kelime");
            int indexanlamlari = cursor.getColumnIndex("anlamlari");
            int indexTest = cursor.getColumnIndex("test");
            cursor.moveToFirst();
            while (true) {
                yazilan.add(cursor.getString(indexKelime));
                yazilan_cevaplar.add(cursor.getString(indexanlamlari));
                yazilan_test.add(cursor.getInt(indexTest));
                boolean son = cursor.moveToNext();
                if (!son) break;
            }
        } catch (Exception e) {
            System.out.println("hata");
        } finally {
            chooseWeekActivity.database.close();
        }

        //VERİTABANI

        int dogru_sik = (int) (Math.random() * 4 + 1);
        sikleriAyarlar(dogru_sik);
        skorRekorCek();
        //3311


        btn_sik1.setBackgroundColor(Color.WHITE);
        btn_sik2.setBackgroundColor(Color.WHITE);
        btn_sik3.setBackgroundColor(Color.WHITE);
        btn_sik4.setBackgroundColor(Color.WHITE);


    }

    public void sikleriAyarlar(int sik) {
        int yanlis_sik, yanlis_sik1, yanlis_sik2;
        do {
            yanlis_sik = (int) (Math.random() * yazilan_test.size());
        } while (kelimeninYeri == yanlis_sik);

        do {
            yanlis_sik1 = (int) (Math.random() * yazilan_test.size());
        } while (yanlis_sik == yanlis_sik1 || kelimeninYeri == yanlis_sik1);

        do {
            yanlis_sik2 = (int) (Math.random() * yazilan_test.size());
        }
        while (yanlis_sik == yanlis_sik2 || yanlis_sik1 == yanlis_sik2 || kelimeninYeri == yanlis_sik2);

        kelimeninYeri = kelimeBul();
        switch (sik) {
            case 1:
                btn_sik1.setText(yazilan.get(kelimeninYeri));
                cevap = 1;

                btn_sik2.setText(yazilan.get(yanlis_sik));
                btn_sik3.setText(yazilan.get(yanlis_sik1));
                btn_sik4.setText(yazilan.get(yanlis_sik2));

                break;
            case 2:
                btn_sik2.setText(yazilan.get(kelimeninYeri));
                cevap = 2;

                btn_sik1.setText(yazilan.get(yanlis_sik));
                btn_sik3.setText(yazilan.get(yanlis_sik1));
                btn_sik4.setText(yazilan.get(yanlis_sik2));
                break;
            case 3:
                btn_sik3.setText(yazilan.get(kelimeninYeri));
                cevap = 3;
                btn_sik2.setText(yazilan.get(yanlis_sik));
                btn_sik1.setText(yazilan.get(yanlis_sik1));
                btn_sik4.setText(yazilan.get(yanlis_sik2));
                break;
            case 4:
                btn_sik4.setText(yazilan.get(kelimeninYeri));
                cevap = 4;
                btn_sik2.setText(yazilan.get(yanlis_sik));
                btn_sik3.setText(yazilan.get(yanlis_sik1));
                btn_sik1.setText(yazilan.get(yanlis_sik2));
                break;
        }
        tw_anlamlar.setText("");
        String[] d = yazilan_cevaplar.get(kelimeninYeri).toString().split(",");
        for (int i = 0; i < d.length; i++) {
            tw_anlamlar.setText(tw_anlamlar.getText() + d[i] + "\n");
        }
        tw_anlamlar.setMovementMethod(new ScrollingMovementMethod());

    }

    public int kelimeBul() {
        for (int i = 0; i < yazilan_test.size(); i++) {
            if (yazilan_test.get(i) == 0) return i;
        }
        return -1;
    }

    public void testBildiArttır(String kelime) {
        database = testOyunuActivity.this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE " + secilenhafta + " SET test=1 WHERE kelime='" + kelime + "'");
        database.close();
    }

    public void TDArttır() {
        database = testOyunuActivity.this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE istatistik SET TD=TD+1");
        database.execSQL("UPDATE istatistik SET TS=TS+1");
        database.execSQL("UPDATE istatistik SET TR=TR+1 WHERE TS>TR");

        database.close();
    }

    public void TYArttır() {
        database = testOyunuActivity.this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE istatistik SET TY=TY+1");
        database.execSQL("UPDATE istatistik SET TS=0");

        database.close();
    }

    public void metot() {
        testBildiArttır(yazilan.get(kelimeninYeri));
        yazilan_test.set(kelimeninYeri, 1);

        kelimeninYeri = kelimeBul();
        if (kelimeninYeri != -1) {
            int dogru_sik = (int) (Math.random() * 4 + 1);
            sikleriAyarlar(dogru_sik);
        } else {
            Intent ıntent = new Intent(this, practiceActivity.class);
            ıntent.putExtra("secilenhafta", secilenhafta);
            startActivity(ıntent);
        }
    }

    public void skorRekorCek() {
        database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM istatistik", null);
            int inHafta1 = cursor.getColumnIndex("TS");
            int inHafta3 = cursor.getColumnIndex("TR");
            cursor.moveToFirst();
            tV_Skor.setText("Skor : " + String.valueOf(cursor.getInt(inHafta1)));
            tV_Rekor.setText("Rekor : " + String.valueOf(cursor.getInt(inHafta3)));
        } catch (SQLException e) {
            System.out.println("Hafta Çekerken hata");
        } finally {
            database.close();
        }
    }

    @Override
    public void onClick(View view) {
        boolean dogrumu = true;

        btn_sik1.setClickable(false);
        btn_sik2.setClickable(false);
        btn_sik3.setClickable(false);
        btn_sik4.setClickable(false);
        final Button tıklanan = findViewById(view.getId());
        if (tıklanan.getText().toString().equals(yazilan.get(kelimeninYeri).toString())) {
            tıklanan.setBackgroundColor(Color.GREEN);
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
        } else {
            tV_Skor.setText("Skor : 0");
            tıklanan.setBackgroundColor(Color.RED);
            TYArttır();
            dogrumu = false;
        }

        final long changeTime = 200;
        final boolean finalDogrumu = dogrumu;
        tıklanan.postDelayed(new Runnable() {
            @Override
            public void run() {
                tıklanan.setBackgroundColor(Color.WHITE);
                if (finalDogrumu) metot();
                btn_sik1.setClickable(true);
                btn_sik2.setClickable(true);
                btn_sik3.setClickable(true);
                btn_sik4.setClickable(true);
            }
        }, changeTime);
    }
}

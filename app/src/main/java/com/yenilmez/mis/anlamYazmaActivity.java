package com.yenilmez.mis;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class anlamYazmaActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<String> yazilan;
    ArrayList<String> yazilan_anlamlari;
    ArrayList<Integer> yazilan_anlamDoldur;
    HashSet<Integer> rasgele, dogruBtnler;
    char[] temp;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8;
    SQLiteDatabase database = chooseWeekActivity.database;
    String secilenhafta;
    int kelimeninYeri = 0;
    TextView tW_Kelime, tW_Anlamlar,tV_Skor,tV_Rekor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anlam_yazma);

        Intent ıntent = getIntent();
        secilenhafta = ıntent.getStringExtra("secilenhafta");

        yazilan = new ArrayList<>();
        yazilan_anlamlari = new ArrayList<>();
        yazilan_anlamDoldur = new ArrayList<>();
        kelimeleriGetir(secilenhafta);
        tW_Kelime = findViewById(R.id.tW_kelime);
        tW_Anlamlar = findViewById(R.id.tW_anlamlar);
        tV_Skor=findViewById(R.id.tVSkor);
        tV_Rekor=findViewById(R.id.tVRekor);

        btnTanımla();

        rasgele = new HashSet<>();
        dogruBtnler = new HashSet<>();
        butonlarıAyarla();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "321impact.ttf");
        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "Ubuntu.ttf");
        tW_Kelime.setTypeface(typeface);
        tW_Anlamlar.setTypeface(typeface1);

        skorRekorCek();
    }


    //METOTLAR
    public void kelimeleriGetir(String secilenhafta1) {
        chooseWeekActivity.database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        Cursor cursor = chooseWeekActivity.database.rawQuery("SELECT * FROM " + secilenhafta1 + " WHERE anlamDoldur=0", null);
        int indexKelime = cursor.getColumnIndex("kelime");
        int indexanlamlari = cursor.getColumnIndex("anlamlari");
        int indexAnlamDoldur = cursor.getColumnIndex("anlamDoldur");
        cursor.moveToFirst();
        while (true) {
            yazilan.add(cursor.getString(indexKelime));
            yazilan_anlamlari.add(cursor.getString(indexanlamlari));
            yazilan_anlamDoldur.add(cursor.getInt(indexAnlamDoldur));
            boolean son = cursor.moveToNext();
            if (!son) break;
        }
        database.close();
    }

    public void anlamYazmaArttır(String kelime){
        database = anlamYazmaActivity.this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE " + secilenhafta + " SET anlamDoldur=1 WHERE kelime='" + kelime + "'");
        database.close();
    }
    public void butonlarıAyarla() {
        kelimeninYeri = kelimeninYeriniBul();
        if (kelimeninYeri == -1) {
            Bitir();
            return;
        }

        temp = new char[yazilan.get(kelimeninYeri).length()];

        for (int i = 0; i < yazilan.get(kelimeninYeri).length(); i++) {
            temp[i] = yazilan.get(kelimeninYeri).charAt(i);
        }
        //BUTONLARI SIFIRLA
        btn1.setText("");
        btn2.setText("");
        btn3.setText("");
        btn4.setText("");
        btn5.setText("");
        btn6.setText("");
        btn7.setText("");
        btn8.setText("");
        //BUTONLARI SIFIRLA


        rasgeleHarfleriSecerGösterilen();
        rasgeleAyarlaGösterilmeyenHarflerHangiButunda();
        Iterator i2 = rasgele.iterator();
        while (i2.hasNext()) {
            int a = (int) i2.next();
            temp[a] = '_';
            switch (a) {
                case 0:
                    btn1.setText(String.valueOf(yazilan.get(kelimeninYeri).charAt(a)));
                    break;
                case 1:
                    btn2.setText(String.valueOf(yazilan.get(kelimeninYeri).charAt(a)));
                    break;
                case 2:
                    btn3.setText(String.valueOf(yazilan.get(kelimeninYeri).charAt(a)));
                    break;
                case 3:
                    btn4.setText(String.valueOf(yazilan.get(kelimeninYeri).charAt(a)));
                    break;
                case 4:
                    btn5.setText(String.valueOf(yazilan.get(kelimeninYeri).charAt(a)));
                    break;
                case 5:
                    btn6.setText(String.valueOf(yazilan.get(kelimeninYeri).charAt(a)));
                    break;
                case 6:
                    btn7.setText(String.valueOf(yazilan.get(kelimeninYeri).charAt(a)));
                    break;
                case 7:
                    btn8.setText(String.valueOf(yazilan.get(kelimeninYeri).charAt(a)));
                    break;
            }

        }
        String topla = "";
        for (char yaz : temp) {
            topla += yaz;
        }
        tW_Kelime.setText(topla);
        tW_Anlamlar.setText("");
        String d[] = yazilan_anlamlari.get(kelimeninYeri).toString().split(",");
        for (int i = 0; i < d.length; i++) {
            tW_Anlamlar.setText(tW_Anlamlar.getText() + "\n" + d[i]);
        }

        //BOS BUTONLARA RASGELE HARF ATA
        int a='a';
        int z='z';
        int fark=z-a+1;

        for (int i=0;i<8-rasgele.size();i++){
            boolean  varmı;
            int rasgeleHarf;
            do {
                rasgeleHarf=(int)(Math.random()*fark+a);
                varmı=harfVarmı((char) rasgeleHarf);
            }while (varmı);
            if (btn1.getText().equals(""))btn1.setText(String.valueOf((char)rasgeleHarf));
            else if (btn2.getText().equals(""))btn2.setText(String.valueOf((char)rasgeleHarf));
            else if (btn3.getText().equals(""))btn3.setText(String.valueOf((char)rasgeleHarf));
            else if (btn4.getText().equals(""))btn4.setText(String.valueOf((char)rasgeleHarf));
            else if (btn5.getText().equals(""))btn5.setText(String.valueOf((char)rasgeleHarf));
            else if (btn6.getText().equals(""))btn6.setText(String.valueOf((char)rasgeleHarf));
            else if (btn7.getText().equals(""))btn7.setText(String.valueOf((char)rasgeleHarf));
            else if (btn8.getText().equals(""))btn8.setText(String.valueOf((char)rasgeleHarf));

        }
        //BOS BUTONLARA RASGELE HARF ATA
    }
    public boolean harfVarmı(char harf){
        if(btn1.getText().length()!=0)
            if (btn1.getText().charAt(0)==harf)return true;
        if(btn2.getText().length()!=0)
            if (btn2.getText().charAt(0)==harf)return true;
        if(btn3.getText().length()!=0)
            if (btn3.getText().charAt(0)==harf)return true;
        if(btn4.getText().length()!=0)
            if (btn4.getText().charAt(0)==harf)return true;
        if(btn5.getText().length()!=0)
            if (btn5.getText().charAt(0)==harf)return true;
        if(btn6.getText().length()!=0)
            if (btn6.getText().charAt(0)==harf)return true;
        if(btn7.getText().length()!=0)
            if (btn7.getText().charAt(0)==harf)return true;
        if(btn8.getText().length()!=0)
            if (btn8.getText().charAt(0)==harf)return true;
        return false;
    }

    public int kelimeninYeriniBul() {
        for (int i = 0; i < yazilan_anlamDoldur.size(); i++) {
            if (yazilan_anlamDoldur.get(i) == 0) {
                kelimeninYeri = i;
                return i;
            }
        }
        return -1;
    }

    public void rasgeleHarfleriSecerGösterilen() {
        rasgele.removeAll(rasgele);
        int uzunluk = yazilan.get(kelimeninYeri).length();
        uzunluk = uzunluk * 30 / 100;
        uzunluk = (int) Math.floor(uzunluk);
        uzunluk += 1;

        while (true) {
            int ras = (int) (Math.random() * yazilan.get(kelimeninYeri).length());
            rasgele.add(ras);
            if (rasgele.size() == uzunluk) {
                return;
            }
        }
    }

    public void rasgeleAyarlaGösterilmeyenHarflerHangiButunda() {
        dogruBtnler.removeAll(dogruBtnler);
        int uzunluk = yazilan.get(kelimeninYeri).length();
        uzunluk = uzunluk * 30 / 100;
        uzunluk = (int) Math.floor(uzunluk);
        uzunluk += 1;

        while (true) {
            int ras = (int) (Math.random() * 8);
            dogruBtnler.add(ras);
            if (dogruBtnler.size() == uzunluk) {
                return;
            }
        }
    }

    public void Bitir() {
        Intent ıntent = new Intent(this, practiceActivity.class);
        ıntent.putExtra("secilenhafta", secilenhafta);
        startActivity(ıntent);
    }

    public void btnTanımla() {
        btn1 = findViewById(R.id.button2);
        btn1.setOnClickListener(this);
        btn2 = findViewById(R.id.button3);
        btn2.setOnClickListener(this);
        btn3 = findViewById(R.id.button4);
        btn3.setOnClickListener(this);
        btn4 = findViewById(R.id.button5);
        btn4.setOnClickListener(this);
        btn5 = findViewById(R.id.button6);
        btn5.setOnClickListener(this);
        btn6 = findViewById(R.id.button7);
        btn6.setOnClickListener(this);
        btn7 = findViewById(R.id.button8);
        btn7.setOnClickListener(this);
        btn8 = findViewById(R.id.button9);
        btn8.setOnClickListener(this);
    }

    public int boslukBul() {
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == '_') {
                return i;
            }
        }
        return -1;
    }


    public void skorRekorCek() {
        database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM istatistik", null);
            int inHafta1 = cursor.getColumnIndex("BS");
            int inHafta3 = cursor.getColumnIndex("BR");
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
        database.execSQL("UPDATE istatistik SET BD=BD+1");
        database.execSQL("UPDATE istatistik SET BS=BS+1");
        database.execSQL("UPDATE istatistik SET BR=BR+1 WHERE BS>BR");

        database.close();
    }

    public void TYArttır() {
        database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE istatistik SET BosY=BosY+1");
        database.execSQL("UPDATE istatistik SET BS=0");

        database.close();
    }

    @Override
    public void onClick(View view) {
        Button tıklanan = findViewById(view.getId());
        char a = tıklanan.getText().charAt(0);
        int i = boslukBul();

        if (yazilan.get(kelimeninYeri).charAt(i) == a) {

            //SKOR ve REKOR
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
            //SKOR ve REKOR BİTİş


            temp[i] = a;
            String topla = "";
            for (char yaz : temp) {
                topla += yaz;
            }
            tW_Kelime.setText(topla);
            anlamYazmaArttır(yazilan.get(kelimeninYeri));
            i=boslukBul();
            if (i == -1) {
                yazilan_anlamDoldur.set(kelimeninYeri,1);
                butonlarıAyarla();
                return;
            }

        }else{
            tV_Skor.setText("Skor : 0");
            TYArttır();
        }


        return;


    }
}

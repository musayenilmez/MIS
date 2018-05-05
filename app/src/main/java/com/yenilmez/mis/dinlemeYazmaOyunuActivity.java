package com.yenilmez.mis;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

public class dinlemeYazmaOyunuActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {


    ArrayList<String> yazilan;
    ArrayList<String> yazilan_anlamlari;
    ArrayList<Integer> yazilan_dinlemeDoldur;
    HashSet<Integer> rasgele, dogruBtnler;
    char[] temp;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8;
    ImageButton btn_KonusturmaButonu;
    SQLiteDatabase database = chooseWeekActivity.database;
    String secilenhafta;
    int kelimeninYeri = 0;
    TextView tW_Kelime,tV_Skor,tV_Rekor;


    //Konusturma
    TextToSpeech tts;
    private static final int RC_CHECKTTSDATA = 100;
    String dilCifti = "en-tr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinleme_yazma_oyunu);

        //Konusturma
        Intent checkIntent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, RC_CHECKTTSDATA);
        //Konusturma


        Intent ıntent = getIntent();
        secilenhafta = ıntent.getStringExtra("secilenhafta");

        yazilan = new ArrayList<>();
        yazilan_anlamlari = new ArrayList<>();
        yazilan_dinlemeDoldur = new ArrayList<>();
        btn_KonusturmaButonu = findViewById(R.id.btn_KonusturmaButonu);
        kelimeleriGetir(secilenhafta);
        tW_Kelime = findViewById(R.id.tW_kelime);
        tV_Skor=findViewById(R.id.tVSkor);
        tV_Rekor=findViewById(R.id.tVRekor);
        tts=new TextToSpeech(this,this);




        btnTanımla();

        rasgele = new HashSet<>();
        dogruBtnler = new HashSet<>();
        butonlarıAyarla();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "321impact.ttf");
        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "Ubuntu.ttf");
        tW_Kelime.setTypeface(typeface);

        skorRekorCek();

    }


    //İSTATİSDİK
    public void skorRekorCek() {
        database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM istatistik", null);
            int inHafta1 = cursor.getColumnIndex("BDS");
            int inHafta3 = cursor.getColumnIndex("BDR");
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
        database.execSQL("UPDATE istatistik SET BDD=BDD+1");
        database.execSQL("UPDATE istatistik SET BDS=BDS+1");
        database.execSQL("UPDATE istatistik SET BDR=BDR+1 WHERE BDS>BDR");

        database.close();
    }

    public void TYArttır() {
        database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE istatistik SET BDY=BDY+1");
        database.execSQL("UPDATE istatistik SET BDS=0");

        database.close();
    }
    //İSTATİSDİK BİTİŞ


    //METOTLAR

    public void kelimeleriGetir(String secilenhafta1) {
        chooseWeekActivity.database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        Cursor cursor = chooseWeekActivity.database.rawQuery("SELECT * FROM " + secilenhafta1 + " WHERE dinlemeDoldur=0", null);
        int indexKelime = cursor.getColumnIndex("kelime");
        int indexanlamlari = cursor.getColumnIndex("anlamlari");
        int indexAnlamDoldur = cursor.getColumnIndex("dinlemeDoldur");
        cursor.moveToFirst();
        while (true) {
            yazilan.add(cursor.getString(indexKelime));
            yazilan_anlamlari.add(cursor.getString(indexanlamlari));
            yazilan_dinlemeDoldur.add(cursor.getInt(indexAnlamDoldur));
            boolean son = cursor.moveToNext();
            if (!son) break;
        }
        database.close();
    }

    public void anlamDinlemeArttır(String kelime) {
        database = dinlemeYazmaOyunuActivity.this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        database.execSQL("UPDATE " + secilenhafta + " SET dinlemeDoldur=1 WHERE kelime='" + kelime + "'");
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

        //BOS BUTONLARA RASGELE HARF ATA
        int a = 'a';
        int z = 'z';
        int fark = z - a + 1;

        for (int i = 0; i < 8 - rasgele.size(); i++) {
            boolean varmı;
            int rasgeleHarf;
            do {
                rasgeleHarf = (int) (Math.random() * fark + a);
                varmı = harfVarmı((char) rasgeleHarf);
            } while (varmı);
            if (btn1.getText().equals("")) btn1.setText(String.valueOf((char) rasgeleHarf));
            else if (btn2.getText().equals("")) btn2.setText(String.valueOf((char) rasgeleHarf));
            else if (btn3.getText().equals("")) btn3.setText(String.valueOf((char) rasgeleHarf));
            else if (btn4.getText().equals("")) btn4.setText(String.valueOf((char) rasgeleHarf));
            else if (btn5.getText().equals("")) btn5.setText(String.valueOf((char) rasgeleHarf));
            else if (btn6.getText().equals("")) btn6.setText(String.valueOf((char) rasgeleHarf));
            else if (btn7.getText().equals("")) btn7.setText(String.valueOf((char) rasgeleHarf));
            else if (btn8.getText().equals("")) btn8.setText(String.valueOf((char) rasgeleHarf));

        }
        Konus(yazilan.get(kelimeninYeri), "en-tr");
        //BOS BUTONLARA RASGELE HARF ATA
    }

    public boolean harfVarmı(char harf) {
        if (btn1.getText().length() != 0)
            if (btn1.getText().charAt(0) == harf) return true;
        if (btn2.getText().length() != 0)
            if (btn2.getText().charAt(0) == harf) return true;
        if (btn3.getText().length() != 0)
            if (btn3.getText().charAt(0) == harf) return true;
        if (btn4.getText().length() != 0)
            if (btn4.getText().charAt(0) == harf) return true;
        if (btn5.getText().length() != 0)
            if (btn5.getText().charAt(0) == harf) return true;
        if (btn6.getText().length() != 0)
            if (btn6.getText().charAt(0) == harf) return true;
        if (btn7.getText().length() != 0)
            if (btn7.getText().charAt(0) == harf) return true;
        if (btn8.getText().length() != 0)
            if (btn8.getText().charAt(0) == harf) return true;
        return false;
    }

    public int kelimeninYeriniBul() {
        for (int i = 0; i < yazilan_dinlemeDoldur.size(); i++) {
            if (yazilan_dinlemeDoldur.get(i) == 0) {
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

    public void kelimeyiSöyle(View view) {
        Konus(yazilan.get(kelimeninYeri), "en-tr");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != btn_KonusturmaButonu.getId()) {

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
                anlamDinlemeArttır(yazilan.get(kelimeninYeri));
                i = boslukBul();
                if (i == -1) {
                    yazilan_dinlemeDoldur.set(kelimeninYeri, 1);
                    butonlarıAyarla();
                    return;
                }
            }else{
                tV_Skor.setText("Skor : 0");
                TYArttır();
            }


        }

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

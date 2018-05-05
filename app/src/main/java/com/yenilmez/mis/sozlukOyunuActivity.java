package com.yenilmez.mis;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class sozlukOyunuActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {


    ViewPager viewPager;
    viewPagerAdapter adapter;
    ArrayList<String> yazlan;
    ArrayList<String> yazlan_cevaplar;
    String secilenhafta;
    SQLiteDatabase database = chooseWeekActivity.database;

    //Konusturma
    TextToSpeech tts;
    private static final int RC_CHECKTTSDATA = 100;
    String dilCifti = "en-tr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sozluk_oyunu);

        yazlan = new ArrayList<>();
        yazlan_cevaplar = new ArrayList<>();
        //Konusturma
        Intent checkIntent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, RC_CHECKTTSDATA);
        //Konusturma

        Intent ıntent = getIntent();
        secilenhafta = ıntent.getStringExtra("secilenhafta");

        kelimeleriGetir(secilenhafta);

        viewPager = findViewById(R.id.vp);
        adapter = new viewPagerAdapter(this, yazlan, yazlan_cevaplar);
        viewPager.setAdapter(adapter);


        //VİEWPAGER SAYFA DEĞŞİTİĞİNDE TETİKLENEN METOTLAR
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                try {
                    if(positionOffset==0){
                        database = sozlukOyunuActivity.this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
                        database.execSQL("UPDATE " + secilenhafta + " SET sozluk=1 WHERE kelime='" + yazlan.get(position) + "'");
                        database.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void kelimeleriGetir(String secilenhafta1) {
        chooseWeekActivity.database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        Cursor cursor = chooseWeekActivity.database.rawQuery("SELECT * FROM " + secilenhafta1 + " WHERE sozluk=0", null);
        int indexKelime = cursor.getColumnIndex("kelime");
        int indexanlamlari = cursor.getColumnIndex("anlamlari");
        cursor.moveToFirst();
        while (true) {
            yazlan.add(cursor.getString(indexKelime));
            yazlan_cevaplar.add(cursor.getString(indexanlamlari));
            boolean son = cursor.moveToNext();
            if (!son) break;
        }
    }

    public void konus(View view) {
        setLanguage("türkce", "en-tr");
        speak(tts, yazlan.get(viewPager.getCurrentItem()).toString());

    }

    //Konuşturma
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

    public void setLanguage(String dil, String dilCifti) {
        if (dil.equals("türkce")) {
            if (dilCifti.equals("en-tr")) {
                tts.setLanguage(Locale.ENGLISH);
            } else if (dilCifti.equals("tr-en")) {
                tts.setLanguage(new Locale("tr"));
            }
        } else if (dil.equals("ingilizce")) {
            if (dilCifti.equals("en-tr")) {
                tts.setLanguage(new Locale("tr"));
            } else if (dilCifti.equals("tr-en")) {
                tts.setLanguage(Locale.ENGLISH);
            }
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

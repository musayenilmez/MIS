package com.yenilmez.mis;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class addWordActivity extends AppCompatActivity  {
    HashSet<String> kelimeler;
    HashSet<String> kalıplar;
    ArrayList<String> cevaplar;
    EditText txtParagraf;
    String yandexKey = "trnsl.1.1.20180224T192321Z.967021125d02dca4.d76b5fc4c0c3fc65f913870e6d48700ff8d779c5";
    String yandexKey_Dictionary = "dict.1.1.20180301T204948Z.6f4bf7a271061f5b.f2905b615a1153730721007fbaabfbbe02d6e8de";
    String dilCifti = "en-tr";
    String sonEleman = "";
    TranslatorBackgrondTask te;
    int kacTaneHazir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        kelimeler = new HashSet<String>();
        kalıplar = new HashSet<String>();
        cevaplar = new ArrayList<>();
        kacTaneHazir = 0;


        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "kgiwantcrazy.otf");
        Button button = findViewById(R.id.btnKelimeleriListele);
        button.setTypeface(myTypeface);
        txtParagraf = findViewById(R.id.txtParagraf);

        //Konusturma
        Intent checkIntent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.yardim, menu);


        return super.onCreateOptionsMenu(menu);
    }

    public void cevir(String kelime, String dilCifti, int Tür) {
        //0  kelime çevirisidir.
        //1 cümle veya kalıp çevirisidir.
        te = new TranslatorBackgrondTask();
        try {
            String url = null;
            if (Tür == 0) {
                url = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=" + yandexKey_Dictionary + "&lang=" + dilCifti + "&text=" + kelime;
            } else if (Tür == 1) {
                url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                        + "&text=" + kelime + "&lang=" + dilCifti;
            }
            te.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parcala(View view) {
        String paragraf = txtParagraf.getText().toString().toLowerCase().trim();
        txtParagraf.setText(kacTaneHazir + " tane kelime hazır...");
        kacTaneHazir = 0;

        kelimeler.removeAll(kelimeler);
        kalıplar.removeAll(kalıplar);
        cevaplar.removeAll(cevaplar);

        //KALIP CIKARMA
        int tut = -1;
        for (int i = 0; i < paragraf.length() - 1; i += 1) {
            if (paragraf.charAt(i) == '#' && paragraf.charAt(i + 1) == '#') {
                if (tut == -1) {
                    tut = i;
                } else {
                    String kalıp = paragraf.substring(tut + 2, i);

                    paragraf = paragraf.substring(0, tut) + paragraf.substring(i + 2, paragraf.length());

                    kalıp.trim().toLowerCase();
                    kalıplar.add(kalıp);
                    tut = -1;
                    i = 0;
                    continue;
                }
            }
        }
        //KALIP CIKARMA BITIS

        String kelime = "";
        for (int i = 0; i < paragraf.length(); i++) {
            if (paragraf.charAt(i) >= 'a' && paragraf.charAt(i) <= 'z') {
                kelime += paragraf.charAt(i);
                continue;
            }
            if (kelime.trim().toLowerCase().length() > 2) {
                kelime = kelime.toLowerCase().trim();
                kelimeler.add(kelime);
            }
            kelime = "";
        }
        if (kelime.toLowerCase().trim().length() > 2) {
            kelime = kelime.toLowerCase().trim();
            kelimeler.add(kelime);
        }

        for (String kalı : kalıplar) {
            cevir(kalı, "en-tr", 1);
            sonEleman = kalı;
        }
        for (String keli : kelimeler) {
            cevir(keli, dilCifti, 0);
            sonEleman = keli;
        }

        //setLanguage("türkce","tr-en");
        /*
        setLanguage("türkce","en-tr");
        speak(tts,"come");
         */

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.yardimMenü) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nasıl Kayıt Eder ?").setMessage("1)Girilen paragraftaki kelimeleri parcalayıp türkçe karşılığını otomatik olarak sizin için bulur.\n" +
                    "2)Eğer cevap yanlış ise düzeltebilirsiniz.\n" +
                    "3)>Kelime=anlam=anlam1 formatında 1 den fazla anlam ekliyebilirsiniz.\n" +
                    "4)Eğer bir kalıp kaydetmek isterseniz ya 3.kural gibi elinizle eklemelisiniz yada paragrafta kalıbın başına ve bitişine ## koymalısınız.").setIcon(android.R.drawable.ic_menu_help);
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    class TranslatorBackgrondTask extends AsyncTask<String, Void, String> {
        String arananKelime = "";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            char hangi = arananKelime.split("key")[1].charAt(1);

            String[] d = arananKelime.split("&text");
            String[] d1 = d[1].split("&lang");
            arananKelime = ">" + d1[0].substring(1);


            String anlamları = "";

            try {
                if (hangi == 't') {
                    JSONObject anla = new JSONObject(s);
                    anlamları = anla.getString("text");
                    anlamları = anlamları.substring(2, anlamları.length() - 2);

                } else if (hangi == 'd') {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray dw = jsonObject.getJSONArray("def");
                    for (int j = 0; j < dw.length(); j++) {
                        JSONObject dw1 = dw.getJSONObject(j);
                        JSONArray dw2 = dw1.getJSONArray("tr");
                        JSONObject anlam = null;

                        for (int i = 0; i < dw2.length(); i++) {
                            anlam = dw2.getJSONObject(i);
                            anlamları = anlamları + anlam.getString("text") + ",";
                            if(i==4)break;

                        }
                    }

                }
                if (anlamları.trim().length() < 2) {

                    return;
                } else {
                    boolean tru = arananKelime.substring(2).equals(anlamları.substring(1, anlamları.length()));

                    if (!tru) {
                        if (anlamları.charAt(anlamları.length() - 1) == ',')
                            anlamları = anlamları.substring(0, anlamları.length() - 1);
                        for (int i = 0; i < 2; i++) {
                            if (arananKelime.charAt(0) == '>') {
                                arananKelime = arananKelime.substring(1);
                                i = 0;
                            } else if (anlamları.charAt(0) == '>') {
                                anlamları = anlamları.substring(1);
                                i = 0;
                            }
                            if (sonEleman.charAt(0) == '>') {
                                sonEleman = sonEleman.substring(1);
                                i = 0;
                            }

                        }
                        anlamları = anlamları.toLowerCase().trim();
                        String[] virgülSayısı = anlamları.split(",");
                        String temp="";
                        for (int i=0;i<virgülSayısı.length;i++){
                            if(i>3)break;
                            temp=temp+virgülSayısı[i]+",";
                        }
                        temp=temp.substring(0,temp.length()-1);
                        cevaplar.add(arananKelime + " : " + temp);
                        kacTaneHazir++;
                        txtParagraf.setText(kacTaneHazir + " tane kelime hazır...");
                    }

                }

                System.out.println(arananKelime+"  "+sonEleman);
                if (arananKelime.equals(sonEleman)) {
                    if (cevaplar.size() == 0) {
                        txtParagraf.setText("Kelime girmediniz veya program EN ve TR karşılığı aynı olan kelimeliri çıkardı için kelime listeniz boştur.");
                        return;
                    }
                    Intent inIntent = new Intent(getApplicationContext(), addWordListActivity.class);
                    inIntent.putExtra("cevaplar", cevaplar);
                    startActivity(inIntent);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(params[0]);
                arananKelime = params[0];
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream ınputStream = httpURLConnection.getInputStream();
                InputStreamReader ınputStreamReader = new InputStreamReader(ınputStream);

                int data = ınputStreamReader.read();
                while (data > 0) {
                    char character = (char) data;
                    result += character;
                    data = ınputStreamReader.read();
                }

            } catch (Exception e) {
                System.out.println("Baglanti Hatasi");
            }
            return result;
        }
    }

}

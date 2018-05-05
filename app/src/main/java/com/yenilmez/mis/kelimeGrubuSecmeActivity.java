package com.yenilmez.mis;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class kelimeGrubuSecmeActivity extends AppCompatActivity {
    ArrayList<String> arl_cekilenHaftalar;
    ArrayList<String> arl_cekilenKelimeler;
    ArrayAdapter aa_cekilenHaftalar;
    ListView lw_haftalar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelime_grubu_secme);

        //Label Yazı Tipi Değiştirme
        arl_cekilenHaftalar = new ArrayList<>();
        aa_cekilenHaftalar = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arl_cekilenHaftalar);
        lw_haftalar = findViewById(R.id.lw_Haftalar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "kgiwantcrazy.otf");
        TextView textView = findViewById(R.id.tw_Label);
        textView.setTypeface(typeface);

        //Label Yazı Tipi Değiştirme

        //Hafta Seçme İşlemleri
        cekHaftalar();
        lw_haftalar.setAdapter(aa_cekilenHaftalar);
        lw_haftalar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String haftaSecildi=arl_cekilenHaftalar.get(position);
                String d[]=haftaSecildi.split("-");
                System.out.println(d[0]);
                int kelimeSayisi = kelimeGrubundaKacKelime(d[0].trim());
                if (kelimeSayisi > 10) {
                    Intent ıntent = new Intent(kelimeGrubuSecmeActivity.this, practiceActivity.class);
                    ıntent.putExtra("secilenhafta", d[0]);
                    startActivity(ıntent);
                } else {
                    Toast.makeText(kelimeGrubuSecmeActivity.this, "Egzersiz yapabilmeniz için " +
                            "kelime grubunuzda en az 10 kelime olmalıdır.", Toast.LENGTH_LONG).show();
                }


            }
        });
        //Hafta Seçme İşlemleri

    }

    public void cekHaftalar() {
        try {
            chooseWeekActivity.database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
            Cursor cursor = chooseWeekActivity.database.rawQuery("SELECT * FROM kelime_Grublari", null);
            int indexHafta = cursor.getColumnIndex("hafta");
            cursor.moveToFirst();
            while (true) {
                String haftaİsmi = cursor.getString(indexHafta);
                Cursor cursor2 = chooseWeekActivity.database.rawQuery("SELECT COUNT(*) FROM " + haftaİsmi, null);
                cursor2.moveToFirst();
                int kelimesayisi = cursor2.getInt(0);
                arl_cekilenHaftalar.add(haftaİsmi + "-" + kelimesayisi);
                boolean son = cursor.moveToNext();
                if (!son) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chooseWeekActivity.database.close();
        }

    }

    public int kelimeGrubundaKacKelime(String hafta) {
        int count = 0;
        try {
            chooseWeekActivity.database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
            Cursor mCount = chooseWeekActivity.database.rawQuery("select count(*) from " + hafta, null);
            mCount.moveToFirst();
            count = mCount.getInt(0);
            mCount.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chooseWeekActivity.database.close();
            return count;
        }

    }

    public void HaftaSilMetot(String haftaİsmi) {
        chooseWeekActivity.database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        chooseWeekActivity.database.execSQL("DROP TABLE "+haftaİsmi);
        chooseWeekActivity.database.execSQL("DELETE FROM kelime_Grublari WHERE hafta='"+haftaİsmi+"'");
        chooseWeekActivity.database.close();
    }
    public void HaftaGüncellemelMetot(String haftaİsmi,String yeni) {
        chooseWeekActivity.database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
        chooseWeekActivity.database.execSQL("ALTER TABLE "+haftaİsmi+" RENAME  TO "+yeni+"");
        chooseWeekActivity.database.execSQL("UPDATE kelime_Grublari SET hafta='"+yeni+"' WHERE hafta='"+haftaİsmi+"'");
        chooseWeekActivity.database.close();
    }

    public void HaftaSil(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(kelimeGrubuSecmeActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.hafta_silme_guncelleme, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        final EditText editText = mView.findViewById(R.id.edt_ekle_düzenle);
        Button sil = mView.findViewById(R.id.btn_Sil);
        sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HaftaSilMetot(editText.getText().toString().trim().toLowerCase());

                Intent ıntent = new Intent(kelimeGrubuSecmeActivity.this, kelimeGrubuSecmeActivity.class);
                finish();
                startActivity(ıntent);

                dialog.cancel();
            }
        });
        Button güncelle = mView.findViewById(R.id.btn_Güncelle);
        güncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String d[]=editText.getText().toString().split(",");
                if(d.length==2){
                    HaftaGüncellemelMetot(d[0],d[1]);
                    Intent ıntent = new Intent(kelimeGrubuSecmeActivity.this, kelimeGrubuSecmeActivity.class);
                    finish();
                    startActivity(ıntent);
                    dialog.cancel();
                }

            }
        });
        dialog.show();
    }

    public void HaftaGüncelle(View view) {

    }
}

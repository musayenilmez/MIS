package com.yenilmez.mis;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class chooseWeekActivity extends AppCompatActivity {
    static SQLiteDatabase database;
    ArrayList<String> kelime_Grublari;
    ArrayList<String> kelimeler;
    ListView lw_haftalar;
    EditText et_Hafta_ekle;
    ArrayAdapter haftaAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_week);

        Intent ıntentAl=getIntent();
        kelimeler=ıntentAl.getStringArrayListExtra("kelimeler");

        lw_haftalar=findViewById(R.id.lw_Haftalar);
        kelime_Grublari =new ArrayList<>();
        HaftalarıCek();
        for (String yaz: kelime_Grublari){
            System.out.println(yaz);
        }
        haftaAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, kelime_Grublari);
        lw_haftalar.setAdapter(haftaAdapter);
        lw_haftalar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder=new AlertDialog.Builder(chooseWeekActivity.this);
                builder.setTitle("Dikkat !!!").setMessage("Kelimeleriniz kelime grubuna kaydetmek istediğinizden emin misiniz ?")
                        .setIcon(android.R.drawable.stat_sys_warning)
                        .setCancelable(false);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        dialogInterface.dismiss();

                        for (String yaz:kelimeler){
                            String d[]=yaz.split(":");
                            for (int i=0;i<1;i++){
                                VT_kayit_Kelimler(kelime_Grublari.get(position),d[0].trim().toLowerCase(),d[1].trim().toLowerCase());
                            }
                        }

                        Intent ıntent=new Intent(chooseWeekActivity.this,MainActivity.class);
                        startActivity(ıntent);


                    }
                });
                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                builder.create().show();
            }
        });

    }
    public void VT_kayit_hafta(View view){
        et_Hafta_ekle=findViewById(R.id.et_Hafta_ekle);
        String haftaİsim=et_Hafta_ekle.getText().toString();
        if(haftaİsim.trim().toLowerCase().length()<2)return;

        try {
            database=this.openOrCreateDatabase("kelimeler_VT",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS kelime_Grublari (hafta VARCHAR(15) PRIMARY KEY NOT NULL)");
            database.execSQL("INSERT INTO kelime_Grublari (hafta) VALUES ('"+et_Hafta_ekle.getText().toString()+"')");
            database.execSQL("CREATE TABLE IF NOT EXISTS "+et_Hafta_ekle.getText().toString()+" (kelime VARCHAR(16) PRIMARY KEY NOT NULL," +
                    "anlamlari TEXT NOT NULL," +
                    "sozluk INT DEFAULT 0 CHECK(sozluk IN(0,1))," +
                    "eslestirme INT DEFAULT 0 CHECK(eslestirme IN(0,1))," +
                    "test INT DEFAULT 0 CHECK(test IN(0,1))," +
                    "anlamDoldur INT DEFAULT 0 CHECK(anlamDoldur IN(0,1))," +
                    "dinlemeTest INT DEFAULT 0 CHECK(dinlemeTest IN(0,1))," +
                    "dinlemeDoldur INT DEFAULT 0 CHECK(dinlemeDoldur IN(0,1)))");

            kelime_Grublari.add(haftaİsim);
            lw_haftalar.setAdapter(haftaAdapter);
            lw_haftalar.setSelection(kelime_Grublari.size());
        }catch (SQLException e){
            Toast.makeText(this,"Hafta eklenirken hata meydana geldi.",Toast.LENGTH_SHORT).show();
        }finally {
            database.close();
        }


    }
    public void VT_kayit_Kelimler(String hafta1,String kelime,String anlamlari){
        try {
            database=this.openOrCreateDatabase("kelimeler_VT",MODE_PRIVATE,null);
            database.execSQL("INSERT INTO "+hafta1+" (kelime,anlamlari) VALUES ('"+kelime+"','"+anlamlari+"')");
        }catch (SQLException e){
            System.out.println("Kelime Ekleme Hata");
        }finally {
            database.close();
        }
    }
    public void HaftalarıCek(){
        database=this.openOrCreateDatabase("kelimeler_VT",MODE_PRIVATE,null);
        try {
            Cursor cursor=database.rawQuery("SELECT * FROM kelime_Grublari",null);
            int inHafta=cursor.getColumnIndex("hafta");
            cursor.moveToFirst();
            while (cursor != null){
                kelime_Grublari.add(cursor.getString(inHafta));
                boolean son =cursor.moveToNext();
                if(!son)break;
            }
        }catch (SQLException e){
            System.out.println("Hafta Çekerken hata");
        }finally {
            database.close();
        }
    }

}

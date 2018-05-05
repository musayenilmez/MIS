package com.yenilmez.mis;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class practiceActivity extends AppCompatActivity {
    String secilenhafta;
    Button btn_Sozluk,btn_Test,btn_Test_Dinlenme,btn_AnlamYazma,btn_DinlemeYazma;
    SQLiteDatabase database=chooseWeekActivity.database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        Intent  ıntent=getIntent();
        secilenhafta=ıntent.getStringExtra("secilenhafta");

        btn_Sozluk=findViewById(R.id.btn_sozluk);
        btn_Test=findViewById(R.id.btn_Test);
        btn_Test_Dinlenme=findViewById(R.id.btn_Test_Dinleme);
        btn_AnlamYazma=findViewById(R.id.btn_AnlamYazma);
        btn_DinlemeYazma=findViewById(R.id.btn_DinlemeYazma);
        kontrolSozluk(secilenhafta);
        kontrolTest(secilenhafta);
        kontrolDinlemeTest(secilenhafta);
        kontrolAnlamYazma(secilenhafta);
        kontrolDinlemeYazma(secilenhafta);



    }
    public void sozluk(View view){
        int sonuc= kontrolSozluk(secilenhafta);
        if(sonuc==0)return;
        Intent ıntent=new Intent(this,sozlukOyunuActivity.class);
        ıntent.putExtra("secilenhafta",secilenhafta);
        startActivity(ıntent);
    }
    public void test(View view){
        int sonuc= kontrolTest(secilenhafta);
        if(sonuc==0)return;
        Intent ıntent=new Intent(this,testOyunuActivity.class);
        ıntent.putExtra("secilenhafta",secilenhafta);
        startActivity(ıntent);
    }
    public void test_dinleme(View view){
        Intent ıntent=new Intent(this,dinlemeTestOyunuActivity.class);
        ıntent.putExtra("secilenhafta",secilenhafta);
        startActivity(ıntent);
    }
    public void anlam_yazma(View view){
        Intent ıntent=new Intent(this,anlamYazmaActivity.class);
        ıntent.putExtra("secilenhafta",secilenhafta);
        startActivity(ıntent);
    }
    public void dinleme_yazma(View view){
        Intent ıntent=new Intent(this,dinlemeYazmaOyunuActivity.class);
        ıntent.putExtra("secilenhafta",secilenhafta);
        startActivity(ıntent);
    }
    public void Reset(View view){
        try {
            database = practiceActivity.this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
            database.execSQL("UPDATE " + secilenhafta + " SET sozluk=0 ");
            database.execSQL("UPDATE " + secilenhafta + " SET test=0 ");
            database.execSQL("UPDATE " + secilenhafta + " SET dinlemeTest=0 ");
            database.execSQL("UPDATE " + secilenhafta + " SET anlamDoldur=0 ");
            database.execSQL("UPDATE " + secilenhafta + " SET dinlemeDoldur=0 ");
            btn_Sozluk.setEnabled(true);
            btn_Test.setEnabled(true);
            btn_Test_Dinlenme.setEnabled(true);
            btn_AnlamYazma.setEnabled(true);
            btn_DinlemeYazma.setEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            database.close();
        }
    }
    public int kontrolSozluk(String kelimeGrubu){
        chooseWeekActivity.database=this.openOrCreateDatabase("kelimeler_VT",MODE_PRIVATE,null);
        Cursor cursor=chooseWeekActivity.database.rawQuery("SELECT COUNT(sozluk) FROM "+kelimeGrubu+" WHERE sozluk=0",null);
        cursor.moveToFirst();
        int count=cursor.getInt(0);
        if(count==0)btn_Sozluk.setEnabled(false);

        chooseWeekActivity.database.close();
        return count;
    }
    public int kontrolTest(String kelimeGrubu){
        chooseWeekActivity.database=this.openOrCreateDatabase("kelimeler_VT",MODE_PRIVATE,null);
        Cursor cursor=chooseWeekActivity.database.rawQuery("SELECT COUNT(test) FROM "+kelimeGrubu+" WHERE test=0",null);
        cursor.moveToFirst();
        int count=cursor.getInt(0);
        if(count==0)btn_Test.setEnabled(false);

        chooseWeekActivity.database.close();
        return count;
    }
    public int kontrolDinlemeTest(String kelimeGrubu){
        chooseWeekActivity.database=this.openOrCreateDatabase("kelimeler_VT",MODE_PRIVATE,null);
        Cursor cursor=chooseWeekActivity.database.rawQuery("SELECT COUNT(dinlemeTest) FROM "+kelimeGrubu+" WHERE dinlemeTest=0",null);
        cursor.moveToFirst();
        int count=cursor.getInt(0);
        if(count==0)btn_Test_Dinlenme.setEnabled(false);

        chooseWeekActivity.database.close();
        return count;
    }
    public int kontrolAnlamYazma(String kelimeGrubu){
        chooseWeekActivity.database=this.openOrCreateDatabase("kelimeler_VT",MODE_PRIVATE,null);
        Cursor cursor=chooseWeekActivity.database.rawQuery("SELECT COUNT(anlamDoldur) FROM "+kelimeGrubu+" WHERE anlamDoldur=0",null);
        cursor.moveToFirst();
        int count=cursor.getInt(0);
        if(count==0)btn_AnlamYazma.setEnabled(false);

        chooseWeekActivity.database.close();
        return count;
    }
    public int kontrolDinlemeYazma(String kelimeGrubu){
        chooseWeekActivity.database=this.openOrCreateDatabase("kelimeler_VT",MODE_PRIVATE,null);
        Cursor cursor=chooseWeekActivity.database.rawQuery("SELECT COUNT(dinlemeDoldur) FROM "+kelimeGrubu+" WHERE dinlemeDoldur=0",null);
        cursor.moveToFirst();
        int count=cursor.getInt(0);
        if(count==0)btn_DinlemeYazma.setEnabled(false);

        chooseWeekActivity.database.close();
        return count;
    }

}

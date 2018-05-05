package com.yenilmez.mis;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class statisticsActivity extends AppCompatActivity {
    SQLiteDatabase database = chooseWeekActivity.database;
    ProgressBar pBTest, pB_DinlemeTest, pB_Bosluk, pBDinlemeBosluk;
    int maxTest = 0, maxDinlemeTest = 0, maxBosluk = 0, maxBoslukDinleme = 0;
    TextView tV_Test, tv_DinlemeTest, tV_Bosluk, tV_DinlemeBosluk,tV_d1,tV_y1,tV_d2,tV_y2,tV_d3,tV_y3,tV_d4,tV_y4;
    int testDogru=0,testYanlis=0,DinlemeTestDogru=0,DinlemeTestYanlis=0,BoslukDogru=0,BoslukYanlis=0,DinlemeBoslukDogru=0,DinlemeBoslukYanlis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pBTest = findViewById(R.id.pBTest);
        pB_DinlemeTest = findViewById(R.id.pBTestDinleme);
        pB_Bosluk = findViewById(R.id.pBBosluk);
        pBDinlemeBosluk = findViewById(R.id.pbDinlemeBosluk);
        tV_Test = findViewById(R.id.tV_TestYüzde);
        tv_DinlemeTest = findViewById(R.id.tV_DinlemeTestYüzde);
        tV_Bosluk = findViewById(R.id.tV_BoslukYüzde);
        tV_DinlemeBosluk = findViewById(R.id.tv_DinlemeBoslukDoldurYüzde);

        tV_d1=findViewById(R.id.d1);
        tV_d2=findViewById(R.id.d2);
        tV_d3=findViewById(R.id.d3);
        tV_d4=findViewById(R.id.d4);
        tV_y1=findViewById(R.id.y1);
        tV_y2=findViewById(R.id.y2);
        tV_y3=findViewById(R.id.y3);
        tV_y4=findViewById(R.id.y4);

        maxAyarla();

        tV_d1.setText("Dogru : "+testDogru);
        tV_y1.setText("Yanlis : "+testYanlis);

        tV_d2.setText("Dogru : "+DinlemeTestDogru);
        tV_y2.setText("Yanlis : "+DinlemeTestYanlis);

        tV_d3.setText("Dogru : "+BoslukDogru);
        tV_y3.setText("Yanlis : "+BoslukYanlis);

        tV_d4.setText("Dogru : "+DinlemeBoslukDogru);
        tV_y4.setText("Yanlis : "+DinlemeBoslukYanlis);




        new CountDownTimer(10000, 100) {
            int i = 0;

            @Override
            public void onTick(long l) {


                if (i <= maxTest) {
                    pBTest.setProgress(i);
                    tV_Test.setText(i+"%");

                }
                if (i <= maxDinlemeTest) {
                    pB_DinlemeTest.setProgress(i);
                    tv_DinlemeTest.setText(i+"%");

                }
                if (i <= maxBosluk) {
                    pB_Bosluk.setProgress(i);
                    tV_Bosluk.setText(i+"%");

                }
                if (i <= maxBoslukDinleme) {
                    pBDinlemeBosluk.setProgress(i);
                    tV_DinlemeBosluk.setText(i+"%");

                }
                i++;


            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    public void maxAyarla() {
        try {
            database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * FROM istatistik", null);
            int TDCol = cursor.getColumnIndex("TD");
            int TYCol = cursor.getColumnIndex("TY");
            int TRCol = cursor.getColumnIndex("TR");
            int DTDCol = cursor.getColumnIndex("TDD");
            int DTYCol = cursor.getColumnIndex("TDY");
            int DTRCol = cursor.getColumnIndex("TDR");
            int BDCol = cursor.getColumnIndex("BD");
            int BYCol = cursor.getColumnIndex("BosY");
            int BRCol = cursor.getColumnIndex("BR");
            int DBDCol = cursor.getColumnIndex("BDD");
            int DBYCol = cursor.getColumnIndex("BDY");
            int DBRCol = cursor.getColumnIndex("BDR");



            cursor.moveToFirst();

            int TD = cursor.getInt(TDCol);
            int TY = cursor.getInt(TYCol);
            int TR = cursor.getInt(TRCol);

            testDogru=TD;
            testYanlis=TY;
            maxTest = BasarıHesapla(TD, TY, TR);

            int DTD = cursor.getInt(DTDCol);
            int DTY = cursor.getInt(DTYCol);
            int DTR = cursor.getInt(DTRCol);

            DinlemeTestYanlis=DTY;
            DinlemeTestDogru=DTD;
            maxDinlemeTest = BasarıHesapla(DTD, DTY, DTR);

            int BD = cursor.getInt(BDCol);
            int BY = cursor.getInt(BYCol);
            int BR = cursor.getInt(BRCol);

            BoslukDogru=BD;
            BoslukYanlis=BY;

            maxBosluk = BasarıHesapla(BD, BY, BR);

            int DBD = cursor.getInt(DBDCol);
            int DBY = cursor.getInt(DBYCol);
            int DBR = cursor.getInt(DBRCol);

            DinlemeBoslukDogru=DBD;
            DinlemeBoslukYanlis=DBY;

            maxBoslukDinleme = BasarıHesapla(DBD, DBY, DBR);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int BasarıHesapla(int Dogru, int Yanlis, int Rekor) {
        int fark = Dogru - Yanlis;
        if (fark < 0) fark = 0;
        if (fark > 101) fark = 100;
        if (Rekor > 101) Rekor = 100;
        fark = fark / 2;
        Rekor = Rekor / 2;

        return fark + Rekor;

    }

    public void yüzdeDegis(int i) {
        tV_Test.setText(i);
    }

}

package com.yenilmez.mis;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.yenilmez.mis.chooseWeekActivity;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase database = chooseWeekActivity.database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VT_Olustur();

    }


    //MENU SEÇENEKLER ve OLAYLAR
    public void VT_Olustur() {
        try {
            database = this.openOrCreateDatabase("kelimeler_VT", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS istatistik (TD INT DEFAULT 0," +
                    "TY INT DEFAULT 0," +
                    "TR INT DEFAULT 0 ," +
                    "TS INT DEFAULT 0 ," +
                    "TDD INT DEFAULT 0 ," +
                    "TDY INT DEFAULT 0 ," +
                    "TDR INT DEFAULT 0 ," +
                    "TDS INT DEFAULT 0 ," +
                    "BD INT DEFAULT 0 ," +
                    "BosY INT DEFAULT 0 ," +
                    "BR INT DEFAULT 0 ," +
                    "BS INT DEFAULT 0 ," +
                    "BDD INT DEFAULT 0 ," +
                    "BDY INT DEFAULT 0 ," +
                    "BDR INT DEFAULT 0 ," +
                    "BDS INT DEFAULT 0)");
            database.execSQL("DELETE FROM kelime_Grublari WHERE hafta='deneme'");

            Cursor cursor = database.rawQuery("SELECT COUNT (*) FROM istatistik", null);
            cursor.moveToFirst();

            int satırSayisi = cursor.getInt(0);

            if(satırSayisi<1)database.execSQL("INSERT INTO istatistik VALUES (0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");


        } catch (SQLException e) {
            Toast.makeText(this, "İstatistik oluşturulurken hata meydana geldi.", Toast.LENGTH_SHORT).show();
        } finally {
            database.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.calis) {
            Intent intent = new Intent(getApplicationContext(), kelimeGrubuSecmeActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.ekle) {
            Intent intent = new Intent(getApplicationContext(), addWordActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.istatistik) {
            Intent intent = new Intent(getApplicationContext(), statisticsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

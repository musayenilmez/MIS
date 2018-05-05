package com.yenilmez.mis;

import android.content.Intent;
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

import java.util.ArrayList;

public class addWordListActivity extends AppCompatActivity {
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word_list);
        Intent inIntent=getIntent();


        list=inIntent.getStringArrayListExtra("cevaplar");

        Typeface myTypeface=Typeface.createFromAsset(getAssets(),"kgiwantcrazy.otf");
        Button button=findViewById(R.id.btnKelimeleriKaydet);
        button.setTypeface(myTypeface);


        final ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        final ListView listView=findViewById(R.id.listview);



        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(addWordListActivity.this);
                View mView=getLayoutInflater().inflate(R.layout.kelime_ekle_duzenle,null);
                mBuilder.setView(mView);
                final AlertDialog dialog =mBuilder.create();

                final EditText editText=mView.findViewById(R.id.edt_ekle_düzenle);
                editText.setText(list.get(position));
                Button sil=mView.findViewById(R.id.btn_Sil);
                sil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        list.remove(position);
                        listView.setAdapter(arrayAdapter);
                        listView.setSelection(position);
                        dialog.cancel();
                        if(list.size()==0){
                            Intent ıntent=new Intent(addWordListActivity.this,MainActivity.class);
                            startActivity(ıntent);
                        }
                    }
                });
                Button güncelle=mView.findViewById(R.id.btn_Güncelle);
                güncelle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        list.set(position,editText.getText().toString());
                        listView.setAdapter(arrayAdapter);
                        listView.setSelection(position);
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });


    }
    public void kelimeleri_Kaydet(View view){
        Intent ıntent=new Intent(getApplicationContext(),chooseWeekActivity.class);
        ıntent.putStringArrayListExtra("kelimeler",list);
        startActivity(ıntent);
    }

}

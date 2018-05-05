package com.yenilmez.mis;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class viewPagerAdapter extends PagerAdapter {
    private Context context;
    ArrayList<String> yazılan,yazılan_cevaplar;
    LayoutInflater inflater;


    public viewPagerAdapter(Context context, ArrayList<String> yazılan,ArrayList<String> yazılan_cevaplar) {
        this.context = context;
        this.yazılan = yazılan;
        this.yazılan_cevaplar=yazılan_cevaplar;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item=inflater.inflate(R.layout.viewpager_1,null);
        TextView tw_Kelime=item.findViewById(R.id.tw_Kelime);
        TextView tw_Anlamlari=item.findViewById(R.id.tw_Anlamları);


        tw_Kelime.setText(yazılan.get(position));
        String d[]=yazılan_cevaplar.get(position).split(",");
        for (int i =0; i<d.length;i++){
            tw_Anlamlari.setText(tw_Anlamlari.getText().toString()+d[i]+"\n");
        }
        tw_Anlamlari.setMovementMethod(new ScrollingMovementMethod());



        ViewPager vp=(ViewPager)container;
        vp.addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView((View)object);
    }

    @Override
    public int getCount() {return yazılan.size();  }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object==view;
    }
}

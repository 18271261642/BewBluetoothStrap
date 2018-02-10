package com.example.bozhilun.android.adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.bozhilun.android.coverflow.CoverFlowAdapter;

public class MyCoverFlowAdapter extends CoverFlowAdapter {

    private int[] imgId;
    private Context context;

    public MyCoverFlowAdapter(Context context, int[] imgId) {
        this.context = context;
        this.imgId = imgId;
    }

    @Override
    public int getCount() {
        return imgId.length;
    }

    @Override
    public Bitmap getImage(final int position) {
        return BitmapFactory.decodeResource(context.getResources(), imgId[position]);
    }
}

package com.example.bozhilun.android.activity.wylactivity.wyl_util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by admin on 2017/3/17.
 * wyl  动画效果
 */

public class StartFlick {

    public static  void startFlick( View view ){

        if( null == view ){

            return;

        }
        ScaleAnimation animation_suofang =new ScaleAnimation(1f, 1.1f, 1f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation_suofang.setDuration(2000);                     //执行时间
        animation_suofang.setRepeatCount(-1);                   //重复执行动画
        animation_suofang.setRepeatMode(Animation.REVERSE);     //重复 缩小和放大效果
        view.startAnimation(animation_suofang);

    }


    public static void stopFlick( View view ){
        if( null == view ){
            return;
        }
        view.clearAnimation( );

    }


//从下往上动画
    public static  void startFlickbutton( View view ){

        if( null == view ){

            return;

        }
        Animation translateInab= new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0.8f,
                Animation.RELATIVE_TO_PARENT, 0);
        translateInab.setDuration(1500);
        view.startAnimation(translateInab);

    }

    public static void stopFlickbutton( View view ){
        if( null == view ){
            return;
        }
        view.clearAnimation( );

    }


    /**
     * 从左往右
     * @param view
     */
    public static  void startFlickzou( View view ){

        if( null == view ){

            return;

        }
        Animation translateInab= new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,  -0.8f,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT,0,
                Animation.RELATIVE_TO_PARENT, 0);
        translateInab.setDuration(1500);
        view.startAnimation(translateInab);

    }


    public static void stopFlickzuo( View view ){
        if( null == view ){
            return;
        }
        view.clearAnimation( );

    }
    /**
     * 从右往左
     * @param view
     */
    public static  void startFlickyou( View view ){

        if( null == view ){

            return;

        }
        Animation translateInac= new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.8f,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        translateInac.setDuration(1500);
        view.startAnimation(translateInac);

    }


    public static void stopFlickyou( View view ){
        if( null == view ){
            return;
        }
        view.clearAnimation( );

    }

}

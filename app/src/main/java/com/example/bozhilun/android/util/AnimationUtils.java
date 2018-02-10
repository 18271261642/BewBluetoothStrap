package com.example.bozhilun.android.util;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.example.bozhilun.android.onekeyshare.OnekeyShare;
import com.example.bozhilun.android.onekeyshare.OnekeyShareTheme;

/**
 * Created by thinkpad on 2017/3/17.
 */

public class AnimationUtils {

    public static void startFlick(View view) {
        if (null == view) {
            return;
        }
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(400);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(alphaAnimation);
    }

    public static void stopFlick(View view) {
        if (null == view) {
            return;
        }
        view.clearAnimation();

    }


}

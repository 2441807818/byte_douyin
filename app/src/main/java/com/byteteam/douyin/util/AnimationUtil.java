package com.byteteam.douyin.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.HashMap;

/**
 * @introduction： 个人主页Fragment
 * @author： 何文鹏
 * @time： 2022/8/21
 */

public class AnimationUtil {

    private static HashMap<String, Boolean> animationMap = new HashMap<>();

    public static void doPressAnimation(final View view) {
        float y = view.getY();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "y", y, y + 20, y);
        animator.setDuration(300);
        animator.setRepeatCount(0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animationMap.put(view.getId() + "PRESS", false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationMap.put(view.getId() + "PRESS", true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }



}
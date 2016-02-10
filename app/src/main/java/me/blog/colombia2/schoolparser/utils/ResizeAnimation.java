package me.blog.colombia2.schoolparser.utils;

import android.view.*;
import android.view.animation.*;

public class ResizeAnimation extends Animation {
    private View view;
    private int startWidth;
    private int endWidth;
    private int startHeight;
    private int endHeight;

    public ResizeAnimation(View view, int endWidth, int endHeight) {
        this.view = view;
        this.startWidth = view.getWidth();
        this.endWidth = endWidth;
        this.startHeight = view.getHeight();
        this.endHeight = endHeight;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(endWidth >= 0) {
            int width = (int) (this.startWidth+(this.endWidth-this.startWidth)*interpolatedTime);
            this.view.getLayoutParams().width = width;
        }
        if(endHeight >= 0) {
            int height = (int) (this.startHeight+(this.endHeight-this.startHeight)*interpolatedTime);
            this.view.getLayoutParams().height = height;
        }
        this.view.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

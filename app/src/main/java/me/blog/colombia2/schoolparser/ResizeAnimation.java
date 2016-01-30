package me.blog.colombia2.schoolparser;

import android.view.*;
import android.view.animation.*;

public class ResizeAnimation extends Animation {
    private View view;
    private int startHeight;
    private int endHeight;
    
    public ResizeAnimation(View view, float f) {
        this.view = view;
        this.startHeight = view.getHeight();
        this.endHeight = (int) (view.getHeight()*f);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int result = (int) (this.startHeight+(this.endHeight-this.startHeight)*interpolatedTime);
        this.view.getLayoutParams().height = result;
        this.view.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

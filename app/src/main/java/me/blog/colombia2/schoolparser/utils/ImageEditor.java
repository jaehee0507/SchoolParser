package me.blog.colombia2.schoolparser.utils;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.renderscript.*;
import android.view.*;

public class ImageEditor {
    //from http://www.kmshack.kr/2013/08/flat%EB%94%94%EC%9E%90%EC%9D%B8%EC%9D%98-%ED%95%B5%EC%8B%AC-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%9D%B4%EB%AF%B8%EC%A7%80-blur-%ED%9A%A8%EA%B3%BC-%EB%82%B4%EA%B8%B0/
    public static Bitmap blur(Context context, Bitmap sentBitmap, float radius) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                                                                 Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }
        return sentBitmap;
    }
    
    public static Bitmap captureScreen(View rootView) {
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache();
        Bitmap output = rootView.getDrawingCache();
        Bitmap result = output.copy(output.getConfig(), true);
        rootView.setDrawingCacheEnabled(false);
        return result;
    }
}

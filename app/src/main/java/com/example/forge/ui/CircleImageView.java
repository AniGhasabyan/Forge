package com.example.forge.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CircleImageView extends AppCompatImageView {

    private float radius;
    private RectF rect;
    private Paint paint;

    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = getBitmapFromDrawable();
        if (bitmap != null) {
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);

            float width = getWidth();
            float height = getHeight();

            float reducedRadius = Math.min(width, height) * 0.4f;

            rect.set(0, 0, width, height);
            canvas.drawRoundRect(rect, reducedRadius, reducedRadius, paint);
        } else {
            super.onDraw(canvas);
        }
    }

    private Bitmap getBitmapFromDrawable() {
        if (getDrawable() != null) {
            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            getDrawable().setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            getDrawable().draw(canvas);
            return bitmap;
        }
        return null;
    }
}

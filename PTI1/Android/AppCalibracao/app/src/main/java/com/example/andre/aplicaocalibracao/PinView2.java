package com.example.andre.aplicaocalibracao;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
public class PinView2 extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private final PointF vPin = new PointF();
    private PointF sPin[]=new PointF[50];
    private Bitmap pin[]=new Bitmap[50];
    int index=0;

    public PinView2(Context context) {
        this(context, null);
        setText("inicio");
    }

    public PinView2(Context context, AttributeSet attr) {
        super(context, attr);
        setText("inicio");
        initialise();
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public void setText(String text){
        pin[index]= textAsBitmap(text,40, Color.BLUE);
    }

    public void setPin(PointF sPin, Boolean save, String text) {
        pin[index]= textAsBitmap(text,40, Color.BLUE);
        this.sPin[index] = sPin;
        initialise();
        if(save)
            index++;
        invalidate();
    }

    private void initialise() {
        final float density = getResources().getDisplayMetrics().densityDpi;
        for(int i=0; i<=index; i++){
            float w = (density/420f) * pin[i].getWidth();
            float h = (density/420f) * pin[i].getHeight();
            pin[i] = Bitmap.createScaledBitmap(pin[i], (int)w, (int)h, true);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        paint.setAntiAlias(true);

        for(int i=0;i<=index; i++){
            if (sPin[i] != null && pin[i] != null) {
                sourceToViewCoord(sPin[i], vPin);
                float vX = vPin.x - (pin[i].getWidth()/2);
                float vY = vPin.y - pin[i].getHeight();
                canvas.drawBitmap(pin[i], vX, vY, paint);
            }
        }
    }
}


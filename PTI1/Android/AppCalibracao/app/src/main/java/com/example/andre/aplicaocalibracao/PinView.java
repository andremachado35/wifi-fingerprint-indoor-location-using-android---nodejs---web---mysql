package com.example.andre.aplicaocalibracao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PinView extends SubsamplingScaleImageView {

    private final Paint paint = new Paint();
    private final PointF vPin = new PointF();
    private PointF sPin[]=new PointF[50];
    private Bitmap pin[]=new Bitmap[50];
    int index=0;

    public PinView(Context context) {
        this(context, null);
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    public void setPin(PointF sPin, Boolean save) {
        this.sPin[index] = sPin;
        if(save)
            index++;
        initialise();
        invalidate();
    }

    private void initialise() {
        final float density = getResources().getDisplayMetrics().densityDpi;
        AsyncTask download = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    return BitmapFactory.decodeStream((InputStream)new URL("https://www.psmania.net/wp-content/uploads/2012/09/dot.png").getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                for(int i=0; i<=index; i++){
                    pin[i]=(Bitmap) o;
                    float w = (density/420f) * pin[i].getWidth();
                    float h = (density/420f) * pin[i].getHeight();
                    pin[i] = Bitmap.createScaledBitmap(pin[i], (int)w, (int)h, true);
                }
            }
        };
        download.execute();
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


package com.example.andre.aplicaocalibracao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AddPI_SelectOnMap extends AppCompatActivity {
    private PinView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pi__select_on_map);
        imageView = (PinView) findViewById(R.id.imageView);
        map();
    }

    public void map() {
        //Carregar imagem do espaço
        final Spaces.MapID_SpacesImpl spaces = Spaces.MapID_SpacesImpl.getInstance();
        String SelectedItem = spaces.GetSelectedSpace();
        String id_space = spaces.get_by_the_Name(SelectedItem);
        String Largura = spaces.get_ID_Space_Largura(id_space);
        String Comprimento = spaces.get_ID_Space_Comprimento(id_space);
        String SpaceName = spaces.get_id_Space_Name(id_space);
        SpaceName = SpaceName.replace(" ", "+");
        final int largura = Integer.parseInt(Largura) * 2;
        final int comprimento = Integer.parseInt(Comprimento) * 2;
        //final String urlfinal = "https://dummyimage.com/" + largura + "x" + comprimento + "/000/fff.png&text=" + SpaceName;
        final String urlfinal = "http://fpoimg.com/" + largura+"x"+comprimento+"?text="+SpaceName;

        //apresentar imagem
        AsyncTask download = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    return BitmapFactory.decodeStream((InputStream) new URL(urlfinal).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                imageView.setImage(ImageSource.bitmap((Bitmap) o));
            }
        };
        download.execute();
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imageView.isReady()) {
                    final PointF sCoord = imageView.viewToSourceCoord(e.getX(), e.getY());
                    Toast.makeText(getApplicationContext(), "scord:" + sCoord.toString() + "\n larg: " + largura + " comp: " + comprimento, Toast.LENGTH_LONG).show();
                    if (sCoord.y > 0 && sCoord.y <= comprimento && sCoord.x > 0 && sCoord.x <= largura) {
                                //adicionar o pino
                        imageView.setPin(sCoord, false);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(AddPI_SelectOnMap.this);
                        builder.setTitle("Confirmar!");
                        builder.setMessage("Tem a certeza que pretende calibrar em x:" + Math.round(sCoord.x / 2) + " y: " + Math.round(sCoord.y / 2) + "?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //adicionar PI ao singleton para usar no addPI_withmoreinfo
                        final PontosDeInteresse.Map_ID_PIImpl PI = PontosDeInteresse.Map_ID_PIImpl.getInstance();
                        PI.put_PI_Coordx("largura", Integer.toString(Math.round(sCoord.x / 2)));
                        PI.put_PI_Coordy("comprimento", Integer.toString(Math.round(sCoord.y / 2)));
                        imageView.setPin(sCoord, true);
                        dialog.dismiss();
                        salto();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageView.setPin(sCoord, false);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                return true;
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    public void salto() {
        Intent intent = new Intent(AddPI_SelectOnMap.this,AddPi_withmoreinfo.class);
        startActivity(intent);
    }
}


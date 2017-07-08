package com.campolim.drawingfun;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
    private float smallBrush, mediumBrush, largeBrush;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = (DrawingView)findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));

        // Brush size
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        // Brush button
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        drawView.setBrushSize(mediumBrush);

        // Erase button
        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        // New drawing
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        // Save drawing
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    drawView.refog();
                }
                catch (Exception e) {
                    System.err.println("Error refogging!");
                }
                finally{
                    // Also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 250);
                }
            }
        };

        runnable.run();
    }

    public void paintClicked(View view){
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());

        // Use chosen color
        if(view!=currPaint){
            // Update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            System.out.println(color);
            drawView.setColor(color);

            imgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
            currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint, null));
            currPaint=(ImageButton)view;
        }

        drawView.refog();
    }

    /* Method to save the current bitmap to the device gallery */
    public void saveDrawing() {
        drawView.setDrawingCacheEnabled(true);
        String imgSaved = MediaStore.Images.Media.insertImage(
            getContentResolver(), drawView.getDrawingCache(),
            UUID.randomUUID().toString()+".png", "drawing");

        if(imgSaved!=null){
            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
            savedToast.show();
        }
        else{
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
            unsavedToast.show();
        }

        drawView.destroyDrawingCache();
    }

    @Override
    public void onClick(View view){
        // Respond to clicks
        if(view.getId()==R.id.draw_btn){
            // Draw button clicked

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Brush Size:");
            builder.setView(R.layout.brush_chooser);

            final AlertDialog brushDialog = builder.create();

            brushDialog.show();

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

        } else if(view.getId()==R.id.erase_btn){
            // Switch to erase - choose size

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Eraser Size:");
            builder.setView(R.layout.brush_chooser);

            final AlertDialog brushDialog = builder.create();

            brushDialog.show();

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

        } else if(view.getId()==R.id.new_btn){
            // 'New' button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if(view.getId()==R.id.save_btn){
            // Save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){

                    // First check for permissions
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        saveDrawing();
                    }
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

    /* Handle what to do after permissions are requested */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    saveDrawing();
                } else {
                    // Permission denied
                    Toast permissionDeniedToast = Toast.makeText(getApplicationContext(),
                            "Image save cancelled", Toast.LENGTH_SHORT);
                    permissionDeniedToast.show();
                }
                return;
            }
        }
    }

}

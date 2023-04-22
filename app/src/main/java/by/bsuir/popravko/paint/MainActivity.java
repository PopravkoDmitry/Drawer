package by.bsuir.popravko.paint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import by.bsuir.popravko.paint.view.PaintView;
import by.bsuir.popravko.paint.view.PaintView2;

public class MainActivity extends AppCompatActivity {

    private PaintView2 paintView;
    private AlertDialog.Builder currentAlerDialog;
    private ImageView widthImageView;
    private AlertDialog dialogLineWidth;
    private AlertDialog colorDialog;

    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private View colorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = findViewById(R.id.view);

        paintView.postDelayed(() -> paintView.clear(), 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearId:
                paintView.setDrawingColor(Color.WHITE);
                break;
            case R.id.saveId:
                paintView.saveImage();
                break;
            case R.id.colorId:
                showColorDialog();
                break;
            case R.id.lineWidh:
                showLineWidthDialog();
                break;
            case R.id.eraceId:
                break;
            case R.id.rectangleShape:
                paintView.setStatus(2);
                break;
            case R.id.circleShape:
                paintView.setStatus(1);
                break;
            case R.id.triangleShape:
                paintView.setStatus(3);
                break;
            case R.id.lineShape:
                paintView.setStatus(4);
                break;
            case R.id.new_btn:
                New();
        }

        if(item.getItemId() == R.id.clearId) {
            paintView.clear();
        }
        return super.onOptionsItemSelected(item);
    }



    void showColorDialog() {
        currentAlerDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog, null);
        alphaSeekBar = view.findViewById(R.id.alphaSeekBar);
        redSeekBar = view.findViewById(R.id.redSeekBar);
        greenSeekBar = view.findViewById(R.id.greenSeekBar);
        blueSeekBar = view.findViewById(R.id.blueSeekBar);
        colorView = view.findViewById(R.id.colorView);

        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);

        int color = paintView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        Button setColorButton = view.findViewById(R.id.setColorButton);
        setColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setDrawingColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                                                     greenSeekBar.getProgress(), blueSeekBar.getProgress()));
                colorDialog.dismiss();
            }
        });

        currentAlerDialog.setView(view);
        currentAlerDialog.setTitle("Choose Color");
        colorDialog = currentAlerDialog.create();
        colorDialog = currentAlerDialog.show();

    }

    void showLineWidthDialog() {
        currentAlerDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.width_dialog, null);
        SeekBar widthSeekbar = view.findViewById(R.id.widthDSeekBar);
        Button setLineWidthButton = view.findViewById(R.id.widthDialogButton);
        widthImageView = view.findViewById(R.id.imageViewId);
        setLineWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setLineWidth(widthSeekbar.getProgress());
                dialogLineWidth.dismiss();
                currentAlerDialog = null;
            }
        });

        widthSeekbar.setOnSeekBarChangeListener(widthSeekbarChange);

        currentAlerDialog.setView(view);
        dialogLineWidth = currentAlerDialog.create();
        dialogLineWidth.show();


    }

    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            paintView.setBackgroundColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                                                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));

            colorView.setBackgroundColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                                                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener widthSeekbarChange = new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Paint p = new Paint();
            p.setColor(paintView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);

            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30, 50, 370, 50, p);
            widthImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public void New() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 800);

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                paintView.loadFile(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
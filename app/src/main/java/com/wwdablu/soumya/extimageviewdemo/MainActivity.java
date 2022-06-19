package com.wwdablu.soumya.extimageviewdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wwdablu.soumya.extimageview.BaseExtImageView;
import com.wwdablu.soumya.extimageview.Result;
import com.wwdablu.soumya.extimageview.free.ExtFreeImageView;
import com.wwdablu.soumya.extimageview.nanit.MotionRoiCoords;
import com.wwdablu.soumya.extimageview.nanit.MotionRoiWidget;
import com.wwdablu.soumya.extimageview.rect.CropMode;
import com.wwdablu.soumya.extimageview.rect.ExtRectImageView;
import com.wwdablu.soumya.extimageview.rect.GridMode;
import com.wwdablu.soumya.extimageview.trapez.ExtTrapezImageView;

public class MainActivity extends AppCompatActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btn_capture);

        String mode = getIntent().getStringExtra("mode");

        switch (mode) {
            case "rect":
                demoRect();
                break;

            case "free":
                demoFree();
                break;

            case "trapez":
                demoTrapez();
                break;

            case "nanit":
                demoNanit();
                break;
        }
    }

    private void demoNanit() {

        findViewById(R.id.iv_display).setVisibility(View.GONE);
        findViewById(R.id.iv_display_free).setVisibility(View.GONE);
        findViewById(R.id.iv_display_trapez).setVisibility(View.GONE);
        findViewById(R.id.iv_display_nanit).setVisibility(View.VISIBLE);

        final MotionRoiWidget motionRoiWidget = findViewById(R.id.iv_display_nanit);

        motionRoiWidget.setInitialDimensions(new MotionRoiCoords(0, 0, 500, 500));

        motionRoiWidget.registerListener(() -> {

        });

        motionRoiWidget.post(() -> {
            motionRoiWidget.drawInitialCoords();
            motionRoiWidget.showRoi();
        });


    }

    private void demoTrapez() {

        btn.setText("Crop Trapez");

        findViewById(R.id.iv_display).setVisibility(View.GONE);
        findViewById(R.id.iv_display_free).setVisibility(View.GONE);
        findViewById(R.id.iv_display_trapez).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_display_nanit).setVisibility(View.GONE);

        final ExtTrapezImageView extTrapezImageView = findViewById(R.id.iv_display_trapez);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inDensity = 0;
        options.inTargetDensity = 0;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_1);

        extTrapezImageView.setImageBitmap(bitmap);

        findViewById(R.id.btn_capture).setOnClickListener(v -> extTrapezImageView.crop(new Result<Void>() {
            @Override
            public void onComplete(Void data) {
                extTrapezImageView.getCroppedBitmap(new Result<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        runOnUiThread(() -> {

                            findViewById(R.id.iv_display_trapez).setVisibility(View.GONE);
                            findViewById(R.id.iv_display_cropped).setVisibility(View.VISIBLE);

                            ((AppCompatImageView) findViewById(R.id.iv_display_cropped))
                                    .setImageBitmap(data);
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                "Could not get cropped bitmap" + throwable.getMessage(),
                                Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {

            }
        }));
    }

    private void demoFree() {

        btn.setText("Crop Freeform");

        findViewById(R.id.iv_display).setVisibility(View.GONE);
        findViewById(R.id.iv_display_free).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_display_trapez).setVisibility(View.GONE);

        final ExtFreeImageView extFreeImageView = findViewById(R.id.iv_display_free);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inDensity = 0;
        options.inTargetDensity = 0;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);

        extFreeImageView.setImageBitmap(bitmap);

        findViewById(R.id.btn_capture).setOnClickListener(v -> extFreeImageView.crop(new Result<Void>() {
            @Override
            public void onComplete(Void data) {

                extFreeImageView.getCroppedBitmap(new Result<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        runOnUiThread(() -> {

                            findViewById(R.id.iv_display_free).setVisibility(View.GONE);
                            findViewById(R.id.iv_display_cropped).setVisibility(View.VISIBLE);

                            ((AppCompatImageView) findViewById(R.id.iv_display_cropped))
                                    .setImageBitmap(data);
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                "Could not get cropped bitmap" + throwable.getMessage(),
                                Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Could not get cropped bitmap" + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }
        }));
    }

    private void demoRect() {

        btn.setText("Crop Defined");

        findViewById(R.id.iv_display).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_display_free).setVisibility(View.GONE);
        findViewById(R.id.iv_display_trapez).setVisibility(View.GONE);

        final ExtRectImageView extRectImageView = findViewById(R.id.iv_display);
        extRectImageView.setGridColor(Color.GREEN);
        extRectImageView.setGridVisibility(GridMode.ALWAYS);
        extRectImageView.setCropMode(CropMode.RECT);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inDensity = 0;
        options.inTargetDensity = 0;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);

        Toast.makeText(MainActivity.this, "Rotating image.", Toast.LENGTH_SHORT).show();

        extRectImageView.setImageBitmap(bitmap);
        extRectImageView.rotate(BaseExtImageView.Rotate.CW_90, new Result<Void>() {
            @Override
            public void onComplete(Void data) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Rotation completed.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Rotation failed. " + throwable.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        findViewById(R.id.btn_capture).setOnClickListener(v -> extRectImageView.crop(new Result<Void>() {
            @Override
            public void onComplete(Void data) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Crop completed", Toast.LENGTH_SHORT).show();

                    findViewById(R.id.iv_display).setVisibility(View.GONE);
                    findViewById(R.id.iv_display_cropped).setVisibility(View.VISIBLE);

                    extRectImageView.getCroppedBitmap(new Result<Bitmap>() {
                        @Override
                        public void onComplete(Bitmap data) {
                            runOnUiThread(() -> {

                                Bitmap d = extRectImageView.scaleToFit(data, extRectImageView
                                        .getMeasuredWidth(), extRectImageView.getMeasuredHeight());

                                data.recycle();
                                ((AppCompatImageView) findViewById(R.id.iv_display_cropped))
                                        .setImageBitmap(d);
                            });
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                    "Could not get cropped bitmap" + throwable.getMessage(),
                                    Toast.LENGTH_SHORT).show());
                        }
                    });
                });
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Crop failed" + throwable.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }));
    }
}

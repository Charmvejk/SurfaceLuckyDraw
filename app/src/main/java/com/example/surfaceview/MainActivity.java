package com.example.surfaceview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    //按钮
    private ImageView imageView;
    private Luckpan luckpan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.id_start_btn);
        luckpan = findViewById(R.id.id_lucky);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!luckpan.isStart()) {
                    luckpan.LuckyStart(1);
                    imageView.setImageResource(R.drawable.stop);

                } else {
                    if (!luckpan.isShouldEnd()) {
                        luckpan.LuckyEnd();
                        imageView.setImageResource(R.drawable.start);

                    }
                }
            }
        });
    }
}

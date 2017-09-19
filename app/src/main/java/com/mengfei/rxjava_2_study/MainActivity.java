package com.mengfei.rxjava_2_study;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv1;
    TextView tv2;
    TextView tv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         tv1 = (TextView) findViewById(R.id.article1);
         tv2 = (TextView) findViewById(R.id.article2);
         tv3 = (TextView) findViewById(R.id.article3);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String txtname = "";
        if (view.getId() == R.id.article1) {
//            txtname = "zbkq.txt";
            txtname = "VerticalSwitchTextView.java";
        }
        else if (view.getId() == R.id.article2) {
            txtname = "zbkq.txt";
        }
        else if (view.getId() == R.id.article3) {
            startActivity(new Intent(this, PanoramaActivity.class));
            return;
        }
        if (txtname.isEmpty()) {
            Toast.makeText(this, "emmm... 出了点问题呢..", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("txtname", txtname);

        startActivity(intent);
    }
}

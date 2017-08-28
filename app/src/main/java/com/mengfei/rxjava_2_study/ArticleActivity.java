package com.mengfei.rxjava_2_study;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class ArticleActivity extends AppCompatActivity {
    Subscription mDownRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        final Context con = this;

        final LinearLayout ll = (LinearLayout) findViewById(R.id.content);

        final String txtname = getIntent().getStringExtra("txtname");
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> e) throws Exception {

//                AssetManager.AssetInputStream as = new Ass
//                e.requested()
                InputStream in = getResources().getAssets().open(txtname);
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));
                String readLine = null;
                    while ((readLine = br.readLine()) != null && !e.isCancelled()) {
//                        if (e.requested() == 0) {
//                            break;
//                        }
                        while (e.requested() == 0) {
                            if (e.isCancelled()) {
                                break;
                            }
                        }

                        Log.e("ceshi", "subscribe" + readLine);
                        e.onNext(readLine);
                    }
                in.close();
                br.close();
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mDownRequest = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e("ceshi", "onnext" + s);
                        TextView line = new TextView(con);
                        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        line.setGravity(Gravity.CENTER_HORIZONTAL);
                        line.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        line.setSingleLine(true);
                        line.setKeepScreenOn(true);
                        line.setText(s);
                        ll.addView(line);
                                mDownRequest.request(1);

                    }

                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(con, "error?? " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(con, "complete", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        mDownRequest.cancel();
    }
}

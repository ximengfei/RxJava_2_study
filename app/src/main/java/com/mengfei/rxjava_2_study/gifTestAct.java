package com.mengfei.rxjava_2_study;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class gifTestAct extends AppCompatActivity {
    private String gifCacheFile  ;
    String key;
    File gifFile;
    ImageView gifIv;
    GIfTask giftask;
//    private final String testGifUrl = "http://bmob-cdn-10726.b0.upaiyun.com/2017/09/06/0efcb245406b5ba680a42e05eba611f2.jpg";
    private final String testGifUrl = "http://bmob-cdn-10726.b0.upaiyun.com/2017/09/06/557af2bd406c479780e3a7df2f6d5f11.gif";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_test);
        gifIv = (ImageView) findViewById(R.id.gifIv);

        gifCacheFile =  getExternalCacheDir().getAbsolutePath();

        key = computeMD5(testGifUrl) + ".gif";
        gifFile = new File(gifCacheFile, key);
        if (gifFile.exists() && gifFile.length() > 0) {
//            Glide.with(this).load();
            Glide.with(this).load(gifFile).asGif().into(gifIv);
//            Glide.with(this).load("http://bmob-cdn-10726.b0.upaiyun.com/2017/09/06/0efcb245406b5ba680a42e05eba611f2.jpg").into(gifIv);
        }
        else
        {
           giftask = new GIfTask();
            giftask.execute(testGifUrl);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(gifTestAct.this, MainActivity.class));
                finish();
            }
        }, 3000);

    }

    public static String computeMD5(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = messageDigest.digest(string.getBytes());
            return bytesToHexString(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    class GIfTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            InputStream inputStream = null;
            ByteArrayOutputStream bos = null;
            FileOutputStream fos = null;
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                String pathName=gifCacheFile + File.separator + key;//文件存储路径

                inputStream = conn.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                bos = new ByteArrayOutputStream();
                while ((len = inputStream.read(buffer)) != -1) {
                    if (isCancelled()) {
                            return 0;
                        }
                    bos.write(buffer, 0, len);
                }

                byte[] getData = bos.toByteArray();

                //文件保存位置
                File file = new File(pathName);
                fos = new FileOutputStream(file);
                fos.write(getData);

                Log.e("xmf", "dowload success!!!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("xmf", "dowload error " + e.getMessage());
                return 0;
            } finally{
                try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {

                        inputStream.close();

                }
                bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 1;
        }
        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                // ### 显示
                Glide.with(gifTestAct.this).load(gifFile).asGif().into(gifIv);
            } else {
                if (gifFile.exists())
                    gifFile.delete();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (giftask != null) {
            giftask.cancel(true);
        }
    }

}

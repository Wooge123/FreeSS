package me.yukang.freess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yukang on 17-7-21.
 */

public class QRdownload extends Thread {

    private String baseUrl;
    private String[] images;

    public QRdownload(String baseUrl, String[] images) {
        this.baseUrl = baseUrl;
        this.images = images;
    }

    @Override
    public void run() {

        HttpURLConnection conn = null;
        URL url;

        for (int i = 0; i < images.length; i++) {

            try {
                url = new URL(baseUrl + images[i]);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                FileOutputStream fos = new FileOutputStream(new File(Constant.path, images[i]));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        conn.disconnect();

    }
}

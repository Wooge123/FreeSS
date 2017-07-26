package me.yukang.freess;

import android.os.Environment;

/**
 * Created by yukang on 17-7-22.
 */

public class Constant {
    public static final String[] images = {"jp01.png", "jp02.png", "jp03.png", "us01.png", "us02.png", "us03.png"};
    public static final String path = Environment.getExternalStorageDirectory() + "/Android/data/me.yukang.freess/images/";
    public static final String[] servers = {"日本#1", "日本#2", "日本#3", "美国#1", "美国#2", "美国#3",};
    public static final String baseUrl = "http://freess.cx/images/servers/";
}

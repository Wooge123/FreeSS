package me.yukang.freess;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;

import static me.yukang.freess.Constant.baseUrl;
import static me.yukang.freess.Constant.images;
import static me.yukang.freess.Constant.path;
import static me.yukang.freess.Constant.servers;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView serverList;
    private ListAdapter adapter;
    private String parseContent;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                parseContent = (String) msg.obj;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverList = (ListView) findViewById(R.id.server_list);
        adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, servers);
        serverList.setAdapter(adapter);

        File dir = new File(Constant.path);
        if (!dir.exists())
            dir.mkdirs();
        
        if (dir.listFiles().length < 6)
            new QRdownload(baseUrl, images).start();

        serverList.setOnItemClickListener(this);

        // 创建上下文菜单
        serverList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, 0, 0, "复制IP");
                contextMenu.add(0, 1, 0, "复制密码");
                contextMenu.add(0, 2, 0, "复制解析结果");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 1, 0, "刷新").setIcon(R.drawable.ic_refresh_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == Menu.FIRST + 1) {
            new QRdownload(baseUrl, images).start();
            Toast.makeText(MainActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        view.showContextMenu();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = QRCodeDecoder.syncDecodeQRCode(path + images[position]);
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        String result;

        if (this.parseContent != null && this.parseContent != "") {
            result = base64Decode(this.parseContent);

            int i = result.indexOf("@");
            int j = result.indexOf(":");
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            switch (item.getItemId()) {

                case 0:
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, result.substring(i + 1, result.indexOf(":", i))));
                    Toast.makeText(MainActivity.this, "复制IP成功", Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, result.substring(j + 1, i)));
                    Toast.makeText(MainActivity.this, "复制密码成功", Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, parseContent));
                    Toast.makeText(MainActivity.this, "复制解析结果成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        return super.onContextItemSelected(item);
    }

    // base64解码
    private String base64Decode(String result) {
        return new String(Base64.decode(result.getBytes(), Base64.DEFAULT));
    }
}


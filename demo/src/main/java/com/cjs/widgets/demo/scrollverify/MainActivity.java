package com.cjs.widgets.demo.scrollverify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;

import com.cjs.widgets.scrollverifyview.ScrollVerifyView;

import java.io.File;

public class MainActivity extends Activity {
    private ScrollVerifyView svv;
    private Button btn_invalidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        svv = findViewById(R.id.svv);
        btn_invalidate = findViewById(R.id.btn_invalidate);
        btn_invalidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void openExcel() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+File.separator+"eee"+File.separator+"a.xlsx"));
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else {
            // 声明需要的临时权限
            // 第二个参数，即第一步中配置的authorities
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStorageDirectory()+File.separator+"eee"+File.separator+"a.xlsx"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.ms-excel");
        }

        startActivity(intent);
    }
}

package com.example.sunray.ftpforandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sunray.ftpforandroid.FtpService.FtpService;
import com.example.sunray.ftpforandroid.MessageTools.MessageTools;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button uploadBt;
    private Button downloadBt;
    private Button searchBt;
    private TextView showView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadBt = (Button)findViewById(R.id.upload_bt);
        downloadBt = (Button)findViewById(R.id.download_bt);
        searchBt = (Button)findViewById(R.id.search_bt);
        showView = (TextView) findViewById(R.id.show_view);
        uploadBt.setOnClickListener(this);
        downloadBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent startIntent = new Intent(MainActivity.this,FtpService.class);
        startService(startIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent stopIntent = new Intent(MainActivity.this,FtpService.class);
        stopService(stopIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.upload_bt:
                Intent uploadIntent = new Intent(MessageTools.UPLOAD_FILE);
                sendBroadcast(uploadIntent);
                break;
            case R.id.download_bt:
                Intent downloadIntent = new Intent(MessageTools.DOWNLOAD_FILE);
                sendBroadcast(downloadIntent);
                break;
            case R.id.search_bt:
                Intent searchItent = new Intent(MessageTools.SEARCH_4_FILE);
                sendBroadcast(searchItent);
                break;
            default:
                break;
        }
    }
}

package com.example.sunray.ftpforandroid.FtpService;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.sunray.ftpforandroid.FTPUtil.FTPToolKit;
import com.example.sunray.ftpforandroid.FtpAsyncTask.FtpAsyncTask;
import com.example.sunray.ftpforandroid.MessageTools.MessageTools;
import com.example.sunray.ftpforandroid.logUtil.LogUtil;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPFile;

import com.example.sunray.ftpforandroid.Ftp2Util.FtpUtil;

import java.io.File;
import java.net.URL;

/**
 * Created by sunray on 2017-9-5.
 */

public class FtpService extends Service {
    private String TAG = "FtpService";
    private FTPClient ftpClient = null;
    private FtpUtil ftpUtil = new FtpUtil("192.168.0.199",2121,"sunray","121212");

    private BroadcastReceiver mBoradcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MessageTools.UPLOAD_FILE.equals(action)){
                new FtpAsyncTask(ftpUtil).execute(MessageTools.UPLOAD_FILE);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }).start();
            } else if (MessageTools.DOWNLOAD_FILE.equals(action)){
//                new Thread(new Runnable() {
//                @Override
//                public void run() {
//
////                        LogUtil.e(TAG,"______________________________");
//                     }
//                }).start();
                new FtpAsyncTask(ftpUtil).execute(MessageTools.DOWNLOAD_FILE);
            } else if(MessageTools.SEARCH_4_FILE.equals(action)){
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }).start();
                new FtpAsyncTask(ftpUtil).execute(MessageTools.SEARCH_4_FILE);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageTools.DOWNLOAD_FILE);
        intentFilter.addAction(MessageTools.UPLOAD_FILE);
        intentFilter.addAction(MessageTools.SEARCH_4_FILE);
        registerReceiver(mBoradcastReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBoradcastReceiver);
    }
}

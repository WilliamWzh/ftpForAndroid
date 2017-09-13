package com.example.sunray.ftpforandroid.FtpAsyncTask;

import android.os.AsyncTask;
import android.os.Environment;

import com.example.sunray.ftpforandroid.Ftp2Util.FtpUtil;
import com.example.sunray.ftpforandroid.MessageTools.MessageTools;
import com.example.sunray.ftpforandroid.logUtil.LogUtil;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;


/**
 * Created by sunray on 2017-9-6.
 */

public class FtpAsyncTask extends AsyncTask {
    private FtpUtil ftpUtil = null;

    public FtpAsyncTask(FtpUtil ftpUtil){
        this.ftpUtil = ftpUtil;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if(objects[0] == MessageTools.UPLOAD_FILE){
            try {
                ftpUtil.login();
                ftpUtil.upload("/LocalUser",new File(Environment.getExternalStorageDirectory().toString()+"/test.txt"));
                ftpUtil.logout();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(objects[0] == MessageTools.DOWNLOAD_FILE){
            try {
                ftpUtil.login();
                ftpUtil.downloadFile(Environment.getExternalStorageDirectory().toString()+"/test.txt","/LocalUser/ftp/test.txt");
                ftpUtil.logout();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(objects[0] == MessageTools.SEARCH_4_FILE){
            try {
                ftpUtil.login();
                ftpUtil.changeDirectory("/LocalUser");
                String[] files = ftpUtil.getFileList();
                for(String file:files){
                    LogUtil.e("TAG",file);
                }
                ftpUtil.logout();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}

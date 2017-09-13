package com.example.sunray.ftpforandroid.Ftp2Util;

import android.util.Log;

import com.example.sunray.ftpforandroid.logUtil.LogUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import it.sauronsoftware.ftp4j.FTPReply;


/**
 * Created by sunray on 2017-9-6.
 */

public class FtpUtil {
    private String host = "";
    private int port ;
    private String name = "";
    private String password = "";
    private FTPClient ftpClient = null;

    public FtpUtil(String host,int port,String name,String password){
        this.host = host;
        this.port = port;
        this.name = name;
        this.password = password;
    }


    private FtpUtil() {
    }

    public static FtpUtil getInstance() {
        return new FtpUtil();
    }

    /**
     * 创建目录
     * @param client
     *            FTP客户端对象
     * @param dir
     *            目录
     * @throws Exception
     */
    public void mkdirs(FTPClient client, String dir) throws Exception {
        if (dir == null) {
            return;
        }
        dir = dir.replace("\\", "/");
        String[] dirs = dir.split("/");
        for (int i = 0; i < dirs.length; i++) {
            dir = dirs[i];
            if (dir != null && !"".equals(dir)) {
                if (!existsDirectory(client, dir)) {
                    client.createDirectory(dir);
                }
                client.changeDirectory(dir);
            }
        }
    }


    public String[] getFileList() {
        try {
            return ftpClient.listNames();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (FTPAbortedException e) {
            e.printStackTrace();
        } catch (FTPListParseException e) {
            e.printStackTrace();
        } catch (FTPException e) {
            e.printStackTrace();
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 判断目录是否存在
     * @param client
     *            FTP 客户端对
     * @return
     * @throws Exception
     */
    public boolean existsDirectory(FTPClient client, String dir) throws Exception {
        try {
            String tempPath = client.currentDirectory();
            client.changeDirectory(dir);
            client.changeDirectory(tempPath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断当前为文件还是目录
     * @param client
     *            FTP客户端对象
     * @param dir
     *            文件或目录
     * @return -1、文件或目录不存在 0、文件 1、目录
     * @throws Exception
     */
    public int getFileType(FTPClient client, String dir) throws Exception {
        FTPFile[] files = null;
        try {
            files = client.list(dir);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getClass().getName());
            return -1;
        }
        if (files.length > 1) {
            return FTPFile.TYPE_DIRECTORY;
        } else if (files.length == 1) {
            FTPFile f = files[0];
            if (f.getType() == FTPFile.TYPE_DIRECTORY) {
                return FTPFile.TYPE_DIRECTORY;
            }
            String path = dir + "/" + f.getName();
            try {
                int len = client.list(path).length;
                if (len == 1) {
                    return FTPFile.TYPE_DIRECTORY;
                } else {
                    return FTPFile.TYPE_FILE;
                }
            } catch (Exception e) {
                return FTPFile.TYPE_FILE;
            }
        } else {
            try {
                client.changeDirectory(dir);
                client.changeDirectoryUp();
                return FTPFile.TYPE_DIRECTORY;
            } catch (Exception e) {
                return -1;
            }
        }
    }

    /**
     * 获取FTP目录
     * @param url
     *            原FTP目录
     * @param dir
     *            目录
     * @return
     * @throws Exception
     */
    public URL getURL(URL url, String dir) throws Exception {
        String path = url.getPath();
        if (!path.endsWith("/") && !path.endsWith("\\")) {
            path += "/";
        }
        dir = dir.replace("\\", "/");
        if (dir.startsWith("/")) {
            dir = dir.substring(1);
        }
        path += dir;
        return new URL(url, path);
    }

    /**
     * 获取FTP目录
     * @param dir
     *            目录
     * @return
     * @throws Exception
     */
    public URL getURL(String dir) throws Exception {
        return getURL(new URL("ftp://" + host + ":" + port), dir);
    }

    /**
     * 连接并登录FTP服务器
     * @return
     * @throws Exception
     */
    public boolean login() throws Exception {
        try {
            if (ftpClient == null) {
                ftpClient = new FTPClient();
            }
            // 建立连接
            if (!ftpClient.isConnected()) {
                ftpClient.connect(host, port);

                ftpClient.setType(FTPClient.TYPE_BINARY);
                ftpClient.setCharset("utf-8");
                System.out.println("建立连接");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("与FTP服务器建立连接失败！" + e.getMessage());
        }
        try {
            if (!ftpClient.isAuthenticated()) {
                ftpClient.login(name, password);
                System.out.println("认证通过");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("登录FTP服务器失败！" + e.getMessage());
        }
        return true;
    }

    /**
     * 退出并关闭FTP连接
     */
    public void close() {
        if (null != ftpClient) {
            try {
                ftpClient.disconnect(true);// 安全退出
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("安全退出FTP服务时异常！" + e.getMessage());
            }
        }

    }

    /**
     * 注销客户端连接
     * @throws Exception
     */
    public void logout() throws Exception {
        logout(ftpClient);
    }

    /**
     * 注销客户端连接
     * @param client
     *            FTP客户端对象
     * @throws Exception
     */
    public void logout(FTPClient client) throws Exception {
        if (client != null) {
            try {
                client.logout(); // 退出登录
            } catch (FTPException fe) {
                throw new Exception(fe.getMessage());
            } catch (Exception e) {
                throw e;
            } finally {
                if (client.isConnected()) { // 断开连接
                    client.disconnect(true);
                }
            }
        }
    }

    /**
     * 下载文件
     * @param localFilePath 本地文件名及路径
     * @param remoteFileName
     *            远程文件名称
     * @return
     * @throws Exception
     */
    public void downloadFile(String localFilePath, String remoteFileName) throws Exception {
        File localFile = new File(localFilePath);
        try {
            ftpClient.download(remoteFileName, localFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("下载" + remoteFileName + "时出现异常！" + e.getMessage());
            throw e;
        }
    }

    /**
     * 上传文件或目录
     *
     * @param dir
     *            ftp服务器目标路径
     * @param files
     *            文件或目录对象数组
     * @throws Exception
     */
    public void upload(String dir, File... files) throws Exception {
        upload(dir, false, files);
    }

    /**
     * 上传文件或目录到ftp服务器
     *
     * @param dir
     *            ftp服务器目标路径
     * @param del
     *            是否删除源文件，默认为false
     * @param files
     *            文件或目录对象数组
     * @throws Exception
     */
    public void upload(String dir, boolean del, File... files) throws Exception {
        if (files == null) {
            return;
        }
        try {
            if (dir != null && !"".equals(dir)) {
                mkdirs(ftpClient, dir); // 创建文件
            }
            for (File file : files) {
                if (file.isDirectory()) { // 上传目录
                    uploadFolder(getURL(dir), file, del);
                } else {
                    ftpClient.upload(file); // 上传文件
                    if (del) { // 删除源文件
                        file.delete();
                    }
                }
            }
        } finally {
            // logout(ftpClient);
        }
    }

    public void changeDirectory(String dir) throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException {
        ftpClient.changeDirectory(dir);
    }

    /**
     * 删除ftp服务文件或目录
     *
     * @param dirs
     *            文件或目录数组
     * @throws Exception
     */
    public void deleteFile(String... dirs) throws Exception {
        if (dirs == null || dirs.length <= 0) {
            return;
        }
        try {
            for (String dir : dirs) {
                ftpClient.changeDirectory("/"); // 切换至根目录
                ftpClient.deleteFile(dir);
            }
        } finally {
            // logout(client);
        }
    }

    /**
     * 删除ftp服务器上的目录
     *
     * @param dirs
     *            文件或目录数组
     * @throws Exception
     */
    public void delete(String... dirs) throws Exception {
        if (dirs == null || dirs.length <= 0) {
            return;
        }
        try {
            int type = -1;
            for (String dir : dirs) {
                ftpClient.changeDirectory("/"); // 切换至根目录
                type = getFileType(ftpClient, dir); // 获取当前类型
                if (type == 0) { // 删除文件
                    ftpClient.deleteFile(dir);
                } else if (type == 1) { // 删除目录
                    deleteFolder(getURL(dir));
                }
                System.out.println(dir + " delete success!!!");
            }
        } finally {
            // logout(client);
        }
    }

    /**
     * 通过url删除ftp服务器目录
     *
     * @param url
     *            FTP URL
     * @throws Exception
     */
    private void deleteFolder(URL url) throws Exception {
        String path = url.getPath();
        ftpClient.changeDirectory(path);
        FTPFile[] files = ftpClient.list();
        String name = null;
        for (FTPFile file : files) {
            name = file.getName();
            // 排除隐藏目录
            if (".".equals(name) || "..".equals(name)) {
                continue;
            }
            if (file.getType() == FTPFile.TYPE_DIRECTORY) { // 递归删除子目录
                deleteFolder(getURL(url, file.getName()));
            } else if (file.getType() == FTPFile.TYPE_FILE) { // 删除文件
                ftpClient.deleteFile(file.getName());
            }
        }
        ftpClient.changeDirectoryUp();
        ftpClient.deleteDirectory(url.getPath()); // 删除当前目录
    }

    /**
     * 上传目录
     *
     * @param parentUrl
     *            父节点URL
     * @param file
     *            目录
     * @throws Exception
     */
    private void uploadFolder(URL parentUrl, File file, boolean del) throws Exception {
        ftpClient.changeDirectory(parentUrl.getPath());
        String dir = file.getName(); // 当前目录名称
        URL url = getURL(parentUrl, dir);
        if (!existsDirectory(ftpClient, url.getPath())) { // 判断当前目录是否存在
            ftpClient.createDirectory(dir); // 创建目录
        }
        ftpClient.changeDirectory(dir);
        File[] files = file.listFiles(); // 获取当前文件夹所有文件及目录
        for (int i = 0; i < files.length; i++) {
            file = files[i];
            if (file.isDirectory()) { // 如果是目录，则递归上传
                uploadFolder(url, file, del);
            } else { // 如果是文件，直接上传
                ftpClient.changeDirectory(url.getPath());
                ftpClient.upload(file);
                if (del) { // 删除源文件
                    file.delete();
                }
            }
        }
    }

    /**
     * 测试ftp连接
     *
     * @return 0连通，1：连接失败；2:连接但登陆失败。
     * @throws Exception
     */
    public int test() throws Exception {
        int flag = 0;
        try {
            ftpClient = new FTPClient();
            // 建立连接
            ftpClient.connect(host, port);
            ftpClient.setType(FTPClient.TYPE_AUTO);
        } catch (Exception e) {
            flag = 1;
        }

        if (ftpClient.isConnected()) {
            try {
                ftpClient.login(name, password);
                if (!ftpClient.isAuthenticated()) {
                    flag = 2;
                } else {
                    flag = 0;
                    ftpClient.disconnect(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 2;
            }
        } else {
            flag = 1;
        }

        return flag;
    }

//    public static void main(String[] args) throws Exception {
//        FtpUtil ftp = FtpUtil.getInstance();
//        ftp.login();
//        ftp.delete("ENV.txt");
//        ftp.logout();
//
//    }


}

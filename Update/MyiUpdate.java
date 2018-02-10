//
// Decompiled by Jadx - 4892ms
//
package com.netspace.library.utilities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import com.netspace.library.application.MyiBaseApplication;
import com.netspace.library.interfaces.IDownloadStatus;
import com.netspace.library.ui.StatusBarDisplayer;
import com.netspace.pad.library.R;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MyiUpdate extends Thread {
    private static int mStatusBarID = 5;
    private final int PROGRESSDIALOG_CLOSE = 3;
    private final int PROGRESSDIALOG_SETPROGRESS = 1;
    private final int PROGRESSDIALOG_SETTEXT = 2;
    private MyiUpdateCallBack mCallBack;
    private Context mContext;
    private IDownloadStatus mDownloadStatus = new 2(this);
    private String mFileURL;
    private IDownloadStatus mMETAINFAnalysis = new 3(this);
    private HashMap<String, String> mOriginalFileHash = new HashMap();
    private ProgressDialog mProgressDialog;
    private Handler mProgressDialogMessageHandler = new 1(this);
    private StatusBarDisplayer mStatusBarDisplayer;
    private WeakReference<TextView> mTextView;
    private String mWorkFileName;
    private ZipOutputStream mZipOutputStream;
    private ArrayList<String> marrFilesToDownload = new ArrayList();
    private boolean mbCancel = false;
    private boolean mbIncreaseMode = false;
    private boolean mbSilence;
    private String mszNewVersionURL;
    private String mszServerAddress;

    public void setServerAddress(String szServerAddress) {
        this.mszServerAddress = szServerAddress;
    }

    public void setSilence(boolean bEnable) {
        this.mbSilence = bEnable;
    }

    protected boolean checkApkFileSign(String szApkFilePath) {
        PackageManager pm = this.mContext.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(szApkFilePath, 64);
        if (info == null) {
            return false;
        }
        try {
            PackageInfo current = pm.getPackageInfo(this.mContext.getPackageName(), 64);
            if (info.signatures == null || current.signatures == null || info.signatures.length <= 0 || info.signatures.length != current.signatures.length || !info.signatures[0].toString().equalsIgnoreCase(current.signatures[0].toString())) {
                return false;
            }
            return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public MyiUpdate(Context context, String szUpdateFileName) {
        this.mContext = context;
        mStatusBarID++;
        SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        String szVersionCheckURL = "http://updates.myi.cn/release/updates/" + szUpdateFileName;
        String szBaseAddress = Settings.getString("BaseAddress", "");
        if (!szBaseAddress.isEmpty() && szBaseAddress.indexOf("webservice.myi.cn") == -1) {
            if (!szBaseAddress.endsWith("/")) {
                szBaseAddress = new StringBuilder(String.valueOf(szBaseAddress)).append("/").toString();
            }
            szVersionCheckURL = new StringBuilder(String.valueOf(szBaseAddress)).append("updates/release/updates/").append(szUpdateFileName).toString();
        }
        this.mFileURL = szVersionCheckURL;
    }

    private void analysisCurrentPackage() {
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(HardwareInfo.getFilePath(this.mContext))));
            ZipEntry ze;
            do {
                ze = zis.getNextEntry();
                if (ze == null) {
                    break;
                }
            } while (!ze.getName().equalsIgnoreCase("META-INF/MANIFEST.MF"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (true) {
                int count = zis.read(buffer);
                if (count == -1) {
                    break;
                }
                baos.write(buffer, 0, count);
            }
            parserMetaINF(new String(baos.toByteArray(), "UTF-8"), this.mOriginalFileHash);
            zis.close();
        } catch (IOException ioe) {
            System.out.println("Error opening zip file" + ioe);
        }
    }

    private void buildNewPackage(HashMap<String, String> mapNewFileContent, HashMap<String, String> mapOldFileContent) {
        String szSourceApkFileName = HardwareInfo.getFilePath(this.mContext);
        try {
            this.mZipOutputStream = new ZipOutputStream(new FileOutputStream(this.mWorkFileName));
            this.mZipOutputStream.setMethod(8);
            this.mZipOutputStream.setLevel(9);
            InputStream is = new FileInputStream(szSourceApkFileName);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
            while (true) {
                ZipEntry ze = zis.getNextEntry();
                if (ze == null) {
                    break;
                }
                String filename = ze.getName();
                if (filename.indexOf("META-INF/") == -1) {
                    if (mapNewFileContent.containsKey(filename) && ((String) mapNewFileContent.get(filename)).equalsIgnoreCase((String) mapOldFileContent.get(filename))) {
                        this.mZipOutputStream.putNextEntry(new ZipEntry(filename));
                        byte[] buffer = new byte[1024];
                        while (true) {
                            int count = zis.read(buffer);
                            if (count == -1) {
                                break;
                            }
                            this.mZipOutputStream.write(buffer, 0, count);
                        }
                        this.mZipOutputStream.closeEntry();
                        mapNewFileContent.remove(filename);
                    }
                    if (this.mbCancel) {
                        return;
                    }
                }
            }
            zis.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        for (String szKey : mapNewFileContent.keySet()) {
            this.marrFilesToDownload.add(szKey);
            Log.d("MyiUpdate", "new file, " + szKey);
        }
        if (mapNewFileContent.size() > 0) {
            this.marrFilesToDownload.add("META-INF/MYI.RSA");
            this.marrFilesToDownload.add("META-INF/MYI.SF");
            this.marrFilesToDownload.add("META-INF/MANIFEST.MF");
            startDownload();
            return;
        }
        setDisplayText("版本完全一致，无需更新。", false);
    }

    private void startDownload() {
        if (this.mProgressDialog != null) {
            setDisplayText("正在下载所需文件...", false);
        } else if (!this.mbSilence) {
            if (this.mStatusBarDisplayer == null) {
                this.mStatusBarDisplayer = new StatusBarDisplayer(this.mContext);
                this.mStatusBarDisplayer.setNotifyID(mStatusBarID * 1000);
            }
            this.mStatusBarDisplayer.setTitle("软件更新");
            this.mStatusBarDisplayer.setText("正在下载更新...");
            this.mStatusBarDisplayer.setIcon(R.drawable.ic_cloud_download);
            this.mStatusBarDisplayer.showProgressBox(null);
        }
        int i = 0;
        while (i < this.marrFilesToDownload.size()) {
            setDisplayText((((float) (i + 1)) / ((float) this.marrFilesToDownload.size())) * 100.0f);
            if (this.mStatusBarDisplayer != null) {
                this.mStatusBarDisplayer.setProgressMax(this.marrFilesToDownload.size());
                this.mStatusBarDisplayer.setProgress(i + 1);
            }
            String szFileName = (String) this.marrFilesToDownload.get(i);
            String szURL = "/getcontent?apkfilename=" + Utilities.getURLFileName(this.mszNewVersionURL) + "&contentfilename=" + szFileName;
            File nullFile = new File("/dev/null");
            szURL = MyiBaseApplication.getProtocol() + "://" + this.mszServerAddress + szURL;
            if (Utilities.downloadFileToLocalFile(szURL, nullFile, new DownloadToPackage(this, szFileName)) == null) {
                Log.d("MyiUpdate", "Download " + szURL + " failed. skip rest.");
                return;
            } else if (!this.mbCancel) {
                i++;
            } else {
                return;
            }
        }
        try {
            this.mZipOutputStream.close();
            File outputFile = new File(this.mWorkFileName);
            if (this.mProgressDialog != null) {
                this.mProgressDialogMessageHandler.obtainMessage(3).sendToTarget();
                Utilities.showAlertMessage(this.mContext, "软件更新", "更新下载完毕并开始安装。");
            } else {
                setDisplayText("更新下载完毕，准备开始安装。", true);
                if (this.mStatusBarDisplayer != null) {
                    this.mStatusBarDisplayer.hideProgressBox();
                    this.mStatusBarDisplayer.setTitle("软件更新");
                    this.mStatusBarDisplayer.setText("下载完毕，点击这里开始安装。");
                    this.mStatusBarDisplayer.setPendingIntent(PendingIntent.getActivity(this.mContext, 0, getInstallAPKIntent(outputFile.getAbsolutePath()), 0x08000000));
                    this.mStatusBarDisplayer.showAlertBox();
                }
            }
            outputFile.setExecutable(true, false);
            outputFile.setReadable(true, false);
            InstallAPK(outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCallBack(MyiUpdateCallBack callBack) {
        this.mCallBack = callBack;
    }

    private void parserMetaINF(String szInfContent, HashMap<String, String> mapFileHash) {
        String[] arrContents = szInfContent.split("\r\n\r\n");
        for (int i = 1; i < arrContents.length; i++) {
            String szOneLine = arrContents[i];
            if (szOneLine.indexOf("\r\n ") != -1) {
                szOneLine = szOneLine.replace("\r\n ", "");
            }
            int nPos = szOneLine.indexOf("\n");
            int nStartPos = szOneLine.indexOf(":");
            mapFileHash.put(szOneLine.substring(nStartPos + 1, nPos).trim().replace("\r", "").replace("\n", ""), szOneLine.substring(szOneLine.indexOf(":", nStartPos + 1) + 2, szOneLine.length()));
        }
    }

    public void setTextView(TextView textView) {
        if (textView != null) {
            this.mTextView = new WeakReference(textView);
        }
    }

    public void showProgressDialog() {
        this.mProgressDialog = new ProgressDialog(this.mContext);
        this.mProgressDialog.setTitle("版本更新检测");
        this.mProgressDialog.setMessage("正在检查服务器上是否有新版本，请稍候...");
        this.mProgressDialog.setCancelable(true);
        this.mProgressDialog.setCanceledOnTouchOutside(false);
        this.mProgressDialog.setIndeterminate(true);
        this.mProgressDialog.setProgressStyle(1);
        this.mProgressDialog.setButton(-2, "取消", new 4(this));
        this.mProgressDialog.setOnCancelListener(new 5(this));
        this.mProgressDialog.show();
    }

    public void cancel() {
        this.mbCancel = true;
    }

    private void setDisplayText(String szText, boolean bSkipIfNoTextView) {
        if (this.mProgressDialog != null) {
            this.mProgressDialogMessageHandler.obtainMessage(2, szText).sendToTarget();
        }
        if (this.mTextView != null) {
            TextView textView = (TextView) this.mTextView.get();
            if (textView != null) {
                textView.post(new 6(this, szText));
            }
        } else if (!bSkipIfNoTextView && this.mProgressDialog == null && !this.mbSilence) {
            if (this.mStatusBarDisplayer == null) {
                this.mStatusBarDisplayer = new StatusBarDisplayer(this.mContext);
                this.mStatusBarDisplayer.setNotifyID(mStatusBarID * 1000);
                this.mStatusBarDisplayer.setIcon(R.drawable.ic_cloud_download);
                this.mStatusBarDisplayer.setTitle("软件更新");
            } else {
                this.mStatusBarDisplayer.hideMessage();
            }
            this.mStatusBarDisplayer.setTitle("软件更新");
            this.mStatusBarDisplayer.setText(szText);
            this.mStatusBarDisplayer.showAlertBox();
        }
    }

    private void setDisplayText(float fProgress) {
        if (this.mProgressDialog != null) {
            this.mProgressDialogMessageHandler.obtainMessage(1, Float.valueOf(fProgress)).sendToTarget();
        }
        if (this.mTextView != null) {
            TextView textView = (TextView) this.mTextView.get();
            if (textView != null) {
                textView.post(new 7(this, fProgress));
            }
        }
    }

    private int CompareVersion(String szVersion1, String szVersion2) {
        String[] arrVersion1 = szVersion1.split("\\.");
        String[] arrVersion2 = szVersion2.split("\\.");
        if (arrVersion1.length == arrVersion2.length) {
            for (int i = 0; i < arrVersion1.length; i++) {
                int a = Integer.parseInt(arrVersion1[i]);
                int b = Integer.parseInt(arrVersion2[i]);
                if (a > b) {
                    return 1;
                }
                if (a < b) {
                    return -1;
                }
            }
        }
        return 0;
    }

    private void InstallAPK(String szFileName) {
        if (this.mCallBack == null || !this.mCallBack.onBeforeInstall(szFileName)) {
            File file = new File(szFileName);
            Intent intent = new Intent();
            intent.addFlags(0x10000000);
            intent.setAction("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            this.mContext.startActivity(intent);
        }
    }

    private Intent getInstallAPKIntent(String szFileName) {
        File file = new File(szFileName);
        Intent intent = new Intent();
        intent.addFlags(0x10000000);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }

    public void run() {
        currentThread().setName("MyiUpdate Work Thread");
        ConnectivityManager cm = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = false;
        if (activeNetwork != null) {
            isConnected = activeNetwork.isConnected();
        }
        if (!isConnected) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            setDisplayText("正在等待网络就绪...", false);
        }
        while (!isConnected) {
            if (activeNetwork != null) {
                isConnected = activeNetwork.isConnected();
            }
            activeNetwork = cm.getActiveNetworkInfo();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
        setDisplayText("开始检查更新...", false);
        Utilities.downloadFileToLocalFile(this.mFileURL, null, this.mDownloadStatus);
    }
}

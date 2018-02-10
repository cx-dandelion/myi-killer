//
// Decompiled by Jadx - 3457ms
//
package com.netspace.library.utilities;

import android.app.PendingIntent;
import android.preference.PreferenceManager;
import com.netspace.library.application.MyiBaseApplication;
import com.netspace.library.interfaces.IDownloadStatus;
import com.netspace.library.ui.StatusBarDisplayer;
import com.netspace.pad.library.R;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class MyiUpdate$2 implements IDownloadStatus {
    private boolean mbFileContent = false;
    private int nLastPercent = 0;
    private String szNewVersion;
    final /* synthetic */ MyiUpdate this$0;

    MyiUpdate$2(MyiUpdate myiUpdate) {
        this.this$0 = myiUpdate;
    }

    public void onBeginDownload() {
        if (this.mbFileContent) {
            MyiUpdate.access$1(this.this$0, "开始下载新版本...", false);
        } else {
            MyiUpdate.access$1(this.this$0, "正在检查更新文件...", false);
        }
    }

    public void onDownloadProgress(long nCurrentPos, long nMaxLength) {
        float fPercent = (((float) nCurrentPos) / ((float) nMaxLength)) * 100.0f;
        int nPercent = (int) fPercent;
        MyiUpdate.access$2(this.this$0, fPercent);
        if (MyiUpdate.access$3(this.this$0) != null) {
            if (this.nLastPercent != nPercent) {
                MyiUpdate.access$3(this.this$0).setProgressMax((int) nMaxLength);
                MyiUpdate.access$3(this.this$0).setProgress((int) nCurrentPos);
            }
            this.nLastPercent = nPercent;
        }
    }

    public void onProgressLineContent(String szLineContent) {
        int nSepPos = szLineContent.indexOf("=");
        String szFieldName = "";
        String szFieldValue = "";
        if (nSepPos != -1) {
            szFieldName = szLineContent.substring(0, nSepPos);
            szFieldValue = szLineContent.substring(nSepPos + 1).trim();
            if (szFieldName.compareToIgnoreCase("url") == 0) {
                MyiUpdate.access$4(this.this$0, szFieldValue);
            }
            if (szFieldName.compareToIgnoreCase("version") == 0) {
                this.szNewVersion = szFieldValue;
            }
        }
    }

    public void onDownloadComplete(File outputFile) {
        MalformedURLException e;
        IOException e2;
        if (this.mbFileContent) {
            if (this.this$0.checkApkFileSign(outputFile.getAbsolutePath())) {
                if (MyiUpdate.access$0(this.this$0) != null) {
                    MyiUpdate.access$12(this.this$0).obtainMessage(3).sendToTarget();
                    Utilities.showAlertMessage(MyiUpdate.access$6(this.this$0), "软件更新", "更新下载完毕并开始安装。");
                } else {
                    MyiUpdate.access$1(this.this$0, "更新下载完毕，准备开始安装。", true);
                    if (MyiUpdate.access$3(this.this$0) != null) {
                        MyiUpdate.access$3(this.this$0).hideProgressBox();
                        MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                        MyiUpdate.access$3(this.this$0).setText("下载完毕，点击这里开始安装。");
                        MyiUpdate.access$3(this.this$0).showAlertBox();
                        MyiUpdate.access$3(this.this$0).setPendingIntent(PendingIntent.getActivity(MyiUpdate.access$6(this.this$0), 0, MyiUpdate.access$19(this.this$0, outputFile.getAbsolutePath()), 0x08000000));
                    }
                }
                outputFile.setExecutable(true, false);
                outputFile.setReadable(true, false);
                MyiUpdate.access$20(this.this$0, outputFile.getAbsolutePath());
            } else if (MyiUpdate.access$0(this.this$0) != null) {
                MyiUpdate.access$12(this.this$0).obtainMessage(3).sendToTarget();
                Utilities.showAlertMessage(MyiUpdate.access$6(this.this$0), "软件更新", "安装包下载完毕但签名无效，不能安装。");
            } else {
                MyiUpdate.access$1(this.this$0, "安装包下载完毕但签名无效，不能安装。", true);
                if (MyiUpdate.access$3(this.this$0) != null) {
                    MyiUpdate.access$3(this.this$0).hideProgressBox();
                    MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                    MyiUpdate.access$3(this.this$0).setText("安装包下载完毕但签名无效，不能安装。");
                    MyiUpdate.access$3(this.this$0).showAlertBox();
                }
            }
        } else if (MyiUpdate.access$5(this.this$0) != null && this.szNewVersion != null) {
            if (MyiUpdate.access$7(this.this$0, this.szNewVersion, HardwareInfo.getVersionName(MyiUpdate.access$6(this.this$0))) > 0) {
                File outputDir;
                this.mbFileContent = true;
                String szBaseAddress = PreferenceManager.getDefaultSharedPreferences(MyiUpdate.access$6(this.this$0)).getString("BaseAddress", "");
                boolean bUseOnlyFileName = false;
                if (!szBaseAddress.isEmpty() && szBaseAddress.indexOf("webservice.myi.cn") == -1) {
                    if (!szBaseAddress.endsWith("/")) {
                        szBaseAddress = new StringBuilder(String.valueOf(szBaseAddress)).append("/").toString();
                    }
                    bUseOnlyFileName = true;
                }
                URL TargetFileURL = null;
                URL UpdateFileURL = null;
                try {
                    URL TargetFileURL2 = new URL(MyiUpdate.access$5(this.this$0));
                    try {
                        UpdateFileURL = new URL(MyiUpdate.access$8(this.this$0));
                        TargetFileURL = TargetFileURL2;
                    } catch (MalformedURLException e3) {
                        e = e3;
                        TargetFileURL = TargetFileURL2;
                        e.printStackTrace();
                        if (bUseOnlyFileName) {
                            MyiUpdate.access$4(this.this$0, TargetFileURL.getProtocol() + "://" + UpdateFileURL.getAuthority() + "/updates" + TargetFileURL.getFile());
                        }
                        if (MyiUpdate.access$0(this.this$0) == null) {
                            MyiUpdate.access$12(this.this$0).obtainMessage(2, "正在下载更新...").sendToTarget();
                        } else if (!MyiUpdate.access$9(this.this$0)) {
                            if (MyiUpdate.access$3(this.this$0) == null) {
                                MyiUpdate.access$10(this.this$0, new StatusBarDisplayer(MyiUpdate.access$6(this.this$0)));
                                MyiUpdate.access$3(this.this$0).setNotifyID(MyiUpdate.access$11() * 1000);
                            } else {
                                MyiUpdate.access$3(this.this$0).hideMessage();
                            }
                            MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                            MyiUpdate.access$3(this.this$0).setText("正在下载更新...");
                            MyiUpdate.access$3(this.this$0).setIcon(R.drawable.ic_cloud_download);
                            MyiUpdate.access$3(this.this$0).showProgressBox(null);
                        }
                        outputDir = new File(new StringBuilder(String.valueOf(MyiUpdate.access$6(this.this$0).getCacheDir().getAbsolutePath())).append("/update").toString());
                        try {
                            outputDir.mkdir();
                            outputDir.setExecutable(true, false);
                            outputDir.setReadable(true, false);
                            Utilities.deleteDir(outputDir, outputDir);
                            outputFile = File.createTempFile("upgrade", ".apk", outputDir);
                            MyiUpdate.access$13(this.this$0, outputFile.getAbsolutePath());
                            MyiUpdate.access$14(this.this$0, true);
                            if (!MyiUpdate.access$15(this.this$0)) {
                                MyiUpdate.access$1(this.this$0, "正在分析当前版本...", false);
                                MyiUpdate.access$16(this.this$0);
                                Utilities.downloadFileToLocalFile(MyiBaseApplication.getProtocol() + "://" + MyiUpdate.access$17(this.this$0) + ("/getcontent?apkfilename=" + Utilities.getURLFileName(MyiUpdate.access$5(this.this$0)) + "&contentfilename=META-INF/MANIFEST.MF"), null, MyiUpdate.access$18(this.this$0));
                            } else if (MyiBaseApplication.getCommonVariables().UserInfo.checkPermission("disable_local_normal_upgrade")) {
                                MyiUpdate.access$1(this.this$0, "服务器禁用标准更新，请使用增量更新", true);
                                if (MyiUpdate.access$3(this.this$0) == null) {
                                    MyiUpdate.access$3(this.this$0).hideProgressBox();
                                    MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                                    MyiUpdate.access$3(this.this$0).setText("服务器禁用标准更新，请使用增量更新");
                                    MyiUpdate.access$3(this.this$0).showAlertBox();
                                }
                            } else {
                                MyiUpdate.access$1(this.this$0, "正在下载新版本...", false);
                                Utilities.downloadFileToLocalFile(MyiUpdate.access$5(this.this$0), outputFile, this);
                            }
                        } catch (IOException e4) {
                            e2 = e4;
                            File file = outputDir;
                            e2.printStackTrace();
                            MyiUpdate.access$1(this.this$0, "检查更新遇到错误，" + e2.getMessage(), true);
                            if (MyiUpdate.access$3(this.this$0) != null) {
                                MyiUpdate.access$3(this.this$0).hideProgressBox();
                                MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                                MyiUpdate.access$3(this.this$0).setText("检查更新遇到错误，" + e2.getMessage());
                                MyiUpdate.access$3(this.this$0).showAlertBox();
                            }
                        }
                    }
                } catch (MalformedURLException e5) {
                    e = e5;
                    e.printStackTrace();
                    if (bUseOnlyFileName) {
                        MyiUpdate.access$4(this.this$0, TargetFileURL.getProtocol() + "://" + UpdateFileURL.getAuthority() + "/updates" + TargetFileURL.getFile());
                    }
                    if (MyiUpdate.access$0(this.this$0) == null) {
                        MyiUpdate.access$12(this.this$0).obtainMessage(2, "正在下载更新...").sendToTarget();
                    } else if (MyiUpdate.access$9(this.this$0)) {
                        if (MyiUpdate.access$3(this.this$0) == null) {
                            MyiUpdate.access$3(this.this$0).hideMessage();
                        } else {
                            MyiUpdate.access$10(this.this$0, new StatusBarDisplayer(MyiUpdate.access$6(this.this$0)));
                            MyiUpdate.access$3(this.this$0).setNotifyID(MyiUpdate.access$11() * 1000);
                        }
                        MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                        MyiUpdate.access$3(this.this$0).setText("正在下载更新...");
                        MyiUpdate.access$3(this.this$0).setIcon(R.drawable.ic_cloud_download);
                        MyiUpdate.access$3(this.this$0).showProgressBox(null);
                    }
                    outputDir = new File(new StringBuilder(String.valueOf(MyiUpdate.access$6(this.this$0).getCacheDir().getAbsolutePath())).append("/update").toString());
                    outputDir.mkdir();
                    outputDir.setExecutable(true, false);
                    outputDir.setReadable(true, false);
                    Utilities.deleteDir(outputDir, outputDir);
                    outputFile = File.createTempFile("upgrade", ".apk", outputDir);
                    MyiUpdate.access$13(this.this$0, outputFile.getAbsolutePath());
                    MyiUpdate.access$14(this.this$0, true);
                    if (!MyiUpdate.access$15(this.this$0)) {
                        MyiUpdate.access$1(this.this$0, "正在分析当前版本...", false);
                        MyiUpdate.access$16(this.this$0);
                        Utilities.downloadFileToLocalFile(MyiBaseApplication.getProtocol() + "://" + MyiUpdate.access$17(this.this$0) + ("/getcontent?apkfilename=" + Utilities.getURLFileName(MyiUpdate.access$5(this.this$0)) + "&contentfilename=META-INF/MANIFEST.MF"), null, MyiUpdate.access$18(this.this$0));
                    } else if (MyiBaseApplication.getCommonVariables().UserInfo.checkPermission("disable_local_normal_upgrade")) {
                        MyiUpdate.access$1(this.this$0, "正在下载新版本...", false);
                        Utilities.downloadFileToLocalFile(MyiUpdate.access$5(this.this$0), outputFile, this);
                    } else {
                        MyiUpdate.access$1(this.this$0, "服务器禁用标准更新，请使用增量更新", true);
                        if (MyiUpdate.access$3(this.this$0) == null) {
                            MyiUpdate.access$3(this.this$0).hideProgressBox();
                            MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                            MyiUpdate.access$3(this.this$0).setText("服务器禁用标准更新，请使用增量更新");
                            MyiUpdate.access$3(this.this$0).showAlertBox();
                        }
                    }
                }
                if (bUseOnlyFileName) {
                    MyiUpdate.access$4(this.this$0, TargetFileURL.getProtocol() + "://" + UpdateFileURL.getAuthority() + "/updates" + TargetFileURL.getFile());
                }
                if (MyiUpdate.access$0(this.this$0) == null) {
                    MyiUpdate.access$12(this.this$0).obtainMessage(2, "正在下载更新...").sendToTarget();
                } else if (MyiUpdate.access$9(this.this$0)) {
                    if (MyiUpdate.access$3(this.this$0) == null) {
                        MyiUpdate.access$10(this.this$0, new StatusBarDisplayer(MyiUpdate.access$6(this.this$0)));
                        MyiUpdate.access$3(this.this$0).setNotifyID(MyiUpdate.access$11() * 1000);
                    } else {
                        MyiUpdate.access$3(this.this$0).hideMessage();
                    }
                    MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                    MyiUpdate.access$3(this.this$0).setText("正在下载更新...");
                    MyiUpdate.access$3(this.this$0).setIcon(R.drawable.ic_cloud_download);
                    MyiUpdate.access$3(this.this$0).showProgressBox(null);
                }
                try {
                    outputDir = new File(new StringBuilder(String.valueOf(MyiUpdate.access$6(this.this$0).getCacheDir().getAbsolutePath())).append("/update").toString());
                    outputDir.mkdir();
                    outputDir.setExecutable(true, false);
                    outputDir.setReadable(true, false);
                    Utilities.deleteDir(outputDir, outputDir);
                    outputFile = File.createTempFile("upgrade", ".apk", outputDir);
                    MyiUpdate.access$13(this.this$0, outputFile.getAbsolutePath());
                    if (!(MyiUpdate.access$5(this.this$0).indexOf("enc") == -1 && MyiUpdate.access$5(this.this$0).indexOf(".zip") == -1)) {
                        MyiUpdate.access$14(this.this$0, true);
                    }
                    if (!MyiUpdate.access$15(this.this$0)) {
                        MyiUpdate.access$1(this.this$0, "正在分析当前版本...", false);
                        MyiUpdate.access$16(this.this$0);
                        Utilities.downloadFileToLocalFile(MyiBaseApplication.getProtocol() + "://" + MyiUpdate.access$17(this.this$0) + ("/getcontent?apkfilename=" + Utilities.getURLFileName(MyiUpdate.access$5(this.this$0)) + "&contentfilename=META-INF/MANIFEST.MF"), null, MyiUpdate.access$18(this.this$0));
                    } else if (MyiBaseApplication.getCommonVariables().UserInfo.checkPermission("disable_local_normal_upgrade")) {
                        MyiUpdate.access$1(this.this$0, "服务器禁用标准更新，请使用增量更新", true);
                        if (MyiUpdate.access$3(this.this$0) == null) {
                            MyiUpdate.access$3(this.this$0).hideProgressBox();
                            MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                            MyiUpdate.access$3(this.this$0).setText("服务器禁用标准更新，请使用增量更新");
                            MyiUpdate.access$3(this.this$0).showAlertBox();
                        }
                    } else {
                        MyiUpdate.access$1(this.this$0, "正在下载新版本...", false);
                        Utilities.downloadFileToLocalFile(MyiUpdate.access$5(this.this$0), outputFile, this);
                    }
                } catch (IOException e6) {
                    e2 = e6;
                    e2.printStackTrace();
                    MyiUpdate.access$1(this.this$0, "检查更新遇到错误，" + e2.getMessage(), true);
                    if (MyiUpdate.access$3(this.this$0) != null) {
                        MyiUpdate.access$3(this.this$0).hideProgressBox();
                        MyiUpdate.access$3(this.this$0).setTitle("软件更新");
                        MyiUpdate.access$3(this.this$0).setText("检查更新遇到错误，" + e2.getMessage());
                        MyiUpdate.access$3(this.this$0).showAlertBox();
                    }
                }
            } else if (MyiUpdate.access$0(this.this$0) != null) {
                MyiUpdate.access$12(this.this$0).obtainMessage(3).sendToTarget();
                Utilities.showAlertMessage(MyiUpdate.access$6(this.this$0), "软件更新", "当前使用的是最新版，无需更新。");
            } else {
                MyiUpdate.access$1(this.this$0, "当前使用的是最新版，无需更新。", false);
            }
        }
    }

    public void onDownloadError(int nErrorCode, String szErrorText) {
        if (MyiUpdate.access$0(this.this$0) != null) {
            MyiUpdate.access$12(this.this$0).obtainMessage(3).sendToTarget();
            Utilities.showAlertMessage(MyiUpdate.access$6(this.this$0), "软件更新", "检查更新遇到错误，" + szErrorText);
            return;
        }
        MyiUpdate.access$1(this.this$0, "检查更新遇到错误，" + szErrorText, true);
        if (MyiUpdate.access$3(this.this$0) != null) {
            MyiUpdate.access$3(this.this$0).hideMessage();
            MyiUpdate.access$3(this.this$0).hideProgressBox();
            MyiUpdate.access$3(this.this$0).setTitle("软件更新");
            MyiUpdate.access$3(this.this$0).setText("检查更新遇到错误，" + szErrorText);
            MyiUpdate.access$3(this.this$0).showAlertBox();
        }
    }

    public void onProgressFileBlock(byte[] fileContent, long nLength) {
    }

    public boolean isCancelled() {
        return MyiUpdate.access$21(this.this$0);
    }
}

//
// Decompiled by Jadx - 6365ms
//
package com.netspace.library.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.foxit.app.App;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.NovaIconsModule;
import com.netspace.library.activity.WifiConfigActivity;
import com.netspace.library.bluetooth.BlueToothPen;
import com.netspace.library.components.ChatComponent;
import com.netspace.library.components.DrawComponent;
import com.netspace.library.global.CommonVariables;
import com.netspace.library.im.IMService;
import com.netspace.library.parser.ServerConfigurationParser;
import com.netspace.library.plugins.PluginsManager;
import com.netspace.library.receiver.ShutdownReceiver;
import com.netspace.library.receiver.WifiReceiver;
import com.netspace.library.servers.HttpServer;
import com.netspace.library.servers.MJpegServer;
import com.netspace.library.struct.RecentUser;
import com.netspace.library.struct.ServerInfo;
import com.netspace.library.struct.Session;
import com.netspace.library.struct.UserInfo;
import com.netspace.library.threads.LoadExamDataThread3;
import com.netspace.library.threads.UsageDataUploadThread;
import com.netspace.library.ui.StatusBarDisplayer;
import com.netspace.library.ui.UI;
import com.netspace.library.utilities.SSLConnection;
import com.netspace.library.utilities.Utilities;
import com.netspace.library.virtualnetworkobject.DataSynchronizeEngine;
import com.netspace.library.virtualnetworkobject.VirtualNetworkObject;
import com.netspace.library.window.ChatWindow;
import com.netspace.pad.library.R;
import java.io.File;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;
import wei.mark.standout.StandOutWindow;

public class MyiBaseApplication extends Application {
    public static boolean DEBUG = true;
    public static boolean EncryptedBuild = false;
    public static boolean PhoneMode = false;
    public static PluginsManager PluginsManager = new PluginsManager();
    public static boolean PowerOff = false;
    public static boolean ReleaseBuild = false;
    private static final String TAG = "MyiBaseApplication";
    public static boolean UseThreadModeForIM = false;
    protected static boolean WakeUpOnWifiConnected = false;
    protected static Context mBaseContext;
    protected static CommonVariables mCommonVariables;
    protected static boolean mConfigured = false;
    protected static File mDexFile = null;
    protected static HttpServer mHttpServer;
    protected static RecentUser mRecentUser;
    protected static int mRequiredService;
    protected static ShutdownReceiver mShutdownReceiver;
    protected static UsageDataUploadThread mUsageDataUploadThread;
    protected static WifiReceiver mWifiReceiver;
    private static boolean mbDebugOn = false;
    private static boolean mbDebugTested = false;
    protected static boolean mbDisableStateSave = false;
    protected static boolean mbUseSSL = false;

    public void onCreate() {
        super.onCreate();
        boolean bPreviousStateLoaded = false;
        App.instance().setApplicationContext(this);
        App.instance().loadModules();
        EventBus.getDefault().register(this);
        try {
            if (getPackageManager().getPackageInfo(getPackageName(), 64).signatures[0].toCharsString().equalsIgnoreCase("308203253082020da00302010202040966f52d300d06092a864886f70d01010b05003042310b300906035504061302434e310f300d060355040713064e696e67426f31223020060355040a13194e696e67426f2052756959694b654a6920436f2e204c74642e3020170d3132313231313130313133355a180f32303632313132393130313133355a3042310b300906035504061302434e310f300d060355040713064e696e67426f31223020060355040a13194e696e67426f2052756959694b654a6920436f2e204c74642e30820122300d06092a864886f70d01010105000382010f003082010a0282010100abf2c60e5fcb7776da3d22c3180e284da9c4e715cec2736646da086cbf979a7f74bc147167f0f32ef0c52458e9183f0dd9571d7971e49564c00fbfd30bef3ca9a2d52bffcd0142c72e10fac158cb62c7bc7e9e17381a555ad7d39a24a470584a0e6aafdce2e4d6877847b15cbf4de89e3e4e71b11dca9920843ccc055acf8781db29bdaf3f06e16f055bf579a35ae3adb4d1149f8d43d90add54596acef8e4a28905f9f19fc0aa7fda9e8d56aa63db5d8d5e0fc4c536629f0a25a44429c699318329af6a3e869dd5e8289c78f55d14563559ffc9ccbf71fac5a03e13a3ee1fb8fc3857d10d5d3990bf9b84cd6fa555eb17a74809a7bb501e953a639104146adb0203010001a321301f301d0603551d0e04160414da4b4d8147840ff4b03f10fc5dd534bb133204e6300d06092a864886f70d01010b05000382010100801b8d796b90ab7a711a88f762c015158d75f1ae5caf969767131e6980ebe7f194ce33750902e6aa561f33d76d37f4482ff22cccbf9d5fecb6ed8e3f278fd1f988ea85ae30f8579d4afe710378b3ccb9cb41beaddef22fb3d128d9d61cfcb3cb05d32ab3b2c4524815bfc9a53c8e5ee3ad4589dc888bcdbdaf9270268eb176ff2d43c2fd236b5bf4ef8ffa8dd920d1583d70f971b988ee4054e1f739ea71510ee7172546ffcda31e6b270178f91086db9ff1051dedf453a6bad4f9b432d362bbe173fd1cc7350853fddd552a27a82fdfaf98e5b08186a03ffc6e187387e4bbd52195126c7c6cec6ab07fd5aadc43a0edb7826b237ba8c8aa443f132516fe89ba")) {
                ReleaseBuild = true;
                DEBUG = false;
            } else {
                DEBUG = true;
                ReleaseBuild = false;
            }
            EncryptedBuild = false;
            String[] fileList = getApplicationContext().getAssets().list("");
            int i = 0;
            while (i < fileList.length) {
                if (fileList[i].contains("ijiami") || fileList[i].contains("ijm")) {
                    EncryptedBuild = true;
                    DEBUG = false;
                    break;
                }
                i++;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        if (getResources().getBoolean(R.bool.isTablet)) {
            Log.i(TAG, "Tablet mode.");
            PhoneMode = false;
        } else {
            Log.i(TAG, "Phone mode.");
            PhoneMode = true;
        }
        Iconify.with(new FontAwesomeModule()).with(new NovaIconsModule());
        mBaseContext = this;
        if (mbDisableStateSave || !loadState()) {
            Log.i(TAG, "Using new state.");
            mCommonVariables = new CommonVariables();
            mCommonVariables.UserInfo = new UserInfo();
            mCommonVariables.ServerInfo = new ServerInfo();
            mCommonVariables.Session = new Session();
            mCommonVariables.ServerInfo.szServerAddress = "webservice.myi.cn:8089";
        } else {
            mCommonVariables.UserInfo.decodeLoginJson(PreferenceManager.getDefaultSharedPreferences(getBaseAppContext()).getString("OfflineLoginJson", ""));
            Log.i(TAG, "Previous state loaded.");
            bPreviousStateLoaded = true;
        }
        mRecentUser = new RecentUser();
        mDexFile = getBaseAppContext().getDir("dex", 0);
        SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(mBaseContext);
        if (Settings.contains("FullAccount")) {
            mCommonVariables.UserInfo.szFullAccount = Settings.getString("FullAccount", "");
            mCommonVariables.UserInfo.szUserName = Settings.getString("username", "");
            mCommonVariables.ServerInfo.szServerAddress = Settings.getString("ServerAddress", "");
            mCommonVariables.ServerInfo.szResourceBaseURL = Settings.getString("BaseAddress", "");
            LoadExamDataThread3.setupServerAddress(mCommonVariables.ServerInfo.szServerAddress);
            LoadExamDataThread3.setUserInfo(mCommonVariables.UserInfo.szUserName, "");
            if (mCommonVariables.UserInfo.isConfigured() && mCommonVariables.ServerInfo.isConfigured()) {
                mConfigured = true;
            } else {
                mConfigured = false;
            }
        } else {
            mConfigured = false;
        }
        if (mWifiReceiver == null) {
            mWifiReceiver = new WifiReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.wifi.STATE_CHANGE");
            registerReceiver(mWifiReceiver, intentFilter);
        }
        if (mShutdownReceiver == null) {
            mShutdownReceiver = new ShutdownReceiver();
            registerReceiver(mShutdownReceiver, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
        }
        LoadExamDataThread3.setSessionFailListener(new 1(this));
        enableSSL();
        if (bPreviousStateLoaded) {
            Log.i(TAG, "Automatic start background service.");
            if (mCommonVariables.ServerInfo.bUseSSL) {
                enableSSL();
            } else {
                disableSSL();
            }
            if (isUseSSL()) {
                LoadExamDataThread3.setSessionID(mCommonVariables.Session.getSessionID());
            }
            Utilities.runOnUIThreadFirst(mBaseContext, new 2(this));
        }
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if ("mounted".equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(new StringBuilder(String.valueOf(cachePath)).append(File.separator).append(uniqueName).toString());
    }

    public static boolean getWakeUpOnWifiConnect() {
        return WakeUpOnWifiConnected;
    }

    public static boolean isDebugOn() {
        if (!mbDebugTested) {
            mbDebugOn = mCommonVariables.UserInfo.checkPermission("debug");
            mbDebugTested = true;
        }
        return mbDebugOn;
    }

    public static MJpegServer createMJpegServer() {
        return mCommonVariables.MyiApplication.createScreenCaptureServer();
    }

    public static void startBackgroundService() {
        mRequiredService = mCommonVariables.MyiApplication.getRequiredService();
        mRecentUser.init();
        LoadExamDataThread3.setupServerAddress(mCommonVariables.ServerInfo.szServerAddress);
        LoadExamDataThread3.setUserInfo(mCommonVariables.UserInfo.szUserName, "");
        DataSynchronizeEngine.setClientID(mCommonVariables.MyiApplication.getClientID());
        VirtualNetworkObject.initEngines(mBaseContext);
        VirtualNetworkObject.getDataSynchronizeEngine().setDownloadTickCount(-1);
        DrawComponent.registerAvailableGraphics();
        BlueToothPen BlueToothPen = new BlueToothPen();
        mUsageDataUploadThread = new UsageDataUploadThread();
        mUsageDataUploadThread.start();
        if ((mRequiredService & 1) == 1) {
            String[] arrBlockedModules = mCommonVariables.MyiApplication.getBlockedModules();
            if ((arrBlockedModules == null || !Utilities.isInArray(arrBlockedModules, IMService.class.getName())) && !VirtualNetworkObject.getOfflineMode()) {
                Intent imService = new Intent(mBaseContext, IMService.class);
                imService.setAction("init");
                String szProtocol = "http://";
                if (isUseSSL()) {
                    szProtocol = "https://";
                }
                imService.putExtra("listenUrl", new StringBuilder(String.valueOf(szProtocol)).append(mCommonVariables.ServerInfo.szServerAddress).append("/WaitResponse?clientid=").append(mCommonVariables.MyiApplication.getClientID()).append("&version=").append(mCommonVariables.MyiApplication.getAppName()).append("&enablehistroy=on&").append("&sessionid=").append(mCommonVariables.Session.getSessionID()).append("&alias=").append(mCommonVariables.UserInfo.getClassesGUIDs("_Class_")).append("_UserGUID_").append(mCommonVariables.UserInfo.szUserGUID).toString());
                imService.putExtra("postUrl", new StringBuilder(String.valueOf(szProtocol)).append(mCommonVariables.ServerInfo.szServerAddress).append("/SendResponse?sessionid=").append(mCommonVariables.Session.getSessionID()).append("&clientid=").toString());
                imService.putExtra("from", mCommonVariables.MyiApplication.getClientID());
                mBaseContext.startService(imService);
            }
        }
        if ((mRequiredService & 2) == 2) {
            if (UI.ScreenJpegServer == null) {
                UI.ScreenJpegServer = createMJpegServer();
                UI.ScreenJpegServer.SetSendOnlyDiff(true);
                UI.ScreenJpegServer.start();
                Log.i(TAG, "MJPEG server start.");
            } else {
                Log.i(TAG, "MJPEG server already running.");
            }
        }
        if ((mRequiredService & 4) == 4 && mHttpServer == null) {
            try {
                mHttpServer = new HttpServer();
            } catch (IOException e) {
                mHttpServer = null;
                e.printStackTrace();
            }
        }
        mCommonVariables.MyiApplication.startAppBackgroundService();
    }

    public static void stopBackgroundService() {
        DrawComponent.unregisterGraphics();
        LoadExamDataThread3.cancelAndWaitAll();
        mRecentUser.clear();
        IMService.hideChatNotifyBar();
        ChatComponent.shutdown();
        StandOutWindow.closeAll(mBaseContext, ChatWindow.class);
        StatusBarDisplayer.shutdownAll();
        if (mUsageDataUploadThread != null) {
            mUsageDataUploadThread.stopThread();
            mUsageDataUploadThread = null;
        }
        if ((mRequiredService & 1) == 1) {
            IMService.hideChatNotifyBar();
            mBaseContext.stopService(new Intent(mBaseContext, IMService.class));
        }
        if ((mRequiredService & 2) == 2 && UI.ScreenJpegServer != null) {
            UI.ScreenJpegServer.Stop();
            UI.ScreenJpegServer = null;
        }
        if ((mRequiredService & 4) == 4) {
            mCommonVariables.MyiApplication.stopAppBackgroundService();
            VirtualNetworkObject.shutDown();
            mCommonVariables.ServerInfo.ServerConfiguration = new ServerConfigurationParser();
        } else {
            mCommonVariables.MyiApplication.stopAppBackgroundService();
            VirtualNetworkObject.shutDown();
            mCommonVariables.ServerInfo.ServerConfiguration = new ServerConfigurationParser();
        }
    }

    public static void enableSSL() {
        if (!mbUseSSL) {
            SSLConnection.allowAllSSL();
            mbUseSSL = true;
            mCommonVariables.ServerInfo.bUseSSL = true;
        }
    }

    public static void disableSSL() {
    }

    public static boolean isUseSSL() {
        return mbUseSSL;
    }

    public static File getDexPath() {
        return mDexFile;
    }

    public static Context getBaseAppContext() {
        return mBaseContext;
    }

    public static CommonVariables getCommonVariables() {
        return mCommonVariables;
    }

    public static RecentUser getRecentUser() {
        return mRecentUser;
    }

    public static boolean isRootRequired() {
        return mCommonVariables.MyiApplication.isAppRootRequired();
    }

    public static boolean canExecuteIPTableScript() {
        if ((mCommonVariables.MyiApplication.getMDMFlags() & 1) == 1) {
            return true;
        }
        return false;
    }

    public static boolean isConfigured() {
        SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(mBaseContext);
        if (Settings.contains("FullAccount")) {
            if (mCommonVariables.UserInfo.szFullAccount.isEmpty()) {
                mCommonVariables.UserInfo.szFullAccount = Settings.getString("FullAccount", "");
                mCommonVariables.UserInfo.szUserName = Settings.getString("username", "");
                mCommonVariables.ServerInfo.szServerAddress = Settings.getString("ServerAddress", "");
                mCommonVariables.ServerInfo.szResourceBaseURL = Settings.getString("BaseAddress", "");
                LoadExamDataThread3.setupServerAddress(mCommonVariables.ServerInfo.szServerAddress);
                LoadExamDataThread3.setUserInfo(mCommonVariables.UserInfo.szUserName, "");
            }
            if (mCommonVariables.UserInfo.isConfigured() && mCommonVariables.ServerInfo.isConfigured()) {
                mConfigured = true;
            } else {
                mConfigured = false;
            }
        } else {
            mConfigured = false;
        }
        return mConfigured;
    }

    public static void startMainActivity() {
        mCommonVariables.MyiApplication.startAppMainActivity();
    }

    public static void startLogout() {
        mCommonVariables.MyiApplication.startAppLogout();
    }

    public static boolean isLoggedIn() {
        return mCommonVariables.Session.isLoggedIn();
    }

    public static void startConfigActivity() {
        Intent intent = new Intent(mBaseContext, WifiConfigActivity.class);
        intent.setFlags(0x10000000);
        mBaseContext.startActivity(intent);
    }

    public static String getProtocol() {
        if (mbUseSSL) {
            return "https";
        }
        return "http";
    }

    public static void saveState() {
        Gson gson4Expose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization().setPrettyPrinting().create();
        String szFileName = new StringBuilder(String.valueOf(mBaseContext.getCacheDir().getAbsolutePath())).append("/state.txt").toString();
        if (mCommonVariables.Session.isLoggedIn()) {
            Utilities.writeTextToFile(szFileName, gson4Expose.toJson(mCommonVariables));
        } else {
            cleanState();
        }
    }

    public static void cleanState() {
        new File(new StringBuilder(String.valueOf(mBaseContext.getCacheDir().getAbsolutePath())).append("/state.txt").toString()).delete();
    }

    public static boolean loadState() {
        String szFileName = new StringBuilder(String.valueOf(mBaseContext.getCacheDir().getAbsolutePath())).append("/state.txt").toString();
        if (!new File(szFileName).exists()) {
            return false;
        }
        mCommonVariables = (CommonVariables) new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization().setPrettyPrinting().create().fromJson(Utilities.readTextFile(szFileName), CommonVariables.class);
        if (!ReleaseBuild) {
            Toast.makeText(mBaseContext, "已自动恢复上次的状态", 0).show();
        }
        return true;
    }

    @Subscribe
    public void onSubscriberExceptionEvent(SubscriberExceptionEvent e) {
        Log.d(TAG, "SubscriberExceptionEvent happen.");
        e.throwable.printStackTrace();
    }
}

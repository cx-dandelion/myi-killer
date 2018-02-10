//
// Decompiled by Jadx - 766ms
//
package com.netspace.library.interfaces;

import android.content.Context;
import com.netspace.library.parser.ServerConfigurationParser;
import java.util.Calendar;

public interface IDeviceMDM {
    boolean captureScreen(String str);

    void classLockScreen();

    void classUnlockScreen();

    void disableLimitsForReconfig();

    void enableAppFromServerList(ServerConfigurationParser serverConfigurationParser);

    void enableUsbDebug();

    boolean enterKiosMode();

    String getFirmwareUpgradeFileName();

    int getMDMFlags();

    boolean installApk(String str);

    boolean isActive();

    boolean leaveKiosMode();

    boolean onBootloader();

    boolean onWipe();

    void removeLimits(boolean z);

    void resaveSettings(Context context);

    void restart();

    void setAppEnable(String str, boolean z);

    void setCurrentTime(Calendar calendar);

    void setLimit();

    void setNetworkLimitFromServerList(ServerConfigurationParser serverConfigurationParser);

    void setPhoneFunctions(boolean z);

    void startActiveUI(Context context);

    void startFirmwareUpgrade();
}

//
// Decompiled by Jadx - 2610ms
//
package com.netspace.teacherpad;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.netspace.library.threads.CheckNewVersionThread;
import com.netspace.library.utilities.HardwareInfo;
import com.netspace.library.utilities.Utilities;

public class LoginActivity2 extends Activity implements OnClickListener {
    public static final int MSG_AUTHFAIL = 258;
    public static final int MSG_AUTHSUCCESS = 257;
    public static final int MSG_DOWNLOADFAIL = 260;
    public static final int MSG_DOWNLOADPROGRESS = 256;
    public static final int MSG_DOWNLOADSUCCESS = 261;
    public static final int MSG_NONEWVERSION = 259;
    public static final int MSG_VERSIONCHECKFAIL = 262;
    private static CheckNewVersionThread m_CheckThread;
    private static boolean m_bLoggedIn = false;
    private final Runnable UserImageGetRunnable = new 3(this);
    private Runnable m_AutologinRunnable = new 1(this);
    private Context m_Context = null;
    private Handler m_Handler;
    private Button m_LoginButton;
    private SharedPreferences m_Settings = null;
    private EditText m_TextPassword;
    private TextView m_TextViewVersionCheck;
    private Handler m_ThreadMessageHandler = new 2(this);
    private boolean m_bDataCenterInfoComplete = false;
    private boolean m_bSaveLoginInfo = false;
    private boolean m_bSimpleLoginComplete = false;
    private String m_szAvatarFileName;
    private String m_szPassword = "";
    private String m_szUserName = "";
    private LoginActivity2 m_this = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        Utilities.trimCache(this);
        this.m_this = this;
        this.m_Handler = new Handler();
        setContentView(0x7f040024);
        this.m_Context = this;
        this.m_LoginButton = (Button) findViewById(0x7f0d00e5);
        this.m_LoginButton.setOnClickListener(this);
        this.m_TextPassword = (EditText) findViewById(0x7f0d00e4);
        this.m_TextPassword.setImeOptions(2);
        this.m_TextPassword.setOnEditorActionListener(new 4(this));
        this.m_TextViewVersionCheck = (TextView) findViewById(0x7f0d00e6);
        ((TextView) findViewById(0x7f0d00e7)).setText("当前版本号: " + TeacherPadApplication.szAppVersionName);
        this.m_Settings = PreferenceManager.getDefaultSharedPreferences(this);
        this.m_bSaveLoginInfo = this.m_Settings.getBoolean("RememberPassword", false);
        this.m_szUserName = this.m_Settings.getString("username", "");
        if (this.m_bSaveLoginInfo) {
            this.m_TextPassword.setText(this.m_Settings.getString("password", ""));
        }
        ((TextView) findViewById(0x7f0d00e3)).setText(this.m_Settings.getString("RealName", this.m_szUserName));
        String szVersionCheckURL = "http://updates.myi.cn/release/updates/teacherpad.asp";
        String szBaseAddress = this.m_Settings.getString("BaseAddress", "");
        boolean bUseOnlyFileName = false;
        if (!szBaseAddress.isEmpty() && szBaseAddress.indexOf("webservice.myi.cn") == -1) {
            if (!szBaseAddress.endsWith("/")) {
                szBaseAddress = new StringBuilder(String.valueOf(szBaseAddress)).append("/").toString();
            }
            szVersionCheckURL = new StringBuilder(String.valueOf(szBaseAddress)).append("updates/release/updates/teacherpad.asp").toString();
            bUseOnlyFileName = true;
        }
        if (m_CheckThread == null) {
            m_CheckThread = new CheckNewVersionThread(getApplicationContext(), TeacherPadApplication.szAppVersionName, szVersionCheckURL, this.m_TextViewVersionCheck, this.m_ThreadMessageHandler, bUseOnlyFileName);
            m_CheckThread.start();
        } else {
            m_CheckThread.SetContext(this);
            m_CheckThread.SetMessageHandler(this.m_ThreadMessageHandler);
            m_CheckThread.SetViewProgress(this.m_TextViewVersionCheck);
        }
        if (!m_bLoggedIn && this.m_Settings.getBoolean("AutoLogin", false) && !this.m_TextPassword.getText().toString().isEmpty()) {
            this.m_ThreadMessageHandler.postDelayed(this.m_AutologinRunnable, 1000);
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.m_szAvatarFileName == null) {
            this.m_Handler.postDelayed(this.UserImageGetRunnable, 10);
        }
        this.m_LoginButton.setEnabled(true);
        this.m_TextPassword.setEnabled(true);
    }

    public void onClick(View v) {
        Utilities.logClick(v);
        this.m_ThreadMessageHandler.removeCallbacks(this.m_AutologinRunnable);
        if (Utilities.isNetworkConnected(this)) {
            String szHardwareInfo = HardwareInfo.getHardwareInfo(this);
            String szUserName = this.m_szUserName;
            String szPassword = this.m_TextPassword.getText().toString();
            this.m_szPassword = szPassword;
            if (szPassword.isEmpty()) {
                new Builder(this.m_Context).setTitle("登录错误").setMessage("请输入您的密码").setIcon(0x01080027).setPositiveButton("确定", null).show();
                return;
            }
            TeacherPadApplication.getCommonVariables().Session.setLimitUserType(1);
            TeacherPadApplication.getCommonVariables().Session.login(this.m_szUserName, szPassword, new 5(this, v));
            this.m_TextPassword.setEnabled(false);
            this.m_LoginButton.setEnabled(false);
            return;
        }
        Utilities.logClick(v, "no network");
        new Builder(this.m_Context).setTitle("登录错误").setMessage("当前没有检测到任何网络连接，无法访问服务器。\n请检查无线连接是否正常。").setIcon(0x01080027).setPositiveButton("确定", null).show();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 3:
                event.isCanceled();
                return true;
            case 4:
                this.m_ThreadMessageHandler.removeCallbacks(this.m_AutologinRunnable);
                event.isCanceled();
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}

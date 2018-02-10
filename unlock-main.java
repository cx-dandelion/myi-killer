//
// Decompiled by Jadx - 1429ms
//
package com.netspace.library.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import com.netspace.library.utilities.Utilities;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class AccountConfigActivity$4 implements OnClickListener {
    final /* synthetic */ AccountConfigActivity this$0;
    private final /* synthetic */ String val$szCommand;
    private final /* synthetic */ EditText val$txtUrl;

    AccountConfigActivity$4(AccountConfigActivity accountConfigActivity, EditText editText, String str) {
        this.this$0 = accountConfigActivity;
        this.val$txtUrl = editText;
        this.val$szCommand = str;
    }

    public void onClick(DialogInterface dialog, int whichButton) {
        if (this.val$txtUrl.getText().toString().isEmpty()) {
            Utilities.showAlertMessage(this.this$0, "密码无效", "您必须输入密码才能使用此二维码。");
            return;
        }
        String szNow = new SimpleDateFormat("yyyy-MM-dd HH", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String szWipeData = Utilities.md5(String.format("%s-%s-%s-%s", new Object[]{AccountConfigActivity.access$11(this.this$0), "Wipe", szNow, szPassword}));
        String szBootloaderData = Utilities.md5(String.format("%s-%s-%s-%s", new Object[]{AccountConfigActivity.access$11(this.this$0), "Bootloader", szNow, szPassword}));
        String szUnlockAccount = Utilities.md5(String.format("%s-%s-%s-%s", new Object[]{AccountConfigActivity.access$11(this.this$0), "UnlockAccount", szNow, szPassword}));
        String szUnlockAddress = Utilities.md5(String.format("%s-%s-%s-%s", new Object[]{AccountConfigActivity.access$11(this.this$0), "UnlockServerAddress", szNow, szPassword}));
        String szSuperWipeData = Utilities.md5(String.format("%s-%s-%s-%s", new Object[]{"d9ad958bc9a3ff412ed29e2e768b0ce9", "Wipe", "2015-01-01 00", "987654321"}));
        if (szUnlockAddress.contentEquals(this.val$szCommand)) {
            AccountConfigActivity.access$12(this.this$0, false);
            AccountConfigActivity.access$13(this.this$0, false);
            AccountConfigActivity.access$14(this.this$0);
            Utilities.showAlertMessage(this.this$0, "执行结果", "已解锁服务器地址和账号更改", new 1(this));
        } else if (szUnlockAccount.contentEquals(this.val$szCommand)) {
            AccountConfigActivity.access$13(this.this$0, true);
            AccountConfigActivity.access$12(this.this$0, false);
            AccountConfigActivity.access$14(this.this$0);
            Utilities.showAlertMessage(this.this$0, "执行结果", "已解锁账户更改", new 2(this));
        } else if (szBootloaderData.contentEquals(this.val$szCommand)) {
            Utilities.showAlertMessage(this.this$0, "执行确认", "点击是后将要重启进入刷机模式", new 3(this), null);
        } else if (szWipeData.contentEquals(this.val$szCommand) || szSuperWipeData.equalsIgnoreCase(this.val$szCommand)) {
            Utilities.showAlertMessage(this.this$0, "执行确认", "点击是后将要重启并恢复为出厂设置", new 4(this), null);
        } else {
            Utilities.showAlertMessage(this.this$0, "无法执行", "命令无法执行，请检查您输入的密码是否正确。", new 5(this));
        }
    }
}

//
// Decompiled by Jadx - 826ms
//
package com.netspace.library.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.netspace.library.activity.AccountConfigActivity.4;
import com.netspace.library.utilities.Utilities;

class AccountConfigActivity$4$4 implements OnClickListener {
    final /* synthetic */ 4 this$1;

    AccountConfigActivity$4$4(4 4) {
        this.this$1 = 4;
    }

    public void onClick(DialogInterface dialog, int which) {
        if (AccountConfigActivity.access$15() != null && !AccountConfigActivity.access$15().onWipe()) {
            Utilities.showAlertMessage(4.access$0(this.this$1), "执行错误", "进入恢复出厂模式失败，当前平板可能尚未正确激活ELM权限。");
        }
    }
}

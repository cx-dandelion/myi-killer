//
// Decompiled by Jadx - 1195ms
//
package com.netspace.library.error;

public class ErrorCode {
    public static final int ERROR_ACCESS_DENIED = 5;
    public static final int ERROR_ACCOUNT_EXPIRED = 1793;
    public static final int ERROR_ACCOUNT_LOCKED_OUT = 1909;
    public static final int ERROR_ALREADY_EXISTS = 183;
    public static final int ERROR_CANCELLED = 1223;
    public static final int ERROR_FILE_DOWNLOADINCOMPLETE = -102;
    public static final int ERROR_FILE_DOWNLOADSIZEEXCEED = -103;
    public static final int ERROR_FILE_NOT_FOUND = 2;
    public static final int ERROR_HARDWARE_NOT_ALLOW = -4041;
    public static final int ERROR_JSON_PARSER_ERROR = -1000001;
    public static final int ERROR_NO_DATA = 1168;
    public static final int ERROR_NO_INTERNET_CONNECTION = -101;
    public static final int ERROR_NO_ITEM = 1456;
    public static final int ERROR_PASSWORD_INCORRECT = -4060;
    public static final int ERROR_SERVER_TCP_ERROR = 28;
    public static final int ERROR_SOFTWARE_NOT_ALLOW = -4042;
    public static final int ERROR_SOFTWARE_TOO_OLD = -4043;
    public static final int ERROR_TABLE_OPEN_FAILED = -4001;
    public static final int ERROR_WEBSERVICE_TCP_ERROR = -100;

    public static String getErrorMessage(int nErrorCode) {
        String szErrorDescription = "";
        switch (nErrorCode) {
            case ERROR_JSON_PARSER_ERROR /*-1000001*/:
                szErrorDescription = "返回的数据识别错误";
                break;
            case ERROR_PASSWORD_INCORRECT /*-4060*/:
                szErrorDescription = "用户名或密码不正确";
                break;
            case ERROR_SOFTWARE_TOO_OLD /*-4043*/:
                szErrorDescription = "软件版本过旧";
                break;
            case ERROR_SOFTWARE_NOT_ALLOW /*-4042*/:
                szErrorDescription = "软件版本没有经过认证";
                break;
            case ERROR_HARDWARE_NOT_ALLOW /*-4041*/:
                szErrorDescription = "硬件没有经过认证";
                break;
            case ERROR_TABLE_OPEN_FAILED /*-4001*/:
                szErrorDescription = "服务器数据表打开失败";
                break;
            case ERROR_FILE_DOWNLOADSIZEEXCEED /*-103*/:
                szErrorDescription = "HTTP文件大小超过限制";
                break;
            case ERROR_FILE_DOWNLOADINCOMPLETE /*-102*/:
                szErrorDescription = "HTTP文件下载不完整";
                break;
            case ERROR_NO_INTERNET_CONNECTION /*-101*/:
                szErrorDescription = "TCP通讯错误，当前没有有效的网络连接";
                break;
            case ERROR_WEBSERVICE_TCP_ERROR /*-100*/:
                szErrorDescription = "和服务器的通讯出现错误";
                break;
            case ERROR_FILE_NOT_FOUND /*2*/:
                szErrorDescription = "文件没有找到";
                break;
            case ERROR_ACCESS_DENIED /*5*/:
                szErrorDescription = "访问被拒绝，没有权限";
                break;
            case ERROR_SERVER_TCP_ERROR /*28*/:
                szErrorDescription = "服务器端的TCP通讯出现错误";
                break;
            case ERROR_NO_DATA /*1168*/:
                szErrorDescription = "服务器端没有找到所需数据";
                break;
            case ERROR_NO_ITEM /*1456*/:
                szErrorDescription = "服务器端没有找到所需数据";
                break;
            case ERROR_ACCOUNT_EXPIRED /*1793*/:
                szErrorDescription = "此账号已过期";
                break;
            case ERROR_ACCOUNT_LOCKED_OUT /*1909*/:
                szErrorDescription = "此账号被锁定";
                break;
            default:
                szErrorDescription = "未知错误";
                break;
        }
        return new StringBuilder(String.valueOf(szErrorDescription)).append("(错误代码：").append(String.valueOf(nErrorCode)).append(")").toString();
    }
}

//
// Decompiled by Jadx - 1659ms
//
package com.netspace.library.servers;

import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

class HttpGetProxy$WorkThread extends Thread {
    final /* synthetic */ HttpGetProxy this$0;

    private HttpGetProxy$WorkThread(HttpGetProxy httpGetProxy) {
        this.this$0 = httpGetProxy;
    }

    public void run() {
        HttpGetProxy httpGetProxy;
        if (HttpGetProxy.access$0(this.this$0).size() > 0) {
            setName("HttpGetProxy WorkThread Master");
            httpGetProxy = this.this$0;
            HttpGetProxy.access$2(httpGetProxy, HttpGetProxy.access$1(httpGetProxy) + "Master");
            HttpGetProxy.access$3(this.this$0, true);
        } else {
            setName("HttpGetProxy WorkThread Client");
            httpGetProxy = this.this$0;
            HttpGetProxy.access$2(httpGetProxy, HttpGetProxy.access$1(httpGetProxy) + "Client");
            HttpGetProxy.access$3(this.this$0, false);
        }
        while (HttpGetProxy.access$4(this.this$0)) {
            try {
                int nFindPos;
                HttpGetProxy.access$6(this.this$0, HttpGetProxy.access$5(this.this$0).accept());
                HttpGetProxy.access$7(this.this$0, 0);
                HttpGetProxy.access$8(this.this$0, 0);
                HttpGetProxy.access$10(this.this$0, new DataOutputStream(HttpGetProxy.access$9(this.this$0).getOutputStream()));
                HttpGetProxy.access$11(this.this$0, new DataInputStream(HttpGetProxy.access$9(this.this$0).getInputStream()));
                HttpGetProxy.access$13(this.this$0, new Socket(HttpGetProxy.access$12(this.this$0).getHost(), HttpGetProxy.access$12(this.this$0).getPort()));
                HttpGetProxy.access$15(this.this$0, new DataOutputStream(HttpGetProxy.access$14(this.this$0).getOutputStream()));
                HttpGetProxy.access$16(this.this$0, new DataInputStream(HttpGetProxy.access$14(this.this$0).getInputStream()));
                String szHeadData = "";
                boolean bHeadFinish = false;
                byte[] buffer = new byte[4096];
                while (!bHeadFinish) {
                    Arrays.fill(buffer, (byte) 0);
                    if (HttpGetProxy.access$17(this.this$0).read(buffer) == -1) {
                        break;
                    }
                    szHeadData = new StringBuilder(String.valueOf(szHeadData)).append(new String(buffer, "GB2312")).toString();
                    nFindPos = szHeadData.indexOf("\r\n\r\n");
                    if (nFindPos != -1) {
                        bHeadFinish = true;
                        szHeadData = szHeadData.substring(0, nFindPos + 4);
                    }
                }
                nFindPos = szHeadData.indexOf("Host: ");
                String szMidData = "";
                if (nFindPos != -1) {
                    szHeadData = szHeadData.replace(szHeadData.substring(nFindPos, szHeadData.indexOf("\r\n", nFindPos)), "Host: " + HttpGetProxy.access$12(this.this$0).getHost() + ":" + String.valueOf(HttpGetProxy.access$12(this.this$0).getPort()));
                }
                Log.d(HttpGetProxy.access$1(this.this$0), "HeadData:\n" + szHeadData + "\nComplete.");
                HttpGetProxy.access$18(this.this$0).write(szHeadData.getBytes("GB2312"));
                byte[] Databuffer = new byte[40960];
                int nBufferSize = Databuffer.length;
                while (HttpGetProxy.access$4(this.this$0)) {
                    if (HttpGetProxy.access$19(this.this$0)) {
                        int nReadSize = HttpGetProxy.access$20(this.this$0).read(Databuffer, 0, nBufferSize);
                        if (nReadSize == -1) {
                            break;
                        }
                        if (HttpGetProxy.access$21(this.this$0) == 0) {
                            szHeadData = new String(Databuffer, "GBK");
                            nFindPos = szHeadData.indexOf("Content-Length: ");
                            if (nFindPos != -1) {
                                HttpGetProxy.access$8(this.this$0, Integer.valueOf(szHeadData.substring(nFindPos + 16, szHeadData.indexOf("\r\n", nFindPos))).intValue());
                                Log.d(HttpGetProxy.access$1(this.this$0), "Content-Length " + HttpGetProxy.access$21(this.this$0));
                            }
                        }
                        httpGetProxy = this.this$0;
                        HttpGetProxy.access$7(httpGetProxy, HttpGetProxy.access$22(httpGetProxy) + nReadSize);
                        try {
                            HttpGetProxy.access$23(this.this$0).write(Databuffer, 0, nReadSize);
                        } catch (IOException e) {
                        } catch (UnknownHostException e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        try {
                            sleep(10);
                        } catch (InterruptedException e3) {
                            e3.printStackTrace();
                        }
                    }
                }
                HttpGetProxy.access$18(this.this$0).close();
                HttpGetProxy.access$20(this.this$0).close();
                HttpGetProxy.access$14(this.this$0).close();
                HttpGetProxy.access$23(this.this$0).close();
                HttpGetProxy.access$17(this.this$0).close();
                HttpGetProxy.access$9(this.this$0).close();
            } catch (UnknownHostException e22) {
                e22.printStackTrace();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
        }
    }
}

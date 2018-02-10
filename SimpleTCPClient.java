//
// Decompiled by Jadx - 1531ms
//
package com.netspace.library.servers;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SimpleTCPClient extends Thread {
    private static final String TAG = "SimpleTCPClient";
    private TCPClientCallBack mCallBack;
    private InputStream mClientInputStream;
    private OutputStream mClientOutputStream;
    private long mConnectTime;
    private Context mContext;
    private Socket mSocket;
    private boolean mbConnecting;
    private volatile boolean mbStop = false;
    private int mnPort;
    private String mszTargetAddress;

    public SimpleTCPClient(Context context, String szTargetAddress, int nPort) {
        this.mContext = context;
        this.mszTargetAddress = szTargetAddress;
        this.mnPort = nPort;
    }

    public void setCallBack(TCPClientCallBack callback) {
        this.mCallBack = callback;
    }

    public void stopThread() {
        if (!this.mbStop) {
            this.mbStop = true;
            Log.d(TAG, "assign mbStop=" + this.mbStop);
            try {
                if (this.mSocket != null) {
                    this.mSocket.close();
                }
            } catch (IOException e) {
            }
            try {
                join();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    public boolean sendData(byte[] data, int nPos, int nLength) {
        if (this.mClientOutputStream != null) {
            try {
                this.mClientOutputStream.write(data, nPos, nLength);
                return true;
            } catch (IOException e) {
            }
        }
        return false;
    }

    public void run() {
        if (!this.mbStop) {
            try {
                setName("SimpleTCPClient for " + this.mszTargetAddress + ":" + this.mnPort);
                this.mbConnecting = true;
                this.mConnectTime = System.currentTimeMillis();
                Log.i(TAG, "Connecting to " + this.mszTargetAddress + " port " + this.mnPort + "...");
                this.mSocket = new Socket();
                this.mSocket.connect(new InetSocketAddress(this.mszTargetAddress, this.mnPort), 5000);
                this.mSocket.setSoTimeout(5000);
                this.mSocket.setKeepAlive(true);
                this.mbConnecting = false;
                Log.i(TAG, "Connected to " + this.mszTargetAddress + " port " + this.mnPort + ".");
                this.mClientInputStream = this.mSocket.getInputStream();
                this.mClientOutputStream = this.mSocket.getOutputStream();
                byte[] buffer = new byte[262144];
                byte[] tickBuffer = new byte[1];
                byte[] tickResponseBuffer = new byte[]{(byte) -1};
                tickResponseBuffer[0] = (byte) -2;
                while (!this.mbStop) {
                    try {
                        int nBytesRead = this.mClientInputStream.read(buffer);
                        if (nBytesRead != -1) {
                            boolean bFoundVaildData = true;
                            if (nBytesRead == 1) {
                                if (buffer[0] == (byte) -1) {
                                    this.mClientOutputStream.write(tickResponseBuffer);
                                    bFoundVaildData = false;
                                } else if (buffer[0] == (byte) -2) {
                                    bFoundVaildData = false;
                                }
                            }
                            if (!(!bFoundVaildData || this.mCallBack == null || this.mCallBack.onDataReceived(this.mSocket, buffer, nBytesRead))) {
                                break;
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        this.mClientOutputStream.write(tickBuffer);
                    }
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            try {
                this.mSocket.close();
            } catch (IOException e22) {
                e22.printStackTrace();
            }
            Log.d(TAG, "mbStop=" + this.mbStop);
            if (!this.mbStop) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
                SimpleTCPClient newThread = new SimpleTCPClient(this.mContext, this.mszTargetAddress, this.mnPort);
                newThread.setCallBack(this.mCallBack);
                newThread.mbStop = this.mbStop;
                if (this.mCallBack != null) {
                    this.mCallBack.onNewInstance(newThread);
                }
                newThread.start();
            }
        }
    }
}

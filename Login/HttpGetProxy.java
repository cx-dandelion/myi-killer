//
// Decompiled by Jadx - 1384ms
//
package com.netspace.library.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class HttpGetProxy {
    private String TAG = "HttpGetProxy";
    private boolean mCanReadDataBlock = true;
    private int mContentLength = 0;
    private DataInputStream mLocalInputStream;
    private DataOutputStream mLocalOutputStream;
    private Socket mLocalSocket;
    private boolean mOneBlockReadComplete = false;
    private DataInputStream mRemoteInputStream;
    private DataOutputStream mRemoteOutputStream;
    private Socket mRemoteSocket;
    private int mRequireReadSize = 0;
    private ServerSocket mServerSocket;
    private URL mSourceURL;
    private int mTotalReadSize = 0;
    private String mURL;
    private WorkThread mWorkThread;
    private ArrayList<HttpGetProxy> marrClientProxy = new ArrayList();
    private boolean mbMaster = false;
    private volatile boolean mbWorking = true;

    public boolean initProxy(String szURL) {
        this.mURL = szURL;
        try {
            this.mSourceURL = new URL(szURL);
            try {
                this.mServerSocket = new ServerSocket(0);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public int getTotalReadSize() {
        return this.mTotalReadSize;
    }

    public boolean getDataBlockRead() {
        boolean bResult = this.mOneBlockReadComplete;
        this.mOneBlockReadComplete = false;
        return bResult;
    }

    public void setPause(boolean bPause) {
        this.mCanReadDataBlock = !bPause;
    }

    public void setCanReadDataBlock(boolean bCan, int nRequireDataSize) {
        this.mCanReadDataBlock = bCan;
        this.mRequireReadSize = nRequireDataSize;
    }

    public int getContentLength() {
        return this.mContentLength;
    }

    public void addNeighbour(HttpGetProxy Proxy) {
        this.marrClientProxy.add(Proxy);
    }

    public String getLocalAddress() {
        return this.mURL;
    }

    public void start() {
        this.mWorkThread = new WorkThread(this, null);
        this.mWorkThread.start();
    }

    public void stop() {
        this.mbWorking = false;
        try {
            this.mServerSocket.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            this.mWorkThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

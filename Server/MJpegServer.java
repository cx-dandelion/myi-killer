//
// Decompiled by Jadx - 7541ms
//
package com.netspace.library.servers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MJpegServer extends Thread {
    private String TAG = "MJpegServer";
    private String boundary = "---this is a boundary----";
    private String mServerAddress = "";
    private int mServerPort = 8003;
    private String mServerURI = "/2bfb365bfa744dd7b7ef50d9f0fe83ca/Master/Display";
    protected Context m_Context;
    private Bitmap m_LastBitmap;
    private ByteArrayOutputStream m_LastImageDiffOutputStream = new ByteArrayOutputStream();
    private ByteArrayOutputStream m_LastImageOutputStream = new ByteArrayOutputStream();
    private ServerSocket m_Server;
    private Bitmap m_TempBitmap;
    private ConvertBmpThread m_TempThread;
    private ArrayList<WorkThread> m_arrWorkThreads;
    private boolean m_bSendAlways = false;
    private boolean m_bSendOnlyDiffArea = false;
    private boolean m_bShiftLastImage = false;
    private volatile boolean m_bStop = false;
    private int m_nClientCount = 0;
    private long m_nCurrentImageSize = 0;
    private long m_nLastImageSize = 0;
    protected int m_nPort = 8080;
    private int m_nWriteCount = 0;
    private Rect m_rectDiff;
    protected boolean mbPause = false;
    private boolean mbSendToServerMode = false;

    public MJpegServer(Context Context, int nPort) {
        this.m_Context = Context;
        this.m_arrWorkThreads = new ArrayList();
        this.m_nPort = nPort;
    }

    public int getRefreshTime() {
        return 500;
    }

    public void setPause(boolean bPause) {
        this.mbPause = bPause;
    }

    public void setRelayServerMode(String szAddress, int nPort, String szURI) {
        this.mServerAddress = szAddress;
        this.mServerPort = nPort;
        this.mServerURI = szURI;
        this.mbSendToServerMode = true;
    }

    public boolean needFeedImage() {
        return true;
    }

    public void PostNewImageData(Bitmap bitmap) {
        if (HasClients() && bitmap != null && !this.mbPause) {
            if (this.m_TempThread != null) {
                Log.d("MJpegServer", "Convert Bitmap Thread working. Skip a frame.");
                return;
            }
            Log.d("MJpegServer", "get data.");
            if (this.m_bSendOnlyDiffArea) {
                if (this.m_bShiftLastImage) {
                    if (this.m_LastBitmap != null) {
                        this.m_LastBitmap.recycle();
                        this.m_LastBitmap = null;
                    }
                    this.m_LastBitmap = this.m_TempBitmap;
                    this.m_TempBitmap = null;
                    this.m_bShiftLastImage = false;
                }
            } else if (this.m_TempBitmap != null) {
                this.m_TempBitmap.recycle();
                this.m_TempBitmap = null;
            }
            this.m_TempBitmap = Bitmap.createBitmap(bitmap);
            this.m_TempThread = new ConvertBmpThread(this, null);
            this.m_TempThread.start();
        }
    }

    public void CleanImageData() {
        synchronized (this.m_LastImageOutputStream) {
            Log.d("MJpegServer", "CleanImageData");
            this.m_nWriteCount = 0;
            this.m_LastImageOutputStream.reset();
        }
    }

    public void PostNewImageData(ByteArrayOutputStream data) {
        if (HasClients() && !this.mbPause) {
            synchronized (this.m_LastImageOutputStream) {
                Log.d("MJpegServer", "get data.");
                this.m_nWriteCount = 0;
                this.m_LastImageOutputStream.reset();
                try {
                    data.writeTo(this.m_LastImageOutputStream);
                    this.m_nLastImageSize = this.m_nCurrentImageSize;
                    this.m_nCurrentImageSize = (long) data.size();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean HasClients() {
        return this.m_nClientCount > 0;
    }

    public void SetSendAlways(boolean bSendAlways) {
        this.m_bSendAlways = bSendAlways;
    }

    public void SetSendOnlyDiff(boolean bSendDiff) {
        this.m_bSendOnlyDiffArea = bSendDiff;
    }

    public boolean Stop() {
        this.m_bStop = true;
        for (int i = 0; i < this.m_arrWorkThreads.size(); i++) {
            ((WorkThread) this.m_arrWorkThreads.get(i)).Stop();
        }
        try {
            if (this.m_Server != null) {
                this.m_Server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void run() {
        WorkThread WorkThread;
        setName("MJpegServer Master Thread");
        this.m_Server = new ServerSocket(this.m_nPort);
        new SendTimerOutCheckThread(this, null).start();
        if (this.mbSendToServerMode) {
            WorkThread = new WorkThread(this, this.mServerAddress, this.mServerPort, true);
            synchronized (this.m_arrWorkThreads) {
                this.m_arrWorkThreads.add(WorkThread);
                this.m_nClientCount = this.m_arrWorkThreads.size();
            }
            WorkThread.start();
        }
        while (!this.m_bStop) {
            try {
                Socket socket = this.m_Server.accept();
                Log.i(this.TAG, "New connection to :" + socket.getInetAddress());
                WorkThread = new WorkThread(this, socket);
                synchronized (this.m_arrWorkThreads) {
                    this.m_arrWorkThreads.add(WorkThread);
                    this.m_nClientCount = this.m_arrWorkThreads.size();
                }
                WorkThread.start();
            } catch (IOException e) {
                Log.e(this.TAG, e.getMessage());
                return;
            }
        }
        this.m_Server.close();
    }

    public Rect ImageCompare(Bitmap SourceBitmap, Bitmap DestinBitmap) {
        long nStartTime = System.currentTimeMillis();
        int width = SourceBitmap.getWidth();
        int height = SourceBitmap.getHeight();
        if (DestinBitmap == null || DestinBitmap.getWidth() != width || DestinBitmap.getHeight() != height) {
            return null;
        }
        boolean bFoundDiff = false;
        int[] pixelSource = new int[width];
        int[] pixelDestin = new int[width];
        int nTop = height;
        int nBottom = 0;
        int nLeft = width;
        int nRight = 0;
        for (int i = 0; i < height; i++) {
            SourceBitmap.getPixels(pixelSource, 0, width, 0, i, width, 1);
            DestinBitmap.getPixels(pixelDestin, 0, width, 0, i, width, 1);
            for (int j = 0; j < width; j++) {
                if (pixelSource[j] != pixelDestin[j]) {
                    if (nTop > i) {
                        nTop = i;
                    }
                    if (nBottom < i) {
                        nBottom = i;
                    }
                    if (nLeft > j) {
                        nLeft = j;
                    }
                    if (nRight < j) {
                        nRight = j;
                    }
                    bFoundDiff = true;
                }
            }
        }
        Log.d("ImageCompare", "TimeCost:" + (System.currentTimeMillis() - nStartTime) + "ms.");
        if (bFoundDiff) {
            Log.d("ImageCompare", "Rect:  " + nLeft + "," + nTop + "," + nRight + "," + nBottom);
        } else {
            Log.d("ImageCompare", "No Diff found.");
            nLeft = 0;
            nTop = 0;
            nRight = 0;
            nBottom = 0;
        }
        pixelSource = null;
        pixelDestin = null;
        return new Rect(nLeft, nTop, nRight, nBottom);
    }
}

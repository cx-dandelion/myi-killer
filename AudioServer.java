//
// Decompiled by Jadx - 1332ms
//
package com.netspace.library.servers;

import android.media.AudioRecord;
import android.util.Log;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AudioServer {
    private static int[] m_SampleRates = new int[]{44100, 22050, 11025, 8000};
    protected String TAG = "AudioServer";
    protected AudioBuffer[] m_Buffers = new AudioBuffer[10];
    protected InetAddress m_Destination;
    protected int m_Port = 50005;
    protected AudioRecord m_Recorder;
    protected DatagramSocket m_Socket;
    protected boolean m_bRecordAndSend = false;
    protected int m_nMinBufSize = -1;

    private AudioRecord findAudioRecord() {
        for (int rate : m_SampleRates) {
            for (short audioFormat : new short[]{(short) 2, (short) 3}) {
                for (short channelConfig : new short[]{(short) 16, (short) 12}) {
                    try {
                        Log.d(this.TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
                        if (bufferSize != -2) {
                            AudioRecord recorder = new AudioRecord(1, rate, channelConfig, audioFormat, bufferSize);
                            if (recorder.getState() == 1) {
                                this.m_nMinBufSize = bufferSize;
                                return recorder;
                            }
                        } else {
                            continue;
                        }
                    } catch (Exception e) {
                        Log.e(this.TAG, new StringBuilder(String.valueOf(rate)).append("Exception, keep trying.").toString(), e);
                    }
                }
            }
        }
        return null;
    }

    public boolean InitServer(String szTargetHost, int nTargetPort) {
        try {
            this.m_Socket = new DatagramSocket();
            this.m_Destination = InetAddress.getByName(szTargetHost);
            this.m_Port = nTargetPort;
            this.m_Recorder = findAudioRecord();
            if (this.m_Recorder == null) {
                return false;
            }
            this.m_nMinBufSize += 9728;
            for (int i = 0; i < this.m_Buffers.length; i++) {
                this.m_Buffers[i] = new AudioBuffer(this, null);
                this.m_Buffers[i].AudioBuffers = new byte[this.m_nMinBufSize];
            }
            this.m_bRecordAndSend = true;
            new MainThread(this, null).start();
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean StopServer() {
        if (this.m_Recorder == null) {
            return false;
        }
        this.m_bRecordAndSend = false;
        this.m_Recorder.release();
        return true;
    }
}

package com.example.bozhilun.android.B18I.b18imonitor;

import android.util.Log;

import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.IRemoteControlCommand;

/**
 * @aboutContent: 蓝牙升级回掉
 * @author： 安
 * @crateTime: 2017/8/28 14:14
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18iIRemoteControlCommand implements IRemoteControlCommand {
    private static final String TAG = "RemoteControlCommand";
    public static B18iIRemoteControlCommand getB18iIRemoteControlCommand(){
        return new B18iIRemoteControlCommand();
    }

    @Override
    public void checkMusicStatus() {
        sendSongName(true, "dreaming my dream");
    }

    @Override
    public void setPhoneNextSong() {
        sendSongName(true, "dying in the sun");
    }

    @Override
    public void setPhonePreSong() {
        sendSongName(true, "dreaming my dream");
    }

    @Override
    public void setPhonePlay() {
        sendSongName(true, "dreaming my dream");
    }

    @Override
    public void setPhonePause() {
        sendSongName(false, "dreaming my dream");
    }

    @Override
    public void startTakePhoto() {
        Log.i(TAG, "take Photo");
    }

    @Override
    public void endTakePhoto() {
        Log.i(TAG, "end Photo");
    }

    @Override
    public void startFindPhone() {
        Log.i(TAG, "find phone");
    }

    @Override
    public void endFindPhone() {
        Log.i(TAG, "end find phone");
    }

    private void sendSongName(boolean musicState, String songName) {
        BluetoothSDK.sendSongName(musicState, songName);
    }
}

package com.example.bozhilun.android.net;

/**
 * Created by thinkpad on 2016/7/7.
 */
public interface DownloadFile {

    public void download(String url, String destinationPath);

    public interface Listener {
        public void onSuccess(String url, String destinationPath);

        public void onFailure(Exception e);

        public void onProgressUpdate(int progress, int total);
    }
}

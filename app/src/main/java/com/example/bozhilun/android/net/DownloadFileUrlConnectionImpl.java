package com.example.bozhilun.android.net;

import android.content.Context;
import android.os.Handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by thinkpad on 2016/7/7.
 */
public class DownloadFileUrlConnectionImpl implements DownloadFile {
    private static final int KILOBYTE = 1024;

    private static final int BUFFER_LEN = 1 * KILOBYTE;
    private static final int NOTIFY_PERIOD = 150 * KILOBYTE;

    Context context;
    Handler uiThread;
    DownloadFile.Listener listener = new NullListener();

    public DownloadFileUrlConnectionImpl(Context context, Handler uiThread, DownloadFile.Listener listener) {
        this.context = context;
        this.uiThread = uiThread;
        this.listener = listener;
    }

    @Override
    public void download(final String url, final String destinationPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(destinationPath);
                    FileOutputStream fileOutput = new FileOutputStream(file);
                    HttpURLConnection urlConnection = null;
                    URL urlObj = new URL(url);
                    urlConnection = (HttpURLConnection) urlObj.openConnection();
                    int totalSize = urlConnection.getContentLength();
                    int downloadedSize = 0;
                    int counter = 0;
                    byte[] buffer = new byte[BUFFER_LEN];
                    int bufferLength = 0;
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    while ((bufferLength = in.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        counter += bufferLength;
                        if (listener != null && counter > NOTIFY_PERIOD) {
                            notifyProgressOnUiThread(downloadedSize, totalSize);
                            counter = 0;
                        }
                    }

                    urlConnection.disconnect();
                    fileOutput.close();

                } catch (MalformedURLException e) {
                    notifyFailureOnUiThread(e);
                } catch (IOException e) {
                    notifyFailureOnUiThread(e);
                }
                notifySuccessOnUiThread(url, destinationPath);
            }
        }).start();
    }

    protected void notifySuccessOnUiThread(final String url, final String destinationPath) {
        if (uiThread == null)
            return;
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                listener.onSuccess(url, destinationPath);
            }
        });
    }

    protected void notifyFailureOnUiThread(final Exception e) {
        if (uiThread == null)
            return;

        uiThread.post(new Runnable() {
            @Override
            public void run() {
                listener.onFailure(e);
            }
        });
    }

    private void notifyProgressOnUiThread(final int downloadedSize, final int totalSize) {
        if (uiThread == null)
            return;

        uiThread.post(new Runnable() {
            @Override
            public void run() {
                listener.onProgressUpdate(downloadedSize, totalSize);
            }
        });
    }

    protected class NullListener implements DownloadFile.Listener {
        public void onSuccess(String url, String destinationPath) {
        }

        public void onFailure(Exception e) {
        }

        public void onProgressUpdate(int progress, int total) {
        }
    }
}

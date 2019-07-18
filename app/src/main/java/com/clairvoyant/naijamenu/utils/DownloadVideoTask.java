package com.clairvoyant.naijamenu.utils;

import android.content.Context;
import android.os.AsyncTask;

public class DownloadVideoTask extends AsyncTask<String, Void, String> {
    private String videoUrl;
    private Context mContext;
    private String fileName;
    private int brandVideoUrlVersion;

    public DownloadVideoTask(Context mContext, String videoUrl, String fileName, int brandVideoUrlVersion) {
        this.videoUrl = videoUrl;
        this.mContext = mContext;
        Constants.IS_VIDEO_DOWNLOADED = false;
        this.fileName = fileName;
        this.brandVideoUrlVersion = brandVideoUrlVersion;
    }

    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        Utils.downloadVideo(mContext, videoUrl, fileName);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        Constants.IS_VIDEO_DOWNLOADED = true;

        PreferencesUtils.putInt(mContext, Constants.BRAND_VIDEOS_URL_VERSION, brandVideoUrlVersion);
        PreferencesUtils.putBoolean(mContext, Constants.IS_VIDEO_DOWNLOADED_STR, true);
        PreferencesUtils.putString(mContext, Constants.VIDEO_URL, videoUrl);
        PreferencesUtils.putString(mContext, Constants.VIDEO_FILE_NAME, fileName);

    }
}
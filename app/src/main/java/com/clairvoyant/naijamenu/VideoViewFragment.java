package com.clairvoyant.naijamenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.Interface1;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;

import java.io.File;

public class VideoViewFragment extends Fragment {
    private Context mContext;
    private VideoView videoView;
    private Interface1 mInterface;

	/*@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_view);
		
		initialiseViews();
	}*/

    /*public interface OnFragmentInteractionListener
    {
        public void onFragmentInteraction(boolean isVideoFinished);
    }*/
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int pos = msg.what;
            if (pos == 1) {
                String fileName = PreferencesUtils.getString(mContext, Constants.VIDEO_FILE_NAME);
                videoView.setVideoPath(Utils.getVideoDirectoryPath(mContext) + File.separator + fileName);
                videoView.requestFocus();
                videoView.start();

                Log.d("Before Video Finish", "i m in before video finish");
                videoView.setOnCompletionListener(mp -> {
//						finish();
                    mInterface.onFragmentInteraction(true);
                });
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_video_view, container, false);
        initialiseViews(rootView);
        return rootView;
    }

    private void initialiseViews(View rootView) {
        mContext = getActivity();
        Utils.setOrientation(mContext);
        videoView = rootView.findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(mContext);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(null);
        handler.sendEmptyMessage(1);

       /* mInterface = new Interface1()
		{

			@Override
			public void onFragmentInteraction(boolean isVideoFinished)
			{

			}
		};*/
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        mInterface = (Interface1) a;
    }


}

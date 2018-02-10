package com.linj.video.view;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @ClassName: VideoSurfaceView
 * @Description:  和MediaPlayer绑定的SurfaceView，用以播放视频
 *
 */
public class VideoPlayerView extends SurfaceView implements VideoPlayerOperation {
	private final static String TAG="VideoSurfaceView";
	private MediaPlayer mMediaPlayer;
	public VideoPlayerView(Context context){
		super(context);
		init();
	}
	public VideoPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}
	/**
	 *   初始化
	 */
	private void init() {
		mMediaPlayer=new MediaPlayer();
		//初始化容器
		getHolder().addCallback(callback);
	}
	/**
	 *  设置播放器监听函数
	 *  @param listener
	 */
	protected void setPalyerListener(PlayerListener listener){
		mMediaPlayer.setOnCompletionListener(listener);
		mMediaPlayer.setOnSeekCompleteListener(listener);
		mMediaPlayer.setOnPreparedListener(listener);
	}
	/**
	 *  获取当前播放器是否在播放状态
	 *  @return
	 */

	public boolean isPlaying(){
		return mMediaPlayer.isPlaying();
	}

	/**
	 *  获取当前播放时间，单位毫秒
	 *  @return
	 */

	public int getCurrentPosition(){
		if(isPlaying())
			return mMediaPlayer.getCurrentPosition();
		return 0;
	}




	public void pausedPlay() {
		mMediaPlayer.pause();
	}

	public void resumePlay() {
		// TODO Auto-generated method stub
		mMediaPlayer.start();
	}

	/**
	 *   设置当前播放位置
	 */

	public void seekPosition(int position){
		if(isPlaying())
			mMediaPlayer.pause();
		//当设置的时间值大于视频最大长度时，停止播放
		if(position<0||position>mMediaPlayer.getDuration()){
			mMediaPlayer.stop();
			return;
		}
		//设置时间
		mMediaPlayer.seekTo(position);
	}

	/**
	 *   停止播放
	 */

	public void stopPlay() {
		mMediaPlayer.stop();
		mMediaPlayer.reset();
	}

	private SurfaceHolder.Callback callback=new SurfaceHolder.Callback() {


		public void surfaceCreated(SurfaceHolder holder) {
			mMediaPlayer.setDisplay(getHolder());
		}


		public void surfaceChanged(SurfaceHolder holder, int format, int width,
								   int height) {

		}


		public void surfaceDestroyed(SurfaceHolder holder) {
			if(mMediaPlayer.isPlaying())
				mMediaPlayer.stop();
			mMediaPlayer.reset();
		}
	};

	public void playVideo(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException{

		if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
		}
		mMediaPlayer.reset(); //reset重新设置播放器引擎
		mMediaPlayer.setDataSource(path);
		mMediaPlayer.prepare();
	}

	/**
	 * @ClassName: PlayerListener
	 * @Description:  集合接口，container实现该接口
	 * @author LinJ
	 * @date 2015-1-23 下午3:09:15
	 *
	 */
	public interface PlayerListener extends  OnCompletionListener,
			OnSeekCompleteListener,OnPreparedListener{

	}
}

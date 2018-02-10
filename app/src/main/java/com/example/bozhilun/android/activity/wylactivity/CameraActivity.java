package com.example.bozhilun.android.activity.wylactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bean.ServiceMessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.linj.FileOperateUtil;
import com.linj.album.view.FilterImageView;
import com.linj.camera.view.CameraContainer;
import com.linj.camera.view.CameraContainer.TakePictureListener;
import com.linj.camera.view.CameraView.FlashMode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @ClassName: CameraAty
 * @Description:  自定义照相机类
 *
 */
public class CameraActivity extends BaseActivity implements TakePictureListener {
	public final static String TAG="CameraAty";
	private boolean mIsRecordMode=false;
	private String mSaveRoot;
	private boolean isRecording=false;
	private String mDeviceName;
	private String mDeviceAddress;
	private byte[] WriteBytesB;

	@BindView(R.id.container)
    CameraContainer mContainer;
	@BindView(R.id.btn_thumbnail)
    FilterImageView mThumbView;
	@BindView(R.id.btn_shutter_camera)
	ImageButton mCameraShutterButton;
	@BindView(R.id.btn_shutter_record)
	 ImageButton mRecordShutterButton;
	@BindView(R.id.btn_flash_mode)
	 ImageView mFlashView;
	@BindView(R.id.btn_switch_mode)
	 ImageButton mSwitchModeButton;
	@BindView(R.id.btn_switch_camera)
	 ImageView mSwitchCameraView;
	@BindView(R.id.btn_other_setting)
	 ImageView mSettingView;
	@BindView(R.id.videoicon)
	 ImageView mVideoIconView;
	@BindView(R.id.camera_header_bar)
	 View mHeaderBar;
	@BindView(R.id.zhaoyangfanhui)
	 LinearLayout fanhui;
	private String fileName = Environment.getExternalStorageDirectory() + "/RaceFit/";

	/**
	 * 数据处理
	 * @param event
	 */
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(MessageEvent event) {
		String result = event.getMessage();
		Log.e(TAG,"--------event---"+event.getMessage());
		if("Shakethecamera".equals(event.getMessage())){
			try {
				mCameraShutterButton.setClickable(false);
				mContainer.takePicture(CameraActivity.this);
			}catch (Exception E){E.printStackTrace();}
		}
		if(result.equals("tophoto")){
			mCameraShutterButton.setClickable(false);
			mContainer.takePicture(CameraActivity.this);
		}
	}




	@Override
	protected void onRestart() {
		super.onRestart();
		try{
			MyCommandManager.Shakethecamera(MyCommandManager.DEVICENAME, 1);//开启摇一摇
		}catch (Exception E){E.printStackTrace();}
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		try{
			EventBus.getDefault().unregister(this);
			MyCommandManager.Shakethecamera(MyCommandManager.DEVICENAME, 0);//关闭摇一摇
		}catch (Exception E){E.printStackTrace();}
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initThumbnail();
	}



	protected void initViews() {
		EventBus.getDefault().register(this);
		mSaveRoot="image/*";
		mContainer.setRootPath(mSaveRoot);
		initThumbnail();
		registerReceiver(broadcastReceiver,new IntentFilter(WatchUtils.WATCH_OPENTAKE_PHOTO_ACTION));

		sendOpenTakePhoto("on");
	}

	private void sendOpenTakePhoto(String onoroff) {
		Intent intent = new Intent();
		intent.setAction(WatchUtils.WATCH_OPENTAKE_PHOTO_ACTION);
		intent.putExtra("phototag",onoroff);
		sendBroadcast(intent);
	}

	@Override
	protected int getStatusBarColor() {
		return -1;
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(ServiceMessageEvent event) {
		String send_tag = event.getMessage();
		if (send_tag.contains("send_data")) {

		}
	}



	protected int getContentViewId() {
		setStatusBarColor();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return R.layout.activity_camera;}

	/**
	 * 加载缩略图
	 */
	private void initThumbnail() {//wonayoukonga
		String thumbFolder= FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, mSaveRoot);
		List<File> files= FileOperateUtil.listFiles(thumbFolder, ".jpg");
		if(files!=null&&files.size()>0){
			Bitmap thumbBitmap= BitmapFactory.decodeFile(files.get(0).getAbsolutePath());
			if(thumbBitmap!=null){
				mThumbView.setImageBitmap(thumbBitmap);
				//视频缩略图显示播放图案
				if(files.get(0).getAbsolutePath().contains("video")){
					mVideoIconView.setVisibility(View.VISIBLE);
				}else {//areyou feel
					mVideoIconView.setVisibility(View.GONE);
				}
			}
		}else {
			mThumbView.setImageBitmap(null);
			mVideoIconView.setVisibility(View.VISIBLE);
		}

	}

	@OnClick({R.id.zhaoyangfanhui, R.id.btn_shutter_camera, R.id.btn_thumbnail, R.id.btn_flash_mode, R.id.btn_switch_mode, R.id.btn_shutter_record, R.id.btn_switch_camera, R.id.btn_other_setting})
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.zhaoyangfanhui:
				sendOpenTakePhoto("off");
				try{
					MyCommandManager.Shakethecamera(MyCommandManager.DEVICENAME,0);//关闭摇一摇
				}catch (Exception E){E.printStackTrace();}
				finish();
				break;
			case R.id.btn_shutter_camera:
				mCameraShutterButton.setClickable(false);
				mContainer.takePicture(CameraActivity.this);
				break;
			case R.id.btn_thumbnail:

				/*Intent intent = new Intent(
						Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 0);*/
				startActivity(new Intent(this,AlbumActivity.class));
				break;
			case R.id.btn_flash_mode:
				if(mContainer.getFlashMode()== FlashMode.ON){
					mContainer.setFlashMode(FlashMode.OFF);
					mFlashView.setImageResource(R.drawable.btn_flash_off);
				}else if (mContainer.getFlashMode()== FlashMode.OFF) {
					mContainer.setFlashMode(FlashMode.AUTO);
					mFlashView.setImageResource(R.drawable.btn_flash_auto);
				}
				else if (mContainer.getFlashMode()== FlashMode.AUTO) {
					mContainer.setFlashMode(FlashMode.TORCH);
					mFlashView.setImageResource(R.drawable.btn_flash_torch);
				}
				else if (mContainer.getFlashMode()== FlashMode.TORCH) {
					mContainer.setFlashMode(FlashMode.ON);
					mFlashView.setImageResource(R.drawable.btn_flash_on);
				}
				break;
			case R.id.btn_switch_mode:
				if(mIsRecordMode){
					mSwitchModeButton.setImageResource(R.drawable.ic_switch_camera);
					mCameraShutterButton.setVisibility(View.VISIBLE);
					mRecordShutterButton.setVisibility(View.GONE);
					//拍照模式下显示顶部菜单
					mHeaderBar.setVisibility(View.VISIBLE);
					mIsRecordMode=false;
					mContainer.switchMode(0);
					stopRecord();
				}
				else {
					mSwitchModeButton.setImageResource(R.drawable.ic_switch_video);
					mCameraShutterButton.setVisibility(View.GONE);
					mRecordShutterButton.setVisibility(View.VISIBLE);
					//录像模式下隐藏顶部菜单
					mHeaderBar.setVisibility(View.GONE);
					mIsRecordMode=true;
					mContainer.switchMode(5);
				}
				break;
			case R.id.btn_shutter_record:
				if(!isRecording){
					isRecording=mContainer.startRecord();
					if (isRecording) {
						mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_recording);
					}
				}else {
					stopRecord();
				}
				break;
			case R.id.btn_switch_camera:	//转换摄像头
				mContainer.switchCamera();
				break;
			case R.id.btn_other_setting:
				mContainer.setWaterMark();
				break;
			default:
				break;
		}
	}


	private void stopRecord() {
		mContainer.stopRecord(this);
		isRecording=false;
		mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_record);
	}

	@Override
	public void onTakePictureEnd(Bitmap thumBitmap) {
		mCameraShutterButton.setClickable(true);
	}



	@Override
	public void onAnimtionEnd(Bitmap bm, boolean isVideo) {
		if(bm!=null){
			//生成缩略图
			Bitmap thumbnail= ThumbnailUtils.extractThumbnail(bm, 213, 213);
			mThumbView.setImageBitmap(thumbnail);
			saveImg(thumbnail);
			if(isVideo)
				mVideoIconView.setVisibility(View.VISIBLE);
			else {
				mVideoIconView.setVisibility(View.GONE);
			}
		}
	}


	protected void saveImg(Bitmap bitmap) {
		//Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher) ;
		File file = new File(fileName) ;
		if (!file.exists()) {
			file.mkdir();
		}
		try {
			int math=(int)(Math.random()*1000);
			File myCaptureFile = new File(fileName + math + "08329.jpg");
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
			bos.flush();
			bos.close();

//			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//			Uri uri = Uri.fromFile(file);
//			intent.setData(uri);
			// 其次把文件插入到系统图库
			try {
				MediaStore.Images.Media.insertImage(getContentResolver(),
						myCaptureFile.getAbsolutePath(), math + "08329.jpg", null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,  Uri.fromFile(new File(myCaptureFile.getPath()))));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {

	}
};

	//返回按键
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			sendOpenTakePhoto("off"); //关闭拍照模式
			finish();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}




}
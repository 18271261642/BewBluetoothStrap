package com.example.bozhilun.android.activity.wylactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.linj.FileOperateUtil;
import com.linj.album.view.AlbumGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: AlbumAty
 * @Description: 相册Activity
 * @author LinJ
 * @date 2015-1-6 下午5:03:48
 *
 */
public class AlbumActivity extends BaseActivity implements AlbumGridView.OnCheckedChangeListener{
	public final static String TAG="AlbumActivity";
	/**
	 * 显示相册的View
	 */
	@BindView(R.id.albumview)
    AlbumGridView mAlbumView;
	@BindView(R.id.header_bar_enter_selection)
	TextView mEnterView;
	@BindView(R.id.header_bar_leave_selection)
	TextView mLeaveView;
	@BindView(R.id.header_bar_select_counter)
	TextView mSelectedCounterView;
	@BindView(R.id.select_all)
	TextView mSelectAllView;
	@BindView(R.id.delete)
	Button mDeleteButton;
	@BindView(R.id.header_bar_back)
	ImageView mBackView;
	@BindView(R.id.move)
	Button mCutButton;


	private String mSaveRoot;
	@Override
	protected int getStatusBarColor() {
		return -1;
	}

	protected void initViews() {
		mSelectedCounterView.setText("0");
		mAlbumView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (mAlbumView.getEditable()) return;
				Intent intent=new Intent(AlbumActivity.this,AlbumItemActivity.class);
				intent.putExtra("path", arg1.getTag().toString());
				startActivity(intent);}});
		mAlbumView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if(mAlbumView.getEditable()) return true;enterEdit();
				return true;}});
		mSaveRoot="image/*";



	}

	protected int getContentViewId() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return R.layout.acticity_album;
	}
	@OnClick({R.id.header_bar_enter_selection, R.id.header_bar_leave_selection, R.id.select_all, R.id.delete, R.id.header_bar_back})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.header_bar_enter_selection:
				enterEdit();
				break;
			case R.id.header_bar_leave_selection:
				leaveEdit();
				break;
			case R.id.select_all:
				selectAllClick();
				break;
			case R.id.delete:
				showDeleteDialog();
				break;
			case R.id.header_bar_back:
				finish();
				break;
			default:
				break;
		}}
	/**
	 *  加载图片
	 *  @param rootPath 根目录文件夹名
	 *  @param format 需要加载的文件格式
	 */
	public void loadAlbum(String rootPath, String format){
		//获取根目录下缩略图文件夹
		String thumbFolder= FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, rootPath);
		List<File> files= FileOperateUtil.listFiles(thumbFolder, format);
		if(files!=null&&files.size()>0){
			List<String> paths=new ArrayList<String>();
			for (File file : files) {
				paths.add(file.getAbsolutePath());
			}
			mAlbumView.setAdapter(mAlbumView.new AlbumViewAdapter(paths));
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		loadAlbum(mSaveRoot,".jpg");
	}



	private void enterEdit() {
		mAlbumView.setEditable(true,this);
		mSelectAllView.setText(getResources().getString(R.string.album_phoot_select_all));
		mDeleteButton.setEnabled(false);
		mCutButton.setEnabled(false);
		findViewById(R.id.header_bar_navi).setVisibility(View.GONE);
		findViewById(R.id.header_bar_select).setVisibility(View.VISIBLE);
		findViewById(R.id.album_bottom_bar).setVisibility(View.VISIBLE);
	}

	private void leaveEdit() {
		mAlbumView.setEditable(false);
		mCutButton.setEnabled(false);
		findViewById(R.id.header_bar_navi).setVisibility(View.VISIBLE);
		findViewById(R.id.header_bar_select).setVisibility(View.GONE);
		findViewById(R.id.album_bottom_bar).setVisibility(View.GONE);
	}

	private void selectAllClick() {
		if(mSelectAllView.getText().equals(getResources().getString(R.string.album_phoot_select_all))){
			mAlbumView.selectAll(this);
			mSelectAllView.setText(getResources().getString(R.string.album_phoot_unselect_all));
		}else{
			mAlbumView.unSelectAll(this);
			mSelectAllView.setText(getResources().getString(R.string.album_phoot_select_all));
		}

	}

	private void showDeleteDialog() {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.deleda))
				.setPositiveButton(getResources().getString(R.string.confirm), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Set<String> items=mAlbumView.getSelectedItems();
						for (String path : items) {
							boolean flag= FileOperateUtil.deleteThumbFile(path,AlbumActivity.this);
							if(!flag) Log.i(TAG, path);
						}
						mAlbumView.invalidate();
						mAlbumView.notifyDataSetChanged();//刷新当前
						loadAlbum(mSaveRoot,".jpg");
						leaveEdit();
					}
				})
				.setNegativeButton(getResources().getString(R.string.cancle), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		builder.create().show();
	}









	@Override
	public void onCheckedChanged(Set<String> set) {
		// TODO Auto-generated method stub
		mSelectedCounterView.setText(String.valueOf(set.size()));
		if(set.size()>0){
			mDeleteButton.setEnabled(true);
			mCutButton.setEnabled(true);
		}else {
			mDeleteButton.setEnabled(false);
			mCutButton.setEnabled(false);
		}
	}
	@Override
	public void onBackPressed() {
		if(mAlbumView.getEditable()){
			leaveEdit();
			return;
		}
		super.onBackPressed();
	}
}

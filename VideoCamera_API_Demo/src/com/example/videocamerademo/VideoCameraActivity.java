package com.example.videocamerademo;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.camera.exif.Exif;
import com.android.camera.exif.ExifInterface;

public class VideoCameraActivity extends Activity implements SurfaceHolder.Callback,android.view.View.OnClickListener{
	private SurfaceView mCameraView = null;
	private Button mVideoStartButton,mCaptureButton,mDeleteButton,mTurnOffButton,mVideoEndButton;
	private android.hardware.Camera mCameraDevice = null;
	private boolean videoDuring = true,isPreview = false, duringCapture = false,afterCapture=false,videoEnd = false;
	SurfaceHolder holder;
	private ContentResolver m_contentResolver;
	private Camera.Parameters parameters = null;
	private long mStorageSpaceBytes = Storage.LOW_STORAGE_THRESHOLD_BYTES;
	private static final String TEST_ITEM = "test_item";
	private MediaSaveService mMediaSaveService;
	private MediaRecorder mMediaRecorder;
	private CamcorderProfile mProfile;
	
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.video_camera_test); 
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		m_contentResolver = this.getContentResolver();
		bindMediaSaveService();
		
		
		mCameraView = (SurfaceView)findViewById(R.id.cameraView);
		mVideoStartButton = (Button)findViewById(R.id.camera_video_start_btn);
		mCaptureButton = (Button)findViewById(R.id.camera_capture_btn);
		mDeleteButton = (Button)findViewById(R.id.camera_delete_btn);
		mTurnOffButton = (Button)findViewById(R.id.camera_turn_off_btn);
		mVideoEndButton = (Button)findViewById(R.id.camera_video_end_btn);
		holder = mCameraView.getHolder();
		holder.addCallback(this);



		//set up listeners
		mVideoStartButton.setOnClickListener(this);
		mCaptureButton.setOnClickListener(this);
		mDeleteButton.setOnClickListener(this);
		mTurnOffButton.setOnClickListener(this);
		mVideoEndButton.setOnClickListener(this);
	}
	
	    private ServiceConnection mConnection = new ServiceConnection() {
	        @Override
	        public void onServiceConnected(ComponentName className, IBinder b) {
	            mMediaSaveService = ((MediaSaveService.LocalBinder) b).getService();
	            Log.i("weibu", "connection  "+ mMediaSaveService);
//	            mCurrentModule.onMediaSaveServiceConnected(mMediaSaveService);
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName className) {
	            if (mMediaSaveService != null) {
	                mMediaSaveService.setListener(null);
	                mMediaSaveService = null;
	            }
	        }
	    };

	public void onResume(){
		super.onResume();
		//switch camera to the front one
//		android.os.SystemProperties.set("service.camera.id", "0");
		setButtonState();
		resetStates();
		isPreview = true;
		setButtonState();
		
		

	}
	
	private void bindMediaSaveService() {
		Log.i("weibu", "start binder");
        Intent intent = new Intent(VideoCameraActivity.this, MediaSaveService.class);
       bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
	public void onPause(){
		super.onPause();
		//switch camera to the front one
//		android.os.SystemProperties.set("service.camera.id", "1");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unbindService(mConnection); 
	}
	
	 
	 public long getStorageSpaceBytes() {
	        return mStorageSpaceBytes;
	    }

	/**
	 *  total 3 states, before preview, previewing, after capture , 
	 */
	private void setButtonState(){
		mVideoStartButton.setEnabled(false);
		mVideoEndButton.setEnabled(false);
		mCaptureButton.setEnabled(true);
		mDeleteButton.setEnabled(true);
		if(videoDuring){
			mVideoStartButton.setEnabled(false);
			mCaptureButton.setEnabled(false);
			mDeleteButton.setEnabled(true);
			mVideoEndButton.setEnabled(true);
		}else if(videoEnd){
			mCaptureButton.setEnabled(false);
		}else if(isPreview){
			mVideoStartButton.setEnabled(true);
			mDeleteButton.setEnabled(false);
			mVideoEndButton.setEnabled(false);
		}else if(duringCapture){
			mVideoStartButton.setEnabled(false);
			mDeleteButton.setEnabled(false);
			mCaptureButton.setEnabled(false);
			mVideoEndButton.setEnabled(false);
		}else if(afterCapture){
			mCaptureButton.setEnabled(false);
		}

	}

	private void resetStates(){
		videoDuring = false;
		isPreview = false;
		afterCapture=false;
		duringCapture = false;
		videoEnd = false;
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i("yjc","surfaceChanged width="+width+" height="+height);
		//已经获得Surface的width和height，设置Camera的参数
				Camera.Parameters parameters = mCameraDevice.getParameters();
				parameters.setPreviewSize(width,height);
				if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
				//如果是竖屏
//				parameters.set("orientation", "portrait");
				//在2.2以上可以使用
					mCameraDevice.setDisplayOrientation(270);
				}else{
//				parameters.set("orientation", "landscape");
				//在2.2以上可以使用
					mCameraDevice.setDisplayOrientation(180);
				}
				mCameraDevice.setParameters(parameters);
				try {
				//设置显示
					mCameraDevice.setPreviewDisplay(holder);
				} catch (IOException exception) {
					mCameraDevice.release();
				mCameraDevice = null;
				}
				//开始预览
				mCameraDevice.startPreview();

	}
	
	/**
	 * 默认打开后置摄像头，0后置--1前置
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i("yjc","surfaceCreated ");
		mCameraDevice = android.hardware.Camera.open(0);//for the small camera yjcui
	}
	
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("yjc","surfaceDestroyed ");
		if(isPreview){
			mCameraDevice.stopPreview();
		}
		mCameraDevice.release();
	}

	/**
	 * 添加控件点击监听
	 */
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.camera_video_start_btn:
//				mCameraDevice.startPreview();
				resetStates();
				videoDuring = true;
				setButtonState();
				takeVideo();
				break;
			case R.id.camera_video_end_btn:
				videoDuring = false;
				videoEnd = true;
				setButtonState();
				stopVideo();
				break;
			case R.id.camera_capture_btn:
				resetStates();
				duringCapture = true;
				setButtonState();
				doCapture();
				break;
			
			case R.id.camera_delete_btn:
				mCameraDevice.startPreview();
				resetStates();
				isPreview = true;
				setButtonState();
				break;
			case R.id.camera_turn_off_btn:
				finish();
				break;
		}

	}
	
	/**
	 * 拍照调用
	 */
	private void doCapture(){
		mCameraDevice.takePicture(mShutter, mRaw, mPostview,mJpeg);
	}
	private ShutterCallback mShutter = new ShutterCallback(){

		public void onShutter() {
			Log.i("yjc","onShutter ");
		}};
	private PictureCallback mRaw = new PictureCallback() {

		public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
			Log.i("yjc","mRaw "+(data==null?"has no data":"has data") + " camera="+camera.getParameters());
		}
	};
	private PictureCallback mPostview = new PictureCallback() {

		public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
			Log.i("yjc","mPostview "+data.length);
		}
	};
	
	/**
	 * 拍照后，照片保存的回调接口
	 */
	private PictureCallback mJpeg = new PictureCallback() {

		public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
			Log.i("yjc","mJpeg ");
			resetStates();
			afterCapture = true;
			setButtonState();
			 parameters = mCameraDevice.getParameters();
			 Size s = parameters.getPictureSize();
			 int width = s.width;
			 int height = s.height;
			 String title = "ZGJ_"+System.currentTimeMillis();
			 ExifInterface exif = Exif.getExif(data);
	            int orientation = Exif.getOrientation(exif);
	            Log.i("weibu", "getMediaSaveService() "+getMediaSaveService());
			getMediaSaveService().addImage(
					data, title, System.currentTimeMillis(), null, width, height,
					orientation, exif, null, m_contentResolver);
			Toast.makeText(VideoCameraActivity.this, "保存成功!", Toast.LENGTH_SHORT).show();
//			mCameraDevice.startPreview();//后面如果不用返回按键时需要拍照后重新进入预览
			
		}
	};		

	public MediaSaveService getMediaSaveService() {
        return mMediaSaveService;
    }
	
	private void setTestResult () {
		Intent intent = new Intent();
		Intent mIntent = getIntent();
		int pos = mIntent.getIntExtra(TEST_ITEM,-1);
		intent.putExtra(TEST_ITEM,pos);
		//		Log.e(TAG,"pos ="+pos);
		setResult(0, intent);
	}
	@Override
		public void finish() {
			setTestResult (); 
			super.finish();
		}
	
	private void takeVideo(){
		Toast.makeText(this, "录像开始", Toast.LENGTH_SHORT).show();
		mMediaRecorder = new MediaRecorder();
		mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH); 
		// Unlock the camera object before passing it to media recorder.
		mCameraDevice.unlock();
		mMediaRecorder.setCamera(mCameraDevice);
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setProfile(mProfile);
//		mMediaRecorder.setMaxDuration(10000);//ms为单位
		long dateTaken = System.currentTimeMillis();
		Date date = new Date(dateTaken);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yy-mm-dd");
		String title = dateFormat.format(date);
		String filename = "wb_zgj"+title + ".3gp"; // Used when emailing.
		String cameraDirPath = "/sdcard/DCIM/Camera";
		String filePath = cameraDirPath + "/" + filename;
		File cameraDir = new File(cameraDirPath);
		cameraDir.mkdirs();
		mMediaRecorder.setOutputFile(filePath);
		try {
			mMediaRecorder.prepare();
			// TODO Auto-generated catch block
			mMediaRecorder.start(); // Recording is now started
		} catch (RuntimeException e) {

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void stopVideo(){
		mMediaRecorder.stop();
		mMediaRecorder.reset();
		mMediaRecorder.release();
		mMediaRecorder = null;
		if(mCameraDevice != null)
			mCameraDevice.lock();
		Toast.makeText(this, "录像结束", Toast.LENGTH_SHORT).show();
	}
}


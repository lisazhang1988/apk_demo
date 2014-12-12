package com.example.checkramdemo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.WindowManager;

public class CheckService extends Service {
	 private static String TAG = "AM_MEMORYIPROCESS";

     private ActivityManager mActivityManager = null;

     private TimerTask timerTask;
     private Timer timer;
     private AlertDialog dialog;
     private MyHandler mHandler = new MyHandler();
     
     class MyHandler extends Handler{
    	 @Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		super.handleMessage(msg);
    		
    	 dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
       	 dialog.show();
    			
    	}
     }

	private MyBinder myBinder = new MyBinder();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return myBinder;
	}
	
	
	class MyBinder extends Binder{
		public CheckService getService(){
			return CheckService.this;
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		initDialog();
		 mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		 timerTask = new TimerTask(){
             public void run(){
                  //Doing something
            	 long couldUse = getSystemAvaialbeMemorySize();//剩下可使用的内存大小
            	 long totalMe = getSystemTotalMemorySize();//系统总内存大小
            	 if ((totalMe - couldUse) <= totalMe * 0.95) {//如果剩余的内存大小小于总内存的95%，则发消息弹出是对话框
            		 mHandler.sendEmptyMessage(1);
				}
              }
    	 };
    	 
         long delayTime = 1000;
         long period = 30*60*1000;//每半个小时进行一次检查，查看剩余内存占总内存的大小
         if (timerTask != null) {
        	 timer = new Timer();
        	 timer.schedule(timerTask, delayTime, period) ;
		}

	}
	
	
	private void initDialog(){
		dialog = new AlertDialog.Builder(this)
		.setTitle("警告： ")
		.setIcon(R.drawable.ic_launcher)
		.setMessage("请注意内存使用情况，已超过总内存的95% !")
		.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		})
		.setCancelable(false)
		.create();
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.i("weibu", "服务已启动！");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	 // 获得系统可用内存信息
    private long getSystemAvaialbeMemorySize() {
            // 获得MemoryInfo对象
            MemoryInfo memoryInfo = new MemoryInfo();
            // 获得系统可用内存，保存在MemoryInfo对象上
            mActivityManager.getMemoryInfo(memoryInfo);
            long memSize = memoryInfo.availMem;

            // 字符类型转换
//            String availMemStr = formateFileSize(memSize);

            return memSize;
    }
    
 // 获得系统总内存信息
    private long getSystemTotalMemorySize() {
            // 获得MemoryInfo对象
            MemoryInfo memoryInfo = new MemoryInfo();
            // 获得系统可用内存，保存在MemoryInfo对象上
            mActivityManager.getMemoryInfo(memoryInfo);
            long memSize = memoryInfo.totalMem;

            // 字符类型转换
//            String availMemStr = formateFileSize(memSize);

            return memSize;
    }

    // 调用系统函数，字符串转换 long -String KB/MB
    private String formateFileSize(long size) {
            return Formatter.formatFileSize(this, size);
    }

}

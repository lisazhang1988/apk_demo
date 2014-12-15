package com.example.g_senser_test;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class WBService extends Service{

	private final String TAG = "weibu";
	private Timer mTimer;
	private TimerTask mTimerTask;
	private MyHandler mMyHandler = new MyHandler();
	private final int STOP_SERVICE = 0;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		startTask();
		Log.i(TAG, "服务已启动！");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "服务结束！");
	}
	
	class MyHandler extends Handler{
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		super.handleMessage(msg);
    		int key = msg.what;
    		switch (key) {
			case STOP_SERVICE:
				WBService.this.stopSelf();
				if (mTimerTask != null) {
					mTimer.cancel();
				}
				break;

			default:
				break;
			}
    	}
    }
	
    
    /**
     * 返回当前的应用是否处于前台显示状态
     * @param $packageName
     * @return
     */
    private boolean isTopActivity(String packageName) {
        //_context是一个保存的上下文
        ActivityManager am = (ActivityManager) this.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningAppProcessInfo> list = (ArrayList<RunningAppProcessInfo>) am.getRunningAppProcesses();
        if(list.size() == 0) return false;
        for(RunningAppProcessInfo info:list){
            Log.d(TAG,Integer.toString(info.importance));
            Log.d(TAG,info.processName);
            if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
            		info.processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }
    
private void startTask(){
    	
    	mTimer = new Timer();
    	mTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean isTop = isTopActivity("com.example.g_senser_test");
				if (!isTop) {
					Message msg = Message.obtain();
					msg.what = 0;
					mMyHandler.sendMessage(msg);
				}
			}
		};
		
		if (mTimerTask != null) {
			mTimer.schedule(mTimerTask, 1000, 1000);
		}
    }
}

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
            	 long couldUse = getSystemAvaialbeMemorySize();//ʣ�¿�ʹ�õ��ڴ��С
            	 long totalMe = getSystemTotalMemorySize();//ϵͳ���ڴ��С
            	 if ((totalMe - couldUse) <= totalMe * 0.95) {//���ʣ����ڴ��СС�����ڴ��95%������Ϣ�����ǶԻ���
            		 mHandler.sendEmptyMessage(1);
				}
              }
    	 };
    	 
         long delayTime = 1000;
         long period = 30*60*1000;//ÿ���Сʱ����һ�μ�飬�鿴ʣ���ڴ�ռ���ڴ�Ĵ�С
         if (timerTask != null) {
        	 timer = new Timer();
        	 timer.schedule(timerTask, delayTime, period) ;
		}

	}
	
	
	private void initDialog(){
		dialog = new AlertDialog.Builder(this)
		.setTitle("���棺 ")
		.setIcon(R.drawable.ic_launcher)
		.setMessage("��ע���ڴ�ʹ��������ѳ������ڴ��95% !")
		.setPositiveButton("ȷ��", new OnClickListener() {
			
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
		Log.i("weibu", "������������");
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
	
	 // ���ϵͳ�����ڴ���Ϣ
    private long getSystemAvaialbeMemorySize() {
            // ���MemoryInfo����
            MemoryInfo memoryInfo = new MemoryInfo();
            // ���ϵͳ�����ڴ棬������MemoryInfo������
            mActivityManager.getMemoryInfo(memoryInfo);
            long memSize = memoryInfo.availMem;

            // �ַ�����ת��
//            String availMemStr = formateFileSize(memSize);

            return memSize;
    }
    
 // ���ϵͳ���ڴ���Ϣ
    private long getSystemTotalMemorySize() {
            // ���MemoryInfo����
            MemoryInfo memoryInfo = new MemoryInfo();
            // ���ϵͳ�����ڴ棬������MemoryInfo������
            mActivityManager.getMemoryInfo(memoryInfo);
            long memSize = memoryInfo.totalMem;

            // �ַ�����ת��
//            String availMemStr = formateFileSize(memSize);

            return memSize;
    }

    // ����ϵͳ�������ַ���ת�� long -String KB/MB
    private String formateFileSize(long size) {
            return Formatter.formatFileSize(this, size);
    }

}

package com.example.g_senser_test;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	
	private String TAG = "weibu";
	private  final String packageName= "com.example.g_senser_test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	 startService();
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    private void startService(){
    	Intent intent = new Intent(this, WBService.class);
    	this.startService(intent);
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	stopService();
    }
    
    private void stopService(){
    	Intent intent = new Intent(this, WBService.class);
    	this.stopService(intent);
    }
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	if (!isTopActivity(packageName)) {
			stopService();
		}
    }
    
    /*
     * 获取当前Activity是否置于前台，传入所属包名
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    
}

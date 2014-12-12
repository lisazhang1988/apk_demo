package com.example.checkramdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CheckBroadcast extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i("weibu", "已开机，准备开启检测内存服务！");
		Intent inte = new Intent(context, CheckService.class);
		context.startService(inte);
	}

}

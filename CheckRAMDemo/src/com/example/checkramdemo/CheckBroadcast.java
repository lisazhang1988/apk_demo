package com.example.checkramdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CheckBroadcast extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i("weibu", "�ѿ�����׼����������ڴ����");
		Intent inte = new Intent(context, CheckService.class);
		context.startService(inte);
	}

}

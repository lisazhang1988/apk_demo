package com.example.videocamerademo;

import android.hardware.Camera.CameraInfo;
import android.view.OrientationEventListener;

public class CameraUtil {

	 public static int getJpegRotation(int cameraId, int orientation) {
	        // See android.hardware.Camera.Parameters.setRotation for
	        // documentation.
	        int rotation = 0;
	        if (orientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
//	            CameraInfo info = CameraHolder.instance().getCameraInfo()[cameraId];
//	            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
//	                rotation = (info.orientation - orientation + 360) % 360;
//	            } else {  // back-facing camera
//	                rotation = (info.orientation + orientation) % 360;
//	            }
	        }
	        return rotation;
	    }
}

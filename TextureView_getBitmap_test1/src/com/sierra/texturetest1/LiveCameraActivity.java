package com.sierra.texturetest1;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

/**
 * Demonstrates the variable performance (exec. time) of TextureView.getBitmap(Bitmap).
 * The exec. time is logged and also printed on screen.
 * 
 * Depending on unknown circumstances the getBitmap(Bitmap) function runs slowly most of
 * the time, dropping its performance for about 1/3, compared to when it runs fast.
 * 
 *  * Tested on:
 * - Nexus 7 (4.2.2)
 * - Galaxy Nexus (4.2.2)
 * 
 * Other problems:
 * getBitmap() appears to take too much time: on the Nexus 7 it is at least 15ms ~ 20ms.
 * However, drawing the resulting Bitmap only takes 7 ms.
 * 
 * 
 * Sample adapted from http://developer.android.com/reference/android/view/TextureView.html
 * 
 * @author Rafael Sierra
 *
 */
public class LiveCameraActivity extends Activity implements TextureView.SurfaceTextureListener {
    private Camera mCamera;
    private TextureView mTextureView;
    private SurfaceView mSurfaceView;

    private final int imgW = 640;
    private final int imgH = 480;
	private Bitmap bmp = Bitmap.createBitmap(imgW, imgH, Bitmap.Config.ARGB_8888);
	private Canvas canvas = new Canvas(bmp);
	private Paint paint1 = new Paint();
	private SurfaceHolder mSurfaceHolder;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main2);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mSurfaceHolder = mSurfaceView.getHolder();

        mTextureView = (TextureView) findViewById(R.id.textureview);
        mTextureView.setSurfaceTextureListener(this);

		final int textSize = 24;
		paint1.setColor(0xff00ffff);
		paint1.setTextSize(textSize);

    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open(Camera.getNumberOfCameras()-1);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(imgW, imgH);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
		long time0 = System.currentTimeMillis();
		mTextureView.getBitmap(bmp);
		long time1 = System.currentTimeMillis() - time0;

		final Canvas c = mSurfaceHolder.lockCanvas();
		if ( c != null) {
			canvas.drawText("getBmp= "  + time1, 10, 40, paint1);
			c.drawBitmap(bmp, 0, 0, null);
			mSurfaceHolder.unlockCanvasAndPost(c);
		}
		long total = System.currentTimeMillis() - time0;
		long time2 = total -time1;
		Log.i("onSurfaceTextureUpdated", "timing: getBmp= "  + time1 + " draw= " + time2 + " total= " + total);
    }
}
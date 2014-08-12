package com.example.testcamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.testcamera.util.DisplayUtil;
import com.example.testcamera.util.FileUtil;

public class MainActivity extends Activity implements SurfaceHolder.Callback,Camera.PictureCallback,
                        Camera.PreviewCallback,OnClickListener{

	private static final String previewSizeList = null;
	private Button take_photo;
	private SurfaceView surface;
	private Camera camera;
	private static String Tag ="MAINACTIVITYTAG";
	Handler handler=new Handler();
	private int oldorientation=0;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		context=this;
		context.getString(R.string.app_name);
		
		if(!checkCameraHardware(this)){
			Toast.makeText(this, "", Toast.LENGTH_LONG).show();
		}
		findView();
		
		/**
		 * 自动对焦任务
		 *//*
		 final Runnable mAutoFocusTask = new Runnable() {

			@Override
			public void run() {
				camera.autoFocus(new AutoFocusCallback() {  
					  
				    @Override  
				    public void onAutoFocus(boolean success, Camera camera) {  
				        // TODO Auto-generated method stub  
				        // success为true表示对焦成功  
				    	  System.out.println("自动对焦结果"+success);
				        if (success) {  
				           System.out.println("对焦成功！");
				           camera.cancelAutoFocus();
				        }  
				    }  
				});  


		}};
		
        OrientationEventListener listener=new OrientationEventListener(this) {
			
			@Override
			public void onOrientationChanged(int orientation) {
				
				
				if(camera!=null){
					if(orientation-oldorientation>30){
					  oldorientation=orientation;
					  handler.post(mAutoFocusTask);
					}
				}
			
				
			}
		};
        
		listener.enable();*/
		
	}

	
	
	private void findView() {
	/*	take_photo=(Button) findViewById(R.id.btn_takephoto);
		take_photo.setOnClickListener(this);*/
		surface=(SurfaceView) findViewById(R.id.surface);
		surface.setFocusable(true);
		surface.setFocusableInTouchMode(true);
		surface.setKeepScreenOn(true);
		
		SurfaceHolder surfaceHolder=surface.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		try {
			camera=Camera.open(CameraInfo.CAMERA_FACING_FRONT);
			camera.setPreviewDisplay(holder);
			camera.setDisplayOrientation(90);
			camera.setPreviewCallback(MainActivity.this);
		
			
			updateCameraParameters();
			
			Parameters parameters = camera.getParameters();
			parameters.setRotation(90);
			camera.setParameters(parameters);
			camera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "相机异常", 1).show();
		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	//	updateCameraParameters();
		camera.autoFocus(new AutoFocusCallback() {  
			  
		    @Override  
		    public void onAutoFocus(boolean success, Camera camera) {  
		        // TODO Auto-generated method stub  
		        // success为true表示对焦成功  
		    	  System.out.println("自动对焦结果"+success);
		        if (success) {  
		           System.out.println("对焦成功！");
		        }  
		    }  
		});  
		
	}

	private void updateCameraParameters() {
		//Size size=findBestPreViewSize();
		//Log.d("zby log","PreVireSize---" +size.width+size.height);
		setPictureSize();
		Camera.Parameters parameters=camera.getParameters();
		List<Size> mSupportedPreviewSizes=parameters.getSupportedPreviewSizes();
		Size mPreviewSize= getOptimalPreviewSize(mSupportedPreviewSizes, DisplayUtil.getDefaultDisplay(getApplicationContext()).getWidth(),DisplayUtil.getDefaultDisplay(getApplicationContext()).getHeight());
	    parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
	    surface.getLayoutParams().width=mPreviewSize.height;
	    surface.getLayoutParams().height=mPreviewSize.width;	    
	}



	private Size findBestPreViewSize() {
		int diff=Integer.MAX_VALUE;
		Camera.Parameters parameters=camera.getParameters();
		String previewSizeList=parameters.get("preview-size-values");
		if(previewSizeList==null)
			previewSizeList=parameters.get("preview-size-value");
		Log.e("zby log", "previewSizeList-----"+previewSizeList);

        int bestX = 0;
        int bestY = 0;
           
           
        for(String pictureSizeString : previewSizeList.split(","))
        {
            pictureSizeString = pictureSizeString.trim();
                
            int dimPosition = pictureSizeString.indexOf('x');
            if(dimPosition == -1){
                Log.e("zby log", "Bad pictureSizeString:"+pictureSizeString);
                continue;
            }
                
            int newX = 0;
            int newY = 0;
                
            try{
                newX = Integer.parseInt(pictureSizeString.substring(0, dimPosition));
                newY = Integer.parseInt(pictureSizeString.substring(dimPosition+1));
            }catch(NumberFormatException e){
                Log.e("zby log", "Bad pictureSizeString:"+pictureSizeString);
                continue;
            }
               
            Point screenResolution = new Point (DisplayUtil.getDefaultDisplay(getApplicationContext()).getWidth(),DisplayUtil.getDefaultDisplay(getApplicationContext()).getHeight());
                
            int newDiff = Math.abs(newX - screenResolution.x)+Math.abs(newY- screenResolution.y);
                if(newDiff == diff)
                {
                    bestX = newX;
                    bestY = newY;
                    break;
                } else if(newDiff > diff){
                    if((3 * newX) == (4 * newY)) {
                        bestX = newX;
                        bestY = newY;
                        diff = newDiff;
                    }
                }
            }
                
        if (bestX > 0 && bestY > 0) {
           return camera.new Size(bestX, bestY);
        }
       return null;
	}



	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		releaseCamera();
		
	}


	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseCamera();
	}
	
	
	private void releaseCamera(){
		if(camera!=null){
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera=null;
		}
	}
	
	
	
	private boolean checkCameraHardware(Context context){
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			int cameraNumb=Camera.getNumberOfCameras();
			Log.d(Tag, cameraNumb+"");
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_takephoto:
			//camera.takePicture(null, null, null, MainActivity.this);
			camera.autoFocus(new AutoFocusCallback() {  
				  
			    @Override  
			    public void onAutoFocus(boolean success, Camera camera) {  
			        // TODO Auto-generated method stub  
			        // success为true表示对焦成功  
			    	  System.out.println("自动对焦结果"+success);
			        if (success) {  
			           System.out.println("对焦成功！");
			        }  
			    }  
			});  
			break;

		default:
			break;
		}
		
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		File image=FileUtil.getOutputFile();
		try {
			FileOutputStream os=new FileOutputStream(image);
			os.write(data);
			os.close();
			camera.stopPreview();
			camera.startPreview();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}
	
	
	  private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
	        final double ASPECT_TOLERANCE = 0.1;
	        double targetRatio = (double) w / h;
	        if (sizes == null) return null;

	        Size optimalSize = null;
	        double minDiff = Double.MAX_VALUE;

	        int targetHeight = h;

	        // Try to find an size match aspect ratio and size
	        for (Size size : sizes) {
	            double ratio = (double) size.width / size.height;
	            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
	            if (Math.abs(size.height - targetHeight) < minDiff) {
	                optimalSize = size;
	                minDiff = Math.abs(size.height - targetHeight);
	            }
	        }

	        // Cannot find the one match the aspect ratio, ignore the requirement
	        if (optimalSize == null) {
	            minDiff = Double.MAX_VALUE;
	            for (Size size : sizes) {
	                if (Math.abs(size.height - targetHeight) < minDiff) {
	                    optimalSize = size;
	                    minDiff = Math.abs(size.height - targetHeight);
	                }
	            }
	        }
	        return optimalSize;
	    }
	
	
	/**
	 * 设置图片大小
	 * 
	 * @param size
	 */
	@SuppressWarnings("deprecation")
	public void setPictureSize() {

		Parameters parameters = camera.getParameters();
		
		Log.d("zby log ","previewsize"+parameters.getPreviewSize().width+"--before--"+parameters.getPreviewSize().height);
		Log.d("zby log ","picturesize"+parameters.getPictureSize().width+"--before--"+parameters.getPictureSize().height);

		List<Size> previewSizes = parameters.getSupportedPreviewSizes();

		List<Size> pictureSizes = parameters.getSupportedPictureSizes();

		Comparator<Size> pixelComparator = new Comparator<Size>() {

			@Override
			public int compare(Size lhs, Size rhs) {
				// TODO Auto-generated method stub
				
				return rhs.width * rhs.height - lhs.width * lhs.height;
			}
		};

		Collections.sort(previewSizes, pixelComparator);

		Collections.sort(pictureSizes, pixelComparator);

		List<Size[]> allSizes = new ArrayList<Camera.Size[]>();

		for (Size previewSize : previewSizes) {
			float previewRatio = (float) previewSize.height / previewSize.width;
			Size nearPictureSize = null;
			for (Size pictureSize : pictureSizes) {
				float pictureRation = (float) pictureSize.height
						/ pictureSize.width;
				if (previewRatio == pictureRation) {
					nearPictureSize = pictureSize;
					break;
				} else if (nearPictureSize == null) {
					nearPictureSize = pictureSize;
				} else {
					if (Math.abs(previewRatio - (float) nearPictureSize.height
							/ nearPictureSize.width) > Math.abs(previewRatio
							- pictureRation)) {
						nearPictureSize = pictureSize;
					}
				}

			}
			if (nearPictureSize != null)
				allSizes.add(new Size[] { previewSize, nearPictureSize });

		}

		Display display = DisplayUtil.getDefaultDisplay(getApplicationContext());

		float maxRatio = (float) display.getWidth()
				/ (display.getHeight() * 0.75f);

		Size[] bestSizes = null;

		for (Size[] sizes : allSizes) {
			float previewRatio = (float) sizes[0].height / sizes[0].width;
			if (previewRatio <= maxRatio
					&& previewRatio == (float) sizes[1].height / sizes[1].width) {
				if (bestSizes == null
						|| bestSizes[1].height * bestSizes[1].width < sizes[1].height
								* sizes[1].width)
					bestSizes = sizes;
			}
		}

		if (bestSizes == null) {
			for (Size[] sizes : allSizes) {
				float previewRatio = (float) sizes[0].height / sizes[0].width;
				if (previewRatio == (float) sizes[1].height / sizes[1].width) {
					if (bestSizes == null
							|| bestSizes[1].height * bestSizes[1].width < sizes[1].height
									* sizes[1].width)
						bestSizes = sizes;
				}
			}
		}

		if (bestSizes == null)
			bestSizes = allSizes.get(0);

		parameters.setPreviewSize(bestSizes[0].width, bestSizes[0].height);
		parameters.setPictureSize(bestSizes[1].width, bestSizes[1].height);
		
		

		camera.setParameters(parameters);

		Log.d("zby log ","previewsize"+parameters.getPreviewSize().width+"----"+parameters.getPreviewSize().height);
		Log.d("zby log ","picturesize"+parameters.getPictureSize().width+"----"+parameters.getPictureSize().height);

	}
}

package com.example
.testcamera.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class FileUtil {
	
	public static File imageFiles;
	 static {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			imageFiles=new File(Environment.getExternalStorageDirectory(),"testcamera");
		    if(!imageFiles.exists())
		    	imageFiles.mkdirs();
		}
		
	}
	
	public static File getOutputFile(){
		String imagename=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		File image=new File(imageFiles,imagename+".jpg");
		return image;
	}

}

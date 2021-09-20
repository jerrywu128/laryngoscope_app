/**
 * Added by zhangyanhu C01012,2014-10-17
 */
package com.icatch.sbcapp.test;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.icatch.sbcapp.CustomException.*;
import com.icatch.sbcapp.CustomException.NullPointerException;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Added by zhangyanhu C01012,2014-10-17
 */
public class test {
	private static String writeFile;
	private static FileOutputStream out = null;
	private static File writeLogFile = null;
	static File directory = null;
	static String fileName = null;
	static String path = null;
	private static long lastTime = 0;
	private static long lastTime11 = 0;
	private static Bitmap videoBitmap;
	private static int count = 0;
	
	private static String path1;
	private static File directory1;
	private static String fileName1;
	private static String writeFile1;
	private static FileOutputStream out1;

	public static void saveImage(Bitmap bitmap, long time) {
		videoBitmap = bitmap;
		// if(System.currentTimeMillis() < lastTime + 50){
		// return;
		// }
		// if(videoBitmap == null){
		// Bitmap videoBitmap = Bitmap.createBitmap(640, 360, Config.ARGB_8888);
		// }
		// videoBitmap.copyPixelsFromBuffer(buffer);
		lastTime = System.currentTimeMillis();
		if (path != null) {
			directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
		}
		path = Environment.getExternalStorageDirectory().toString() + "/SportCam/Photo/";
		// fileName = System.currentTimeMillis() + "_count.jpg";
		fileName = time + "_count.jpg";
		File file = new File(directory, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writeFile = path + fileName;
		writeLogFile = new File(writeFile);
		Log.d("tigertiger", "writeFile: " + writeFile);
		try {
			out = new FileOutputStream(writeFile, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			// out.write(bitmap.get,0,size);
			videoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		count++;
	}

	public static void saveImage11(ICatchFrameBuffer buffer, int size) {
		// videoBitmap = bitmap;
		// if(System.currentTimeMillis() < lastTime11 + 5000){
		// return;
		// }
		// if(videoBitmap == null){
		// Bitmap videoBitmap = Bitmap.createBitmap(640, 360, Config.ARGB_8888);
		// }
		// videoBitmap.copyPixelsFromBuffer(buffer);
		lastTime11 = System.currentTimeMillis();

		path = Environment.getExternalStorageDirectory().toString() + "/bitmapSave11/";
		if (path != null) {
			directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
		}
		fileName = System.currentTimeMillis() + "_" + count + ".jpg";
		File file = new File(directory, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writeFile = path + fileName;
		writeLogFile = new File(writeFile);
		Log.d("tigertiger", "writeFile: " + writeFile + " file size=" + size);
		try {
			out = new FileOutputStream(writeFile, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.write(buffer.getBuffer(), 0, size);
			// videoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			// out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		count++;
	}

	public static void savefile(ICatchFrameBuffer buffer, int size) {
		if (path != null) {
			directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
		}
		path = Environment.getExternalStorageDirectory().toString() + "/saveVideo/";
		fileName = "video.aaa";
		File file = new File(directory, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writeFile = path + fileName;
		writeLogFile = new File(writeFile);
		try {
			if (out == null) {
				out = new FileOutputStream(writeFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.write(buffer.getBuffer(), 0, size);
			// videoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			// out.flush();
			// out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}


	public static void saveAduio(ICatchFrameBuffer buffer, int size) {
		if (path1 != null) {
			directory1 = new File(path1);
			if (!directory1.exists()) {
				directory1.mkdirs();
			}
		}
		path1 = Environment.getExternalStorageDirectory().toString() + "/saveAudio/";
		fileName1 = "audio.aaa";
		File file = new File(directory1, fileName1);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writeFile1 = path1 + fileName1;
		Log.d("tigertiger", "writeFile: " + writeFile1);
		try {
			if (out1 == null) {
				out1 = new FileOutputStream(writeFile1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out1.write(buffer.getBuffer(), 0, size);
			// videoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			// out.flush();
			// out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}


	public static void emptyFolder(String path) {
		String folderPath = Environment.getExternalStorageDirectory().toString() + path;
		if (folderPath != null) {
			File file = new File(folderPath);
			File temp = null;
			String[] fileNames = file.list();
			if (file.exists() && fileNames.length != 0) {
				for (int i = 0; i < fileNames.length; i++) {
					temp = new File(folderPath + fileNames[i]);
					if (temp.isFile()) {
						temp.delete();
					}
				}
			}
		}
	}
}

package com.damuzhi.travel.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;

import android.R.integer;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class FileUtil
{
	private static final String TAG = "FileUtil";
	private List<String> lstFile = new ArrayList<String>();
	private ArrayList<FileInputStream> fileInput = new ArrayList<FileInputStream>();
	private List<PlaceList> placeLists = new ArrayList<PlaceList>();
	public List<String> GetFiles(String Path, String Extension,
			boolean IsIterative)
	{
		File[] files = new File(Path).listFiles();
		if(files == null||files.length==0)
		{
			return null;
		}
		for (int i = 0; i < files.length; i++)
		{
			File f = files[i];
			if (f.isFile())
			{
				if (f.getPath()
						.substring(f.getPath().length() - Extension.length())
						.equals(Extension))
				{
					lstFile.add(f.getPath());
				}
				if (!IsIterative)
					break;
			} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)
			{
				GetFiles(f.getPath(), Extension, IsIterative);
			}
		}
		return lstFile;
	}

	/*
	 * 
	 * public List<String> GetFiles(String Path, String type,String Extension, boolean IsIterative) { File[] files = new File(Path).listFiles(); for (int i = 0; i < files.length; i++) { File f = files[i]; if (f.isFile()) { String fileExtension = f.getPath().substring(f.getPath().length() - Extension.length()); String fileType = f.getPath().substring(f.getPath().lastIndexOf("/")+1,f.getPath().lastIndexOf(".")); if (fileExtension.equals(Extension)&&fileType.contains(type)) { lstFile.add(f.getPath()); } //�ж���չ�� if (!IsIterative) break; } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) { GetFiles(f.getPath(), Extension, IsIterative); } } return lstFile; }
	 */

	public ArrayList<FileInputStream> getFileInputStreams(String Path,String type, String Extension, boolean IsIterative)
	{

		File[] files = new File(Path).listFiles();
		if (files == null || files.length == 0)
			return null;
		for (int i = 0; i < files.length; i++)
		{
			File f = files[i];

			if (f.isFile())
			{
				String fileExtension = f.getPath().substring(
						f.getPath().length() - Extension.length());
				String fileType = f.getPath().substring(
						f.getPath().lastIndexOf("/") + 1,
						f.getPath().lastIndexOf("."));

				if (fileExtension.equals(Extension) && fileType.contains(type))
				{
					FileInputStream fileInputStream = null;
					try
					{
						fileInputStream = new FileInputStream(new File(f.getPath()));
						fileInput.add(fileInputStream);
					} catch (Exception e)
					{
						Log.e(TAG, "<getFileInputStreams> but catch exception "
								+ e.toString(), e);
					}
				}
				if (!IsIterative)
					break;
			} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)
			{
				getFileInputStreams(f.getPath(), type, Extension, IsIterative);
			}
		}
		return fileInput;
	}
	
	
	
	public List<PlaceList> getPlaceLists(String Path,String type, String Extension, boolean IsIterative)
	{

		File[] files = new File(Path).listFiles();
		FileInputStream fileInputStream = null;
		BufferedInputStream bufferedInputStream = null;
		if (files == null || files.length == 0)
			return null;
		try{
			for (int i = 0; i < files.length; i++)
			{
				File f = files[i];
	
				if (f.isFile())
				{
					String fileExtension = f.getPath().substring(f.getPath().length() - Extension.length());
					String fileType = f.getPath().substring(f.getPath().lastIndexOf("/") + 1,f.getPath().lastIndexOf("."));	
					if (fileExtension.equals(Extension) && fileType.contains(type))
					{
						try
						{
							fileInputStream = new FileInputStream(new File(f.getPath()));
							bufferedInputStream = new BufferedInputStream(fileInputStream);
							PlaceList placeList = PlaceList.parseFrom(bufferedInputStream);
							placeLists.add(placeList);
							fileInputStream.close();
							bufferedInputStream.close();
						} catch (Exception e)
						{
							Log.e(TAG, "<getPlaceLists> but catch exception "+ e.toString(), e);
							try
							{
								if(fileInputStream != null)
								{
									fileInputStream.close();
									fileInputStream = null;
								}
								if(bufferedInputStream != null)
								{
									bufferedInputStream.close();
									bufferedInputStream = null;
								}
							} catch (Exception e2)
							{
							}
							
							return Collections.emptyList();
						}
					}
					if (!IsIterative)
						break;
				} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)
				{
					getPlaceLists(f.getPath(), type, Extension, IsIterative);
				}
			}
		}finally
		{
			try
			{
				if(fileInputStream != null)
				{
					fileInputStream.close();
					fileInputStream = null;
				}
				if(bufferedInputStream != null)
				{
					bufferedInputStream.close();
					bufferedInputStream = null;
				}
			} catch (Exception e2)
			{
			}
		}
		return placeLists;
	}
	
	
	public FileInputStream getInputStream(String Path,String type, String Extension, boolean IsIterative)
	{

		FileInputStream fileInput = null;
		File[] files = new File(Path).listFiles();
		if (files == null || files.length == 0)
			return null;

		for (int i = 0; i < files.length; i++)
		{
			File f = files[i];

			if (f.isFile())
			{
				String fileExtension = f.getPath().substring(f.getPath().length() - Extension.length());
				String fileType = f.getPath().substring(f.getPath().lastIndexOf("/") + 1,f.getPath().lastIndexOf("."));

				if (fileExtension.equals(Extension) && fileType.contains(type))
				{
					try
					{
						fileInput = new FileInputStream(new File(f.getPath()));
					} catch (Exception e)
					{
						Log.e(TAG, "<getInputStream> but catch exception "+ e.toString(), e);
					}
				}
				if (!IsIterative)
					break;
			} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)
			{
				getInputStream(f.getPath(), type, Extension, IsIterative);
			}
		}
		return fileInput;
	}
	
	
	

	public static boolean copyFile(String srcFile, String targetFile)
	{
		FileInputStream fileInputStream = null;
		OutputStream myOutput = null;
		BufferedInputStream myInput = null;
		boolean result = false;
		try
		{
			if (FileUtil.checkFileIsExits(srcFile))
			{

				fileInputStream = new FileInputStream(new File(srcFile));

				myOutput = new FileOutputStream(targetFile);
				myInput = new BufferedInputStream(fileInputStream);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer)) != -1)
				{
					myOutput.write(buffer, 0, length);
				}
				myOutput.flush();
				result = true;
			} else
			{
				result = false;
			}
		} catch (Exception e)
		{
			Log.e(TAG, "<copyFile> srcFile=" + srcFile + ", to dest file "
					+ targetFile + ", but catch exception " + e.toString(), e);
			result = false;
		} finally
		{
			if (myOutput != null)
			{
				try
				{
					myOutput.close();
				} catch (IOException e)
				{
				}
			}

			if (myInput != null)
			{
				try
				{
					myInput.close();
				} catch (IOException e)
				{
				}
			}

			if (fileInputStream != null)
			{
				try
				{
					fileInputStream.close();
				} catch (IOException e)
				{
				}
			}
		}

		return result;
	}

	public static boolean copyFile(String srcFile, FileOutputStream myOutput)
	{
		FileInputStream fileInputStream = null;
		BufferedInputStream myInput = null;
		boolean result = false;
		try
		{
			if (FileUtil.checkFileIsExits(srcFile))
			{

				fileInputStream = new FileInputStream(new File(srcFile));
				myInput = new BufferedInputStream(fileInputStream);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer)) != -1)
				{
					myOutput.write(buffer, 0, length);
				}
				myOutput.flush();
				result = true;
			} else
			{
				result = false;
			}
		} catch (Exception e)
		{
			Log.e(TAG, "<copyFile> srcFile=" + srcFile
					+ ", to dest file , but catch exception " + e.toString(), e);
			result = false;
		} finally
		{
			try
			{
				if (myOutput != null)
				{
					myOutput.close();
				}
				if (myInput != null)
				{
					myInput.close();
				}
				if (fileInputStream != null)
				{
					fileInputStream.close();
				}
			} catch (IOException e)
			{
			}
		}

		return result;
	}

	public static boolean copyFile(InputStream inputStream, String targetFile)
	{
		OutputStream myOutput = null;
		BufferedInputStream myInput = null;
		boolean result = false;
		try
		{
			myOutput = new FileOutputStream(targetFile);
			myInput = new BufferedInputStream(inputStream);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) != -1)
			{
				myOutput.write(buffer, 0, length);
			}
			myOutput.flush();
			myInput.close();
			myOutput.close();
			result = true;
		} catch (Exception e)
		{
			try
			{
				if (myOutput != null)
				{
					myOutput.close();
				}
				if (myInput != null)
				{
					myInput.close();
				}
			} catch (IOException e1)
			{
			}
			Log.e(TAG, "<copyFile>  to dest file " + targetFile
					+ ", but catch exception " + e.toString(), e);
			result = false;
		}
		return result;

	}

	public static boolean copyFile(InputStream inputStream,FileOutputStream outputStream)
	{
		boolean result = false;
		BufferedInputStream myInput = null;
		if (inputStream != null)
		{
			myInput = new BufferedInputStream(inputStream);
		}
		byte[] buffer = new byte[1024];
		int length;
		try
		{
			while ((length = myInput.read(buffer)) != -1)
			{
				outputStream.write(buffer, 0, length);
			}
			result = true;

		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		} finally
		{
			try
			{
				if (outputStream != null)
				{
					outputStream.flush();
					outputStream.close();
				}
				if (myInput != null)
				{
					myInput.close();
				}
				if (inputStream != null)
				{
					inputStream.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		return result;

	}

	public boolean writeFile(String targetFile, InputStream inputStream)
	{
		boolean flag = true;

		try
		{
			OutputStream myOutput = new FileOutputStream(targetFile);
			BufferedInputStream myInput = new BufferedInputStream(inputStream);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1)
			{
				myOutput.write(buffer, 0, length);
				Log.d(TAG, "file length = " + length);
			}
			myOutput.flush();
			myInput.close();
			myOutput.close();
		} catch (Exception e)
		{
			Log.e(TAG, "<writeFile> but catch exception :" + e.toString(), e);
		}

		return flag;
	}

	public static boolean checkFileIsExits(String filePath)
	{
		File file = new File(filePath);
		if (file.exists())
		{
			return true;
		}
		return false;
	}

	public static boolean deleteFolder(String filePath)
	{
		boolean flag = false;
		File file = new File(filePath);
		if (!file.exists())
		{
			return flag;
		} else
		{
			if (file.isFile())
			{
				return deleteFile(filePath);
			} else
			{
				return deleteDirectory(filePath);
			}
		}
	}

	public static boolean deleteFile(String filePath)
	{
		boolean flag = false;
		File file = new File(filePath);
		if (file.isFile() && file.exists())
		{
			file.delete();
			flag = true;
		}
		return flag;
	}

	
	 public static boolean fileMove(String srcFile, String destPath) {
	        // File (or directory) to be moved
	        File file = new File(srcFile);
	        // Destination directory
	        File dir = new File(destPath);
	        // Move file to new directory
	       boolean moveFlag = file.renameTo(new File(dir, file.getName()));
	        //boolean moveFlag = file.renameTo(new File(destPath));
	        return moveFlag;
	    }
	
	public static boolean folderMove(String srcFile, String destPath)  {
		try {
			boolean moveFlag = false;
			File dir = new File(srcFile);
			File[] files = dir.listFiles();
			if (files == null)
				return false;
			File moveDir = new File(destPath);
			if (!moveDir.exists()) {
				moveDir.mkdirs();
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					folderMove(files[i].getPath(), destPath + "\\" + files[i].getName());
					files[i].delete();
				}
				File moveFile = new File(moveDir.getPath() + "\\"+ files[i].getName());
				if (moveFile.exists()) {
					moveFile.delete();
				}
				moveFlag = files[i].renameTo(moveFile);
			}
			return moveFlag;
		} catch (Exception e) {
			Log.e(TAG, "<fileMove> but catch exception :" + e.toString(), e);
			return false;
		}
	}
	
	
	
	public static boolean deleteDirectory(String filePath)
	{
		if (!filePath.endsWith(File.separator))
		{
			filePath = filePath + File.separator;
		}
		File dirFile = new File(filePath);
		if (!dirFile.exists() || !dirFile.isDirectory())
		{
			return false;
		}
		boolean flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isFile())
			{
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else
			{
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		if (dirFile.delete())
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static Map<Integer, Integer> getFiles(String Path)
	{
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		File[] files = new File(Path).listFiles();
		if (files != null && files.length > 0)
		{
			for (int i = 0; i < files.length; i++)
			{
				File f = files[i];
				if (!f.isFile() && !f.getName().contains("gc"))
				{
					int cityId = Integer.parseInt(f.getName());
					map.put(cityId, cityId);
				}
			}
		}
		return map;
	}

	/*public static long freeSpaceOnSd()
	{
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		// double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / 1024;
		long sdFree = (long)stat.getAvailableBlocks() * (long)stat.getBlockSize();
		return sdFree;
	}*/

	public static boolean sdcardEnable()
	{
		// return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else
		{
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageWriteable;
	}

	/**
	 * @param tempFilePath2
	 * @description
	 * @version 1.0
	 * @author liuxiaokun
	 * @update 2012-8-15 下午2:27:40
	 */
	public static long getFileSize(String path)
	{
		// TODO Auto-generated method stub
		File file = new File(path);
		if (file.exists())
		{
			return file.length();
		} else
		{
			return 0;
		}
	}
	
	
	
	
	public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    /*public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return "ERROR";
        }
    }*/
    
    
    
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = android.os.Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return (availableBlocks * blockSize);
        } else {
            return 0;
        }
    }
    
    

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return "ERROR";
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

	
}

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
import java.util.List;

import com.damuzhi.travel.model.constant.ConstantField;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class FileUtil
{
	private static final String TAG = "FileUtil";
	private List<String> lstFile = new ArrayList<String>(); // ��� List
	private ArrayList<FileInputStream> fileInput = new ArrayList<FileInputStream>();

	/**
	 * @param Path
	 * @param Extension
	 * @param IsIterative
	 * @return
	 * @description
	 * @version 1.0
	 * @author liuxiaokun
	 * @update 2012-5-8 ����11:45:01
	 */
	public List<String> GetFiles(String Path, String Extension,
			boolean IsIterative)
	{
		File[] files = new File(Path).listFiles();
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
				} // �ж���չ��
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

	/**
	 * @param Path
	 * @param type
	 * @param Extension
	 * @param IsIterative
	 * @return
	 * @description
	 * @version 1.0
	 * @author liuxiaokun
	 * @update 2012-5-8 ����11:47:12
	 */
	public ArrayList<FileInputStream> getFileInputStreams(String Path,
			String type, String Extension, boolean IsIterative) // ����Ŀ¼����չ���Ƿ�������ļ���
	{

		File[] files = new File(Path).listFiles();
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
					// lstFile.add(f.getPath());
					FileInputStream fileInputStream;
					try
					{
						// Log.d(TAG, f.getPath());
						fileInputStream = new FileInputStream(new File(
								f.getPath()));
						fileInput.add(fileInputStream);
					} catch (FileNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
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

	public void updateAppFile()
	{

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
				myInput = new BufferedInputStream(
						fileInputStream);
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
			Log.e(TAG, "<copyFile> srcFile="+srcFile+", to dest file "+targetFile 
					+", but catch exception "+e.toString(), e);
			result = false;
		} finally
		{
			if (myOutput != null){
				try
				{
					myOutput.close();
				} catch (IOException e)
				{
				}
			}
			
			if (myInput != null){
				try
				{
					myInput.close();
				} catch (IOException e)
				{
				}
			}
			
			if (fileInputStream != null){
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

	public static void copyFile(InputStream inputStream, String targetFile)
			throws IOException
	{
		OutputStream myOutput = new FileOutputStream(targetFile);
		BufferedInputStream myInput = new BufferedInputStream(inputStream);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) != -1)
		{
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myInput.close();
		myOutput.close();
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
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * @param fileName
	 * @return
	 * @description
	 * @version 1.0
	 * @author liuxiaokun
	 * @update 2012-5-8 ����11:47:21
	 */
	public static boolean checkFileIsExits(String filePath)
	{
		File file = new File(filePath);
		if (file.exists())
		{
			return true;
		}
		return false;
	}

	/**
	 * @return
	 * @description
	 * @version 1.0
	 * @author liuxiaokun
	 * @update 2012-5-8 ����11:47:24
	 */
	public static int freeSpaceOnSd()
	{
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / 1024;
		return (int) sdFreeMB;
	}
}

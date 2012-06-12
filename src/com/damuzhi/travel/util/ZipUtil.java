package com.damuzhi.travel.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.damuzhi.travel.model.constant.ConstantField;

import android.content.Context;

public class ZipUtil
{
	  
	 private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
	 
	   
	    public static void zipFiles(Collection<File> resFileList, File zipFile) throws IOException {
	        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
	        for (File resFile : resFileList) {
	            zipFile(resFile, zipout, "");
	        }
	        zipout.close();
	    }
	 
	 /*   
	    public static void zipFiles(ArrayList<File> resFileList, File zipFile) throws IOException {
	        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
	                zipFile), BUFF_SIZE));
	        for (File resFile : resFileList) {
	            zipFile(resFile, zipout, "");
	        }
	        zipout.close();
	    }*/
	    
	    
	    public static void zipFiles(Collection<File> resFileList, File zipFile, String comment)
	            throws IOException {
	        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
	        for (File resFile : resFileList) {
	            zipFile(resFile, zipout, "");
	        }
	        zipout.setComment(comment);
	        zipout.close();
	    }
	 
	   
	    public static void upZipFile(InputStream zipFile, String folderPath) throws ZipException, IOException {
	        String strEntry; 
	        try {
	        BufferedOutputStream dest = null; 
	        //FileInputStream fis = new FileInputStream(zipFile);
	        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipFile));
	        ZipEntry entry;

	        while ((entry = zis.getNextEntry()) != null) {
	        strEntry = entry.getName();
	        String str = folderPath + File.separator + strEntry; 
	        File entryFile = new File(new String(str.getBytes("8859_1"), "GB2312"));
	        if (entry.isDirectory()) {
		            if (!entryFile.exists()) {
		                entryFile.mkdirs();
		            	}
	    		} 
	        else{
	    			if (!entryFile.getParentFile().exists())
	    			{
	    				entryFile.getParentFile().mkdirs();
	    			}		
    		        int count ;
    		        byte data[] = new byte[BUFF_SIZE];
    		        FileOutputStream fos = new FileOutputStream(new File(folderPath + File.separator + strEntry));
    		        dest = new BufferedOutputStream(fos, BUFF_SIZE);
    		        while ((count = zis.read(data)) != -1) {
    		           dest.write(data, 0, count);
    		        }
    		        dest.flush();
    		        dest.close();
	    		}
	        } 
	        zis.close();
	        } catch (Exception cwj) {
	        cwj.printStackTrace();
	        }
	        
	    	}
	 
	    
	    
	    public static boolean upZipFile(String zipFilePath, String folderPath) throws ZipException, IOException {
	     boolean zipSuccess = false;
	     String strEntry; 
		 try {
		        BufferedOutputStream dest = null; 
		        FileInputStream fis = new FileInputStream(zipFilePath);
		        ZipInputStream zis = null;
		        if(fis != null)
		        {
		        	zis  = new ZipInputStream(new BufferedInputStream(fis));
		        }      
		        ZipEntry entry;
	
		        while ((entry = zis.getNextEntry()) != null) {
		        strEntry = entry.getName();
		        String str = folderPath + File.separator + strEntry; 
		        File entryFile = new File(new String(str.getBytes("8859_1"), "GB2312"));
		        if (entry.isDirectory()) 
		        {
		            if (!entryFile.exists()) 
		                entryFile.mkdirs();
		    	} 
		        else
		        {
	    			if (!entryFile.getParentFile().exists())
	    			{
	    				entryFile.getParentFile().mkdirs();
	    			}		
			        int count ;
			        byte data[] = new byte[BUFF_SIZE];
			        FileOutputStream fos = new FileOutputStream(new File(folderPath + File.separator + strEntry));
			        dest = new BufferedOutputStream(fos, BUFF_SIZE);
			        while ((count = zis.read(data)) != -1) {
			           dest.write(data, 0, count);
			        }
			        zipSuccess = true;
			        dest.flush();
			        dest.close();
	    		}
		        } 
	        fis.close();
	        zis.close();
	        return zipSuccess;
		} catch (Exception cwj) 
	    {
        cwj.printStackTrace();
        return zipSuccess;
        }
	        
	   }
	    
	    
	    
	   
	    public static ArrayList<File> upZipSelectedFile(File zipFile, String folderPath,
	            String nameContains) throws ZipException, IOException {
	        ArrayList<File> fileList = new ArrayList<File>();
	 
	        File desDir = new File(folderPath);
	        if (!desDir.exists()) {
	            desDir.mkdir();
	        }
	 
	        ZipFile zf = new ZipFile(zipFile);
	        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
	            ZipEntry entry = ((ZipEntry)entries.nextElement());
	            if (entry.getName().contains(nameContains)) {
	                InputStream in = zf.getInputStream(entry);
	                String str = folderPath + File.separator + entry.getName();
	                str = new String(str.getBytes("8859_1"), "GB2312");
	                // str.getBytes("GB2312"),"8859_1" 
	                // str.getBytes("8859_1"),"GB2312"
	                File desFile = new File(str);
	                if (!desFile.exists()) {
	                    File fileParentDir = desFile.getParentFile();
	                    if (!fileParentDir.exists()) {
	                        fileParentDir.mkdirs();
	                    }
	                    desFile.createNewFile();
	                }
	                OutputStream out = new FileOutputStream(desFile);
	                byte buffer[] = new byte[BUFF_SIZE];
	                int realLength;
	                while ((realLength = in.read(buffer)) > 0) {
	                    out.write(buffer, 0, realLength);
	                }
	                in.close();
	                out.close();
	                fileList.add(desFile);
	            }
	        }
	        return fileList;
	    }
	 
	   
	    public static ArrayList<String> getEntriesNames(File zipFile) throws ZipException, IOException {
	        ArrayList<String> entryNames = new ArrayList<String>();
	        Enumeration<?> entries = getEntriesEnumeration(zipFile);
	        while (entries.hasMoreElements()) {
	            ZipEntry entry = ((ZipEntry)entries.nextElement());
	            entryNames.add(new String(getEntryName(entry).getBytes("GB2312"), "8859_1"));
	        }
	        return entryNames;
	    }
	 
	  
	    public static Enumeration<?> getEntriesEnumeration(File zipFile) throws ZipException,
	            IOException {
	        ZipFile zf = new ZipFile(zipFile);
	        return zf.entries();
	 
	    }
	 
	   
	    public static String getEntryComment(ZipEntry entry) throws UnsupportedEncodingException {
	        return new String(entry.getComment().getBytes("GB2312"), "8859_1");
	    }
	 
	   
	    public static String getEntryName(ZipEntry entry) throws UnsupportedEncodingException {
	        return new String(entry.getName().getBytes("GB2312"), "8859_1");
	    }
	 
	  
	    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
	            throws FileNotFoundException, IOException {
	        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
	                + resFile.getName();
	        rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
	        if (resFile.isDirectory()) {
	            File[] fileList = resFile.listFiles();
	            for (File file : fileList) {
	                zipFile(file, zipout, rootpath);
	            }
	        } else {
	            byte buffer[] = new byte[BUFF_SIZE];
	            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile), BUFF_SIZE);
	            zipout.putNextEntry(new ZipEntry(rootpath));
	            int realLength;
	            while ((realLength = in.read(buffer)) != -1) {
	                zipout.write(buffer, 0, realLength);
	            }
	            in.close();
	            zipout.flush();
	            zipout.closeEntry();
	        }
	    }
	    
	   
	   
}

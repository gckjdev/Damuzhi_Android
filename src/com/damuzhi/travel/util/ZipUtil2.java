/**  
        * @title ZipUtil2.java  
        * @package com.damuzhi.travel.util  
        * @description   
        * @author liuxiaokun  
        * @update 2012-8-20 下午3:09:08  
        * @version V1.0  
 */
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.R.bool;
import android.util.Log;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-8-20 下午3:09:08  
 */

public class ZipUtil2
{
	/*  *//**
     * 把文件压缩成zip格式
     * @param files         需要压缩的文件
     * @param zipFilePath 压缩后的zip文件路径   ,如"D:/test/aa.zip";
     *//*
    public static void compressFiles2Zip(File[] files,String zipFilePath) {
        if(files != null && files.length >0) {
            if(isEndsWithZip(zipFilePath)) {
                ZipArchiveOutputStream zaos = null;
                try {
                    File zipFile = new File(zipFilePath);
                    zaos = new ZipArchiveOutputStream(zipFile);
                    //Use Zip64 extensions for all entries where they are required
                    zaos.setUseZip64(Zip64Mode.AsNeeded);
                     
                    //将每个文件用ZipArchiveEntry封装
                    //再用ZipArchiveOutputStream写到压缩文件中
                    for(File file : files) {
                        if(file != null) {
                            ZipArchiveEntry zipArchiveEntry  = new ZipArchiveEntry(file,file.getName());
                            zaos.putArchiveEntry(zipArchiveEntry);
                            InputStream is = null;
                            try {
                                is = new BufferedInputStream(new FileInputStream(file));
                                byte[] buffer = new byte[1024 * 5]; 
                                int len = -1;
                                while((len = is.read(buffer)) != -1) {
                                    //把缓冲区的字节写入到ZipArchiveEntry
                                    zaos.write(buffer, 0, len);
                                }
                                //Writes all necessary data for this entry.
                                zaos.closeArchiveEntry(); 
                            }catch(Exception e) {
                                throw new RuntimeException(e);
                            }finally {
                                if(is != null)
                                    is.close();
                            }
                             
                        }
                    }
                    zaos.finish();
                }catch(Exception e){
                    throw new RuntimeException(e);
                }finally {
                        try {
                            if(zaos != null) {
                                zaos.close();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                }
                 
            }
             
        }
         
    }
     
    *//**
     * 把zip文件解压到指定的文件夹
     * @param zipFilePath   zip文件路径, 如 "D:/test/aa.zip"
     * @param saveFileDir   解压后的文件存放路径, 如"D:/test/"
     *//*
    public static boolean decompressZip(String zipFilePath,String saveFileDir) {
    	boolean result  = false;
      //  if(isEndsWithZip(zipFilePath)) {
            File file = new File(zipFilePath);
            if(file.exists()) {
                InputStream is = null;
                //can read Zip archives
                ZipArchiveInputStream zais = null;
                try {
                    is = new FileInputStream(file);
                    zais = new ZipArchiveInputStream(is);
                    ArchiveEntry  archiveEntry = null;
                    while((archiveEntry = zais.getNextEntry()) != null) {
                        String entryFileName = archiveEntry.getName();
                        String entryFilePath = saveFileDir + entryFileName;
                        byte[] content = new byte[(int) archiveEntry.getSize()];
                        zais.read(content);
                        OutputStream os = null;
                        try {
                            File entryFile = new File(entryFilePath);
                            os = new BufferedOutputStream(new FileOutputStream(entryFile));
                            os.write(content);
                        }catch(IOException e) {
                           // throw new IOException(e);
                        	return false;
                        }finally {
                            if(os != null) {
                                os.flush();
                                os.close();
                            }
                        }
                         
                    }
                    result = true;
                }catch(Exception e) {
                    //throw new RuntimeException(e);
                	return false;
                }finally {
                        try {
                            if(zais != null) {
                                zais.close();
                            }
                            if(is != null) {
                                is.close();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                }
             
            }
      //  }
        return result;
    }
     
    *//**
     * 判断文件名是否以.zip为后缀
     * @param fileName        需要判断的文件名
     * @return 是zip文件返回true,否则返回false
     *//*
    public static boolean isEndsWithZip(String fileName) {
        boolean flag = false;
        if(fileName != null && !"".equals(fileName.trim())) {
            if(fileName.endsWith(".ZIP")||fileName.endsWith(".zip")){
                flag = true;
            }
        }
        return flag;
    }
    
    
    
    
    
    @SuppressWarnings("unchecked")
    public static boolean unZipToFolder(String zipfilename, String outputdir)  {
    	boolean result = false;
        File zipfile = new File(zipfilename);
        if (zipfile.exists()) {
            outputdir = outputdir + File.separator;
            try
			{
				FileUtils.forceMkdir(new File(outputdir));
				 ZipFile zf = new ZipFile(zipfile, "UTF-8");
		            Enumeration zipArchiveEntrys = zf.getEntries();
		            while (zipArchiveEntrys.hasMoreElements()) {
		                ZipArchiveEntry zipArchiveEntry = (ZipArchiveEntry) zipArchiveEntrys.nextElement();
		                if (zipArchiveEntry.isDirectory()) {
		                    FileUtils.forceMkdir(new File(outputdir + zipArchiveEntry.getName() + File.separator));
		                } else {
		                    IOUtils.copy(zf.getInputStream(zipArchiveEntry), FileUtils.openOutputStream(new File(outputdir
		                            + zipArchiveEntry.getName())));
		                }
		            }
		            result = true;
			} catch (IOException e)
			{
				return false;
			}

           
        } else {
            result = false;
        }
        return result;
    }*/
	
	
	
	/* private ZipFile zipFile;

	    public ZipUtil2(ZipFile zipFile)
	    {
	        this.zipFile = zipFile;
	    }

	    public ZipUtil2(String zipFilePath) 
	    {
	        try
			{
				this.zipFile = new ZipFile(zipFilePath);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    public void close() throws IOException
	    {
	        zipFile.close();
	    }

	    public boolean unzip(String extractPath) 
	    {
	    	boolean result = false;
	        File extractionDirectory = new File(extractPath);

	        if(!extractionDirectory.exists() && !extractionDirectory.mkdirs()) {
	           // throw new IOException("Unable to create extraction directory");
	        	return false;
	        }
	        if(!extractionDirectory.isDirectory()) {
	           // throw new IOException("Unable to extract ZipFile to non-directory");
	        	return false;
	        }

	        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
	        while(zipEntries.hasMoreElements()) {
	            ZipEntry zipEntry = zipEntries.nextElement();

	            if(zipEntry.isDirectory()) {
	                File newDirectory = new File(extractPath + zipEntry.getName());
	                newDirectory.mkdirs();
	            }
	            else {
	            	BufferedInputStream inputStream = null;
	            	BufferedOutputStream outputStream = null;
	            try{
	                inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));

	                File outputFile = new File(extractPath + zipEntry.getName());
	                File outputDirectory = new File(outputFile.getParent());

	                if(!outputDirectory.exists() && !outputDirectory.mkdirs()) {
	                   // throw new IOException(new StringBuilder("unable to create directory for ").append(zipEntry.getName()).toString());
	                	return false;
	                }

	                if(!outputFile.exists() && !outputFile.createNewFile()) {
	                    //throw new IOException(new StringBuilder("Unable to create file for ").append(zipEntry.getName()).toString());
	                	return false;
	                }

	                 outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
	               
	                    int currByte;
	                    while((currByte = inputStream.read()) != -1) {
	                        outputStream.write(currByte);
	                    }
	                }catch (Exception e) {
						
					}
	                finally {
	                    try
						{
	                    	if(inputStream != null)
	                    	{
								inputStream.close();
	                    	}
	                    	if(outputStream != null)
	                    	{
	                    		outputStream.close();
	                    	}
							
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    
	                }
	            }
	        }
	        result = true;
	        return result;
	    }

	    public List<String> getFilePaths()
	    {
	        ArrayList<String> filePaths = new ArrayList<String>();

	        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
	        while(zipEntries.hasMoreElements()) {
	            ZipEntry zipEntry = zipEntries.nextElement();
	            filePaths.add(zipEntry.getName());
	        }

	        return filePaths;
	    }

	    public InputStream getFileInputStream(String filePath) throws IOException
	    {
	        ZipEntry entry = zipFile.getEntry(filePath);

	        if(entry == null) {
	            throw new FileNotFoundException((new StringBuilder("Unable to find file ").append(filePath).append(" in zipfile ").append(zipFile.getName()).toString()));
	        }

	        return new BufferedInputStream(zipFile.getInputStream(entry));
	    }*/
	
	
	private static void Unzip(String zipFile, String targetDir) {
		   int BUFFER = 4096; //这里缓冲区我们使用4KB，
		   String strEntry; //保存每个zip的条目名称

		   try {
		    BufferedOutputStream dest = null; //缓冲输出流
		    FileInputStream fis = new FileInputStream(zipFile);
		    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		    ZipEntry entry; //每个zip条目的实例

		    while ((entry = zis.getNextEntry()) != null) {

		     try {
		       Log.i("Unzip: ","="+ entry);
		      int count;
		      byte data[] = new byte[BUFFER];
		      strEntry = entry.getName();

		      File entryFile = new File(targetDir + strEntry);
		      File entryDir = new File(entryFile.getParent());
		      if (!entryDir.exists()) {
		       entryDir.mkdirs();
		      }

		      FileOutputStream fos = new FileOutputStream(entryFile);
		      dest = new BufferedOutputStream(fos, BUFFER);
		      while ((count = zis.read(data, 0, BUFFER)) != -1) {
		       dest.write(data, 0, count);
		      }
		      dest.flush();
		      dest.close();
		     } catch (Exception ex) {
		      ex.printStackTrace();
		     }
		    }
		    zis.close();
		   } catch (Exception cwj) {
		    cwj.printStackTrace();
		   }
		  }
}

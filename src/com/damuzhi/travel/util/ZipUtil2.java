/**  
        * @title ZipUtil2.java  
        * @package com.damuzhi.travel.util  
        * @description   
        * @author liuxiaokun  
        * @update 2012-8-20 下午3:09:08  
        * @version V1.0  
 */
package com.damuzhi.travel.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-8-20 下午3:09:08  
 */

public class ZipUtil2
{
	  private static final String TAG = "ZipUtil2";

	/**
     * 把文件压缩成zip格式
     * @param files         需要压缩的文件
     * @param zipFilePath 压缩后的zip文件路径   ,如"D:/test/aa.zip";
     */
  /*  public static void compressFiles2Zip(File[] files,String zipFilePath) {
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
    }*/
    
    
    
    
    
    @SuppressWarnings("unchecked")
    public static boolean unZipToFolder(String zipfilename, String outputdir)  {
    	boolean result = false;
    	//Log.d(TAG, "unZip file ="+zipfilename);
        File zipfile = new File(zipfilename);
        if (zipfile.exists()) {
            outputdir = outputdir + File.separator;
           // Log.d(TAG, "unZip output file ="+outputdir);
            try
			{
				FileUtils.forceMkdir(new File(outputdir));
				 ZipFile zf = new ZipFile(zipfile, "UTF-8");
		            Enumeration zipArchiveEntrys = zf.getEntries();
		            while (zipArchiveEntrys.hasMoreElements()) {
		                ZipArchiveEntry zipArchiveEntry = (ZipArchiveEntry) zipArchiveEntrys.nextElement();
		                if (zipArchiveEntry.isDirectory()) 
		                {
		                    FileUtils.forceMkdir(new File(outputdir + zipArchiveEntry.getName() + File.separator));
		                   // Log.d(TAG, "unZip make fload ");
		                } else 
		                {
		                    IOUtils.copy( zf.getInputStream(zipArchiveEntry), FileUtils.openOutputStream(new File(outputdir
		                            + zipArchiveEntry.getName())));
		                  //  Log.d(TAG, "unzip file");
		                }
		            }
		            result = true;
			} catch (Exception e)
			{
				return false;
			}           
        } else {
            result = false;
        }
        return result;
    }
	
}

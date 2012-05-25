/**  
        * @title PlaceSpinner.java  
        * @package com.damuzhi.travel.model.entity  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-24 下午1:16:57  
        * @version V1.0  
        */
package com.damuzhi.travel.model.entity;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-24 下午1:16:57  
 */

public class PlaceSpinner
{
	private String[] compos;
	private String[] subCatName;
	private int[] subCatKey;
	private String[] serviceName;
	private int[] serviceID;
	private String[] price;
	private String[] areaName;
	private int[] areaID;
	public String[] getCompos()
	{
		return compos;
	}
	public String[] getSubCatName()
	{
		return subCatName;
	}
	public int[] getSubCatKey()
	{
		return subCatKey;
	}
	public String[] getServiceName()
	{
		return serviceName;
	}
	public int[] getServiceID()
	{
		return serviceID;
	}
	public String[] getPrice()
	{
		return price;
	}
	public String[] getAreaName()
	{
		return areaName;
	}
	public int[] getAreaID()
	{
		return areaID;
	}
	public void setCompos(String[] compos)
	{
		this.compos = compos;
	}
	public void setSubCatName(String[] subCatName)
	{
		this.subCatName = subCatName;
	}
	public void setSubCatKey(int[] subCatKey)
	{
		this.subCatKey = subCatKey;
	}
	public void setServiceName(String[] serviceName)
	{
		this.serviceName = serviceName;
	}
	public void setServiceID(int[] serviceID)
	{
		this.serviceID = serviceID;
	}
	public void setPrice(String[] price)
	{
		this.price = price;
	}
	public void setAreaName(String[] areaName)
	{
		this.areaName = areaName;
	}
	public void setAreaID(int[] areaID)
	{
		this.areaID = areaID;
	}
	
	
}

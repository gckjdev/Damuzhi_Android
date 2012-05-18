package com.damuzhi.travel.service;

import java.util.Map;

/**  
        * @description   任务类
        * @version 1.0  
        * @author liuxiaokun  
        * @update 2012-5-9 下午4:12:15  
        */  
public class Task
{
	  private int taskID;//任务ID
      private Object object;     
	 // private Map taskParam;//内容参数
	  public static final int TASK_USER_LOGIN=1;//用户登录任务 
	  public static final int TASK_LOGIN_SCENERY=2;// 进入景点页面
	  public static final int TASK_LOGIN_HOTEL=3;// 进入酒店页面
	  public static final int TASK_LOGIN_SHOPPING=4;// 进入购物页面
	  public static final int TASK_LOGIN_RESTAURANT=5;// 进入餐馆页面
	  public static final int TASK_LOGIN_ENTERTAINMNET=6;// 进入娱乐页面
	  public static final int TASK_LOGIN_NEARBY=7;// 进入附近页面
	  public static final int CITY_ABOUT=8;// 进入城市概况页面
	  public static final int TRAVEL_READY=9;// 进入旅行准备页面
	  public static final int USEFUL_INFO=10;// 进入实用信息页面
	  public static final int CITY_TRAFFIC=11;// 进入城市交通页面
	  public static final int TRAVEL_NOTE=12;// 进入游记攻略页面
	  public static final int TRAVEL_COMMEND=13;// 进入线路推荐页面
	  public static final int MAP_NEARBY= 14;//地图周边推荐

	/**
	 * @param taskID
	 * @param taskParam
	 */
	public Task(int taskID,Object object)
	{
		super();
		this.taskID = taskID;
		this.object = object;
	}
	public int getTaskID()
	{
		return taskID;
	}
	public void setTaskID(int taskID)
	{
		this.taskID = taskID;
	}
	public Object getObject()
	{
		return object;
	}
	public void setObject(Object object)
	{
		this.object = object;
	}
}

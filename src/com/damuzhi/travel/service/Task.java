package com.damuzhi.travel.service;

import java.util.Map;

public class Task
{
	  private int taskID;//任务ID
	  @SuppressWarnings("rawtypes")
	 // private Map taskParam;//内容参数
	  public static final int TASK_USER_LOGIN=1;//用户登录任务 
	  public static final int TASK_LOGIN_SCENERY=2;// 进入景点页面
	  public static final int TASK_LOGIN_HOTEL=3;// 进入酒店页面
	  public static final int TASK_LOGIN_SHOPPING=4;// 进入购物页面
	  public static final int TASK_LOGIN_RESTAURANT=5;// 进入餐馆页面
	  public static final int TASK_LOGIN_FUN=6;// 进入娱乐页面
	  public static final int TASK_LOGIN_ROUND=7;// 进入附近页面
	  public static final int CITY_ABOUT=2;// 进入城市概况页面
	  public static final int TRAVEL_READY=3;// 进入旅行准备页面
	  public static final int USEFUL_INFO=4;// 进入实用信息页面
	  public static final int CITY_TRAFFIC=5;// 进入城市交通页面
	  public static final int TRAVEL_NOTE=6;// 进入游记攻略页面
	  public static final int TRAVEL_COMMEND=7;// 进入线路推荐页面

	/**
	 * @param taskID
	 * @param taskParam
	 */
	public Task(int taskID)
	{
		super();
		this.taskID = taskID;
	}
	public int getTaskID()
	{
		return taskID;
	}
	public void setTaskID(int taskID)
	{
		this.taskID = taskID;
	}
}

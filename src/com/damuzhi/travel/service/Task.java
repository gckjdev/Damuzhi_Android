package com.damuzhi.travel.service;

/**  
        * @description   ������
        * @version 1.0  
        * @author liuxiaokun  
        * @update 2012-5-9 ����4:12:15  
        */  
public class Task
{
	  private int taskID;//����ID
      private Object object;     
	 // private Map taskParam;//���ݲ���
	  public static final int TASK_USER_LOGIN=1;//�û���¼���� 
	  public static final int TASK_LOGIN_SCENERY=2;// ���뾰��ҳ��
	  public static final int TASK_LOGIN_HOTEL=3;// ����Ƶ�ҳ��
	  public static final int TASK_LOGIN_SHOPPING=4;// ���빺��ҳ��
	  public static final int TASK_LOGIN_RESTAURANT=5;// ����͹�ҳ��
	  public static final int TASK_LOGIN_ENTERTAINMNET=6;// ��������ҳ��
	  public static final int TASK_LOGIN_NEARBY=7;// ���븽��ҳ��
	  public static final int OVERVIEW=8;// ����OVERVIEWҳ��
	/*  public static final int CITY_BASE=8;// ������иſ�ҳ��
	  public static final int TRAVEL_PREPRATION=9;// ��������׼��ҳ��
	  public static final int TRAVEL_UTILITY=10;// ����ʵ����Ϣҳ��
	  public static final int TRAVEL_TRANSPORTATION=11;// ������н�ͨҳ��
*/	  public static final int TRAVEL_TIPS=12;// �����μǹ���ҳ��
	  public static final int TRAVEL_COMMEND=13;// ������·�Ƽ�ҳ��
	  public static final int MAP_NEARBY= 14;//��ͼ�ܱ��Ƽ�

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

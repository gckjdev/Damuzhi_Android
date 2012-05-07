package com.damuzhi.travel.model.entity;

public class DLState
{
	public static final int FINISH = 1;
	public static final int PAUSE = 2;
	public static final int DOWNLOADING = 3;
	public static final int FAILED = 4;
	public static final int RESTART = 5;
	public int mStatus;
	public String mKey;
	/**
	 * @param mStatus
	 * @param mkey
	 */
	public DLState(int mStatus, String mKey)
	{
		super();
		this.mStatus = mStatus;
		this.mKey = mKey;
	}
	
	
	
}

package com.damuzhi.travel.util;

import java.util.Comparator;

import com.damuzhi.travel.protos.AppProtos.City;

public class PinyinComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		 City city1 = (City) o1;
		 City city2 = (City) o2;
		 String countryName1 = city1.getCountryName();
		 String countryName2 = city2.getCountryName();
		 PingYinUtil pingYinUtil = new PingYinUtil();
		 String str1 = pingYinUtil.getPingYin(countryName1);
	     String str2 = pingYinUtil.getPingYin(countryName2);
	     return str1.compareTo(str2);
	}
	
	

}

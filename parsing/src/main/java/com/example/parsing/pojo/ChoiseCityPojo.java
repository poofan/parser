package com.example.parsing.pojo;

public class ChoiseCityPojo
{
	private String url;
	private String cityValue;
	
	public ChoiseCityPojo(String url, String cityValue)
	{
		this.url = url;
		this.cityValue = cityValue;
	}
	
	public String getUrl()
	{
		return url;
	}
	public String getCityValue()
	{
		return cityValue;
	}
	
}

package com.example.parsing.pojo;

import java.util.ArrayList;
import java.util.List;

public class ParseInfoPojo
{
	private List<String> email = new ArrayList<>();;
	private String name;
	private String url;
	private String cityValue;

	public String getName()
	{
		return name;
	}

	public String getUrl()
	{
		return url;
	}

	public String getCityValue()
	{
		return cityValue;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public void setCityValue(String cityValue)
	{
		this.cityValue = cityValue;
	}

	public List<String> getEmail()
	{
		return email;
	}

	public void setEmail(List<String> email)
	{
		this.email = email;
	}
	
}

package ict.ada.extractor;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Utilities
{
	/**
	 * 功能：统计字符串中非空白字符的个数，空白符列表：" \f\n\t\v\r"(第一个是空格)，不会改变输入字符串的值。
	 * 参数：str - [IN]需要统计非空白符的字符串。
	 * 返回：int 非空白符的个数。
	 **/
	public static int CalNoBlankNum( String str )
	{
		StandizeString(str);
		str = str.replaceAll("&#[0-9]{5};", " ");
		return str.length();
	}
	
	/**
	 * 功能：正规化字符串，把&nbsp;替换成空格，把多余一个的空格换成一个空格
	 * 参数：str 待转换的字符串
	 * 返回：转换后的字符串
	 **/
	public static String StandizeString( String str )
	{		
		str = str.replaceAll("&nbsp;" , " " );		
		str = str.replaceAll("&lt;" , "<" );
		str = str.replaceAll("&gt;" , ">" );
		str = str.replaceAll("&amp;", "&" );
		str = str.replaceAll("&apos;" , "'" );
		str = str.replaceAll("&quot;" , "\"" );
		str = str.replaceAll("&middot" , " " );
		str = str.replaceAll("[\\x20\\r\\n\\t]{2,}", " ");
		return str;		
	}
	
	public static String StandizeContent( String str )
	{		
		str = str.replaceAll("&nbsp;" , " " );		
		str = str.replaceAll("&lt;" , "<" );
		str = str.replaceAll("&gt;" , ">" );
		str = str.replaceAll("&amp;", "&" );
		str = str.replaceAll("&apos;" , "'" );
		str = str.replaceAll("&quot;" , "\"" );
		str = str.replaceAll("&middot;" , "·" );
		int pos = str.indexOf("&#");
		while(pos!=-1)
		{
			int charInt = 32;	
			int semicolonPos = str.indexOf(";", pos);
			if(str.substring(pos+2, pos+3)=="x")
			{
				try
				{
					charInt = Integer.parseInt(str.substring(pos+3, semicolonPos), 16);
				} catch (NumberFormatException e)
				{
					System.out.println(str.substring(pos+3, semicolonPos));
					e.printStackTrace();
				}
				str = str.replaceAll(str.substring(pos, semicolonPos+1),""+((char)charInt));
			}
			else
			{
				try
				{
					charInt = Integer.parseInt(str.substring(pos+2, semicolonPos), 10);
				} catch (NumberFormatException e)
				{
					System.out.println(str.substring(pos+2, semicolonPos));
					e.printStackTrace();
				}				
				str = str.replaceAll(str.substring(pos, semicolonPos+1),""+((char)charInt));
			}
			pos = str.indexOf("&#");
		}
		return str;		
	}
	
	
	
	public static void setID(Element element)
	{
		String prefix = "6167717379";
		Elements es = element.getAllElements();
		int i = 0;
		for(Element e:es)
		{
			if(e.id().equals(""))
				e.attr("id", prefix+i);
			i++;				
		}
		
	}
}

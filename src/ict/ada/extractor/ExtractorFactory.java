/**
 * 
 */
package ict.ada.extractor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import exception.ErrException;

/**
 * @author Administrator
 *
 */
public class ExtractorFactory
{
	public static NewsExtractor createSimpleExtractorByXMLFile(String configFile) throws ErrException
	{
		if(configFile == null||configFile.length()==0)
			throw new ErrException("配置文件名为空");	
		FileInputStream is;
		try
		{
			is = new FileInputStream(configFile);
		} catch (FileNotFoundException e)
		{
			throw new ErrException("配置文件未找到",e);
		}
		SAXReader reader = new SAXReader();
		Document configDom;
		try
		{
			configDom = reader.read(is);
		} catch (DocumentException e)
		{
			throw new ErrException("配置文件按XML格式解析失败",e);
		}
		
		NewsConfiguration newsConfig = new NewsConfiguration(configDom);	
		
		NewsExtractor se = new NewsExtractor(newsConfig);	
		
		return se;
	}

	public static NewsExtractor createSimpleExtractorDefualt()
	{
		NewsConfiguration newsConfig = new NewsConfiguration();	
		
		NewsExtractor se = new NewsExtractor(newsConfig);	
		
		return se;
	}
	
}

/**
 * 
 */
package ict.ada.extractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Node;

import exception.ErrException;


/**
 * @author Administrator
 *
 */
public class NewsConfiguration
{
//	public Document tagTree;         				//保存Jsoup生成的TagTree	
//	public Map<String,TagInfoNode> tagInfoMap; 		//保存对应节点信息的TagInfoMap	
	
	private ArrayList<String> cleanKeyWordList;       //需要清理的关键字集合
	private ArrayList<String> cleanNodeList;          //需要清理的文本节点列表
	private Set<String> cleanFormSet;                 //需要清理的标签集合
	private boolean preprocessPunctuation;		      //是否根据标点符号进行预先清理
	private boolean postprocessPunctuation;	    	  //是否根据标点符号进行后续清理
	private boolean deleteLinkList;				      //是否删除连接表
	private double acceptThreshold;					  //子节点是否被删除的门限值
	private double charLinkRatio;                     //字符链接比的权重
	private double textRatio;                         //包含字符的权重
	
	public NewsConfiguration()
	{
		this.cleanKeyWordList = new ArrayList<String>();
		this.cleanNodeList = new ArrayList<String>();
		this.preprocessPunctuation = true;
		this.acceptThreshold = 0.9;
		this.charLinkRatio = 0.99;
		this.textRatio = 1 - this.charLinkRatio;
		this.postprocessPunctuation = true;
		this.deleteLinkList = true;
		this.cleanFormSet = new HashSet<String>();
		String defaultTag = "form:input:textarea:marquee:object:select:iframe:style:script";
		String[] sTag = defaultTag.split(":"); 
		for(int i=0;i<sTag.length;i++)
		{
			if(sTag[i].trim().length()>0)
				this.cleanFormSet.add(sTag[i].trim());
		}
	}
	
	public NewsConfiguration(Document configDom) throws ErrException
	{
		String prePunctuationPath = "/extractor/is-preprocess-punctuation";
		String acceptThresholdPath = "/extractor/accept-threshold";
		String weightRatioPath = "/extractor/char-link-ratio";
		String weightText = "/extractor/text-ratio";
		String postPunctuationPath = "/extractor/is-postprocess-punctuation";
		String linkListPath = "/extractor/is-delete-link-list";
		String newsConfigFilePath = "/extractor/deletable-keywords-all";		//读取新闻的配置文件名（后续处理关键字）			
		String newsCleanNodeListPath = "/extractor/deletable-keywords-cur";		//读取新闻的可过滤节点文件名
		String formTagPath = "/extractor/FormTagList";      					//可清理的form标签的路径
		String scriptPath = "/extractor/ScriptTagList";    						//可清理script标签的路径
		
		this.cleanKeyWordList = new ArrayList<String>();
		this.cleanNodeList = new ArrayList<String>();
		
		Node temp =null;
		if((temp=configDom.selectSingleNode(newsConfigFilePath))!=null)
		{
			String newsConfigFileName = temp.getText().trim();
			FileInputStream fi;
			try
			{
				fi = new FileInputStream(newsConfigFileName);
			} catch (Exception e)
			{
				throw new ErrException("配置文件中，NewsConfigFile所指定的文件无法打开",e);
			}
			
			InputStreamReader ir = null;
			try
			{
				ir = new InputStreamReader(fi,"GB2312");
			} catch (UnsupportedEncodingException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			BufferedReader br = new BufferedReader(ir);
			String tempString;
			while(true)
			{
				try
				{
					tempString = br.readLine();
				} catch (IOException e)
				{
					throw new ErrException("NewsConfigFile读取时发生IO异常",e);
				}
				if(tempString==null)
					break;
				if(tempString.trim().length()>0)
					this.cleanKeyWordList.add(tempString.trim());
			}
		}		
		
		if((temp=configDom.selectSingleNode(newsCleanNodeListPath))!=null)
		{
			String newsCleanNodeList = temp.getText().trim();
			FileInputStream fi;
			try
			{
				fi = new FileInputStream(newsCleanNodeList);
			} catch (Exception e)
			{
				throw new ErrException("配置文件中，NewsFilterNode所指定的文件无法打开",e);
			}
			
			InputStreamReader ir = null;
			try
			{
				ir = new InputStreamReader(fi,"gb2312");
			} catch (UnsupportedEncodingException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			BufferedReader br = new BufferedReader(ir);
			String tempString;
			while(true)
			{
				try
				{
					tempString = br.readLine();
				} catch (IOException e)
				{
					throw new ErrException("NewsFilterNode读取时发生IO异常",e);
				}
				if(tempString==null)
					break;
				if(tempString.trim().length()>0)
					this.cleanNodeList.add(tempString.trim());
			}
		}
		
		//解析可以删除的form标签和script标签
		String[] formTag = null;                                        
		String defaultFormTag = "form:input:textarea:marquee:object:select:iframe";
		String[] scriptTag = null;
		String defaultScriptTag = "style:script";
		this.cleanFormSet = new HashSet<String>();
	
		if((temp=configDom.selectSingleNode(formTagPath))==null)
			formTag = defaultFormTag.split(":");
		else
			formTag = temp.getText().split(":");
		
		for(int i=0;i<formTag.length;i++)
		{
			if(formTag[i].trim().length()>0)
				this.cleanFormSet.add(formTag[i].trim());
		}
		
		if((temp=configDom.selectSingleNode(scriptPath))==null)
			scriptTag = defaultScriptTag.split(":");
		else
			scriptTag = temp.getText().split(":");
		
		for(int i=0;i<scriptTag.length;i++)
		{
			if(scriptTag[i].trim().length()>0)
				this.cleanFormSet.add(scriptTag[i].trim());
		}
		
		
		if((temp=configDom.selectSingleNode(prePunctuationPath))==null)
			this.preprocessPunctuation = true;
		else
		{
			if(temp.getText().trim().equals("true"))
				this.preprocessPunctuation = true;
			else if(temp.getText().trim().equals("false"))
				this.preprocessPunctuation = false;
			else 
			{
				throw new ErrException("is-preprocess-punctuation字段值不是true或者false");
			}
		}
		
		if((temp=configDom.selectSingleNode(acceptThresholdPath))==null)
			this.acceptThreshold = 0.9;
		else
		{
			try
			{
				this.acceptThreshold = Double.parseDouble(temp.getText());
			} catch (NumberFormatException e)
			{
				throw new ErrException("配置文件中，accept-threshold字段值不是浮点数类型",e);
			}
			
			if(this.acceptThreshold<0||this.acceptThreshold>1)
			{
				throw new ErrException("配置文件中，accept-threshold字段值不在[0,1]区间");
			}
		}
		
		if((temp=configDom.selectSingleNode(weightRatioPath))==null)
			this.charLinkRatio = 0.99;
		else
		{
			try
			{
				this.charLinkRatio = Double.parseDouble(temp.getText());
			} catch (NumberFormatException e)
			{
				throw new ErrException("配置文件中，char-link-ratio不是浮点数类型",e);
			}
			
			if(this.charLinkRatio<0||this.charLinkRatio>1)
			{
				throw new ErrException("配置文件中，char-link-ratio字段值不在[0,1]区间");
			}
		}
		
		if((temp=configDom.selectSingleNode(weightText))==null)
			this.textRatio = 1 - this.charLinkRatio;
		else
		{
			try
			{
				this.textRatio = Double.parseDouble(temp.getText());
			} catch (NumberFormatException e)
			{
				throw new ErrException("配置文件中，text-ratio不是浮点数类型",e);
			}
			
			if(this.textRatio<0||this.textRatio>1)
			{
				throw new ErrException("配置文件中，text-ratio字段值不在[0,1]区间");
			}
		}
		
		if((temp=configDom.selectSingleNode(postPunctuationPath))==null)
			this.postprocessPunctuation = true;
		else
		{
			if(temp.getText().trim().equals("true"))
				this.postprocessPunctuation = true;
			else if(temp.getText().trim().equals("false"))
				this.postprocessPunctuation = false;
			else 
			{
				throw new ErrException("is-postprocess-punctuation字段值不是true或者false");
			}
		}
		
		if((temp=configDom.selectSingleNode(linkListPath))==null)
			this.deleteLinkList = true;
		else
		{
			if(temp.getText().trim().equals("true"))
				this.deleteLinkList = true;
			else if(temp.getText().trim().equals("false"))
				this.deleteLinkList = false;
			else 
			{
				throw new ErrException("is-delete-ahref字段值不是true或者false");
			}
		}
	}

	public ArrayList<String> getCleanKeyWordList()
	{
		return cleanKeyWordList;
	}

	public ArrayList<String> getCleanNodeList()
	{
		return cleanNodeList;
	}

	public boolean isPreprocessPunctuation()
	{
		return preprocessPunctuation;
	}

	public boolean isPostprocessPunctuation()
	{
		return postprocessPunctuation;
	}

	public boolean isDeleteLinkList()
	{
		return deleteLinkList;
	}

	public double getAcceptThreshold()
	{
		return acceptThreshold;
	}

	public double getCharLinkRatio()
	{
		return charLinkRatio;
	}

	public double getTextRatio()
	{
		return textRatio;
	}

	public Set<String> getCleanFormSet()
	{
		return cleanFormSet;
	}

	

	
	
	
}

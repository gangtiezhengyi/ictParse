/**
 * 
 */
package ict.ada.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import exception.ErrException;




/**
 * @author Administrator
 *
 */
public class NewsExtractor implements IExtractor
{
	private NewsConfiguration configuration;
	public Document tagTree;         				//保存Jsoup生成的TagTree
	public Map<String,TagInfoNode> tagInfoMap; 		//保存对应节点信息的TagInfoMap
	
	/* (non-Javadoc)
	 * @see ict.ada.extractor.IExtractor#parse(java.lang.String)
	 */
	
	public NewsExtractor() {
		this.configuration = new NewsConfiguration();
	}
	
	public NewsExtractor(NewsConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public HTMLMetaData parse(String fileName)  
	{
		try
		{
			BuildTagTreeFromWebPageFile(fileName);
		} catch (ErrException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		String content = null;
		try
		{
			content = GetMainText();
		} catch (ErrException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		HTMLMetaData result = new HTMLMetaData();
		result.setHtmlContent(content);
		
		return result;
	}
	
	public HTMLMetaData parse(String fileName,String charSetName)
	{
		try
		{
			BuildTagTreeFromWebPageFile(fileName,charSetName);
		} catch (ErrException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		String content = null;
		try
		{
			content = GetMainText();
		} catch (ErrException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		HTMLMetaData result = new HTMLMetaData();
		result.setHtmlContent(content);
//		result.setHtmlContent(Utilities.StandizeString(content));
		
		return result;
	}
	
	public HTMLMetaData parseHtmlURL(String URLString)
	{
		try
		{
			BuildTagTreeFromURL(URLString);
		} catch (ErrException e)
		{
			// TODO Auto-generated catch block
			System.out.println(URLString);
			e.printStackTrace();
			return null;
		}
		
		String title = GetTitle();
		
		
		String content = null;
		try
		{
			content = GetMainText();
		} catch (ErrException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		HTMLMetaData result = new HTMLMetaData();
		result.setHtmlTitle(title);
		result.setHtmlContent(content);
		
//		result.setHtmlContent(Utilities.StandizeString(content));
		
		return result;
	}
	
	public HTMLMetaData parseHtmlString(String htmlString)
	{
		try
		{
			BuildTagTreeFromHtmlString(htmlString);
		} catch (ErrException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		String content = null;
		try
		{
			content = GetMainText();
		} catch (ErrException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		HTMLMetaData result = new HTMLMetaData();
		result.setHtmlContent(content);
//		result.setHtmlContent(Utilities.StandizeString(content));
		
		return result;
	}
	
	/**
	 * 功能： 使用默认编码格式，为输入的网页文件建立标签树，并把生成的标签树保存到tagTree
	 * @param fileName 待解析的网页文件
	 * @throws ErrException 网页文件打开、读取或者解析出错
	 */
	public void BuildTagTreeFromWebPageFile( final String fileName ) throws ErrException
	{		
		if(fileName == null||fileName.length()==0)
			throw new ErrException("文件名为空");
		FileInputStream fi;
		try
		{
			fi = new FileInputStream (fileName);
		} catch (Exception e)
		{
			throw new ErrException("输入页面文件打开失败");
		}
		
		InputStreamReader ir = null;
		
		ir = new InputStreamReader(fi);
		
		BufferedReader br = new BufferedReader(ir);
		
		String html = "";
		String temp = null;
		try
		{
			while((temp=br.readLine())!=null)
			{
				html += temp;
			}
		} catch (IOException e)
		{
			throw new ErrException("输入页面文件读取失败",e);
		}
		
		try
		{
			tagTree = Jsoup.parse(html);
		} catch (Exception e)
		{
			throw new ErrException("解析网页失败",e);
		}
		Utilities.setID(tagTree);		
	}
	
	/**
	 * 功能： 使用指定编码格式，为输入的网页文件建立标签树，并把生成的标签树保存到tagTree
	 * @param fileName 待解析的网页文件
	 * @param charsetName 文件编码格式
	 * @throws ErrException 
	 * 
	 */
	public void BuildTagTreeFromWebPageFile( final String fileName,final String charsetName) throws ErrException 
	{
		File inFile = new File(fileName);
		try
		{
			tagTree = Jsoup.parse(inFile,charsetName);
		} catch (IOException e)
		{
			throw new ErrException("解析网页失败",e);
		}
		Utilities.setID(tagTree);
	}
	
	/**
	 * 功能： 为输入的网页字符串建立标签树，并把生成的标签树保存到tagTree
	 * @param htmlString 包含待解析的网页信息的字符串
	 * @throws ErrException 
	 * 
	 */
	public void BuildTagTreeFromHtmlString( final String htmlString) throws ErrException 
	{
		try
		{
			tagTree = Jsoup.parse(htmlString);
		} catch (Exception e)
		{
			throw new ErrException("解析网页失败",e);
		}
		Utilities.setID(tagTree);	
	}
	
	/**
	 * 功能： 为输入的网页链接地址建立标签树，并把生成的标签树保存到tagTree
	 * @param URLString 待解析的网页的链接地址
	 * @throws ErrException 
	 * 
	 */
	public void BuildTagTreeFromURL( final String URLString) throws ErrException
	{
		
		try
		{
			URL url = new URL(URLString);
			tagTree = Jsoup.parse(url,10000);
		} catch (MalformedURLException e)
		{
			throw new ErrException("连接超时。",e);
		} catch (Exception e)
		{
			
			throw new ErrException("解析网页失败。",e);
		}
		Utilities.setID(tagTree);	
	}
	
	public void CleanUpFormTag()
	{
		Elements es = tagTree.getAllElements();
		for(Element e:es)
		{
			if(configuration.getCleanFormSet().contains(e.tagName()))
				e.remove();
		}
	}
	
	/**
	 * 功能：根据关键字对tagtree进行裁剪,即删除匹配关键字的节点和它之后的全部节点
	 **/
	public void CutDownTreeByKeyWord()
	{
		Elements es = tagTree.getAllElements();
		Element matchElement = null;
		
		for(Element e:es)
		{
			String text=e.ownText();
			if(text.length()!=0)
			{				
				for(int i=0;i<configuration.getCleanKeyWordList().size();i++)
				{
					if(text.indexOf(configuration.getCleanKeyWordList().get(i))!=-1)
					{
						matchElement = e;
						break;
					}
				}
			}
			if(matchElement!=null)
				break;
		}
		
		if(matchElement==null)
			return;
		
		Element tempElement = matchElement;
		//删除matchElement之后的所有节点
		Element parent = matchElement.parent();
		while(parent!=null)
		{
			Element nextSibling = matchElement.nextElementSibling();
			while(nextSibling!=null)
			{
				nextSibling.remove();
				nextSibling = matchElement.nextElementSibling();
			}
			matchElement = parent;
			parent = matchElement.parent();
		}
		
		//删除mathcElement节点本身
		tempElement.remove();
	}
	
	/**
	 * 功能：根据可过滤的节点列表进行文本节点的过滤，即删除匹配关键字的节点
	 **/
	public void CutDownTreeByNodeList()
	{
		Elements es = tagTree.getAllElements();
		
		for(Element e:es)
		{
			String text=e.ownText();
			if(text!=null)
			{
				for(int i=0;i<configuration.getCleanNodeList().size();i++)
				{
					if(text.indexOf(configuration.getCleanNodeList().get(i))!=-1)
					{
						e.remove();
					}
				}
			}
		}
	}
	
	/**
	 * 功能：根据m_TagTree生成tagInfoMap
	 **/	
	public void BuildTagInfoMap()
	{
		//tagInfoList = new ArrayList<String>();
		tagInfoMap = new HashMap<String,TagInfoNode>();
		
		Elements es = tagTree.getAllElements();
		
		for(Element e:es)
		{			
			String id = e.id();
			TagInfoNode node = new TagInfoNode();
			if(e.parent()==null)
				node.parentID = null;
			else
				node.parentID = e.parent().id();
			Elements children = e.children();
			for(Element child:children)
				node.childrenID.add(child.id());
			
			//tagInfoList.add(id);
			tagInfoMap.put(id, node);			
		}
	}
	
	/**
	 * 功能：计算每个节点含有的文本数和链接数，把计算的结果填充到tagInfoMap对应的节点中
	 **/
	public void CalTextNumAndLinkNum()
	{
		Elements es = tagTree.getAllElements();
		
		for(Element e:es)
		{
			String id = e.id();
			String text = null;
			if(e.tagName().equals("a"))
			{
				tagInfoMap.get(id).numOfLink++;
				tagInfoMap.get(id).numOfCharacter++;
			}
			else
			{
				text = e.ownText();				
				tagInfoMap.get(id).numOfCharacter += Utilities.CalNoBlankNum(text);
			}
			
			if(e.tagName().equals("a")||Utilities.CalNoBlankNum(text)>0)
			{
				Element parent = e.parent();
				while(parent!=null)
				{
					String parentID = parent.id();
					tagInfoMap.get(parentID).numOfLink += tagInfoMap.get(id).numOfLink;
					tagInfoMap.get(parentID).numOfCharacter +=tagInfoMap.get(id).numOfCharacter;
					parent = parent.parent();
				}
			}			
		}		
	}
	
	/**
	 * 功能：计算每个节点的文本链接比	
	 **/
	public void CalRemovalRatio()
	{
		Elements es = tagTree.getAllElements();
		
		for(Element e:es)
		{
			String id = e.id();
			int numOfCharacter = tagInfoMap.get(id).numOfCharacter;
			if(numOfCharacter==0)
				tagInfoMap.get(id).textScore = 0;
			else
				tagInfoMap.get(id).textScore = (double) (numOfCharacter - tagInfoMap.get(id).numOfLink)/numOfCharacter;
		}
	}
	
	/**
	 * 功能：根据文本链接比删除链接表
	 **/
	public void CleanUpLinkList()
	{		
		Elements es = tagTree.getAllElements();
		
		for(int i=0;i<es.size();)
		{
			Element e = es.get(i);
			String id = e.id();
			TagInfoNode info = tagInfoMap.get(id);
			if(info.textScore<0.0001&&info.numOfLink==1)
			{
				Element parent = e.parent();
				if(parent==null)//若父节点为空，则处理下一个兄弟节点，同时删除自身
				{
					Element nextSibling = e.nextElementSibling();
					i = es.indexOf(nextSibling);
					e.remove();
				}
				else
				{
					String parentID = parent.id();
					double parentScore = tagInfoMap.get(parentID).textScore;
					if(parentScore>0.7)//若父节点得分大于0.7，则处理下一结点
					{
						Element nextSibling = e.nextElementSibling();
						while(nextSibling==null)
						{
							e = e.parent();
							if(e==null)
								return;
							nextSibling = e.nextElementSibling();
						}
						i = es.indexOf(nextSibling);
					}
					else
					{
						Element grandparent = parent.parent();
						if(grandparent!=null)
						{
							double grandparenScore = tagInfoMap.get(grandparent.id()).textScore;
							
							while(grandparent!=null&&grandparenScore<parentScore)
							{
								parent = grandparent;
								parentScore = grandparenScore;
								
								grandparent = parent.parent();								
								grandparenScore = tagInfoMap.get(grandparent.id()).textScore;
							}
						}
						
						Element curElement = parent;
						Element nextSibling = curElement.nextElementSibling();
						while(nextSibling==null)
						{
							curElement = curElement.parent();
							if(curElement==null)
								return;
							nextSibling = curElement.nextElementSibling();
						}
						
						i = es.indexOf(nextSibling);
						parent.remove();
					}
				}
			}
			i++;
		}
		
		
		
	}
		
	/**
	 * 功能：返回抽取的新闻正文	 
	 * @return 新闻正文
	 * @throws ErrException 处理失败
	 **/
	public String GetMainText() throws ErrException  
	{			
		CleanUpFormTag();                                         //清理form标签
		CutDownTreeByKeyWord();                                   //根据关键字进行剪枝操作
		CutDownTreeByNodeList();                                  //根据可清理的节点列表进行裁剪
		BuildTagInfoMap();	                                      //建立TagInfoTree	
		
		if(configuration.isDeleteLinkList())
		{
			CalTextNumAndLinkNum();
			CalRemovalRatio(); 
			CleanUpLinkList();
//			m_TagInfoTree.clear();
			BuildTagInfoMap();			
		}
		
		if ( configuration.isPreprocessPunctuation() )
		{
			try
			{
				CutDownResultNodeByPunctuation( tagTree );
			} catch (ErrException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}		
		
		CalTextNumAndLinkNum();                                   //计算节点包含的字符和建立		
		Element bestElement = CalSetScore();                      //获得最佳节点
		
		if(bestElement==null)
			throw new ErrException("没有得到分值最高的节点");
		
		try
		{
			CutDownResultNode(bestElement);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( configuration.isPostprocessPunctuation() )
		{
			CutDownResultNodeByPunctuation( bestElement );
		} 
		
		return PrintResultToString(bestElement);
		//return bestElement.toString();
	}

	/**
	 * 功能：返回抽取的新闻标题	 
	 * @return 新闻标题
	 * @throws ErrException 处理失败
	 **/
	public String GetTitle()
	{
		String title = tagTree.title();
		
		Elements es = tagTree.getAllElements();
		Elements punctuation = tagTree.getElementsContainingOwnText("。");
		if(punctuation.size()==0)
			return title;
		String tempTitle = "";
		for(Element e:es)
		{
			if(e.equals(punctuation.get(0)))
				break;
			String text = e.ownText();
			if(text!=null&&title.indexOf(text)!=-1&&!title.equals(text)&&text.length()>tempTitle.length())
				tempTitle = text;
		}
		if(tempTitle!=null)
			title = tempTitle;
		return title;
	}
	
	/**
	 * 功能：根据标点符号裁剪节点
	 * @param element 待裁剪节点；
	 * @throws ErrException 待裁剪的元素为空

	 **/
	public static void CutDownResultNodeByPunctuation( Element element ) throws ErrException
	{		
		if(element==null)
			throw new ErrException("待裁剪的元素为空");
		
		Elements es = element.getElementsContainingOwnText("。");
		if(es.size()!=0)
		{
			Element e = es.get(0);			
			Element parent = e.parent();
			String id = e.id();
			
			while(parent!=null)
			{
				Elements siblings = parent.children();
				for(Element sibling:siblings)
				{
					
					if(sibling.id().equals(id))
						break;
					else
						sibling.remove();
				}
				id = parent.id();
				parent = parent.parent();
			}
			
			e = es.get(es.size()-1);
			parent = e.parent();
			id = e.id();		
			while(parent!=null)
			{
				Elements siblings = parent.children();
				
				for(int i=siblings.size()-1;i>=0;i--)
				{
					Element sibling = siblings.get(i);
					if(sibling.id().equals(id))
						break;
					else
						sibling.remove();
				}
				id = parent.id();
				parent = parent.parent();			
			}
		}
	}
	
	/**
	 * 功能：计算节点的得分，返回得分最高的节点	
	 * @return 返回得分最高的节点
	 **/
	public Element CalSetScore()
	{
		Element bestElement = null;
		
		int textPage = tagInfoMap.get(tagTree.id()).numOfCharacter;//页面包含的非空白文本个数
				
		double highestScore = -1; //保存当前的最高得分
					
		Elements es = tagTree.getAllElements();	
				
		for(Element e:es)
		{			
			TagInfoNode info = tagInfoMap.get(e.id());
			info.selectedChildrenID = new ArrayList<String>();
			Elements children = e.children();
			for(Element child:children)
			{
				TagInfoNode childInfo = tagInfoMap.get(child.id());
				int numOfCharacter = childInfo.numOfCharacter;
				if(numOfCharacter==0)
				{
					//child.remove();//测试
				}
				else if ((double)(numOfCharacter-childInfo.numOfLink)/numOfCharacter>configuration.getAcceptThreshold()) 
				{
					info.selectedChildrenID.add(child.id());
					info.numOfSetCharacter += childInfo.numOfCharacter;
					info.numOfSetLink += childInfo.numOfLink;
				}
				else {
					//child.remove();//测试
				}
			}
			
			if(info.numOfSetCharacter==0)
			{
				info.textScore = 0 ;
				e.attr("score", "m_fTextScore");
			}
			else
			{
				info.textScore = configuration.getCharLinkRatio() * ( info.numOfSetCharacter - info.numOfSetLink ) / info.numOfSetCharacter + configuration.getTextRatio() * info.numOfSetCharacter / textPage;
				//e.attr("score", "m_fTextScore");//测试
			}
			
			if(info.textScore>highestScore)
			{
				bestElement = e;
				highestScore = info.textScore;
				//e.attr("hightest", ""+i);//测试
			}
			
		}		
		return bestElement;		
	}
	
	/**
	 * 功能：裁剪结果节点。
	 * @param element 待裁剪的元素
	 * @throws ErrException 当待裁剪的元素为空时
	 **/
	public void CutDownResultNode( Element element ) throws ErrException
	{
		if(element==null)
			throw new ErrException("待裁剪的元素为空");
		
		Elements es = element.getAllElements();
		
		for(Element e:es)
		{		
			TagInfoNode info = tagInfoMap.get(e.id());
			if ( (double)(info.numOfCharacter - info.numOfLink)/info.numOfCharacter > configuration.getAcceptThreshold() )
			{
				continue;
			}
			else if ( info.numOfLink == 1 )
			{
				continue;
			}
			else if ( info.numOfCharacter == 0 )
			{
				continue;
			}
			else
			{
				e.remove();
			}
		}		
	}
		
	/**
	 * 功能：把统领节点下面的全部文本拼接成字符串
	 * @param element 统领节点
	 * @return 文本字符串
	 */
	public String PrintResultToString( Element element )
	{		
		String text = "";
		Stack<Node> nodesStack = new Stack<Node>();
		nodesStack.push(element);
		while(nodesStack.size()>0)
		{
			Node temp = nodesStack.pop();		
			String nodeName = temp.nodeName().toLowerCase();
			if(nodeName.equals("p")||nodeName.equals("br")||nodeName.equals("tr")||nodeName.equals("div"))
				if(text.length()==0||!text.endsWith("\n"))
				text += "\n";
			List<Node> children = temp.childNodes();
			if(children.size()>0)
			{
				for(int i=children.size()-1;i>-1;i--)
					nodesStack.push(children.get(i));				
			}
			else
			{				
				 if(temp.nodeName()=="#text")
					text += Utilities.StandizeContent(temp.toString()).trim();					
			}
		}
		return text.trim();
	}

	
	
	
	
	public NewsConfiguration getConfiguration()
	{
		return configuration;
	}
	
	public void setConfiguration(NewsConfiguration configuration)
	{
		this.configuration = configuration;
	}
	

}

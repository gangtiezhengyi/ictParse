package ict.ada.extractor;

import java.util.ArrayList;

public class TagInfoNode
{
	public String parentID;                            
	public ArrayList<String> childrenID;
	public int numOfCharacter;                          //节点及其全部子节点所包含的非空白文本数
	public int numOfLink ;                              //节点及其全部子节点所包含的链接个数
	public int numOfSetCharacter;                       //选作输出的子节点集合所包含的文本个数
	public int numOfSetLink;                            //选作输出的子节点集合所包含的链接个数
	public double textScore;                            //节点得分
	public ArrayList<String> selectedChildrenID;
	
	TagInfoNode()
	{
		parentID = null;
		childrenID = new ArrayList<String>();
		numOfCharacter = 0;                          
		numOfLink = 0 ;                              
		numOfSetCharacter = 0;                       
		numOfSetLink = 0;                            
		textScore = 0;                            
	}
	
}

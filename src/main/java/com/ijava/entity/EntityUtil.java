package com.ijava.entity;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: EntityUtil 
 * @Description: 生成entity实体类（工具类）
 * @author malongcheng 
 * @date 2018年5月10日
 *
 */
public class EntityUtil {
	
	//是否生成@JsonProperty注解
	private final static boolean jsonProperty = true;
	
	//生成文件路径
	private final static String filePath="D:\\workspace";
	
	//生成文件名称
	private final static String fileName="TrafficEvent";
	
	//生成文件包名
	private final static String bean_package="team.tusvn.framework.module.domain.traffic";
	
	private static Map<String,String> attrMappingMap = new HashMap<String,String>();
	
	private final static String attribute = 
						 "String,roadId,道路id;                   "
						+"List<String>,eventType,事件类型; "
						+"String,eventDescribe,事件描述;           "
						+"Float[],effectLane,影响车道;             "
						+"String,effectLaneDescribe,影响车道描述;   ";

	
	public static void buildEntityBean(){
		/*File folder = new File(bean_path);
	    if ( !folder.exists() ) {
	        folder.mkdir();
	    }

	    File beanFile = new File(bean_path, beanName + ".java");
	    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beanFile)));
	    bw.write("package " + bean_package + ";");
	    bw.newLine();
	    bw.write("import java.io.Serializable;");
	    bw.flush();
	    bw.close();*/
		try {
			RandomAccessFile accessFile = new RandomAccessFile(filePath+"\\"+fileName+".java","rw");
			FileChannel channel = accessFile.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(512);
			buffer.put(new String("package "+bean_package+";").getBytes());
			buffer.flip();
			channel.write(buffer);
			writeLineFeed(buffer,channel);
			writeImport(buffer,channel);
			writeOneWholeLine(buffer, channel, "public class "+fileName+" implements Serializable{");
			writeOneWholeLine(buffer, channel, "\t"+"private static final long serialVersionUID = 1L;");
			String[] splits = attribute.replace(" ", "").split(";");
			for (String split : splits) {
				String[] attr = split.split(",");
				String returnType = attr[0];
				String attributeName = attr[1];
				if(attr.length==3){
					String annotation = attr[2];
					writeAnnotation(buffer, channel, annotation);
				}
				if(jsonProperty){
					writeJsonProperty(buffer, channel, attributeName);
				}
				writeAttributeDeclare(buffer, channel, returnType, attributeName);
			}
			
			//get  set 方法
			for (String split : splits) {
				String[] attr = split.split(",");
				String returnType = attr[0];
				String attributeName = attr[1];
				writeGetAndSetMethod(buffer, channel, returnType, attributeName);
			}
			writeOneWholeLine(buffer, channel, "}");
			channel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}//读写模式，如果文件不存在，会自动创建
	}
	
	public static void writeImport(ByteBuffer buffer, FileChannel channel) throws IOException{
		String[] splits = attribute.replace(" ", "").split(";");
		String utilType = "List,Map,Set,Date";
		writeOneWholeLine(buffer, channel, "import java.io.Serializable;");
		
		for (int i = 0; i < splits.length; i++) {
			String[] split = splits[i].split(",");
			String returnType = split[0];
			if(returnType.contains("<")&&returnType.contains(">")){
				returnType = returnType.substring(0, returnType.indexOf("<"));
			}
			if(utilType.contains(returnType)){
				writeOneWholeLine(buffer, channel, "import java.util.*;");
				break;
			}
		}
		
		if(jsonProperty){
			writeOneWholeLine(buffer, channel, "import com.fasterxml.jackson.annotation.JsonProperty;");
		}
		
	}
	
	/**
	 * 
	 * @Description:输出行注解
	 * @param buffer
	 * @param channel
	 * @param annotation
	 * @throws IOException
	 * void
	 * @exception:
	 * @time:2018年5月10日
	 */
	public static void writeAnnotation(ByteBuffer buffer, FileChannel channel, String annotation) throws IOException{
		String str = "\t/* "+annotation+"*/";
		writeOneWholeLine(buffer, channel, str);
	}
	/**
	 * 
	 * @Description:输出@JsonProperty注解
	 * @param buffer
	 * @param channel
	 * void
	 * @throws IOException 
	 * @exception:
	 * @time:2018年5月10日
	 */
	public static void writeJsonProperty(ByteBuffer buffer, FileChannel channel, String attributeName) throws IOException{
		String str = "\t@JsonProperty(value="+attributeName+")";
		writeOneWholeLine(buffer, channel, str);
	}
	
	/**
	 * 
	 * @Description:输出属性声明
	 * void
	 * @exception:
	 * @time:2018年5月10日
	 */
	public static void writeAttributeDeclare(ByteBuffer buffer ,FileChannel channel ,String returnType ,String attributeName) throws Exception{
		String newAttributeName = "";
		//判断是否包含“_” 如果包含 属性名称变为驼峰形式，如write_attribute_declare------->writeAttributeDeclare
		//如果不包含“_”则不改变
		if(attributeName.contains("_")){
			String[] splits = attributeName.split("_");
			newAttributeName = splits[0];
			for (int i = 1; i < splits.length; i++) {
				newAttributeName += initialToUpperCase(splits[i]);
			}
			String str = "\t"+"private "+returnType+" "+newAttributeName+";";
			writeOneWholeLine(buffer, channel, str);
			attrMappingMap.put(attributeName,newAttributeName);
		}else{
			String str = "\t"+"private "+returnType+" "+attributeName+";";
			writeOneWholeLine(buffer, channel, str);
		}
		writeLineFeed(buffer,channel);
	}
	
	public static void writeGetAndSetMethod(ByteBuffer  buffer,FileChannel channel ,String returnType ,String attributeName) throws Exception{
		
		String newAttributeName = attrMappingMap.get(attributeName);
		if(newAttributeName==null){
			newAttributeName = attributeName;
		}
		String getStr1 = "\t"+"public "+returnType+" get"+initialToUpperCase(newAttributeName)+"(){";
		writeOneWholeLine(buffer, channel, getStr1);
		String getStr2 = "\t\t"+"return "+newAttributeName+";";
		writeOneWholeLine(buffer, channel, getStr2);
		String getStr3 = "\t}";
		writeOneWholeLine(buffer, channel, getStr3);
		writeLineFeed(buffer,channel);
		String setStr1 = "\t"+"public void set"+initialToUpperCase(newAttributeName)+"("+returnType+" "+newAttributeName+"){";
		writeOneWholeLine(buffer, channel, setStr1);
		String setStr2 = "\t\t"+"this."+newAttributeName+"="+newAttributeName+";";
		writeOneWholeLine(buffer, channel, setStr2);
		String setStr3 = "\t}";
		writeOneWholeLine(buffer, channel, setStr3);
		writeLineFeed(buffer,channel);
	}
	
	/**
	 * 
	 * @Description:输出一行字符串
	 * @param buffer
	 * @param channel
	 * @param str
	 * @throws IOException
	 * void
	 * @exception:
	 * @time:2018年5月10日
	 */
	public static void writeOneWholeLine(ByteBuffer buffer, FileChannel channel, String str) throws IOException{
		buffer.clear();
		buffer.put(new String(str).getBytes());
		buffer.flip();
		channel.write(buffer);
		writeLineFeed(buffer,channel);
	}
	
	/**
	 * 
	 * @Description:输出换行符
	 * @param buffer
	 * @param channel
	 * @throws IOException
	 * void
	 * @exception:
	 * @time:2018年5月9日
	 */
	public static void writeLineFeed(ByteBuffer buffer,FileChannel channel) throws IOException{
		buffer.clear();
		buffer.put(new String(System.getProperty("line.separator")).getBytes());
		buffer.flip();
		channel.write(buffer);
	}
	
	/**
	 * 
	 * @Description:首字母变换为大写
	 * @param str
	 * @return
	 * String
	 * @exception:
	 * @time:2018年5月10日
	 */
	public static String initialToUpperCase(String str){
		String upper = "";
		char[] cs=str.toCharArray();
        cs[0]-=32;
        upper += String.valueOf(cs);
		return upper;
	}
	
	public static void main(String[] args) {
		buildEntityBean();
	}
	
}

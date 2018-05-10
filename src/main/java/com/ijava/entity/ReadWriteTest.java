package com.ijava.entity;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * 
 * @ClassName: ReadWriteTest 
 * @Description: 一行一行读写文件内容
 * @author malongcheng 
 * @date 2018年5月10日
 *
 */
public class ReadWriteTest {
	
	
	public static void readFiles(String filepath){
		File file = new File(filepath);
		if(!file.isDirectory()){//文件
			System.out.println("name=" + file.getName());
			readWrite(file);
		}else if(file.isDirectory()){//文件夹
			String[] fileList = file.list();
			for (int i = 0; i < fileList.length; i++) {
				File readfile = new File(filepath+"\\"+fileList[i]);
				if(!readfile.isDirectory()){//文件
					System.out.println("name=" + readfile.getName());
					readWrite(file);
				}else if(readfile.isDirectory()){//文件夹
					readFiles(filepath+"\\"+fileList[i]);
				}
			}
		}
	}
	
	public static void readWrite(File file){
		
		try {
			
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			// 内存流, 作为临时流  
	        CharArrayWriter  tempStream = new CharArrayWriter();
	        // 替换  
	        String line = "";
			while ( line != null) {
				line = br.readLine();
				if(line != null){
					if(line.contains("_")&&!line.contains("@JsonProperty")){
						String[] splits = line.split("_");
						line = splits[0];
						for (int i = 0; i < splits.length; i++) {
							char[] cs=splits[i].toCharArray();
					        cs[0]-=32;
					        line += String.valueOf(cs);
						}
					}
					
		            // 将该行写入内存  
		            tempStream.write(line);
		            System.err.println(line);
		            // 添加换行符  
		            tempStream.append(System.getProperty("line.separator"));
				}
			}
			// 关闭 输入流  
			br.close();
			// 将内存中的流 写入 文件  
	        FileWriter out = new FileWriter(file);  
	        tempStream.writeTo(out);  
	        out.close();

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		readFiles("D:\\qhkjy\\tusvn.framework\\tusvn.framework.domain\\src\\main\\java\\team\\tusvn\\framework\\module\\domain\\vehicleunits\\VehicleUnitsWorkEvent.java");
	}
	
	
	
	
}

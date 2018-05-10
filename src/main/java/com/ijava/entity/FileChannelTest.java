package com.ijava.entity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 
 * @ClassName: FileChannelTest 
 * @Description: FileChannel 和 byteBuffer的一些测试
 * @author malongcheng 
 * @date 2018年5月10日
 *
 */
public class FileChannelTest {
	public static void testFileChannelOnWrite(String filePath) {
        try {
            @SuppressWarnings("resource")
			RandomAccessFile accessFile = new RandomAccessFile(filePath,"rw");//读写模式，如果文件不存在，会自动创建
            FileChannel fc = accessFile.getChannel();
            //System.getProperty("line.separator")  换行符
            byte[] bytes = new String("hello every one").getBytes();
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            System.out.println(new String(byteBuffer.array()));
            fc.write(byteBuffer);
            writeLineFeed(byteBuffer,fc);
            byteBuffer.clear();
            byteBuffer.put(new String(",a good boy").getBytes());
            printBufferContent(byteBuffer);
            byteBuffer.flip();
            fc.write(byteBuffer);
            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * 
	 * @Description:输出换行符
	 * @param byteBuffer
	 * @param fc
	 * @throws IOException
	 * void
	 * @exception:
	 * @time:2018年5月9日
	 */
	public static void writeLineFeed(ByteBuffer byteBuffer,FileChannel fc) throws IOException{
		byteBuffer.clear();
		byteBuffer.put(new String(System.getProperty("line.separator")).getBytes());
		byteBuffer.flip();
        fc.write(byteBuffer);
	}
	
	/**
	 * 
	 * @Description:控制台打印byteBuffer内容
	 * @param byteBuffer
	 * void
	 * @exception:
	 * @time:2018年5月9日
	 */
	public static void printBufferContent(ByteBuffer byteBuffer){
        printBufferContent("",byteBuffer);
	}
	
	public static void printBufferContent(String msg , ByteBuffer byteBuffer){
		byteBuffer.flip();
        byte[] array = new byte[byteBuffer.remaining()];
        byteBuffer.get(array);
        if(msg.equals("")){
        	System.out.println(new String(array));
        }else{
        	System.out.println(msg+"====="+new String(array));
        }
        
	}
	
	/**
	 * 
	 * @Description:打印byteBuffer四个指针的内容
	 * @param byteBuffer
	 * void
	 * @exception:
	 * @time:2018年5月9日
	 */
	public static void sprintMarkPositionLimitCapacity(String msg,ByteBuffer byteBuffer){
		System.out.println(msg+":::mark:"+mark(byteBuffer)+",Position:"+byteBuffer.position()+",Limit:"+byteBuffer.limit()+",Capacity:"+byteBuffer.capacity());
		System.out.println();
	}
	
	public static int mark(ByteBuffer byteBuffer){
		try {
			Field field = byteBuffer.getClass().getSuperclass().getSuperclass().getDeclaredField("mark");
			field.setAccessible(true);
			return field.getInt(byteBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -100;
	}
	
	
	
	public static void main(String[] args) {
		testFileChannelOnWrite("D:\\workspace\\");
		
	}
}

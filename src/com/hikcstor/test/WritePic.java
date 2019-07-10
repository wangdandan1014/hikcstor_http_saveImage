package com.hikcstor.test;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.attribute.IntegerSyntax;

import com.alibaba.fastjson.JSONObject;

public class WritePic {
	
	private Socket socket = null;
	private String gatewayPort = null;
	private String gatewayIP = null;
	private String token = null;
	private int port = 0;
	private DataOutputStream dos = null;
	private InputStream inputStream = null;
	
	
	public synchronized boolean writePic(String picPath){
	
		if(socket == null){
			getBestInfo();
			
		}
	
//		String picPath = "f:/HikCStor_pic.jpg";
		
		
		byte[] picBuf = readPicFile(picPath);
		String fileType = getFileType(picPath);

		String accessKey = GlobalConstant.accessKey;
		String secretKey = GlobalConstant.secretKey;
		String gmtDate = CommonProcess.getGmtTime();
		String content_type = "multipart/form-data";

		
		String auth = AuthProcess.getAuthorisation(accessKey, secretKey, "POST", "",
				content_type, gmtDate, "/HikCStor/Picture/Write");
		
		
		try{
			if(socket == null){
				socket = new Socket(gatewayIP,port);
			}
			
			dos = new DataOutputStream(socket.getOutputStream());
			
			String pic_len = Integer.toString(picBuf.length);
			
			String end_str = "\r\n";
			String twoHyphens = "--";
			String boundary_str = "7e02362550dc4";
			String divide = twoHyphens + boundary_str;
			String tail_str = divide + twoHyphens + end_str + end_str;
			
			String SerialID = GlobalConstant.serial_id;
			String poolId = GlobalConstant.pool_id;
			
			
			String format_head = divide + end_str;
			
			
			
			/**
			 * 拼接串最好修改为stringbuffer拼接
			 */
			
			format_head = format_head + " Content-Disposition: form-data;";
			format_head = format_head + " name=\"SerialID\"" + end_str + end_str + SerialID + end_str + divide + end_str + "Content-Disposition: form-data;";
			format_head = format_head + " name=\"PoolID\"" + end_str + end_str + poolId + end_str + divide + end_str + "Content-Disposition: form-data;";
			format_head = format_head + " name=\"TimeStamp\"" + end_str + end_str + "20161206161818888" + end_str + divide + end_str + "Content-Disposition: form-data;";
			format_head = format_head + " name=\"PictureType\"" + end_str + end_str + 1 + end_str + divide + end_str + "Content-Disposition: form-data;";
			format_head = format_head + " name=\"Token\"" + end_str + end_str + token + end_str + divide + end_str + "Content-Disposition: form-data;";
			format_head = format_head + " name=\"PictureLength\"" + end_str + end_str + pic_len + end_str + divide + end_str + "Content-Disposition: form-data;";
			format_head = format_head + " name=\"Picture\"" + end_str + "Content-Type: "+fileType + end_str + end_str;
			
			long content_len = format_head.length() +picBuf.length + tail_str.length();
			
			String url = "POST /HikCStor/Picture/Write HTTP/1.1";
			
			

			
			// http协议头
			String http_head = url + end_str;
			http_head = http_head + "Host: " + gatewayIP + end_str;
			http_head = http_head + "Accept-Language: zh-cn" + end_str;
			http_head = http_head + "Authorization: " + auth + end_str;
			http_head = http_head + "Date: " + gmtDate + end_str;
			http_head = http_head + "Content-Type: " + content_type + ";boundary=" + boundary_str + end_str;
			http_head = http_head + "Content-Length: " + content_len + end_str;
			http_head = http_head + "Connection: keep-alive" + end_str + end_str;
			
			// 1.先发送协议头
			dos.write(http_head.getBytes());
			// 2.发送表单内容
			dos.write(format_head.getBytes());
			// 3.发送图片数据
			dos.write(picBuf, 0, picBuf.length);
			// 4.发送结束标志
			dos.write(tail_str.getBytes());
			// 5.flush、close
			dos.flush();
			
			StringBuffer revBuf = new StringBuffer();
			
			byte[] response_buf = new byte[1024];
			inputStream = socket.getInputStream();
			inputStream.read(response_buf, 0, 1024);
			String ret = new String(response_buf);
			
			Pattern pattern = Pattern.compile("\"/pic\\?.*\"");
			Matcher matcher = pattern.matcher(ret);
			if (matcher.find()) {
				String imageUrl = matcher.group(0);
//				return imageUrl.replace("\"", "");
				System.out.println(imageUrl);
			} else {
				closeSocket();
				return false;
			}
			
//			System.out.println(ret);
	
		}catch(Exception ex){
			closeSocket();
			return false;
		}
		return true;
		
	}
	
	private static byte[] readPicFile(String pic_path){

		byte[] data = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			byte[] buffer = new byte[1024];
			is = new FileInputStream(pic_path);
			baos = new ByteArrayOutputStream();
			int n = 0;
			while (-1 != (n = is.read(buffer))) {
				baos.write(buffer, 0, n);
			}
			data = baos.toByteArray();
		} catch (Exception e) {
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				System.out.println(e.toString());
			}
			try {
				is.close();
			} catch (IOException e) {
				System.out.println(e.toString());
			}
			
			baos = null;
			is = null;
		}
		
		return data;
	}
	
	
	private static String getFileType(String pic_path){

		String file_type = "";
		try{
        /* 获取文件类型，示例代码中图片的类型为JPEG */
//		String rootPath = getClass().getResource("/").getFile().toString();
		InputStream file_stream = new BufferedInputStream(new FileInputStream(pic_path));
		file_type = URLConnection.guessContentTypeFromStream(file_stream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) { 	
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return file_type;
	}
	
	public void closeSocket(){
		
		if(dos != null){
			try{
				dos.close();
				dos = null;
			}catch(Exception ex){
				ex.printStackTrace();				
			}			
		}
		
		if(inputStream != null){
			try{
				inputStream.close();
				inputStream = null;
			}catch(Exception ex){
				ex.printStackTrace();				
			}			
		} 
		
		if(socket != null){
			try{
				socket.close();
				socket = null;
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}

	}
	
	private void getBestInfo(){
		String bestInfo = GetBestNode.getBestInfo();
		gatewayIP = JSONObject.parseObject(bestInfo).getString("GatewayIP");
		gatewayPort = JSONObject.parseObject(bestInfo).getString("GatewayPort");
		token = JSONObject.parseObject(bestInfo).getString("Token");
		
		try{
			port =  Integer.parseInt(gatewayPort);
		}catch(Exception ex){
			ex.printStackTrace();
			return;
		}
	}

}

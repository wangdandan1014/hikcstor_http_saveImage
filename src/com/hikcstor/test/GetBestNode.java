package com.hikcstor.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class GetBestNode {
	
public static String getBestInfo() {
	
		String serialID = GlobalConstant.serial_id;
		String pooleId = GlobalConstant.pool_id;
		String replication = String.valueOf(GlobalConstant.replication);
		String host = GlobalConstant.host;
		
		String http_uri = "/HikCStor/BestNode" + "?SerialID=" + serialID + "&PoolID=" + pooleId + 
				"&Replication=" + replication;		
		String endpoint = "http://" + host + http_uri;

		
		try {
			String date = CommonProcess.getGmtTime();
			
			URL restServiceURL = new URL(endpoint);
			HttpURLConnection httpConnection = (HttpURLConnection)restServiceURL.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setConnectTimeout(2000);
			httpConnection.setReadTimeout(1000);
			httpConnection.setRequestProperty("Date", date);
			httpConnection.setRequestProperty("Accept-Language", "zh-CN");
			httpConnection.setRequestProperty("Content-Type", "text/json");
			
			String accessKey = GlobalConstant.accessKey;
			String secretKey = GlobalConstant.secretKey;
			
			
			String auth = AuthProcess.getAuthorisation(accessKey, secretKey, "GET", "",
					"text/json", date, http_uri);
			
			httpConnection.setRequestProperty("Authorization", auth);
			if (httpConnection.getResponseCode() != 200) {
				System.out.println("----->>>获取云存储的Token出错, Error code : " + httpConnection.getResponseCode());
			}
			
			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));
			String output = "";
			String result = "";
			while ((output = responseBuffer.readLine()) != null) {
				result = result + output;
			}
			httpConnection.disconnect();
			httpConnection = null;
			
			return result;
			
			
			
		} catch (Exception e) {
			System.out.println("----->>>获取云存储的Token出错：" + e.getMessage());
		}
		return null;
	}
	

}

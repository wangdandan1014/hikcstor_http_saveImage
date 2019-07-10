package com.hikcstor.test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import it.sauronsoftware.base64.Base64;

public class AuthProcess {
	
	public static String getAuthorisation(String accesskey,String secretKey,String httpVerb,String contentMd5,
			String contentType,String date,String uri){		
		StringBuffer auth_data = new StringBuffer("");
		auth_data.append("hikcstor").append(" ");
		auth_data.append(accesskey).append(":");
		
		String signature = getSignature(secretKey,httpVerb,contentMd5,contentType,date,uri);
		auth_data.append(signature);
		
		return auth_data.toString();
	}
	
	private static String getSignature(String secretKey,String httpVerb,String contentMd5,String contentType,String date,
			String uri){
		
		String StringToSign = getStringToSign(httpVerb,contentMd5,contentType,date,uri);
		
		byte[] byteData = Base64.encode(getHMACData(StringToSign,secretKey));
		String encoderData = new String(byteData);
		return encoderData;
	}
	

	private static byte[] getHMACData(String data,String secretKey) {
		
		byte[] byteHMAC = null;
		String MAC_NAME = GlobalConstant.MAC_NAME;
		
		try{
			Mac mac = Mac.getInstance(MAC_NAME);
			SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(), MAC_NAME);
			mac.init(spec);

			// 此处保留
	        //System.out.println("UTF-8 before: " + data);
	        //String dataUTF = URLEncoder.encode(data, ENCODING);
	        //System.out.println("UTF-8 after:" + dataUTF);
	          
			byteHMAC = mac.doFinal(data.getBytes());
		}catch(InvalidKeyException e){
			e.printStackTrace();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}/*catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		
		return byteHMAC;
	}
	

	private static String getStringToSign (String httpVerb,String contentMd5,String contentType,String date,
			String uri){
		
		StringBuffer sign_data = new StringBuffer("");
		
		sign_data.append(httpVerb).append("\n");
		sign_data.append(contentMd5).append("\n");
		sign_data.append(contentType).append("\n");
		sign_data.append(date).append("\n");
		sign_data.append(uri);
		
		//System.out.println(sign_data.toString() + "\n");
		return sign_data.toString();
	}

}

import java.io.*;
import java.net.*;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

//Simple GET Connection tester
//originally referenced from http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
class ConnectionExample{
    

	public static void main(String[] args) throws Exception {

	}

	public static String send (String Url, String JSON_OBJECT)throws Exception 
{

        URL url;
  //      if(args.length != 2){
    //      //  System.out.println("Usage: java ConnectionExample <url>");
         //   System.exit(1);
      //  }
        //set the url to open
        url = new URL(Url);

        Reader serverResponse;
        //open connection based on if it's an HTTP or HTTPS connection



        Map<String,Object> params = new LinkedHashMap<>();
        params.put("data", JSON_OBJECT); 



 StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");




        if(Url.indexOf("https") == 0){
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            connection.setDoOutput(true);

		
            connection.getOutputStream().write(postDataBytes);
	    //connection.setRequestMethod("GET");
            //connection.setDoOutput(true);
            serverResponse = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        }else{
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            connection.setDoOutput(true);

		
            connection.getOutputStream().write(postDataBytes);



	//connection.setRequestMethod("GET");
          //  connection.setDoOutput(true);
            serverResponse = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        }

        //receive response and print out the result
        StringBuilder sb = new StringBuilder();
        for(int c; (c = serverResponse.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();

        System.out.println(response);
	return response;
    }
}
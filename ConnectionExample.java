import java.io.*;
import java.net.*;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

//Simple GET Connection tester
//originally referenced from http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
class ConnectionExample{
    public static void main(String[] args) throws Exception {
        URL url;
        if(args.length != 1){
            System.out.println("Usage: java ConnectionExample <url>");
            System.exit(1);
        }
        //set the url to open
        url = new URL(args[0]);

        Reader serverResponse;
        //open connection based on if it's an HTTP or HTTPS connection
        if(args[0].indexOf("https") == 0){
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            serverResponse = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        }else{
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            serverResponse = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        }

        //receive response and print out the result
        StringBuilder sb = new StringBuilder();
        for(int c; (c = serverResponse.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
        System.out.println(response);
    }
}
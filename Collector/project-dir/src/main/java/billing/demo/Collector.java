package billing.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;


import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;



public class Collector implements RequestStreamHandler {
    JSONParser parser = new JSONParser();


    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");


        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
  

	String user = null;
	String metric = null;
	String countStr = null;
    int count = 0;
        String responseCode = "200";

        try {
            JSONObject event = (JSONObject)parser.parse(reader);
            if (event.get("queryStringParameters") != null) {
                JSONObject qps = (JSONObject)event.get("queryStringParameters");
                if ( qps.get("metric") != null) {
                    metric = (String)qps.get("metric");
                }
            }
	    if (event.get("queryStringParameters") != null) {
                JSONObject qps = (JSONObject)event.get("queryStringParameters");
                if ( qps.get("count") != null) {
                    countStr = (String)qps.get("count");
                }
            }
            count = Integer.parseInt(countStr);
	

            if (event.get("pathParameters") != null) {
                JSONObject pps = (JSONObject)event.get("pathParameters");
                if ( pps.get("proxy") != null) {
                    user = (String)pps.get("proxy");
                }
            }


            String greeting = "Input parameters are ";
            if (user != null && metric != null && countStr != null) greeting += "" + user + " " + metric + " " + count + ";";

            String invokeResult = "";
            invokeResult = invokeUsersHandler(formatPayLoad(user, metric, count));

            greeting += invokeResult;

            JSONObject responseBody = new JSONObject();
            responseBody.put("input", event.toJSONString());
            responseBody.put("message", greeting);

            JSONObject headerJson = new JSONObject();
            headerJson.put("x-custom-header", "my custom header value");

            responseJson.put("isBase64Encoded", false);
            responseJson.put("statusCode", responseCode);
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody.toString());  

        } catch(ParseException pex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", pex);
        }


	

        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toJSONString());  
        writer.close();
    }
    private String invokeUsersHandler(String payLoad){
        String awsAccessKeyId = System.getenv("awsAccessKeyId");
        String awsSecretAccessKey = System.getenv("awsSecretAccessKey");
        String regionName = System.getenv("regionName");
        String functionName = System.getenv("functionName");

        Region region;
        AWSCredentials credentials;
        AWSLambdaClient lambdaClient;

        region = Region.getRegion(Regions.fromName(regionName));
        credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
        lambdaClient = (credentials == null) ? new AWSLambdaClient() : new AWSLambdaClient(credentials);
        lambdaClient.setRegion(region);

        String result = null;

        try{

            InvokeRequest invokeRequest = new InvokeRequest();
            invokeRequest.setFunctionName(functionName);
            invokeRequest.setPayload(payLoad);

            result = byteBufferToString(lambdaClient.invoke(invokeRequest).getPayload(), Charset.forName("UTF-8"));
        } catch(Exception e) {
            System.out.println("Exception while invoke");
        }
        return result;
    }

    private String formatPayLoad(String user, String metric, int count){
        return "{ \"userId\" : \"" + user + "\", \"metric\" : \"" + metric + "\", \"count\" : " + count + " }";
    }

    public String byteBufferToString(ByteBuffer buffer, Charset charset) {
        byte[] bytes;
        if (buffer.hasArray()) {
            bytes = buffer.array();
        } else {
            bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
        }
        return new String(bytes, charset);
    }
}
	

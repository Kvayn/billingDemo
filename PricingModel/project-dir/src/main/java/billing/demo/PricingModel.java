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

import com.amazonaws.services.lambda.runtime.RequestHandler;
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



public class PricingModel implements RequestHandler<Request, String>  {
    JSONParser parser = new JSONParser();



    @Override
    public String handleRequest(Request request, Context context) {

        LambdaLogger logger = context.getLogger();

        JSONObject responseJson = new JSONObject();
  
		String user = null;
		String metric = null;
		String countStr = null;
    	int count = 0;

    	String userData = invokeReader(formatPayLoad(request.userId));
    	return userData;
        


    }
    private String invokeReader(String payLoad){
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

    private String formatPayLoad(String user){
        return "{ \"userId\" : \"" + user + "\" }";
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
	

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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;



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


    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    	boolean dateRequest = !((request.from == null)&(request.to == null));
    	
    	String payLoad = "";

    	if (!dateRequest) {
    		payLoad = formatPayLoad(request.userId, null, null);
    	} else {
    		payLoad = formatPayLoad(request.userId, df.format(request.from), df.format(request.to));
    	}

    	

    	System.out.println("Payload is :" + payLoad);

    	String userData = invokeReader(payLoad);
    	userData = userData.replace("\\", "");
    	userData = userData.substring(1, userData.length() - 1);
    	System.out.println(userData);
    	Users agregatedUser = new Users();
    	ObjectMapper mapper = new ObjectMapper();
    	
    	//String tmp1 = tmp.replace("\"", "");
    	try{
    		agregatedUser = mapper.readValue(userData, Users.class);
    	} catch(Exception e){
    		return "Parsing error";
    	}

    	metric = agregatedUser.getMetric();
    	double price = Double.parseDouble(System.getenv(metric));
    	double bill = price*agregatedUser.getCount();
        bill = bill - (bill*getDiscount(agregatedUser.getUserId()));

        NumberFormat formatter = new DecimalFormat("#0.0000");     

    	String result = "User " + agregatedUser.getUserId() + " is billed for " + formatter.format(bill) + " CHF";
    	if (dateRequest) {
    		result += " in perid from " + df.format(request.from) + " to " + df.format(request.to);
    		
    	}

    	return result;
        


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

    private String formatPayLoad(String user, String from, String to){
    	if ((from == null)&(to == null)) {
    		return "{ \"userId\" : \"" + user + 
        			"\",\"from\" : null" +
        			",\"to\" : null" + 
        			 '}';
    	}
    	return "{ \"userId\" : \"" + user + 
        		"\",\"from\" : \"" + from +
        		"\",\"to\" : \"" + to + 
        		 "\"}";
    	}
    private double getDiscount(String userId){
        if (System.getenv(userId) == null) {
            return 1;
        }
        return Double.parseDouble(System.getenv(userId));
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
	

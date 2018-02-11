package billing.demo.cli;

import com.beust.jcommander.*;
import com.beust.jcommander.JCommander;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Main{
	
	public static void main(String[] args) {
		CommandGetbill getBill = new CommandGetbill();
		CommandConfig config = new CommandConfig();
		CommandGetPeriodBill getPeriodBill = new CommandGetPeriodBill();
		Main main = new Main();
		JCommander jc = JCommander.newBuilder()
		.addObject(main)
		.addCommand("getperiodbill", getPeriodBill)
		.addCommand("getbill", getBill)
		.addCommand("config", config)
		.build();

		jc.parse(args);

		String parsedCommand = jc.getParsedCommand();
		String result = "";

		if(parsedCommand.equals("getbill")){
			 result = invokeReader(formatNullPayLoad(getBill.userId));
		} else {
			if (parsedCommand.equals("getperiodbill")) {
				String from = getPeriodBill.from;
				String to = getPeriodBill.to;
				result = invokeReader(formatPayLoad(getPeriodBill.userId, from, to));
			}
		}





		//String result = invokeReader(formatPayLoad(getBill.userId));

		System.out.println(result);

		
	}
	private static String invokeReader(String payLoad){
		AwsCredentialsReader credentialsReader = new AwsCredentialsReader();
		credentialsReader.read();

        String awsAccessKeyId = credentialsReader.getAwsAccessKeyId();
        String awsSecretAccessKey = credentialsReader.getAwsSecretAccessKey();
        String regionName = "us-west-2";
        String functionName = "PricingModel";

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

    private static String formatPayLoad(String user, String from, String to){
        return "{ \"userId\" : \"" + user + 
        		"\", \"from\" : \"" + from + 
        		"\",\"to\" : \"" + to +
        		"\"}";
    }
    private static String formatNullPayLoad(String user){
        return "{ \"userId\" : \"" + user + 
        		"\", \"from\" : null " + 
        		",\"to\" : null }";
    }

    public static String byteBufferToString(ByteBuffer buffer, Charset charset) {
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





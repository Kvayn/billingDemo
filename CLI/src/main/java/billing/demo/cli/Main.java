package billing.demo.cli;

import com.beust.jcommander.*;
import com.beust.jcommander.JCommander;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.Environment;
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationRequest;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.HashMap;


import java.io.File;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
			} else {
				if(parsedCommand.equals("config")){
					ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
					try {
            			Config configFile = mapper.readValue(config.file, Config.class);
            			Map<String, String> environments = new HashMap<String, String>();
            			environments.putAll(configFile.getMetrics());
            			environments.putAll(configFile.getDiscounts());
            			updateConfig(environments);
      //       			for (Map.Entry<String, String> entry : configFile.getMetrics().entrySet()){
      //       				String key = entry.getKey();
      //       				double value = Double.parseDouble(entry.getValue());
    		// 				System.out.println(key + "/" + value);
						// }
						// for (Map.Entry<String, String> entry : configFile.getDiscounts().entrySet()){
      //       				String key = entry.getKey();
      //       				double value = Double.parseDouble(entry.getValue());
    		// 				System.out.println(key + "/" + value);
						// }



            			
        			} catch (Exception e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
        			}
				}
			}
		}





		//String result = invokeReader(formatPayLoad(getBill.userId));

		System.out.println(result);

		
	}
	private static void updateConfig(Map<String, String> envVars){
		AwsCredentialsReader credentialsReader = new AwsCredentialsReader();
		credentialsReader.read();

        String awsAccessKeyId = credentialsReader.getAwsAccessKeyId();
        String awsSecretAccessKey = credentialsReader.getAwsSecretAccessKey();
        String regionName = "us-west-2";
        String functionName = "PricingModel";

        envVars.put("awsAccessKeyId", awsAccessKeyId);
        envVars.put("awsSecretAccessKey", awsSecretAccessKey);
        envVars.put("regionName", regionName);
        envVars.put("functionName", "Reader");

        Region region;
        AWSCredentials credentials;
        AWSLambdaClient lambdaClient;

        region = Region.getRegion(Regions.fromName(regionName));
        credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
        lambdaClient = (credentials == null) ? new AWSLambdaClient() : new AWSLambdaClient(credentials);
        lambdaClient.setRegion(region);
        try{
        	UpdateFunctionConfigurationRequest invokeConfigRequest = new UpdateFunctionConfigurationRequest();
        	invokeConfigRequest.setFunctionName(functionName);
        	Environment environment = new Environment();
        	environment.setVariables(envVars);
        	invokeConfigRequest.setEnvironment(environment);
        	lambdaClient.updateFunctionConfiguration(invokeConfigRequest);

        }catch(Exception e){
        	System.out.println("update environment exception");
        }
        

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





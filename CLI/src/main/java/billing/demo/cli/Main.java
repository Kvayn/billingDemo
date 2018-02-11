package billing.demo.cli;

import com.beust.jcommander.*;
import com.beust.jcommander.JCommander;

public class Main{
	
	public static void main(String[] args) {
		CommandGetbill getBill = new CommandGetbill();
		CommandConfig config = new CommandConfig();
		Main main = new Main();
		JCommander.newBuilder()
		.addObject(main)
		.addCommand("getbill", getBill)
		.addCommand("config", config)
		.build()
		.parse(args);
		System.out.println("userID = " + getBill.userId);

		
	}
}





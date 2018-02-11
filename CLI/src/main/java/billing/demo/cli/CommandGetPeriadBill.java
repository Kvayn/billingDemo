package billing.demo.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(separators = " ")
public class CommandGetbill {
	@Parameter(names={"--user", "-u"})
	String userId;
	@Parameter(names={"--from", "-f"})
	String from;
	@Parameter(names={"--to", "-t"})
	String to;
}

package billing.demo.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(separators = " ")
public class CommandGetPeriodBill {
	@Parameter(names={"--user", "-u"}, required = true)
	String userId;
	@Parameter(names={"--from", "-f"}, required = true)
	String from;
	@Parameter(names={"--to", "-t"}, required = true)
	String to;
}

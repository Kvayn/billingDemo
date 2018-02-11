package billing.demo.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(separators = " ")
public class CommandGetbill {
	@Parameter(names={"--user", "-u"}, required = true)
	String userId;
}

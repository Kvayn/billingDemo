package billing.demo.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.io.File;


@Parameters(separators = " ")
public class CommandConfig {
	@Parameter(names={"--file", "-f"}, converter=FileConverter.class)
	File file;
}

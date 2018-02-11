package billing.demo.cli;

import com.beust.jcommander.*;
import java.io.File;

public class FileConverter implements IStringConverter<File>{
	@Override
	public File convert(String value){
		return new File(value);
	}
}

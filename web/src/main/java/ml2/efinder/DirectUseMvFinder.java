package ml2.efinder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.tzi.use.uml.sys.MSystemState;

import efinder.usemv.UseMvFinder;

public class DirectUseMvFinder extends UseMvFinder {

	public MSystemState directFind(File useFile, File propertiesFile) throws Exception {
		String useSpec = IOUtils.toString(new FileInputStream(useFile), Charset.defaultCharset());
		String props = IOUtils.toString(new FileInputStream(propertiesFile), Charset.defaultCharset());
		return directFind(useSpec, props);
	}

	public MSystemState directFind(String useSpec, String props) throws Exception {
		KodkodResult result = doFind(new ByteArrayInputStream(useSpec.getBytes()), new StringReader(props));
		if (result.isSatisfiable()) {
			return fSession.system().state();	
		}
		return null;
	}
	
}

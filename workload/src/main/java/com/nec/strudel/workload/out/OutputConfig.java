package com.nec.strudel.workload.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.json.JsonPrinter;

public class OutputConfig {
	private static final JsonObject EMPTY_OBJ =  Json.createObjectBuilder().build();
	public static final String FILE_EXT_JSON = "json";
	private static final Logger LOGGER =
		    Logger.getLogger(OutputConfig.class);

    private JsonObject include = EMPTY_OBJ;

    @Nullable
    private String dstDir;
    

    public OutputConfig() {
    }

    public void warnings(Collection<String> warnings) {
		for (String w : warnings) {
			LOGGER.warn(
				"warning in workload: " + w);
		}
    }
    public void setDstDir(String dstDir) {
		this.dstDir = dstDir;
	}
    public String getDstDir() {
		return dstDir;
	}
 
    public JsonObject getInclude() {
		return include;
	}
    public void setInclude(JsonObject include) {
		this.include = include;
	}

    public void output(OutputSet out, JsonObject input) {
        /**
         * now: outputs are all flatten
         * TODO custom output selection
         */
    	JsonObject json =
    			ResultQuery.query()
    			.output(out.flatten())
    			.execute(input);

    	PrintStream outStr = getOutStream();
    	try {
        	new JsonPrinter(outStr).print(json);
    	} finally {
    		if (outStr != System.out) {
    			outStr.close();
    		}
    	}
    }

    protected PrintStream getOutStream() {
    	if (dstDir != null) {
			File dstFile = generateFile(dstDir);
			LOGGER.info("result file: " + dstFile.getAbsolutePath());
    		try {
				return new PrintStream(dstFile);
			} catch (FileNotFoundException e) {
				LOGGER.error("failed to write a file (using stdout): "
						+ dstFile.getAbsolutePath());
				return System.out;
			}
    	}
    	return System.out;
    }

    protected static File generateFile(String dirPath) {
    	File dir = new File(dirPath);
    	dir.mkdirs();
    	SimpleDateFormat df =
    			new SimpleDateFormat("yyyy-MM-dd'T'HHmmss");
    	String date = df.format(new Date());
    	String suff = "." + FILE_EXT_JSON;
    	File file = new File(dir, date + suff);
    	int i = 1;
    	while (file.exists()) {
    		file = new File(dir, date + "_" + i + suff);
    		i++;
    	}
    	return file;
    }
}

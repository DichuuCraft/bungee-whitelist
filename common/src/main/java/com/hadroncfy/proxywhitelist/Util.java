package com.hadroncfy.proxywhitelist;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Util {
    public static String readFileSync(File file, ILogger logger){
        InputStream is = null;
		try {
			is = new FileInputStream(file);
            return IOUtils.toString(is, Charsets.UTF_8);
		} catch (FileNotFoundException e) {
            logger.error("File " + file.getName() + " not found: " + e.getMessage());
        }
        catch (IOException e) {
            logger.error("Cannot read file " + file.getName() + ": " + e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

    public static boolean writeFileSync(File file, String content, ILogger logger){
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            OutputStreamWriter w = new OutputStreamWriter(os);
            w.append(content);
            w.close();
            return true;
		} catch (FileNotFoundException e) {
			logger.error("File " + file.toString() + " not found: " + e.getMessage());
		} catch (IOException e){
            logger.error("Cannot write file " + file.toString() + ": " + e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(os);
        }
        return false;
    }
}
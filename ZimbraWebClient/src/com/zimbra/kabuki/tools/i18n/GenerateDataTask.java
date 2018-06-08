/*
 * 
 */


package com.zimbra.kabuki.tools.i18n;

import java.io.File;
import org.apache.tools.ant.*;

public class GenerateDataTask
	extends Task {

	//
	// Data
	//

	// required

	private File destdir = null;
	private String basename = "I18nMsg";
	private boolean client = true;
	private boolean server = false;

	//
	// Public methods
	//

	// required

	public void setDestDir(File dir) {
		this.destdir = dir;
	}

	public void setBaseName(String basename) {
		this.basename = basename;
	}

	public void setClient(boolean generate) {
		this.client = generate;
	}

	public void setServer(boolean generate) {
		this.server = generate;
	}

	//
	// Task methods
	//

	public void execute() throws BuildException {

		// check required arguments
		if (destdir == null) {
			throw new BuildException("destination directory required -- use destdir attribute");
		}
		if (!destdir.exists()) {
			throw new BuildException("destination directory doesn't exist");
		}
		if (!destdir.isDirectory()) {
			throw new BuildException("destination must be a directory");
		}

		// build argument list
		String[] argv = {
			this.client ? "-c" : "-C", this.server ? "-s" : "-S",
			"-d", destdir.getAbsolutePath(), "-b", basename
		};

		// run program
		try {
			System.out.print("GenerateData");
			for (String arg : argv) {
				System.out.print(' ');
				System.out.print(arg);
			}
			System.out.println();
			GenerateData.main(argv);
		}
		catch (Exception e) {
			throw new BuildException(e);
		}

	} // execute()

} // class GenerateDataTask
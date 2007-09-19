package org.cubictest.exporters.cubicunit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.IBuildPathSupporter;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

public class BuildPathSupporter implements IBuildPathSupporter {

	public List<File> getFiles() {
		List<File> files = new ArrayList<File>();
		Bundle bundle = CubicUnitRunnerPlugin.getDefault().getBundle();
		try {
			files.add(new Path(FileLocator.toFileURL(FileLocator.find(bundle,new Path("lib/selenium-server.jar"),null)).getPath()).toFile());
			files.add(new Path(FileLocator.toFileURL(FileLocator.find(bundle,new Path("lib/jiffie.jar"),null)).getPath()).toFile());
		} catch (IOException e) {
			ErrorHandler.logAndRethrow(e);
		}
		return files;
	}



}

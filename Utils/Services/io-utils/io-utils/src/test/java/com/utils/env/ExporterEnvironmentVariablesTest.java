package com.utils.env;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.utils.io.PathUtils;
import com.utils.string.StrUtils;

class ExporterEnvironmentVariablesTest {

	@Test
	void testWork() {

		final Path outputPath = Paths.get(PathUtils.ROOT_PATH, "env_" + StrUtils.createDateTimeString() + ".txt");
		final ExporterEnvironmentVariables exporterEnvironmentVariables =
				FactoryExporterEnvironmentVariables.newInstance(outputPath);
		exporterEnvironmentVariables.work();
	}
}

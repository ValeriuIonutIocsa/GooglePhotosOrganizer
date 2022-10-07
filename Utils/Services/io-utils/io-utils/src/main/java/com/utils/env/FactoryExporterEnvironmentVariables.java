package com.utils.env;

import java.nio.file.Path;

import com.utils.annotations.ApiMethod;

public final class FactoryExporterEnvironmentVariables {

	private FactoryExporterEnvironmentVariables() {
	}

	@ApiMethod
	public static ExporterEnvironmentVariables newInstance(
			final Path outputPath) {
		return new ExporterEnvironmentVariablesWindows(outputPath);
	}
}

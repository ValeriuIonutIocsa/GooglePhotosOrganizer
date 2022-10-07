package com.utils.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;

public final class PathUtils {

	public static final String ROOT_PATH = createRootPath();

	private static String createRootPath() {

		final String rootPath;
		if (SystemUtils.IS_OS_WINDOWS) {
			rootPath = "D:\\";
		} else {
			rootPath = "/mnt/d";
		}
		return rootPath;
	}

	private PathUtils() {
	}

	@ApiMethod
	public static Path tryParseExistingFilePath(
			final String pathName,
			final String pathStringParam) {

		Path path = tryParsePath(pathName, pathStringParam);
		final String pathString = path.toString();
		final boolean fileExists = IoUtils.fileExists(pathString);
		if (!fileExists) {

			Logger.printError(pathName + " does not exist:" +
					System.lineSeparator() + pathString);
			path = null;
		}
		return path;
	}

	@ApiMethod
	public static Path tryParseExistingFolderPath(
			final String pathName,
			final String pathStringParam) {

		Path path = tryParsePath(pathName, pathStringParam);
		final String pathString = path.toString();
		final boolean directoryExists = IoUtils.directoryExists(pathString);
		if (!directoryExists) {

			Logger.printError(pathName + " does not exist:" +
					System.lineSeparator() + pathString);
			path = null;
		}
		return path;
	}

	@ApiMethod
	public static Path tryParsePath(
			final String pathName,
			final String pathString) {

		Path path = null;
		try {
			path = Paths.get(pathString);

		} catch (final Exception exc) {
			if (StringUtils.isNotBlank(pathName)) {
				Logger.printError("failed to parse " + pathName + " path:" +
						System.lineSeparator() + pathString);
				Logger.printException(exc);
			}
		}
		return path;
	}

	@ApiMethod
	public static Path tryParseAbsolutePath(
			final String pathName,
			final String pathString,
			final String rootPathString) {

		Path path = null;
		try {
			path = Paths.get(pathString);
			if (!path.isAbsolute()) {
				path = Paths.get(rootPathString, pathString);
			}

		} catch (final Exception exc) {
			if (StringUtils.isNotBlank(pathName)) {
				Logger.printError("failed to parse " + pathName + " path:" +
						System.lineSeparator() + pathString);
				Logger.printException(exc);
			}
		}
		return path;
	}

	@ApiMethod
	public static String tryParseAbsolutePathString(
			final String pathName,
			final String pathString,
			final String rootPathString) {

		String absolutePathString = pathString;
		try {
			Path path = Paths.get(pathString);
			if (!path.isAbsolute()) {

				path = Paths.get(rootPathString, pathString);
				absolutePathString = path.toString();
			}

		} catch (final Exception exc) {
			if (StringUtils.isNotBlank(pathName)) {
				Logger.printError("failed to parse " + pathName + " path:" +
						System.lineSeparator() + pathString);
				Logger.printException(exc);
			}
		}
		return absolutePathString;
	}

	@ApiMethod
	public static String computeFileName(
			final Path path) {

		final String pathString = String.valueOf(path);
		return computeFileName(pathString);
	}

	@ApiMethod
	public static String computeFileName(
			final String pathString) {
		return FilenameUtils.getName(pathString);
	}

	@ApiMethod
	public static String computeParentPathString(
			final String pathString) {

		String folderPathString = null;
		try {
			final Path path = Paths.get(pathString);
			final Path parentPath = path.getParent();
			if (parentPath != null) {
				folderPathString = parentPath.toString();
			}

		} catch (final Exception ignored) {
		}
		return folderPathString;
	}

	@ApiMethod
	public static String computeNormalizedPathString(
			final String pathString) {

		String normalizedPathString = null;
		try {
			final Path path = Paths.get(pathString);
			final Path absolutePath = path.toAbsolutePath();
			final String absolutePathString = absolutePath.toString();
			normalizedPathString = FilenameUtils.normalize(absolutePathString);

		} catch (final Exception ignored) {
		}
		return normalizedPathString;
	}

	@ApiMethod
	public static String computeExtension(
			final Path path) {

		final String pathString = path.toString();
		return computeExtension(pathString);
	}

	@ApiMethod
	public static String computeExtension(
			final String pathString) {
		return FilenameUtils.getExtension(pathString);
	}

	@ApiMethod
	public static String computePathWoExt(
			final Path path) {

		String pathWoExt = null;
		if (path != null) {
			final String pathString = path.toString();
			pathWoExt = computePathWoExt(pathString);
		}
		return pathWoExt;
	}

	@ApiMethod
	public static String computePathWoExt(
			final String pathString) {
		return FilenameUtils.removeExtension(pathString);
	}

	@ApiMethod
	public static String computeFileNameWoExt(
			final Path path) {

		String fileNameWoExt = null;
		if (path != null) {

			final String pathString = path.toString();
			fileNameWoExt = computeFileNameWoExt(pathString);
		}
		return fileNameWoExt;
	}

	@ApiMethod
	public static String computeFileNameWoExt(
			final String pathString) {
		return FilenameUtils.getBaseName(pathString);
	}

	@ApiMethod
	public static String appendFileNameSuffix(
			final String pathString,
			final String suffix) {

		final String resultPathString;
		final String pathStringWoExt = PathUtils.computePathWoExt(pathString);
		final String extension = PathUtils.computeExtension(pathString);
		if (StringUtils.isNotEmpty(extension)) {
			resultPathString = pathStringWoExt + suffix + "." + extension;
		} else {
			resultPathString = pathStringWoExt + suffix;
		}
		return resultPathString;
	}

	@ApiMethod
	public static String computeRelativePathString(
			final String fromPathString,
			final String toPathString) {

		String relativePathString = null;
		try {
			final Path fromPath = Paths.get(fromPathString);
			final Path toPath = Paths.get(toPathString);
			final Path relativePath = fromPath.relativize(toPath);
			relativePathString = relativePath.toString();

		} catch (final Exception ignored) {
		}
		return relativePathString;
	}
}

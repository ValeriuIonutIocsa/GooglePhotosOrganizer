package com.personal.g_photos;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.utils.io.IoUtils;
import com.utils.io.ListFileUtils;
import com.utils.io.PathUtils;
import com.utils.io.ReaderUtils;
import com.utils.io.file_copiers.FactoryFileCopier;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.log.Logger;

final class AppStartGooglePhotosOrganizer {

	private AppStartGooglePhotosOrganizer() {
	}

	public static void main(
			final String[] args) {

		final Instant start = Instant.now();
		Logger.setDebugMode(true);

		final String firstArg;
		if (args.length >= 1) {
			firstArg = args[0];
		} else {
			firstArg = null;
		}
		if (StringUtils.isBlank(firstArg)) {
			Logger.printError("invalid first argument");
			System.exit(-1);
		}

		if ("/?".equals(firstArg) || "--help".equals(firstArg)) {
			Logger.printLine("usage: google_photos_organizer INPUT_FOLDER_PATH OUTPUT_FOLDER_PATH");
			System.exit(0);
		}

		final String secondArg;
		if (args.length >= 2) {
			secondArg = args[1];
		} else {
			secondArg = null;
		}
		if (StringUtils.isBlank(secondArg)) {
			Logger.printError("invalid second argument");
			System.exit(-2);
		}

		final String inputFolderPathString = PathUtils.computeNormalizedPath("input folder", firstArg);
		final String outputFolderPathString = PathUtils.computeNormalizedPath("output folder", secondArg);
		work(inputFolderPathString, outputFolderPathString);

		Logger.printNewLine();
		Logger.printFinishMessage(start);
	}

	static void work(
			final String inputFolderPathString,
			final String outputFolderPathString) {

		Logger.printNewLine();
		Logger.printProgress("GooglePhotosOrganizer starting");

		if (!IoUtils.fileExists(inputFolderPathString)) {

			Logger.printError("input folder does not exist");
			System.exit(-2);
		}

		final boolean success =
				FactoryFolderCreator.getInstance().createDirectories(outputFolderPathString, false, true);
		if (!success) {
			System.exit(-3);
		}

		final List<String> filePathStringList =
				ListFileUtils.listFilesRecursively(inputFolderPathString);
		for (final String filePathString : filePathStringList) {

			if (!IoUtils.directoryExists(filePathString)) {

				final String jsonFilePathString = filePathString + ".json";
				if (IoUtils.fileExists(jsonFilePathString)) {

					processFile(filePathString, jsonFilePathString, outputFolderPathString);
				}
			}
		}
	}

	private static void processFile(
			final String filePathString,
			final String jsonFilePathString,
			final String outputFolderPathString) {

		try {
			Logger.printNewLine();
			Logger.printProgress("processing file:");
			Logger.printLine(filePathString);

			final String fileName = PathUtils.computeFileName(filePathString);
			String outputFilePathString = PathUtils.computePath(outputFolderPathString, fileName);
			if (outputFilePathString.endsWith(".png")) {
				outputFilePathString = PathUtils.computePathWoExt(outputFilePathString) + ".jpg";
			}

			final boolean success = copyFile(filePathString, outputFilePathString);
			if (success) {

				final Instant photoTakenTimeInstant = parsePhotoTakenTimeInstant(jsonFilePathString);
				if (photoTakenTimeInstant != null) {

					IoUtils.configureFileLastModifiedTime(outputFilePathString, photoTakenTimeInstant);
				}
			}

		} catch (final Exception exc) {
			Logger.printError("failed to process file:" + System.lineSeparator() + filePathString);
			Logger.printException(exc);
		}
	}

	private static boolean copyFile(
			final String filePathString,
			final String outputFilePathString) {

		final boolean success;
		if (filePathString.endsWith(".mp4")) {
			success = copyVideoFile(filePathString, outputFilePathString);

		} else if (filePathString.endsWith(".jpg")) {
			success = copyImageFile(filePathString, outputFilePathString, ImageType.JPG);

		} else if (filePathString.endsWith(".png")) {
			success = copyImageFile(filePathString, outputFilePathString, ImageType.PNG);

		} else {
			success = FactoryFileCopier.getInstance()
					.copyFile(filePathString, outputFilePathString, true, true, true);
		}
		return success;
	}

	private static boolean copyVideoFile(
			final String filePathString,
			final String outputFilePathString) {

		boolean success = false;
		try {
			Logger.printProgress("copying video file:");
			Logger.printLine(filePathString);
			Logger.printLine("to:");
			Logger.printLine(outputFilePathString);

			success = FactoryFileDeleter.getInstance().deleteFile(outputFilePathString, false, true);
			if (success) {

				final ProcessBuilder processBuilder = new ProcessBuilder();
				processBuilder.command("ffmpeg", "-i", filePathString,
						"-movflags", "use_metadata_tags", "-map_metadata", "0",
						"-vcodec", "copy", "-acodec", "copy", outputFilePathString);
				processBuilder.directory(new File(outputFilePathString).getParentFile());
				processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
				processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
				final Process process = processBuilder.start();
				final int exitCode = process.waitFor();
				success = exitCode == 0;
			}

		} catch (final Exception exc) {
			Logger.printError("failed to copy file " +
					System.lineSeparator() + filePathString +
					System.lineSeparator() + "to:" +
					System.lineSeparator() + outputFilePathString);
			Logger.printException(exc);
		}
		return success;
	}

	private static boolean copyImageFile(
			final String filePathString,
			final String outputFilePathString,
			final ImageType imageType) {

		boolean success = false;
		try {
			Logger.printProgress("copying image file:");
			Logger.printLine(filePathString);
			Logger.printLine("to:");
			Logger.printLine(outputFilePathString);

			success = FactoryFileDeleter.getInstance().deleteFile(outputFilePathString, false, true);
			if (success) {

				final MetadataExporter metadataExporter = new MetadataExporter(filePathString, imageType);
				metadataExporter.work();
				success = metadataExporter.isSuccess();
				if (success) {

					final String scale;
					final int imageWidth = metadataExporter.getImageWidth();
					final int imageHeight = metadataExporter.getImageHeight();
					if (imageWidth > imageHeight) {
						scale = "scale=-1:1920";
					} else {
						scale = "scale=1920:-1";
					}

					final Process process = new ProcessBuilder()
							.command("ffmpeg", "-i", filePathString,
									"-movflags", "use_metadata_tags", "-map_metadata", "0",
									"-vf", scale, outputFilePathString)
							.directory(new File(outputFilePathString).getParentFile())
							.redirectOutput(ProcessBuilder.Redirect.DISCARD)
							.redirectError(ProcessBuilder.Redirect.DISCARD)
							.start();
					final int exitCode = process.waitFor();
					success = exitCode == 0;
					if (success) {

						final String metadataXmlPathString = metadataExporter.getMetadataXmlPathString();
						final MetadataImporter metadataImporter =
								new MetadataImporter(outputFilePathString, metadataXmlPathString);
						metadataImporter.work();

						success = metadataImporter.isSuccess();
					}
				}
			}

		} catch (final Exception exc) {
			Logger.printError("failed to copy file " +
					System.lineSeparator() + filePathString +
					System.lineSeparator() + "to:" +
					System.lineSeparator() + outputFilePathString);
			Logger.printException(exc);
		}
		return success;
	}

	private static Instant parsePhotoTakenTimeInstant(
			final String jsonFilePathString) {

		Instant photoTakenTimeInstant = null;
		try {
			Logger.printProgress("parsing photo taken time from JSON file:");
			Logger.printLine(jsonFilePathString);

			final String jsonFileContent = ReaderUtils.fileToString(jsonFilePathString);
			final JSONObject jsonObject = new JSONObject(jsonFileContent);
			final JSONObject photoTakenTimeJsonObject = jsonObject.getJSONObject("photoTakenTime");
			final String formattedPhotoTakenTime = photoTakenTimeJsonObject.getString("formatted");

			photoTakenTimeInstant = parseInstantFromString(formattedPhotoTakenTime);

		} catch (final Exception exc) {
			Logger.printProgress("failed to parse photo taken time from JSON file:" +
					System.lineSeparator() + jsonFilePathString);
			Logger.printException(exc);
		}
		return photoTakenTimeInstant;
	}

	static Instant parseInstantFromString(
			final String formattedPhotoTakenTime) {

		Instant instant = null;
		try {
			final String processedFormattedPhotoTakenTime =
					StringUtils.replaceChars(formattedPhotoTakenTime, '\u202f', ' ');
			instant = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss a z")
					.parse(processedFormattedPhotoTakenTime).toInstant();

		} catch (final Exception exc) {
			Logger.printError("failed to parse data from string " + formattedPhotoTakenTime);
			Logger.printException(exc);
		}
		return instant;
	}
}

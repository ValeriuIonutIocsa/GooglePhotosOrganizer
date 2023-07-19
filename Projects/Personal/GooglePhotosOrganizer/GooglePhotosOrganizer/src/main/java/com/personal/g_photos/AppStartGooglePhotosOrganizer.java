package com.personal.g_photos;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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

		if (args.length >= 1 && "-help".equals(args[0])) {

			final String usageMessage = createUsageMessage();
			Logger.printLine(usageMessage);
			System.exit(0);
		}

		if (args.length < 2) {

			final String usageMessage = createUsageMessage();
			Logger.printError("insufficient arguments" + System.lineSeparator() + usageMessage);
			System.exit(1);
		}

		work(args);

		Logger.printNewLine();
		Logger.printFinishMessage(start);
	}

	private static String createUsageMessage() {

		return "usage: google_photos_organizer <INPUT_FOLDER_PATH> <OUTPUT_FOLDER_PATH> " +
				"(-verbose) (-keep_live_photo_videos)";
	}

	static void work(
			final String[] args) {

		Logger.printNewLine();
		Logger.printProgress("GooglePhotosOrganizer starting");

		final String inputFolderPathString = PathUtils.computeNormalizedPath("input folder", args[0]);
		if (StringUtils.isBlank(inputFolderPathString)) {

			Logger.printError("invalid input folder path");
			System.exit(2);
		}

		final String outputFolderPathString = PathUtils.computeNormalizedPath("output folder", args[1]);
		if (StringUtils.isBlank(outputFolderPathString)) {

			Logger.printError("invalid output folder path");
			System.exit(3);
		}

		final boolean verbose = args.length >= 3 && "-verbose".equals(args[2]);
		final boolean keepLivePhotoVideos = args.length >= 4 && "-keep_live_photo_videos".equals(args[3]);

		if (!IoUtils.fileExists(inputFolderPathString)) {

			Logger.printError("input folder does not exist");
			System.exit(3);
		}

		final boolean success = FactoryFolderCreator.getInstance()
				.createDirectories(outputFolderPathString, false, true);
		if (!success) {
			System.exit(4);
		}

		final List<FileData> toProcessFileDataList = new ArrayList<>();
		final List<String> filePathStringList =
				ListFileUtils.listFilesRecursively(inputFolderPathString);
		for (final String filePathString : filePathStringList) {

			if (!IoUtils.directoryExists(filePathString)) {

				final String jsonFilePathString = filePathString + ".json";
				if (IoUtils.fileExists(jsonFilePathString)) {

					final FileData fileData = new FileData(filePathString, jsonFilePathString);
					toProcessFileDataList.add(fileData);
				}

				if (keepLivePhotoVideos) {

					final String extension = PathUtils.computeExtension(filePathString);
					if (!StringUtils.equalsIgnoreCase(extension, "mp4")) {

						final String mp4FilePathString = PathUtils.computePathWoExt(filePathString) + ".mp4";
						if (IoUtils.fileExists(mp4FilePathString)) {

							final FileData fileData = new FileData(mp4FilePathString, jsonFilePathString);
							toProcessFileDataList.add(fileData);
						}
					}
					if (!StringUtils.equalsIgnoreCase(extension, "mov")) {

						final String movFilePathString = PathUtils.computePathWoExt(filePathString) + ".mov";
						if (IoUtils.fileExists(movFilePathString)) {

							final FileData fileData = new FileData(movFilePathString, jsonFilePathString);
							toProcessFileDataList.add(fileData);
						}
					}
				}
			}
		}

		for (int i = 0; i < toProcessFileDataList.size(); i++) {

			final FileData fileData = toProcessFileDataList.get(i);
			final String filePathString = fileData.getFilePathString();
			final String jsonFilePathString = fileData.getJsonFilePathString();
			processFile(filePathString, jsonFilePathString, i, toProcessFileDataList.size(),
					outputFolderPathString, verbose);
		}
	}

	private static void processFile(
            final String filePathString,
            final String jsonFilePathString,
            final int fileIndex,
            final int fileCount,
            final String outputFolderPathString,
            final boolean verbose) {

		try {
			Logger.printNewLine();
			Logger.printProgress("processing file " + fileIndex + "/" + fileCount + ":");
			Logger.printLine(filePathString);

			final String fileName = PathUtils.computeFileName(filePathString);
			final String outputFilePathString = PathUtils.computePath(outputFolderPathString, fileName);

			final boolean success = copyFile(filePathString, outputFilePathString, verbose);
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
            final String outputFilePathString,
            final boolean verbose) {

		final boolean success;
		if (StringUtils.endsWithIgnoreCase(filePathString, ".mp4___") ||
				StringUtils.endsWithIgnoreCase(filePathString, ".mov___")) {
			success = copyVideoFile(filePathString, outputFilePathString, verbose);

		} else if (StringUtils.endsWithIgnoreCase(filePathString, ".jpg") ||
				StringUtils.endsWithIgnoreCase(filePathString, ".jpeg") ||
				StringUtils.endsWithIgnoreCase(filePathString, ".png") ||
				StringUtils.endsWithIgnoreCase(filePathString, ".heic___")) {
			success = copyImageFile(filePathString, outputFilePathString, verbose);

		} else {
			success = FactoryFileCopier.getInstance()
					.copyFile(filePathString, outputFilePathString, true, true, true);
		}
		return success;
	}

	private static boolean copyVideoFile(
			final String filePathString,
			final String outputFilePathString,
			final boolean verbose) {

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
						"-vcodec", "copy", "-c:a", "aac", outputFilePathString);
				processBuilder.directory(new File(outputFilePathString).getParentFile());

				final ProcessBuilder.Redirect processBuilderRedirect;
				if (verbose) {
					processBuilderRedirect = ProcessBuilder.Redirect.INHERIT;
				} else {
					processBuilderRedirect = ProcessBuilder.Redirect.DISCARD;
				}

				processBuilder.redirectOutput(processBuilderRedirect);
				processBuilder.redirectError(processBuilderRedirect);
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
			final boolean verbose) {

		boolean success = false;
		try {
			final List<String> commandPartList = new ArrayList<>();
			Collections.addAll(commandPartList, "cmd",
					"/c", "img_resizer", "1920", filePathString, outputFilePathString);
			if (verbose) {
				commandPartList.add("-verbose");
			}

			final Process process = new ProcessBuilder()
					.command(commandPartList)
					.directory(new File(outputFilePathString).getParentFile())
					.redirectOutput(ProcessBuilder.Redirect.INHERIT)
					.redirectError(ProcessBuilder.Redirect.INHERIT)
					.start();
			final int exitCode = process.waitFor();
			success = exitCode == 0;

		} catch (final Exception exc) {
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

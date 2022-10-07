package com.utils.io;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;

public class CachedFile<
		ObjectT> {

	private String filePathString;
	private long size;
	private long lastModifiedTime;
	private ObjectT dataObject;

	public CachedFile() {

		size = -1;
		lastModifiedTime = -1;
	}

	@ApiMethod
	public void cache(
			final String filePathString,
			final ObjectT dataObject) {

		try {
			this.filePathString = filePathString;
			size = IoUtils.fileSize(filePathString);
			lastModifiedTime = IoUtils.fileLastModifiedTime(filePathString);
			this.dataObject = dataObject;

		} catch (final Exception exc) {
			Logger.printError("failed to cache file:" + System.lineSeparator() + filePathString);
			Logger.printException(exc);
		}
	}

	@ApiMethod
	public boolean isCached(
			final String filePathString) {

		boolean cached = false;
		try {
			final boolean parseFile;
			if (filePathString == null) {
				parseFile = this.filePathString == null;
			} else {
				parseFile = filePathString.equals(this.filePathString);
			}
			if (parseFile) {

				final long size = IoUtils.fileSize(filePathString);
				if (this.size == size) {

					final long lastModifiedTime = IoUtils.fileLastModifiedTime(filePathString);
					cached = this.lastModifiedTime == lastModifiedTime;
				}
			}

		} catch (final Exception exc) {
			Logger.printError("failed to check if file is cached:" +
					System.lineSeparator() + filePathString);
			Logger.printException(exc);
		}
		return cached;
	}

	@ApiMethod
	public ObjectT getDataObject() {
		return dataObject;
	}
}

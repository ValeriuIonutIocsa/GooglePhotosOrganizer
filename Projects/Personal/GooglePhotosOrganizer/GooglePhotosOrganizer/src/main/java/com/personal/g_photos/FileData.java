package com.personal.g_photos;

import com.utils.string.StrUtils;

class FileData {

	private final String filePathString;
	private final String jsonFilePathString;

	FileData(
			final String filePathString,
			final String jsonFilePathString) {

		this.filePathString = filePathString;
		this.jsonFilePathString = jsonFilePathString;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	String getFilePathString() {
		return filePathString;
	}

	String getJsonFilePathString() {
		return jsonFilePathString;
	}
}

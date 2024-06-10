package com.personal.g_photos;

import com.utils.string.StrUtils;

record FileData(
		String filePathString,
		String jsonFilePathString) {

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}

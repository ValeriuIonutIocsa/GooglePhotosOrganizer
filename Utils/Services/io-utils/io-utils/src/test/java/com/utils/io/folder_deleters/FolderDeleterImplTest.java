package com.utils.io.folder_deleters;

import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.io.IoUtils;

class FolderDeleterImplTest {

	@Test
	void testDeleteFolder() {

		String folderPathString = "null";
		folderPathString = Paths.get(folderPathString).toAbsolutePath().toString();
		Assertions.assertTrue(IoUtils.directoryExists(folderPathString));
		new FolderDeleterImpl().deleteFolder(folderPathString, true);
		Assertions.assertFalse(IoUtils.directoryExists(folderPathString));
	}
}

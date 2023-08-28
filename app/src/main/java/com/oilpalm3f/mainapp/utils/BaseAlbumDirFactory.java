package com.oilpalm3f.mainapp.utils;

import android.os.Environment;

import java.io.File;

//Photos Saving Directory
public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

	String root = Environment.getExternalStorageDirectory().toString();

	@Override
	public File getAlbumStorageDir(String albumName) {
		File rootDirectory = new File(root + "/3F_Pictures");
		File pictureDirectory = new File(root + "/3F_Pictures/" + albumName);

		if (!rootDirectory.exists()) {
			rootDirectory.mkdirs();
		}

		if (!pictureDirectory.exists()) {
			pictureDirectory.mkdirs();
		}

		return pictureDirectory;
	}
}

package com.festive.logodetector.model

import android.app.Application
import android.os.Environment
import java.io.*
import javax.inject.Inject

class StorageFeeder @Inject constructor(@Inject @JvmField var application: Application) {

    @Throws(IOException::class)
    fun copyDirorfileFromAssetManager(
        arg_assetDir: String,
        arg_destinationDir: String
    ): String? {
        val sd_path: File = Environment.getExternalStorageDirectory()
        val dest_dir_path = sd_path.toString() + addLeadingSlash(arg_destinationDir)
        val dest_dir = File(dest_dir_path)
        createDir(dest_dir)
        val asset_manager = application.assets
        val files = asset_manager.list(arg_assetDir)
        for (i in files.indices) {
            val abs_asset_file_path = addTrailingSlash(arg_assetDir) + files[i]
            val sub_files = asset_manager.list(abs_asset_file_path)
            if (sub_files.size == 0) {
                // It is a file
                val dest_file_path = addTrailingSlash(dest_dir_path) + files[i]
                copyAssetFile(abs_asset_file_path, dest_file_path)
            } else {
                // It is a sub directory
                copyDirorfileFromAssetManager(
                    abs_asset_file_path,
                    addTrailingSlash(arg_destinationDir) + files[i]
                )
            }
        }
        return dest_dir_path
    }


    @Throws(IOException::class)
    private fun copyAssetFile(
        assetFilePath: String?,
        destinationFilePath: String?
    ) {
        val inputStream: InputStream =
            application.assets.open(assetFilePath)
        val out: OutputStream = FileOutputStream(destinationFilePath)
        inputStream.copyTo(out)
        inputStream.close()
        out.close()
    }

    private fun addTrailingSlash(path: String): String {
        var newPath = ""
        if (path[path.length - 1] != '/') {
            newPath = "$path/"
        }
        return newPath
    }

    private fun addLeadingSlash(path: String): String {
        var newPath = ""
        if (path[0] != '/') {
            newPath = "/$path"
        }
        return newPath
    }

    @Throws(IOException::class)
    private fun createDir(dir: File) {
        if (dir.exists()) {
            if (!dir.isDirectory) {
                throw IOException("Can't create directory, a file is in the way")
            }
        } else {
            dir.mkdirs()
            if (!dir.isDirectory) {
                throw IOException("Unable to create directory")
            }
        }
    }
}
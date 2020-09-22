package com.festive.logodetector

import android.os.Environment
import com.festive.logodetector.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import java.io.*

class BaseApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder().application(this).build()

    override fun onCreate() {
        super.onCreate()
        copyDirorfileFromAssetManager("قوانین و مقررات", "LogoDetector/قوانین و مقررات")
    }

    @Throws(IOException::class)
    fun copyDirorfileFromAssetManager(
        arg_assetDir: String,
        arg_destinationDir: String
    ): String? {
        val sd_path: File = Environment.getExternalStorageDirectory()
        val dest_dir_path = sd_path.toString() + addLeadingSlash(arg_destinationDir)
        val dest_dir = File(dest_dir_path)
        createDir(dest_dir)
        val asset_manager = assets
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
    fun copyAssetFile(
        assetFilePath: String?,
        destinationFilePath: String?
    ) {
        val inputStream: InputStream =
            assets.open(assetFilePath)
        val out: OutputStream = FileOutputStream(destinationFilePath)
        inputStream.copyTo(out)
        inputStream.close()
        out.close()
    }

    fun addTrailingSlash(path: String): String {
        var newPath = ""
        if (path[path.length - 1] != '/') {
            newPath = "$path/"
        }
        return newPath
    }

    fun addLeadingSlash(path: String): String {
        var newPath = ""
        if (path[0] != '/') {
            newPath = "/$path"
        }
        return newPath
    }

    @Throws(IOException::class)
    fun createDir(dir: File) {
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
package com.festive.logodetector.view.content

import android.app.Application
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import javax.inject.Inject


class ContentViewModel @Inject constructor(@Inject @JvmField var context: Application) :
    ViewModel() {

    private lateinit var rootFolder: File
    private val _currentFolderLiveData = MutableLiveData<File>()
    val currentFolderLiveData: LiveData<File>
        get() = _currentFolderLiveData
    private val _currentFolderContentLiveData = MutableLiveData<List<File>>()
    val currentFolderContentLiveData: LiveData<List<File>>
        get() = _currentFolderContentLiveData

    init {
        getRoot()
//        copyDirorfileFromAssetManager("قوانین و مقررات", "LogoDetector/content")
    }

    private fun getRoot() {
        rootFolder =
            File(Environment.getExternalStorageDirectory().absolutePath + "/LogoDetector/قوانین و مقررات")
        setCurrentFolder(rootFolder)
    }

    fun setCurrentFolder(folder: File?) {
//        if (folder?.isDirectory!!){
            _currentFolderLiveData.value = folder
            _currentFolderContentLiveData.value =
                _currentFolderLiveData.value?.listFiles()?.toList() ?: listOf()
//        }else{

//        }
    }

    private fun isDirectory(file: File): Boolean = file.isDirectory

    fun backToParent() {
        if (_currentFolderLiveData.value != rootFolder)
            setCurrentFolder(_currentFolderLiveData.value?.parentFile)
    }

}
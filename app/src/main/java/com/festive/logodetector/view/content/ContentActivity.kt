package com.festive.logodetector.view.content

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festive.logodetector.R
import com.festive.logodetector.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_content.*
import java.io.File
import javax.inject.Inject

class ContentActivity : DaggerAppCompatActivity() {

    private var shouldFinish = false
    private val handler= Handler()

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var contentViewModel: ContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)

        contentViewModel = ViewModelProvider(this, providerFactory)[ContentViewModel::class.java]

        contentViewModel.currentFolderLiveData.observe(this, Observer {file->
            if(file.isDirectory){
                currentDirTextView.text = file.name
                pdfView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recyclerView.bringToFront()
            }else{
                currentDirTextView.text = file.nameWithoutExtension
                recyclerView.visibility = View.INVISIBLE
                pdfView.fromFile(file).load()
                pdfView.visibility = View.VISIBLE
                pdfView.bringToFront()
            }
        })

        contentViewModel.currentFolderContentLiveData.observe(this, Observer { files ->
            recyclerView.adapter = ContentAdapter(files, object : ContentAdapter.OnContentSelected {
                override fun onSelect(item: File) {
                    Log.d(TAG, "onSelect: ${item.name}")
                    contentViewModel.setCurrentFolder(item)
                }
            })
        })

        initRecyclerView()
        backFolderButton.setOnClickListener {
            contentViewModel.backToParent()
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
    }

    override fun onBackPressed() {
        if(shouldFinish)
            finish()
        else{
            shouldFinish = true
            handler.postDelayed({
                shouldFinish = false
            },2000)
            Toast.makeText(this, "برای خروج دکمه بازگشت را مجداا فشار دهید!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "ContentActivity"
    }
}
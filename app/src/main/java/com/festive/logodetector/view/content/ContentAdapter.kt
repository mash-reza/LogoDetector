package com.festive.logodetector.view.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.festive.logodetector.model.PDF
import com.festive.logodetector.R
import com.festive.logodetector.model.Folder
import java.io.File

class ContentAdapter(private val contents: List<File>, val listener: OnContentSelected) :
    RecyclerView.Adapter<ContentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder =
        ContentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.content_item,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.titleTextView.text = contents[position].name
        holder.titleTextView.setOnClickListener {
            listener.onSelect(contents[position])
        }
    }

    interface OnContentSelected {
        fun onSelect(item: File)
    }
}

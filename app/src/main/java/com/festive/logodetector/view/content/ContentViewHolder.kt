package com.festive.logodetector.view.content

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festive.logodetector.R

class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleTextView:TextView = itemView.findViewById(R.id.contentTitleTextView)
    val iconImageView:ImageView = itemView.findViewById(R.id.contentIconImageView)
}
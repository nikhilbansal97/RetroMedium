package com.android.nikhil.retromedium

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class PublicationRecyclerAdapter(private val mContext: Context, private val mList: ArrayList<UserPublication>) :
        RecyclerView.Adapter<PublicationRecyclerAdapter.PublicationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        return PublicationViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        val currentPublication = mList[position]
        Picasso.get().load(currentPublication.imageUrl).into(holder.mImageView)
        holder.mTitleTextView.text = currentPublication.name
    }

    class PublicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mImageView: ImageView = view.articleImage
        val mTitleTextView: TextView = view.articleTitle
    }

}
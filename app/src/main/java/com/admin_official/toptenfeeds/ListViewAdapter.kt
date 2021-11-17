package com.admin_official.toptenfeeds

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

data class ViewHolder(val v: View) {
    val title = v.findViewById<TextView>(R.id.feed_title)
    val artist = v.findViewById<TextView>(R.id.feed_artist)
    val image = v.findViewById<ImageView>(R.id.feed_image)
    val summary = v.findViewById<TextView>(R.id.feed_summary)
}

class ListViewAdapter(private val feedEntries: List<FeedEntry>, context: Context, private val resource: Int)
    : ArrayAdapter<FeedEntry>(context, resource) {

    val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = feedEntries.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View

        if(convertView == null) {
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        val entry = feedEntries[position]

        viewHolder.title.text = entry.name
        viewHolder.artist.text = entry.artist
        viewHolder.summary.text = entry.summary
//        Picasso.get()
//            .load(entry.imageUrl)
//            .placeholder(R.drawable.placeholder)
//            .error(R.drawable.placeholder)
//            .into(viewHolder.image)
        Glide.with(context)
            .load(entry.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade(400))
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(viewHolder.image)
        return view
    }
}
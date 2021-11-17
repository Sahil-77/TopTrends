package com.admin_official.toptenfeeds

import android.os.AsyncTask
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

interface XmlParserCallback {
    fun xmlParserCallback(result: List<FeedEntry>)
}

data class FeedEntry
    (val name: String,
     val artist: String,
     val summary: String,
     val releaseDate: String,
     val imageUrl: String) {

    override fun toString(): String =
        """
            name -> $name
            artist -> $artist
            summary -> $summary
            realeaseDate -> $releaseDate
            image -> $imageUrl
        """.trimIndent()
}

class XmlParser (private val callback: XmlParserCallback): AsyncTask<String, Void, Unit>(), DownloadThreadCallback {

    private val TAG = "De_XmlParser"

    var feedEntries = mutableListOf<FeedEntry>()

    override fun onPostExecute(result: Unit?) {
        callback.xmlParserCallback(feedEntries)
    }

    override fun doInBackground(vararg urlPath: String?) {
        val downloadThread = DownloadThread(this)
        downloadThread.executeOnSameThread(urlPath[0])
    }

    override fun callBackDownloadThread(result: String?): Boolean {
//        Log.d(TAG, "callBackDownloadThread: xml -> $result")
        var status = true
        var inEntry = false
        var textValue = ""

        if(result == null || result.isEmpty()) return false

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(result.reader())
            var eventType = xpp.eventType
            var curr = mutableListOf<String>("", "", "", "", "")
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name?.lowercase()

                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName == "entry") {
                            inEntry = true
                        }
                    }

                    XmlPullParser.TEXT -> {
                        textValue = xpp.text
                    }

                    XmlPullParser.END_TAG -> {
                        if(inEntry) {
                            when(tagName) {
                                "entry" -> {
//                                    Log.d(TAG, "callBackDownloadThread: $curr")
                                    feedEntries.add(FeedEntry(curr[0], curr[1], curr[2], curr[3], curr[4]))
                                    curr = mutableListOf("", "", "", "", "")
                                }

                                "name" -> curr[0] = textValue

                                "artist" -> curr[1] = textValue

                                "summary" -> curr[2] = textValue

                                "releaseDate" -> curr[3] = textValue

                                "image" -> curr[4] = textValue
                            }
                        }
                    }
                }

                eventType = xpp.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }

//        for(i in feedEntries) {
//            Log.d(TAG, "callBackDownloadThread: $i")
//        }
        return status
    }
}
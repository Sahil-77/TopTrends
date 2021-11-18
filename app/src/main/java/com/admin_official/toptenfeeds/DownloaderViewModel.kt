package com.admin_official.toptenfeeds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DownloaderViewModel: ViewModel(), DownloadThreadCallback, XmlParserCallback {

    private var PFeedEntries = MutableLiveData<List<FeedEntry>>()
    val feedEntries: LiveData<List<FeedEntry>>
        get() = PFeedEntries

    private var PTopTen = true
    var topTen: Boolean
        get(): Boolean = PTopTen
        set(topTen: Boolean) {PTopTen = topTen}

    private var PUrlType = UrlType.FreeApps
    var urlType: UrlType
        get(): UrlType = PUrlType
        set(type: UrlType) {PUrlType = type}

    private var prevUrl = ""

    fun download(url: String) {
        if(prevUrl == url) {
            return
        } else prevUrl = url

        val xmlParser = XmlParser(this)
        xmlParser.execute(url)
    }

    //Callbacks-------------------------------------------------------------------------------------
    override fun callBackDownloadThread(result: String?): Boolean {
        TODO("Implement to parse on the same thread")
    }

    override fun xmlParserCallback(result: List<FeedEntry>) {
        PFeedEntries.value = result
    }
}
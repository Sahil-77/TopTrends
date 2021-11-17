package com.admin_official.toptenfeeds

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

enum class UrlType {
    FreeApps, PaidApps, Songs, Movies
}

class MainActivity : AppCompatActivity(), DownloadThreadCallback, XmlParserCallback{
    private val TAG = "De_MainActivity"

    //Variables
    private var topTen = true
    private var urlFreeApps = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var urlPaidApps = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
    private var urlSongs = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
    private var urlMovies = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topMovies/xml"
    private var urlType: UrlType? = UrlType.FreeApps

    //Keys for saved instance state
    private var topTenKey = "topTenKey"
    private var urlTypeKey = "urlTypeKey"

    //Activity LifeCycle----------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d(TAG, "onCreate: in")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        Log.d(TAG, "onRestoreInstanceState: in")
        super.onRestoreInstanceState(savedInstanceState)
        topTen = savedInstanceState.getBoolean(topTenKey)
        urlType = savedInstanceState.get(urlTypeKey) as UrlType?
        Log.d(TAG, "onRestoreInstanceState: $urlType")
    }

    override fun onSaveInstanceState(outState: Bundle) {
//        Log.d(TAG, "onSaveInstanceState: in")
        outState.putBoolean(topTenKey, topTen)
        outState.putSerializable(urlTypeKey, urlType)
        super.onSaveInstanceState(outState)
    }

    //menu------------------------------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        Log.d(TAG, "onCreateOptionsMenu: in")
        menuInflater.inflate(R.menu.feed_menu, menu)

        when(urlType) {
            UrlType.Songs -> menu.findItem(R.id.menu_songs)?.isChecked = true
            UrlType.PaidApps -> menu.findItem(R.id.menu_paidApps)?.isChecked = true
            UrlType.FreeApps -> menu.findItem(R.id.menu_freeApps)?.isChecked = true
            UrlType.Movies -> menu.findItem(R.id.menu_movies)?.isChecked = true
        }

        val item: MenuItem? = if(topTen) menu.findItem(R.id.menu_10)
        else menu.findItem(R.id.menu_25)

        val xmlParser = XmlParser(this)
        xmlParser.execute(urlFormat(item))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.menu_10 -> topTen = true
            R.id.menu_25 -> topTen = false
            R.id.menu_freeApps -> urlType = UrlType.FreeApps
            R.id.menu_paidApps -> urlType = UrlType.PaidApps
            R.id.menu_songs -> urlType = UrlType.Songs
            R.id.menu_movies -> urlType = UrlType.Movies
        }

        val xmlParser = XmlParser(this)
        xmlParser.execute(urlFormat(item))
        return super.onOptionsItemSelected(item)
    }

    private fun urlFormat (item: MenuItem?) :String {
        item?.isChecked = true

        return when(urlType) {
            UrlType.FreeApps -> urlFreeApps.format(if(topTen) 10 else 25)
            UrlType.PaidApps -> urlPaidApps.format(if(topTen) 10 else 25)
            UrlType.Songs -> urlSongs.format(if(topTen) 10 else 25)
            UrlType.Movies -> urlMovies
            else -> urlFreeApps.format(if(topTen) 10 else 25) // if we get a null from restore state for some reason
        }
    }

    //callbacks-------------------------------------------------------------------------------------
    override fun callBackDownloadThread(result: String?): Boolean {
        TODO("Implement to parse on the main UI thread")
    }

    override fun xmlParserCallback(result: List<FeedEntry>) {
//        Log.d(TAG, "xmlParserCallback: result -> $result")
        val listView = findViewById<ListView>(R.id.listView)
        val feedAdapter = ListViewAdapter(result, this, R.layout.feed_entry)
        listView.adapter = feedAdapter
    }
}
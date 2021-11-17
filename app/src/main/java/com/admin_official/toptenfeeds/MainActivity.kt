package com.admin_official.toptenfeeds

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

enum class UrlType {
    FreeApps, PaidApps, Songs, Movies
}

class MainActivity : AppCompatActivity() {
    private val TAG = "De_MainActivity"

    //ViewModel
    private val viewModel by viewModels<DownloaderViewModel>()

//    //Variables
//    private var topTen = true
//    private var urlType = UrlType.FreeApps

    private var urlFreeApps = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var urlPaidApps = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
    private var urlSongs = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
    private var urlMovies = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topMovies/xml"

//    //Keys for saved instance state
//    private var topTenKey = "topTenKey"
//    private var urlTypeKey = "urlTypeKey"

    //Activity LifeCycle----------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: in")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.feedEntries.observe(this, {
            val listView = findViewById<ListView>(R.id.listView)
            val feedAdapter = ListViewAdapter(it, this, R.layout.feed_entry)
            listView.adapter = feedAdapter
        })
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        Log.d(TAG, "onRestoreInstanceState: in")
//        super.onRestoreInstanceState(savedInstanceState)
//        topTen = savedInstanceState.getBoolean(topTenKey)
//        urlType = savedInstanceState.get(urlTypeKey) as UrlType?
//        Log.d(TAG, "onRestoreInstanceState: $urlType")
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        Log.d(TAG, "onSaveInstanceState: in")
//        outState.putBoolean(topTenKey, topTen)
//        outState.putSerializable(urlTypeKey, urlType)
//        super.onSaveInstanceState(outState)
//    }

    //menu------------------------------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        Log.d(TAG, "onCreateOptionsMenu: in")
        menuInflater.inflate(R.menu.feed_menu, menu)

        when(viewModel.urlType) {
            UrlType.Songs -> menu.findItem(R.id.menu_songs)?.isChecked = true
            UrlType.PaidApps -> menu.findItem(R.id.menu_paidApps)?.isChecked = true
            UrlType.FreeApps -> menu.findItem(R.id.menu_freeApps)?.isChecked = true
            UrlType.Movies -> menu.findItem(R.id.menu_movies)?.isChecked = true
        }

        if(viewModel.topTen) menu.findItem(R.id.menu_10).isChecked = true
        else menu.findItem(R.id.menu_25).isChecked = true

//        val xmlParser = XmlParser(this)
//        xmlParser.execute(urlFormat())

        viewModel.download(urlFormat())
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        item.isChecked = true;
        when(item.itemId) {
            R.id.menu_10 -> viewModel.topTen = true
            R.id.menu_25 -> viewModel.topTen = false
            R.id.menu_freeApps -> viewModel.urlType = UrlType.FreeApps
            R.id.menu_paidApps -> viewModel.urlType = UrlType.PaidApps
            R.id.menu_songs -> viewModel.urlType = UrlType.Songs
            R.id.menu_movies -> viewModel.urlType = UrlType.Movies
        }

//        val xmlParser = XmlParser(this)
//        xmlParser.execute(urlFormat())
        viewModel.download(urlFormat())
        return super.onOptionsItemSelected(item)
    }

    private fun urlFormat () :String {

        return when(viewModel.urlType) {
            UrlType.FreeApps -> urlFreeApps.format(if(viewModel.topTen) 10 else 25)
            UrlType.PaidApps -> urlPaidApps.format(if(viewModel.topTen) 10 else 25)
            UrlType.Songs -> urlSongs.format(if(viewModel.topTen) 10 else 25)
            UrlType.Movies -> urlMovies
            else -> urlFreeApps.format(if(viewModel.topTen) 10 else 25) // if we get a null from restore state for some reason
        }
    }

//    //callbacks-------------------------------------------------------------------------------------
//    override fun callBackDownloadThread(result: String?): Boolean {
//        TODO("Implement to parse on the main UI thread")
//    }
//
//    override fun xmlParserCallback(result: List<FeedEntry>) {
//     Log.d(TAG, "xmlParserCallback: result -> $result")
//        val listView = findViewById<ListView>(R.id.listView)
//        val feedAdapter = ListViewAdapter(result, this, R.layout.feed_entry)
//        listView.adapter = feedAdapter
//    }
}
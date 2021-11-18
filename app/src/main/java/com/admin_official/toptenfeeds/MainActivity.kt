package com.admin_official.toptenfeeds

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import java.util.*

enum class UrlType {
    FreeApps, PaidApps, Songs, Movies
}

private const val TAG = "De_MainActivity"

//URLs----------------------------------------------------------------------------------------------
private const val urlFreeApps = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
private const val urlPaidApps = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
private const val urlSongs = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
private const val urlMovies = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topMovies/xml"

//MainActivity--------------------------------------------------------------------------------------
class MainActivity : AppCompatActivity() {

    //ViewModel
    private val viewModel by viewModels<DownloaderViewModel>()

    //ListView
    private val listView by lazy {findViewById<ListView>(R.id.listView)}
    private val adapter by lazy {ListViewAdapter(Collections.emptyList(), this, R.layout.feed_entry)}

    //Activity LifeCycle----------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: in")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView.adapter = adapter
        viewModel.feedEntries.observe(this) {
            adapter.setFeedList(it ?: Collections.emptyList())
        }
    }

    //Menu------------------------------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: in")
        menuInflater.inflate(R.menu.feed_menu, menu)

        when(viewModel.urlType) {
            UrlType.Songs -> menu.findItem(R.id.menu_songs)?.isChecked = true
            UrlType.PaidApps -> menu.findItem(R.id.menu_paidApps)?.isChecked = true
            UrlType.FreeApps -> menu.findItem(R.id.menu_freeApps)?.isChecked = true
            UrlType.Movies -> menu.findItem(R.id.menu_movies)?.isChecked = true
        }

        if(viewModel.topTen) menu.findItem(R.id.menu_10).isChecked = true
        else menu.findItem(R.id.menu_25).isChecked = true

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

        viewModel.download(urlFormat())
        return super.onOptionsItemSelected(item)
    }

    private fun urlFormat () :String {
        return when(viewModel.urlType) {
            UrlType.FreeApps -> urlFreeApps.format(if(viewModel.topTen) 10 else 25)
            UrlType.PaidApps -> urlPaidApps.format(if(viewModel.topTen) 10 else 25)
            UrlType.Songs -> urlSongs.format(if(viewModel.topTen) 10 else 25)
            UrlType.Movies -> urlMovies
        }
    }
}
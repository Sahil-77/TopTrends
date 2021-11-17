package com.admin_official.toptenfeeds

import android.os.AsyncTask
import android.util.Log
import java.net.URL

interface DownloadThreadCallback {
    fun callBackDownloadThread(result: String?): Boolean;
}

class DownloadThread(private val callback: DownloadThreadCallback): AsyncTask<String, Void, String>() {
    private val TAG = "De_DownloadThread"

    fun executeOnSameThread(url: String?) {
        onPostExecute(doInBackground(url))
    }

    override fun onPostExecute(result: String?) {
        Log.d(TAG, "onPostExecute: result: $result")
        Log.d(TAG, "onPostExecute: hello")
        callback.callBackDownloadThread(result)
    }

    override fun doInBackground(vararg url: String?): String? {
        val rssFeed = downloadXml(url[0])
        if(rssFeed == null || rssFeed.isEmpty()) {
            Log.e(TAG, "doInBackground: error downloading")
            return null
        }
        return rssFeed;
    }

    private fun downloadXml(urlPath: String?) : String? {
        val url = URL(urlPath)
        return try {
            url.readText();
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
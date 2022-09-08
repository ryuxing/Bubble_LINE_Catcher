package com.ryuxing.bubblelinecatcher.data

import android.graphics.Color
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView

class Sticker {
    companion object{
        fun setSticker(url:String,view:WebView){
            //WebView.setWebContentsDebuggingEnabled(true)
            view.setBackgroundColor(Color.TRANSPARENT)
            val settings = view.settings
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            settings.useWideViewPort = true
            settings.allowContentAccess = true
            settings.loadsImagesAutomatically = true
            settings.allowFileAccess = true
            settings.setSupportMultipleWindows(true)
            settings.blockNetworkImage = false
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            val html = "<html><head>" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "<title>Sticker</title>" +
                    "<style>" +
                    "body{" +
                        "background:rgba(0,0,0,0);" +
                        "margin:0px;" +
                        "padding:0px;" +
                        "width:100%;" +
                        "height:100%;" +
                        "overflow:hidden;" +
                    "}img{" +
                        "width:100%; " +
                        "height:100%; " +
                        "object-fit:contain ; " +
                        "filter: drop-shadow(0 0 3px white);\br" +
                    "</style></head>" +
                    "<body>" +
                            "<img src='$url'>Stamp</img>" +
                    "</body></html>"
            view.loadData(html, "text/html", "utf-8");


        }
    }
}
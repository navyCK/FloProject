package com.navyck.floproject

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {
    var musicUrl = ""
    var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clearTextView()

        startThread()

        playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }

    }

    private fun startThread() {
        val thread = NetworkThread()
        thread.start()
    }

    private fun setImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).override(300, 300).into(imageView)
    }


    inner class NetworkThread: Thread() {
        override fun run() {
            val site = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json"
            val url = URL(site)
            val conn = url.openConnection()
            val input = conn.getInputStream()
            val isr = InputStreamReader(input)
            val br = BufferedReader(isr)
            var str: String?
            val buf = StringBuffer()

            do {
                str = br.readLine()

                if(str!=null){
                    buf.append(str)
                }
            } while (str!=null)

            val root = JSONObject(buf.toString())
            val image: String = root.getString("image")
            val singer: String = root.getString("singer")
            val album: String = root.getString("album")
            val title: String = root.getString("title")
//            var duration: Int = root.getInt("duration")

            musicUrl = root.getString("file")


            runOnUiThread {
                clearTextView()
                setImage(image)

                mediaPlayer.setDataSource(musicUrl)
                mediaPlayer.prepare()

                titleTextView.append(title)
                artistTextView.append(singer)
                albumTextView.append(album)

            }

        }
    }

    private fun clearTextView() {
        titleTextView.text = ""
        artistTextView.text = ""
        albumTextView.text = ""
    }


}
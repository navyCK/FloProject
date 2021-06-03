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

        var thread = NetworkThread()
        thread.start()

        playButton.setOnClickListener() {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }

    }

    private fun setImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).override(300, 300).into(imageView)
    }


    inner class NetworkThread: Thread() {
        override fun run() {
            var site = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json"
            var url = URL(site)
            var conn = url.openConnection()
            var input = conn.getInputStream()
            var isr = InputStreamReader(input)
            var br = BufferedReader(isr)
            var str: String? = null
            var buf = StringBuffer()

            do {
                str = br.readLine()

                if(str!=null){
                    buf.append(str)
                }
            } while (str!=null)

            var root = JSONObject(buf.toString())
            var image: String = root.getString("image")
            var singer: String = root.getString("singer")
            var album: String = root.getString("album")
            var title: String = root.getString("title")
            var duration: Int = root.getInt("duration")

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
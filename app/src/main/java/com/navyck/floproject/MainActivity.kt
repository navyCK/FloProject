package com.navyck.floproject

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {
    var musicUrl = ""
    var mediaPlayer = MediaPlayer()
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clearTextView()
        startThread()
        touchPlayButton()

        // Seek bar change listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

    }


    private fun touchPlayButton() {
        playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.ic_play_arrow)
            } else {
                mediaPlayer.start()
                playButton.setImageResource(R.drawable.ic_pause)
            }
            initializeSeekBar()
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
            val duration: String = root.getString("duration")

            musicUrl = root.getString("file")


            runOnUiThread {
                clearTextView()
                setImage(image)

                mediaPlayer.setDataSource(musicUrl)
                mediaPlayer.prepare()

                titleTextView.append(title)
                artistTextView.append(singer)
                albumTextView.append(album)
                tv_due.text = "$duration sec"

            }

        }
    }

    private fun clearTextView() {
        titleTextView.text = ""
        artistTextView.text = ""
        albumTextView.text = ""
    }

    private fun initializeSeekBar() {
        seekBar.max = mediaPlayer.seconds

        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentSeconds

            tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    // Creating an extension property to get the media player time duration in seconds
    val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }
    // Creating an extension property to get media player current position in seconds
    val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }


}

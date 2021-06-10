package com.navyck.floproject

import android.annotation.SuppressLint
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
        setSeekBarListener()

    }

    private fun setSeekBarListener() {
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

            val time: String = setFormat(duration.toInt())

            musicUrl = root.getString("file")


            runOnUiThread {
                clearTextView()
                setImage(image)

                mediaPlayer.setDataSource(musicUrl)
                mediaPlayer.prepare()

                titleTextView.append(title)
                artistTextView.append(singer)
                albumTextView.append(album)
                endTimeView.text = time

            }

        }
    }

    private fun clearTextView() {
        titleTextView.text = ""
        artistTextView.text = ""
        albumTextView.text = ""
    }

    @SuppressLint("SetTextI18n")
    private fun initializeSeekBar() {
        seekBar.max = mediaPlayer.seconds

        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentSeconds

            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            val startTime: String = setFormat(mediaPlayer.currentSeconds)
            val endTime: String = setFormat(diff)

            startTimeView.text = startTime
            endTimeView.text = endTime

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun setFormat(second:Int): String {
        var min: Int = second / 60
        val hour = min / 60
        val sec = second % 60
        min %= 60
        var strHour = hour.toString()
        var strMin = min.toString()
        var strSec = sec.toString()
        if (hour.toString().length == 1) {
            strHour = "0$hour"
        }
        if (min.toString().length == 1) {
            strMin = "0$min"
        }
        if (sec.toString().length == 1) {
            strSec = "0$sec"
        }
        return "$strHour:$strMin:$strSec"
    }

    private val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }

    private val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }


}

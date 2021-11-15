package com.example.for_teach

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    var mp
            : MediaPlayer? = null
    var position = 0
    var sb: SeekBar? = null
    var mySongs: ArrayList<File>? = null
    var updateSeekBar: Thread? = null
    var pause: Button? = null
    var songNameText: TextView? = null

    var sname: String? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        songNameText = findViewById(R.id.txtSongLabel) as TextView
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setTitle("Now Playing")
        pause = findViewById(R.id.pause) as Button
        var previous : Button = findViewById(R.id.previous)
        var next : Button  = findViewById(R.id.next)
        sb = findViewById(R.id.seekBar) as SeekBar
        updateSeekBar = object : Thread() {
            override fun run() {
                val totalDuration = mp!!.duration
                var currentPosition = 0
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500)
                        currentPosition = mp!!.currentPosition
                        sb!!.progress = currentPosition
                    } catch (e: InterruptedException) {
                    }
                }
            }
        }
        if (mp != null) {
            mp!!.stop()
            mp!!.release()
        }
        val i = intent
        val b = i.extras
        mySongs = b!!.getSerializable("songs") as ArrayList<File>
        sname = mySongs!![position].name.toString()
        val SongName = i.getStringExtra("songname")
        songNameText!!.text = SongName
        songNameText!!.isSelected = true
        position = b.getInt("pos", 0)
        val u = Uri.parse(mySongs!![position].toString())
        val mp = MediaPlayer.create(applicationContext, u)
        mp.start()
        sb!!.max = mp.getDuration()
        (updateSeekBar as Thread).start()
        sb!!.progressDrawable.setColorFilter(
            resources.getColor(R.color.purple_200),
            PorterDuff.Mode.MULTIPLY
        )
        sb!!.thumb.setColorFilter(resources.getColor(R.color.purple_200), PorterDuff.Mode.SRC_IN)
        sb!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar, i: Int,
                b: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mp.seekTo(seekBar.progress)
            }
        })
        pause!!.setOnClickListener {
            sb!!.max = mp.getDuration()
            if (mp.isPlaying()) {
                pause!!.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp)
                mp.pause()
            } else {
                pause!!.setBackgroundResource(R.drawable.pause)
                mp.start()
            }
        }
        next.setOnClickListener(View.OnClickListener {
            mp.stop()
            mp.release()
            position = (position + 1) % mySongs!!.size
            val u = Uri.parse(mySongs!![position].toString())
            val mp = MediaPlayer.create(applicationContext, u)
            sname = mySongs!![position].name.toString()
            songNameText!!.text = sname
            try {
                mp.start()
            } catch (e: Exception) {
            }
        })
        previous.setOnClickListener(View.OnClickListener {
            mp.stop()
            mp.release()
            position = if (position - 1 < 0) mySongs!!.size - 1 else position - 1
            val u = Uri.parse(mySongs!![position].toString())
            val mp = MediaPlayer.create(applicationContext, u)
            sname = mySongs!![position].name.toString()
            songNameText!!.text = sname
            mp.start()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
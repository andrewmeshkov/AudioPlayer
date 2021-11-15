package com.example.for_teach

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.webkit.PermissionRequest
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.util.*

class SongsPlayer : AppCompatActivity() {
    var listView: ListView? = null
    lateinit var items: Array<String?>
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_songs)
        listView = findViewById(R.id.listView) as ListView?
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    display()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied()) {
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: com.karumi.dexter.listener.PermissionRequest?,
                    token: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                }

                fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun findSong(root: File): ArrayList<File> {
        val at = ArrayList<File>()
        val files = root.listFiles()
        for (singleFile in files) {
            if (singleFile.isDirectory && !singleFile.isHidden) {
                at.addAll(findSong(singleFile))
            } else {
                if (singleFile.name.endsWith(".mp3") ||
                    singleFile.name.endsWith(".wav")
                ) {
                    at.add(singleFile)
                }
            }
        }
        return at
    }

    fun display() {
        val mySongs = findSong(Environment.getExternalStorageDirectory())
        items = arrayOfNulls(mySongs.size)
        for (i in mySongs.indices) {
            items[i] = mySongs[i].name.toString().replace(".mp3", "").replace(".wav", "")
        }
        val adp: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
        listView!!.adapter = adp
        listView!!.onItemClickListener =
            OnItemClickListener { adapterView, view, position, l ->
                val songName = listView!!.getItemAtPosition(position).toString()
                startActivity(
                    Intent(getApplicationContext(), MainActivity::class.java)
                        .putExtra("pos", position).putExtra("songs", mySongs)
                        .putExtra("songname", songName)
                )
            }
    }

}
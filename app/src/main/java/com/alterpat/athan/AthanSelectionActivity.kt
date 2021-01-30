package com.alterpat.athan

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alterpat.athan.adapter.PrayerCallClickListener
import com.alterpat.athan.adapter.PrayerCallsAdapter
import com.alterpat.athan.model.PrayerCallItem
import com.alterpat.athan.model.UserConfig
import com.alterpat.athan.tool.createNotificationChannel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_adhan_selection.*
import org.jetbrains.anko.doAsync
import java.lang.Exception


class AthanSelectionActivity : AppCompatActivity(), PrayerCallClickListener {
    private var prayerCalls = ArrayList<PrayerCallItem>()
    private lateinit var mAdapter : PrayerCallsAdapter
    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var previousPosition = 0

    private var updateProgressRunnable : Runnable? = null
    private lateinit var userConfig : UserConfig


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adhan_selection)

        /** set tool bar **/
        setSupportActionBar(toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        /** load user conf **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()
        var jsonConf: String? = sharedPref.getString("userConfig", "")

        if(jsonConf != "")
            userConfig = gsonBuilder.fromJson(jsonConf, UserConfig::class.java)
        else
            userConfig = UserConfig()

        /** populate recyclerview **/
        prayerCalls.add(PrayerCallItem("Abdul-Basit", R.raw.abdul_basit, "03:25"))
        prayerCalls.add(PrayerCallItem("Abdul-Ghaffar", R.raw.abdul_ghaffar, "03:06"))
        prayerCalls.add(PrayerCallItem("Abdul-Hakam", R.raw.abdul_hakam, "03:10"))
        prayerCalls.add(PrayerCallItem("Al-Aqsa", R.raw.adhan_alaqsa, "03:45"))
        prayerCalls.add(PrayerCallItem("Egypt", R.raw.adhan_egypt, "03:02"))
        prayerCalls.add(PrayerCallItem("Halab", R.raw.adhan_halab, "03:42"))
        prayerCalls.add(PrayerCallItem("Al-Madina", R.raw.adhan_madina, "01:35"))
        prayerCalls.add(PrayerCallItem("Makkah", R.raw.adhan_makkah, "03:21"))
        prayerCalls.add(PrayerCallItem("Al-Hossaini", R.raw.al_hossaini, "03:16"))
        prayerCalls.add(PrayerCallItem("Bakir Bash", R.raw.bakir_bash, "05:47"))
        prayerCalls.add(PrayerCallItem("Hafez", R.raw.hafez, "02:46"))
        prayerCalls.add(PrayerCallItem("Hafiz Murad", R.raw.hafiz_murad, "01:42"))
        prayerCalls.add(PrayerCallItem("Menshawi", R.raw.menshaoui, "03:55"))
        prayerCalls.add(PrayerCallItem("Naghshbandi", R.raw.naghshbandi, "02:42"))
        prayerCalls.add(PrayerCallItem("Saber", R.raw.saber, "02:29"))
        prayerCalls.add(PrayerCallItem("Sharif Doman", R.raw.sharif_doman, "04:41"))
        prayerCalls.add(PrayerCallItem("Yusuf Islam", R.raw.yusuf_islam, "01:38"))
        prayerCalls.add(PrayerCallItem("Default", R.raw.athan, "02:00"))


        // set user athan to selected
        prayerCalls.forEach {
            if(it.athanName == userConfig.athan) {
                it.isSelected = true
                previousPosition = prayerCalls.indexOf(it)
            }
        }

        mAdapter = PrayerCallsAdapter(this, prayerCalls, this)

        athanRecyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }


    }

    override fun onClick(item: PrayerCallItem, position: Int) {
        prayerCalls[previousPosition].isSelected = false
        item.isSelected = true

        prayerCalls[previousPosition].isSelected = false
        item.isSelected = true

        mAdapter.notifyItemChanged(position)
        mAdapter.notifyItemChanged(previousPosition)
        previousPosition = position

        updateUserConf(item.athanName, item.resId)
    }

    /*
    override fun onChecked(item: PrayerCallItem, position: Int) {
        prayerCalls[previousPosition].isSelected = false
        item.isSelected = true

        mAdapter.notifyItemChanged(position)
        mAdapter.notifyItemChanged(previousPosition)
        previousPosition = position

        updateUserConf(item.athanName)
    }
     */


    override fun onPlay(item: PrayerCallItem, position: Int) {
            if(!prayerCalls[position].isPlaying){
                // update items
                prayerCalls[previousPosition].isPlaying = false
                prayerCalls[position].isPlaying = true
                // update buttons
                mAdapter.notifyItemChanged(position)
                mAdapter.notifyItemChanged(previousPosition)

                // stop current athan
                if (mediaPlayer != null) {
                    try{
                        if(mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.stop()
                            mediaPlayer!!.release()
                            mediaPlayer = null
                        }
                    }catch (e: Exception){}

                }

                mediaPlayer = MediaPlayer.create(this, prayerCalls[position].resId)
                val rate = mediaPlayer?.duration?.div(100)
                progressIndicator.visibility = View.VISIBLE
                mediaPlayer?.setOnPreparedListener {
                    mediaPlayer?.start()
                    progressIndicator.progress = 0
                    updateProgressRunnable = null

                    updateProgressRunnable = Runnable {
                        kotlin.run {
                            if(updateProgressRunnable != null && progressIndicator.progress < 100) {
                                Handler(Looper.getMainLooper()).postDelayed(updateProgressRunnable!!, rate!!.toLong())
                                runOnUiThread{
                                    progressIndicator.progress += 1
                                }
                            }else{
                                runOnUiThread{
                                    progressIndicator.progress = 0
                                    progressIndicator.visibility = View.GONE
                                }
                            }
                        }
                    }

                    doAsync { updateProgressRunnable?.run() }


                }
                // update button when done
                mediaPlayer?.setOnCompletionListener {
                    it.stop()
                    it.release()
                    prayerCalls[position].isPlaying = false
                    mAdapter.notifyItemChanged(position)

                    // stop progress indicator
                    updateProgressRunnable = null
                    progressIndicator.progress = 0
                    progressIndicator.visibility = View.GONE
                }

            }else{
                // stop player
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null

                prayerCalls[position].isPlaying = false
                mAdapter.notifyItemChanged(position)

                // stop progress indicator
                updateProgressRunnable = null
                progressIndicator.progress = 0
                progressIndicator.visibility = View.GONE
            }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId ==android.R.id.home){
            if (mediaPlayer != null) {
                try{
                    if(mediaPlayer!!.isPlaying) {
                        mediaPlayer!!.stop()
                        mediaPlayer!!.release()
                        mediaPlayer = null
                    }
                }catch (e: Exception){}

            }
            setResult(Activity.RESULT_OK)
            finish()
        }

        return true
    }

    private fun updateUserConf(athan : String, res: Int){
        /** load userConf from shared prefs **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()

        Log.d("TESTINGSBEFORE", userConfig.athanRes.toString())

        /** update userConf **/
        userConfig.athan = athan
        userConfig.athanRes = res
        userConfig.CHANNEL_ID_OLD = userConfig.CHANNEL_ID
        userConfig.CHANNEL_ID = "athan_notification_channel_$res"

        /** update shared prefs **/
        with (sharedPref.edit()) {
            putString("userConfig", gsonBuilder.toJson(userConfig))
            apply()
        }

        Log.d("TESTINGSAFTER", userConfig.athanRes.toString())

        /** update notification channel **/
        createNotificationChannel(this)
    }
}
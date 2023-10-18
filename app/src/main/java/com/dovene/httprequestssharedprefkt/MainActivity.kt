package com.dovene.httprequestssharedprefkt

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.dovene.httprequestssharedprefkt.AlbumApi.AlbumService
import com.dovene.httprequestssharedprefkt.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val idKey = "ID_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setViewItems()
    }

    private fun setViewItems() {
        binding.searchBtn.setOnClickListener { search() }
        if (getIdFromSharedPreferences() != 0) {
            binding.idEt.setText(getIdFromSharedPreferences().toString())
        }
    }

    private fun search() {
        if (binding.idEt.text.toString().isEmpty()) {
            val alertDialog: AlertDialog = AlertDialog.Builder(this@MainActivity).create()
            alertDialog.setMessage("Veuillez saisir un entier")
            alertDialog.setButton(
                AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { dialog, _ -> dialog.dismiss() }
            alertDialog.show()
            return
        }
        callService()
    }

    private fun callService() {
        val service: AlbumService =
            AlbumApi().getClient().create(AlbumService::class.java)
        val call: Call<List<Album>> =
            service.getBooks(Integer.valueOf(binding.idEt.text.toString()))
        call.enqueue(object : Callback<List<Album>> {
            override fun onResponse(call: Call<List<Album>>, response: Response<List<Album>>) {
                updateView(response)
                writeIdToSharePreferences()
            }
            override fun onFailure(call: Call<List<Album>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun updateView(response: Response<List<Album>>) {
        response.body().let {
            if (!it.isNullOrEmpty()) {
                binding.photoTitle.text = it[0].title
                //API https://via.placeholder.com requires user agent while dealing with images so we need a little addition before querying images
                val glideUrl = GlideUrl(
                    it[0].thumbnailUrl, LazyHeaders.Builder()
                        .addHeader(
                            "User-Agent",
                            WebSettings.getDefaultUserAgent(applicationContext)
                        )
                        .build()
                )
                Glide.with(applicationContext).load(glideUrl)
                    .into(binding.photoImage)
            }else {
                Toast.makeText(this,"Aucun résultat", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun writeIdToSharePreferences() {
        getPreferences(MODE_PRIVATE)
            .edit()
            .putInt(idKey, Integer.valueOf(binding.idEt.getText().toString()))
            .apply()
    }

    private fun getIdFromSharedPreferences(): Int {
        return getPreferences(MODE_PRIVATE).getInt(idKey, 0)
    }
}
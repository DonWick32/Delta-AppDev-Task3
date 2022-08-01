package com.example.quotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.*

const val baseURL = "https://api.quotable.io"
var id : String = ""
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txtQuote = findViewById<TextView>(R.id.txt_quote)
        val txtAuthor = findViewById<TextView>(R.id.txtAuthor)
        val txtHeaderMain = findViewById<TextView>(R.id.txtHeaderMain)
        val toolbarHome = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarHome)
        val btnFavList = findViewById<ImageView>(R.id.btnFavList)
        val btnFavMain = findViewById<CheckBox>(R.id.btnFavMain)
        txtQuote.text = "Loading..."
        txtAuthor.text = ""
        val btn = findViewById<Button>(R.id.btn)
        val btnQuote = findViewById<Button>(R.id.btnQuote)
        val btnAuthor = findViewById<Button>(R.id.btnAuthor)
        val btnSpeak = findViewById<ImageButton>(R.id.btnSpeak)
        val btnShare = findViewById<ImageButton>(R.id.btnShare)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (this as AppCompatActivity?)!!.supportActionBar!!.hide()

        btn.setOnClickListener {
            val quotesFragment = Quotes_List()
            val fragment: Fragment? =
                supportFragmentManager.findFragmentByTag(Quotes_List::class.java.simpleName)
            if (fragment !is Quotes_List) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container_fragment, Quotes_List()).addToBackStack(null)
                    .commit()
            }

            btn.visibility = View.GONE
            txtQuote.visibility = View.GONE
            txtAuthor.visibility = View.GONE
            txtHeaderMain.visibility = View.GONE
            toolbarHome.visibility = View.GONE
            btnFavMain.visibility = View.GONE
            btnQuote.visibility = View.GONE
            btnAuthor.visibility = View.GONE
            btnSpeak.visibility = View.GONE
            btnShare.visibility = View.GONE
            btnFavList.visibility = View.GONE
        }

        btnFavList.setOnClickListener {
            val quotesFragment = Favourites()
            val fragment: Fragment? =
                supportFragmentManager.findFragmentByTag(Favourites::class.java.simpleName)
            if (fragment !is Favourites) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container_fragment, Favourites()).addToBackStack(null)
                    .commit()

            }

            btn.visibility = View.GONE
            txtQuote.visibility = View.GONE
            txtAuthor.visibility = View.GONE
            txtHeaderMain.visibility = View.GONE
            toolbarHome.visibility = View.GONE
            btnFavMain.visibility = View.GONE
            btnQuote.visibility = View.GONE
            btnAuthor.visibility = View.GONE
            btnSpeak.visibility = View.GONE
            btnShare.visibility = View.GONE
            btnFavList.visibility = View.GONE
        }

        btnQuote.setOnClickListener {
            val quotesFragment = SearchQuote()
            val fragment: Fragment? =
                supportFragmentManager.findFragmentByTag(SearchQuote::class.java.simpleName)
            if (fragment !is SearchQuote) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container_fragment, SearchQuote()).addToBackStack(null)
                    .commit()

            }

            btn.visibility = View.GONE
            txtQuote.visibility = View.GONE
            txtAuthor.visibility = View.GONE
            txtHeaderMain.visibility = View.GONE
            toolbarHome.visibility = View.GONE
            btnFavMain.visibility = View.GONE
            btnQuote.visibility = View.GONE
            btnAuthor.visibility = View.GONE
            btnSpeak.visibility = View.GONE
            btnShare.visibility = View.GONE
            btnFavList.visibility = View.GONE
        }

        btnAuthor.setOnClickListener {
            val quotesFragment = SearchAuthor()
            val fragment: Fragment? =
                supportFragmentManager.findFragmentByTag(SearchAuthor::class.java.simpleName)
            if (fragment !is SearchAuthor) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container_fragment, SearchAuthor()).addToBackStack(null)
                    .commit()

            }

            btn.visibility = View.GONE
            txtQuote.visibility = View.GONE
            txtAuthor.visibility = View.GONE
            txtHeaderMain.visibility = View.GONE
            toolbarHome.visibility = View.GONE
            btnFavMain.visibility = View.GONE
            btnQuote.visibility = View.GONE
            btnAuthor.visibility = View.GONE
            btnSpeak.visibility = View.GONE
            btnShare.visibility = View.GONE
            btnFavList.visibility = View.GONE
        }

        var viewModel: ViewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        getMyData(viewModel, btnFavMain)

        btnShare.setOnClickListener{
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plane"
            share.putExtra(Intent.EXTRA_TEXT,"Quote - " + txtQuote.text + "\nAuthor - " + (txtAuthor.text as String).slice(2 until txtAuthor.text.length))
            this.startActivity(share)
        }
        btnSpeak.setOnClickListener{
            lateinit var tts: TextToSpeech
            tts = TextToSpeech(this, TextToSpeech.OnInitListener {
                if (it== TextToSpeech.SUCCESS) {
                    tts.language = Locale.UK
                    tts.setSpeechRate(1.0F)
                    tts.speak(txtQuote.text as String + " Quote by " + txtAuthor.text, TextToSpeech.QUEUE_ADD, null)
                }
            })
        }

        btnFavMain.setOnCheckedChangeListener { buttonView, isChecked ->
            var viewModel: ViewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
            val quote = DataEntity(id, txtQuote.text.toString(), (txtAuthor.text as String).slice(2 until txtAuthor.text.length))
            if (isChecked) {
                viewModel.insertQuote(quote)
                Toast.makeText(this, "Added to Favourites!", Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.deleteQuote(quote)
                Toast.makeText(this, "Removed from Favourites!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getMyData(viewModel: ViewModel, btnFavMain: CheckBox) {
        val client = OkHttpClient.Builder().build()
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseURL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getData()
        val txtQuote = findViewById<TextView>(R.id.txt_quote)
        val txtAuthor = findViewById<TextView>(R.id.txtAuthor)

        retrofitData.enqueue(object : Callback<DataItem?> {
            override fun onResponse(call: Call<DataItem?>, response: Response<DataItem?>) {
                val responseBody = response.body()!!
                txtQuote.text = responseBody.content
                txtAuthor.text = "~ " + responseBody.author
                id = responseBody._id
                var list_it : List<DataEntity> = listOf()
                list_it = viewModel.getQuotes()
                for (i in list_it.indices){
                    if (list_it[i].id == id) {
                        btnFavMain.isChecked = true
                        break
                    }
                }
            }

            override fun onFailure(call: Call<DataItem?>, t: Throwable) {
                //TODO("Not yet implemented")
            }
        })
    }

    interface ApiInterface {
        @GET("/random")
        fun getData(): Call<DataItem>
    }

}
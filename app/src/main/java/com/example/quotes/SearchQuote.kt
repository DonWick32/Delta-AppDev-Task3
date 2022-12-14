package com.example.quotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quotes.databinding.FragmentQuotesListBinding
import com.example.quotes.databinding.FragmentSearchQuoteBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.Exception

class SearchQuote : Fragment() {

    var pgNum = 1
    lateinit var myAdapter: MyAdapter
    private val baseURL = "https://api.quotable.io"
    var limit = 20
    var count = 0
    var list = Data()
    var listt: MutableList<Int> = mutableListOf()

    private val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseURL)
        .build()
        .create(SearchQuote.ApiInterface::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bind = FragmentSearchQuoteBinding.inflate(layoutInflater)
        bind.progressQuote.visibility = View.INVISIBLE
        var viewModel: ViewModel = ViewModelProviders.of(context as FragmentActivity).get(ViewModel::class.java)

        val client = OkHttpClient.Builder().build()
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseURL)
            .build()
            .create(ApiInterface::class.java)
        bind.btnBackQuote.setOnClickListener {
            val intent = Intent (this@SearchQuote.requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        lateinit var linearLayoutManager: LinearLayoutManager
        bind.recyclerQuote.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this@SearchQuote.requireContext())
        bind.recyclerQuote.layoutManager = linearLayoutManager
        bind.inputQuote.hint = "Enter a Phrase"

        bind.btnSearchQuote.setOnClickListener {
            if (bind.inputQuote.text.toString() != "") {
                bind.progressQuote.visibility = View.VISIBLE
                val retrofitData = retrofitBuilder.getData(pgNum, bind.inputQuote.text.toString().lowercase())
                var list_it : List<DataEntity> = listOf()
                retrofitData.enqueue(object : Callback<Data> {
                    override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                        list = Data()
                        listt = mutableListOf()
                        bind.progressQuote.visibility = View.INVISIBLE
                        val responseBody = response.body()!!
                        if (responseBody.count > 0) {
                            try {
                                val imm =
                                    activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(
                                    activity!!.currentFocus!!.windowToken,
                                    0
                                )
                            } catch (e: Exception) {
                            }
                            for (i in responseBody.results.indices)
                                listt += 0
                            list_it = viewModel.getQuotes()
                            for(i in list_it.indices) {
                                for (j in (listt.size-responseBody.results.size) until listt.size) {
                                    if (list_it[i].id == responseBody.results[j - listt.size + responseBody.results.size]._id) {
                                        listt[j] = 1
                                        break
                                    }
                                }
                            }
                            count = responseBody.results.size
                            list.results += responseBody.results
                            myAdapter = MyAdapter(this@SearchQuote.requireContext(), list, listt)
                            bind.recyclerQuote.adapter = myAdapter
                            myAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                this@SearchQuote.requireContext(),
                                "No results found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Data?>, t: Throwable) {
                        Toast.makeText(
                            this@SearchQuote.requireContext(),
                            "Cannot obtain quotes, try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            else {
                Toast.makeText(
                    this@SearchQuote.requireContext(),
                    "Enter a Phrase!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        bind.recyclerQuote.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)  && count == 20 && newState== RecyclerView.SCROLL_STATE_IDLE) {
                    bind.progressQuote.visibility = View.VISIBLE
                    pgNum ++
                    getData(pgNum, bind, viewModel)
                }
                else if (!recyclerView.canScrollVertically(1) && newState== RecyclerView.SCROLL_STATE_IDLE)
                    Toast.makeText(this@SearchQuote.requireContext(), "No more pages to display!", Toast.LENGTH_SHORT).show()
            }
        })
        return bind.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun getData(page: Int, bind: FragmentSearchQuoteBinding, viewModel: ViewModel) {
        if (bind.inputQuote.text.toString() != "") {
            val retrofitData = retrofitBuilder.getData(pgNum, bind.inputQuote.text.toString().lowercase())
            var list_it : List<DataEntity> = listOf()
            retrofitData.enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                    bind.progressQuote.visibility = View.INVISIBLE
                    val responseBody = response.body()!!
                    if (responseBody.count > 0) {
                        try {
                            val imm =
                                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(
                                activity!!.currentFocus!!.windowToken,
                                0
                            )
                        } catch (e: Exception) {
                        }
                        for (i in responseBody.results.indices)
                            listt += 0
                        list_it = viewModel.getQuotes()
                        for(i in list_it.indices) {
                            for (j in (listt.size-responseBody.results.size) until listt.size) {
                                if (list_it[i].id == responseBody.results[j - listt.size + responseBody.results.size]._id) {
                                    listt[j] = 1
                                    break
                                }
                            }
                        }
                        count = responseBody.results.size
                        list.results += responseBody.results
                        myAdapter.list = list
                        myAdapter.presentList = listt
                        myAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@SearchQuote.requireContext(),
                            "No results found!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Data?>, t: Throwable) {
                    Toast.makeText(
                        this@SearchQuote.requireContext(),
                        "Cannot obtain quotes, try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
        else {
            Toast.makeText(
                this@SearchQuote.requireContext(),
                "Enter a Phrase!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    interface ApiInterface {
        @GET("/search/quotes?fields=content")
        fun getData(
            @Query("page") page: Int,
            @Query ("query") query: String,
        ): Call<Data>
    }
}






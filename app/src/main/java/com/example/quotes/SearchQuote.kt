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

private var pgNum = 0
class SearchQuote : Fragment() {

    private val baseURL = "https://api.quotable.io"
    lateinit var myAdapter: MyAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bind = FragmentSearchQuoteBinding.inflate(layoutInflater)
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
        bind.btnNextQuote.setOnClickListener {
            if (pgNum > 0) {
                pgNum++
                bind.txtPgQuote.text = "Page - " + pgNum.toString()
                val retrofitData =
                    retrofitBuilder.getData(pgNum, bind.inputQuote.text.toString().lowercase())

                retrofitData.enqueue(object : Callback<Data> {
                    override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                        val responseBody = response.body()!!
                        if (responseBody.count > 0) {
                            val list: MutableList<Int> = List(responseBody.count) { 0 }.toMutableList()
                            var list_it : List<DataEntity> = listOf()
                            list_it = viewModel.getQuotes()
                            for(i in list_it.indices) {
                                for (j in list.indices) {
                                    if (list_it[i].id == responseBody.results[j]._id) {
                                        list[j] = 1
                                        break
                                    }
                                }
                            }
                            myAdapter = MyAdapter(this@SearchQuote.requireContext(), responseBody, list)
                            myAdapter.notifyDataSetChanged()
                            bind.recyclerQuote.adapter = myAdapter
                        } else {
                            pgNum--
                            bind.txtPgQuote.text = "Page - " + pgNum.toString()
                            Toast.makeText(
                                this@SearchQuote.requireContext(),
                                "No more pages to display!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Data?>, t: Throwable) {
                        pgNum--
                        bind.txtPgQuote.text = "Page - " + pgNum.toString()
                        Toast.makeText(
                            this@SearchQuote.requireContext(),
                            "No more pages to display!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            else {
                Toast.makeText(
                    this@SearchQuote.requireContext(),
                    "Enter a Phrase and Search!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        bind.btnPreviousQuote.setOnClickListener {
            if (pgNum > 1) {
                pgNum--
                bind.txtPgQuote.text = "Page - " + pgNum.toString()

                val retrofitData = retrofitBuilder.getData(pgNum,bind.inputQuote.text.toString().lowercase())

                retrofitData.enqueue(object : Callback<Data> {
                    override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                        val responseBody = response.body()!!
                        val list: MutableList<Int> = List(responseBody.count) { 0 }.toMutableList()
                        var list_it : List<DataEntity> = listOf()
                        list_it = viewModel.getQuotes()
                        for(i in list_it.indices) {
                            for (j in list.indices) {
                                if (list_it[i].id == responseBody.results[j]._id) {
                                    list[j] = 1
                                    break
                                }
                            }
                        }
                        myAdapter = MyAdapter(this@SearchQuote.requireContext(), responseBody, list)
                        myAdapter.notifyDataSetChanged()
                        bind.recyclerQuote.adapter = myAdapter
                    }

                    override fun onFailure(call: Call<Data?>, t: Throwable) {
                        //TODO("Not yet implemented")
                    }
                })
            }
        }
        lateinit var linearLayoutManager: LinearLayoutManager
        bind.recyclerQuote.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this@SearchQuote.requireContext())
        bind.recyclerQuote.layoutManager = linearLayoutManager
        bind.inputQuote.hint = "Enter a Phrase"
        bind.btnSearchQuote.setOnClickListener {
            if (bind.inputQuote.text.toString() != "") {
                val retrofitData =
                    retrofitBuilder.getData(1, bind.inputQuote.text.toString().lowercase())
                retrofitData.enqueue(object : Callback<Data> {
                    override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
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
                            val list: MutableList<Int> = List(responseBody.count) { 0 }.toMutableList()
                            var list_it : List<DataEntity> = listOf()
                            list_it = viewModel.getQuotes()
                            for(i in list_it.indices) {
                                for (j in list.indices) {
                                    if (list_it[i].id == responseBody.results[j]._id) {
                                        list[j] = 1
                                        break
                                    }
                                }
                            }
                            myAdapter = MyAdapter(this@SearchQuote.requireContext(), responseBody, list)
                            myAdapter.notifyDataSetChanged()
                            bind.recyclerQuote.adapter = myAdapter
                            pgNum = 1
                            bind.txtPgQuote.text = "Page - " + pgNum.toString()
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
                            "No results found!",
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

        return bind.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    interface ApiInterface {
        @GET("/search/quotes?fields=content")
        fun getData(
            @Query("page") page: Int,
            @Query ("query") query: String,
        ): Call<Data>
    }
}






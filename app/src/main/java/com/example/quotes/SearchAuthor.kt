package com.example.quotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quotes.databinding.FragmentSearchAuthorBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager

import androidx.core.content.ContextCompat.getSystemService
import java.lang.Exception


private var pgNum = 0
class SearchAuthor : Fragment() {

    private val baseURL = "https://api.quotable.io"
    lateinit var myAdapter: MyAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bind = FragmentSearchAuthorBinding.inflate(layoutInflater)
        var viewModel: ViewModel = ViewModelProviders.of(context as FragmentActivity).get(ViewModel::class.java)
        val client = OkHttpClient.Builder().build()
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseURL)
            .build()
            .create(ApiInterface::class.java)
        bind.btnBackAuthor.setOnClickListener {
            val intent = Intent (this@SearchAuthor.requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        bind.btnNextAuthor.setOnClickListener {
            if (pgNum > 0) {
                pgNum++
                bind.txtPgAuthor.text = "Page - " + pgNum.toString()
                val retrofitData =
                    retrofitBuilder.getData(pgNum, bind.inputAuthor.text.toString().lowercase())

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
                            myAdapter = MyAdapter(this@SearchAuthor.requireContext(), responseBody, list)
                            myAdapter.notifyDataSetChanged()
                            bind.recyclerAuthor.adapter = myAdapter
                        } else {
                            pgNum--
                            bind.txtPgAuthor.text = "Page - " + pgNum.toString()
                            Toast.makeText(
                                this@SearchAuthor.requireContext(),
                                "No more pages to display!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Data?>, t: Throwable) {
                        pgNum--
                        bind.txtPgAuthor.text = "Page - " + pgNum.toString()
                        Toast.makeText(
                            this@SearchAuthor.requireContext(),
                            "No more pages to display!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            else {
                Toast.makeText(
                    this@SearchAuthor.requireContext(),
                    "Enter Author Name and Search!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        bind.btnPreviousAuthor.setOnClickListener {
            if (pgNum > 1) {
                pgNum--
                bind.txtPgAuthor.text = "Page - " + pgNum.toString()

                val retrofitData = retrofitBuilder.getData(pgNum,bind.inputAuthor.text.toString().lowercase())

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
                        myAdapter = MyAdapter(this@SearchAuthor.requireContext(), responseBody, list)
                        myAdapter.notifyDataSetChanged()
                        bind.recyclerAuthor.adapter = myAdapter
                    }

                    override fun onFailure(call: Call<Data?>, t: Throwable) {
                        //TODO("Not yet implemented")
                    }
                })
            }
        }
        lateinit var linearLayoutManager: LinearLayoutManager
        bind.recyclerAuthor.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this@SearchAuthor.requireContext())
        bind.recyclerAuthor.layoutManager = linearLayoutManager
        bind.inputAuthor.hint = "Enter Author Name"
        bind.btnSearchAuthor.setOnClickListener {
            if (bind.inputAuthor.text.toString() != "") {
                val retrofitData =
                    retrofitBuilder.getData(1, bind.inputAuthor.text.toString().lowercase())
                retrofitData.enqueue(object : Callback<Data> {
                    override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                        val responseBody = response.body()!!
                        if (responseBody.count > 0) {
                            try {
                                val imm =
                                    activity!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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
                            myAdapter = MyAdapter(this@SearchAuthor.requireContext(), responseBody, list)
                            myAdapter.notifyDataSetChanged()
                            bind.recyclerAuthor.adapter = myAdapter
                            pgNum = 1
                            bind.txtPgAuthor.text = "Page - " + pgNum.toString()
                        } else {
                            Toast.makeText(
                                this@SearchAuthor.requireContext(),
                                "No results found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Data?>, t: Throwable) {
                        Toast.makeText(
                            this@SearchAuthor.requireContext(),
                            "No results found!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            else {
                Toast.makeText(
                    this@SearchAuthor.requireContext(),
                    "Enter Author Name!",
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
        @GET("/search/quotes?fields=author")
        fun getData(
            @Query("page") page: Int,
            @Query ("query") query: String,
        ): Call<Data>
    }
}






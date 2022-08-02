package com.example.quotes

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
import androidx.recyclerview.widget.RecyclerView
import com.example.quotes.databinding.FragmentQuotesListBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class Quotes_List : Fragment() {

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
        .create(ApiInterface::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bind = FragmentQuotesListBinding.inflate(layoutInflater)
        val client = OkHttpClient.Builder().build()
        var viewModel: ViewModel = ViewModelProviders.of(context as FragmentActivity).get(ViewModel::class.java)
        bind.btnBackList.setOnClickListener {
            val intent = Intent (this@Quotes_List.requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        lateinit var linearLayoutManager: LinearLayoutManager
        bind.recyclerList.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this@Quotes_List.requireContext())
        bind.recyclerList.layoutManager = linearLayoutManager

        val retrofitData = retrofitBuilder.getData(pgNum)
        var list_it : List<DataEntity> = listOf()
        retrofitData.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                bind.progressList.visibility = View.INVISIBLE
                val responseBody = response.body()!!
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
                myAdapter = MyAdapter(this@Quotes_List.requireContext(), list, listt)
                bind.recyclerList.adapter = myAdapter
                myAdapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<Data?>, t: Throwable) {
                Toast.makeText(this@Quotes_List.requireContext(), "Cannot obtain quotes, try again.", Toast.LENGTH_SHORT).show()
            }
        })

        bind.recyclerList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)  && count == 20 && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    bind.progressList.visibility = View.VISIBLE
                    pgNum ++
                    getData(pgNum, bind, viewModel)
                }
                else if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE)
                    Toast.makeText(this@Quotes_List.requireContext(), "No more pages to display!", Toast.LENGTH_SHORT).show()
            }
        })
        return bind.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun getData(page: Int, bind: FragmentQuotesListBinding, viewModel: ViewModel) {
        val retrofitData = retrofitBuilder.getData(page)
        var list_it : List<DataEntity> = listOf()
        retrofitData.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                bind.progressList.visibility = View.INVISIBLE
                val responseBody = response.body()!!
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
            }
            override fun onFailure(call: Call<Data?>, t: Throwable) {
                Toast.makeText(this@Quotes_List.requireContext(), "Cannot obtain quotes, try again.", Toast.LENGTH_SHORT).show()
            }
        })

    }

    interface ApiInterface {
        @GET("/quotes")
        fun getData(
            @Query("page") page: Int,
        ): Call<Data>
    }

}







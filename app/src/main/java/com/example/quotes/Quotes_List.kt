package com.example.quotes

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quotes.databinding.FragmentQuotesListBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private var pgNum = 1
class Quotes_List : Fragment() {

    private val baseURL = "https://api.quotable.io"
    lateinit var myAdapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bind = FragmentQuotesListBinding.inflate(layoutInflater)
        var viewModel: ViewModel = ViewModelProviders.of(context as FragmentActivity).get(ViewModel::class.java)
        val client = OkHttpClient.Builder().build()
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseURL)
            .build()
            .create(ApiInterface::class.java)

        bind.btnBackList.setOnClickListener {
            val intent = Intent (this@Quotes_List.requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        bind.btnNextList.setOnClickListener {
            pgNum ++
            bind.txtPgList.text = "Page - " + pgNum.toString()
            val retrofitData = retrofitBuilder.getData(pgNum)

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
                    myAdapter = MyAdapter(this@Quotes_List.requireContext(), responseBody, list)
                    myAdapter.notifyDataSetChanged()
                    bind.recyclerList.adapter = myAdapter
                }

                override fun onFailure(call: Call<Data?>, t: Throwable) {
                    pgNum --
                    bind.txtPgList.text = "Page - " + pgNum.toString()
                    Toast.makeText(this@Quotes_List.requireContext(), "No more pages to display!", Toast.LENGTH_SHORT).show()
                }
            })
        }
        bind.btnPreviousList.setOnClickListener {
            if (pgNum > 1) {
                pgNum--
                bind.txtPgList.text = "Page - " + pgNum.toString()

                val retrofitData = retrofitBuilder.getData(pgNum)

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
                        myAdapter = MyAdapter(this@Quotes_List.requireContext(), responseBody, list)
                        myAdapter.notifyDataSetChanged()
                        bind.recyclerList.adapter = myAdapter
                    }

                    override fun onFailure(call: Call<Data?>, t: Throwable) {
                        //TODO("Not yet implemented")
                    }
                })
            }
        }

        lateinit var linearLayoutManager: LinearLayoutManager
        bind.recyclerList.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this@Quotes_List.requireContext())
        bind.recyclerList.layoutManager = linearLayoutManager
        val retrofitData = retrofitBuilder.getData(1)
        var list_it : List<DataEntity> = listOf()
        retrofitData.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                val responseBody = response.body()!!
                val list: MutableList<Int> = List(responseBody.count) { 0 }.toMutableList()
                list_it = viewModel.getQuotes()
                    for(i in list_it.indices) {
                        for (j in list.indices) {
                            if (list_it[i].id == responseBody.results[j]._id) {
                                list[j] = 1
                                break
                            }
                        }
                    }
                myAdapter = MyAdapter(this@Quotes_List.requireContext(), responseBody, list)
                myAdapter.notifyDataSetChanged()
                bind.recyclerList.adapter = myAdapter
            }
            override fun onFailure(call: Call<Data?>, t: Throwable) {
                //TODO("Not yet implemented")
            }
        })
        return bind.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    interface ApiInterface {
        @GET("/quotes")
        fun getData(
            @Query("page") page: Int,
        ): Call<Data>
    }

}







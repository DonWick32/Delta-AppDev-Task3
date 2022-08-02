package com.example.quotes

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quotes.databinding.FragmentFavouritesBinding
import androidx.recyclerview.widget.RecyclerView


class Favourites : Fragment() {
    lateinit var myAdapter: MyAdapter
    lateinit var viewModel: ViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bind = FragmentFavouritesBinding.inflate(layoutInflater)
        bind.btnBackFav.setOnClickListener {
            val intent = Intent (this@Favourites.requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        bind.recyclerFav.setHasFixedSize(true)
        var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this@Favourites.requireContext())
        bind.recyclerFav.layoutManager = linearLayoutManager

        var list =  Data()
        var size = 0
        viewModel = ViewModelProviders.of(context as FragmentActivity).get(ViewModel::class.java)
        var list_it = viewModel.getQuotes()
        size = list_it.size
        for(i in list_it.indices){
            list.results += DataItem(list_it[i].id,list_it[i].author,"",list_it[i].quote,"","",0,listOf())
        }

        myAdapter = MyAdapter(this@Favourites.requireContext(), list, List(list.results.size) { 1 })
        bind.recyclerFav.adapter = myAdapter
        myAdapter.notifyDataSetChanged()

        return bind.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
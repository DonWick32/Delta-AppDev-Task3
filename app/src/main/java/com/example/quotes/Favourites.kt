package com.example.quotes

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.os.Binder
import android.widget.Button
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quotes.databinding.FragmentFavouritesBinding
import com.example.quotes.databinding.FragmentQuotesListBinding


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

        bind.recyclerFav.setHasFixedSize(false)
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
        myAdapter = MyAdapter(this@Favourites.requireContext(), list, List(size) { 1 })
        myAdapter.notifyDataSetChanged()
        bind.recyclerFav.adapter = myAdapter
        return bind.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
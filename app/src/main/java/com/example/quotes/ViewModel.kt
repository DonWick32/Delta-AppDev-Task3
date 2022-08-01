package com.example.quotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ViewModel (app: Application): AndroidViewModel(app) {
    lateinit var allQuotes: List<DataEntity>
    init {
        allQuotes = listOf()
    }

    fun getQuotesObservers(): List<DataEntity> {
        return allQuotes
    }

    fun getQuotes(): List<DataEntity>{
        val quoteDao = RoomDbApp.getAppDatabase((getApplication()))?.dao()
        val list: List<DataEntity>? = quoteDao?.getQuotesInfo()
        if (list != null) {
            allQuotes = list
        }
        return allQuotes
    }

    fun insertQuote (entity: DataEntity) {
        val quoteDao =  RoomDbApp.getAppDatabase(getApplication())?.dao()
        quoteDao?.insertQuote(entity)
        getQuotes()
    }

    fun deleteQuote (entity: DataEntity) {
        val quoteDao =  RoomDbApp.getAppDatabase(getApplication())?.dao()
        quoteDao?.deleteQuote(entity)
        getQuotes()
    }
}
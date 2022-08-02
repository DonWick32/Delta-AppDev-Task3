package com.example.quotes

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.MaterialCheckable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext
import kotlin.properties.Delegates

class MyAdapter(val context: Context, var list: Data, var present: List<Int>): RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    var presentList = present.toMutableList()
    class ViewHolder(itemView: View, var present: List<Int>): RecyclerView.ViewHolder(itemView){
        var txtQuoteCard: TextView
        var txtAuthorCard: TextView
        var btnShare: ImageButton
        var btnSpeak: ImageButton
        var btnFav: CheckBox
        var presentList = present.toMutableList()

        init {
            txtQuoteCard = itemView.findViewById(R.id.txtQuoteCard)
            txtAuthorCard = itemView.findViewById(R.id.txtAuthorCard)
            btnShare = itemView.findViewById(R.id.btnShare)
            btnSpeak = itemView.findViewById(R.id.btnSpeak)
            btnFav = itemView.findViewById(R.id.btnFav)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.row_items, parent, false)

        return ViewHolder(itemView, presentList)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.btnFav.setOnClickListener(null)
        holder.txtQuoteCard.text = list.results[position].content.toString()
        holder.txtAuthorCard.text = list.results[position].author.toString()
        if (presentList[position] == 0){
            holder.btnFav.isChecked = false
            }
        else
            holder.btnFav.isChecked = true
        var viewModel: ViewModel = ViewModelProviders.of(context as FragmentActivity).get(ViewModel::class.java)
        val quote = DataEntity(list.results[position]._id, list.results[position].content, list.results[position].author)
        /*
        */
        holder.btnShare.setOnClickListener{
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plane"
            share.putExtra(Intent.EXTRA_TEXT,"Quote - "+list.results[position].content+"\nAuthor - "+list.results[position].author)
            context.startActivity(share)
        }
        holder.btnSpeak.setOnClickListener{
            lateinit var tts: TextToSpeech
            tts = TextToSpeech(context, TextToSpeech.OnInitListener {
                if (it==TextToSpeech.SUCCESS) {
                    tts.language = Locale.UK
                    tts.setSpeechRate(1.0F)
                    tts.speak(list.results[position].content + " Quote by " +list.results[position].author, TextToSpeech.QUEUE_ADD, null)
                }
            })
        }
        holder.btnFav.setOnClickListener {

            if (holder.btnFav.isChecked){
                viewModel.insertQuote(quote)
                Toast.makeText(context, "Added to Favourites!", Toast.LENGTH_SHORT).show()
                holder.btnFav.isChecked = true
                presentList[position] = 1
            }
            else {
                viewModel.deleteQuote(quote)
                Toast.makeText(context, "Removed from Favourites!", Toast.LENGTH_SHORT).show()
                holder.btnFav.isChecked = false
                val fragment: Fragment = FragmentManager.findFragment(holder.itemView)
                if (fragment is Favourites) {
                    list.results.removeAt(position)
                    presentList.removeAt(position)
                    notifyDataSetChanged()
                    /*
                    val activity  = it.context as? AppCompatActivity
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container_fragment, Favourites())?.addToBackStack(null)
                        ?.commit()*/
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return list.results.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}


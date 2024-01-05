package org.hyperskill.phrases

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView

import org.hyperskill.phrases.databinding.RecyclerItemBinding

class RecyclerAdapter(phrases: List<Phrase>, private val appDatabase : AppDatabase,
                      private val reminderField : TextView
) :
    RecyclerView.Adapter<RecyclerAdapter.PhrasesViewHolder>(){

    var data: MutableList<Phrase> = phrases.toMutableList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    //private val mutablePhrases = data.toMutableList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhrasesViewHolder {


        val holder = PhrasesViewHolder(
            RecyclerItemBinding.inflate(
            LayoutInflater.from(parent.context)
                ,parent, false))

        holder.binding.deleteTextView.setOnClickListener{
            val pos : Int = holder.adapterPosition
            appDatabase.getPhraseDao().delete(data[pos])
            data.removeAt(pos)
            notifyItemRemoved(pos)

            if(appDatabase.getPhraseDao().getAll().isEmpty()){
                reminderField.text = "No reminder set"
            }

        }

        return holder
    }

    fun add(newPhrase : Phrase){
        data.add(newPhrase)
        notifyItemInserted(data.size-1)
        appDatabase.getPhraseDao().insert(newPhrase)
        data = appDatabase.getPhraseDao().getAll().toMutableList()
    }

    override fun onBindViewHolder(holder: PhrasesViewHolder, position: Int) {
        val item = data[position]
        holder.binding.phraseTextView.text = item.phrase
    }

    override fun getItemCount(): Int {
        return data.size
    }
    class PhrasesViewHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)

}
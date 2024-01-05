package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import org.hyperskill.phrases.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var notificationManager : NotificationManager

    //private lateinit var notificationManager: NotificationManager
    @RequiresApi(Build.VERSION_CODES.M or Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        appDatabase = (application as ApplicationPhrase).database
        //val listPhrases = listOf("A test phrase", "Another test phrase", "Another test phrase")
        val intent = Intent(applicationContext, MyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val am: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        createNotificationChannel()

        val recyclerAdapter = RecyclerAdapter(
            appDatabase
            .getPhraseDao()
            .getAll(),
            appDatabase, binding.reminderTextView)


        binding.recyclerView.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerAdapter
        }

        binding.addButton.setOnClickListener {
            val contentView = LayoutInflater.from(this).inflate(R.layout.item_alert_dialog,
                null, false)
            AlertDialog.Builder(this)
                .setTitle("Add phrase")
                .setView(contentView)
                .setPositiveButton("ADD") { _, _ ->
                    val editText = contentView.findViewById<EditText>(R.id.editText)

                    if(editText.text.isEmpty()){
                        Toast.makeText(this,"Input is empty",Toast.LENGTH_SHORT).show()
                    }else{
                        recyclerAdapter.add(Phrase(editText.text.toString()))

                    }

                }
                .setNegativeButton(android.R.string.cancel,null)
                .show()

        }

        binding.reminderTextView.setOnClickListener {

            if(appDatabase.getPhraseDao().getAll().isEmpty()){
                Toast.makeText(this,"No reminders sets",Toast.LENGTH_SHORT).show()
                binding.reminderTextView.text = "No reminder set"
            }else{
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)

                    if(cal.timeInMillis<System.currentTimeMillis()){
                        cal.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH)+1)
                        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis,
                            AlarmManager.INTERVAL_DAY,pendingIntent)
                    }else{
                        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis,
                            AlarmManager.INTERVAL_DAY,pendingIntent)
                    }
                    binding.reminderTextView.text =
                        "Reminder set for ${SimpleDateFormat("HH:mm").format(cal.time)}"


                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), true).show()

            }
        }

    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "phraseoftheday"
            val descriptionText = "Your phrase of the day"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(MyReceiver.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
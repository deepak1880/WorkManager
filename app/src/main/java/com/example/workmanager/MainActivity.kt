 package com.example.workmanager

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.inputmethod.InputBinding
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanager.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

 class MainActivity : AppCompatActivity() {

     private lateinit var binding: ActivityMainBinding
     private  var database: UserDatabase?=null
     private  var userDataDao: UserDataDao?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueue(uploadWorkRequest)

        database = UserDatabase.getDatabase(this)
        userDataDao = database?.userDataDao()

        binding.buttonSave.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val age = binding.editTextAge.text.toString().toIntOrNull() ?: 0

            lifecycleScope.launch {
                userDataDao?.insert(UserData(name = name, age = age))
                Toast.makeText(this@MainActivity, "Data saved locally", Toast.LENGTH_SHORT).show()
                checkAndUploadData()
            }
        }
    }

     private fun checkAndUploadData() {
         val networkInfo = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
         if (networkInfo != null && networkInfo.isConnected) {
             val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
                 .setConstraints(
                     Constraints.Builder()
                         .setRequiredNetworkType(NetworkType.CONNECTED)
                         .build()
                 )
                 .build()

             WorkManager.getInstance(this).enqueue(uploadWorkRequest)
         }
     }
 }
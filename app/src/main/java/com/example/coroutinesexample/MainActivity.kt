package com.example.coroutinesexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result 1"
    private val RESULT_2 = "Result 2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart.setOnClickListener{

            // IO =>  For Network request, database actions
            // Main => Doing this on main thread so interacting with UI
            // Default => Heavy Computational things : Filter a large list
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }

        btnCoroutineJob.setOnClickListener {
            startActivity(Intent(this, CoroutineJobsActivitity::class.java));
        }
    }

    private fun setNewText(input : String){
        val newText = textView.text.toString()+ "\n$input"
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(input : String){
        withContext(Main){
            setNewText(input)
        }
    }

    private suspend fun fakeApiRequest(){
        val result1 = getResult1FromApi()
        setTextOnMainThread(result1)

        val result2 = getResult2FromApi(result1)
        setTextOnMainThread(result2)
    }

    private suspend fun getResult1FromApi() : String{
        logThread("getResult1FromApi")
        delay(1000)
        return RESULT_1
    }

    private suspend fun getResult2FromApi(result1 : String) : String{
        logThread("getResult2FromApi")
        delay(1000)
        return if(result1 == "Result 1")
            RESULT_2
        else
            ""
    }

    private fun logThread(methodName : String){
        println("debug : $methodName : ${Thread.currentThread().name}")
    }
}
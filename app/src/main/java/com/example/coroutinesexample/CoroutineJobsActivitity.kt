package com.example.coroutinesexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_coroutine_jobs_activitity.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class CoroutineJobsActivitity : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_MIN = 0
    private val JOB_DURATION = 4000 //ms
    private lateinit var job : CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_jobs_activitity)

        supportActionBar?.title = "Coroutine Jobs"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnStartStop.setOnClickListener {
            if(!::job.isInitialized)
                initJob()
            progressBar.startJobOrCancel(job)
        }
    }

    fun ProgressBar.startJobOrCancel(job : Job){
        if(this.progress > 0){
            println("$job is already active. Cancelling...")
            resetJob()
        }
        else{
            btnStartStop.text = "Cancel Job #1"
            CoroutineScope(IO +  job).launch {
                println("coroutine $this is activated with job $job")

                for (i in PROGRESS_MIN..PROGRESS_MAX){
                    delay((JOB_DURATION / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateTextViewText("Job is complete")
            }
        }
    }

    private fun resetJob() {
        if(job.isActive || job.isCompleted){
            job.cancel(CancellationException("Resetting Job"))
        }
        initJob()
    }

    private fun updateTextViewText(text : String){
       GlobalScope.launch(Main) {
            tvJobComplete.text = text
        }
    }

    private fun initJob(){
        updateTextViewText("")
        btnStartStop.text = "Start Job #1"
        tvJobComplete.text = ""
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if(msg.isNullOrBlank()){
                    msg = "Unknown cancellation error"
                }
                println("$job was canceled. Reason : $msg")
                showToast(msg)
            }
        }
        progressBar.max = PROGRESS_MAX
        progressBar.progress = PROGRESS_MIN
    }

    private fun showToast(text : String){
        GlobalScope.launch(Main) {
            Toast.makeText(this@CoroutineJobsActivitity,text,Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
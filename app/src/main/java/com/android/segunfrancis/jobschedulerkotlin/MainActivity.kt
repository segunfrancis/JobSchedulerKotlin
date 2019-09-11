package com.android.segunfrancis.jobschedulerkotlin

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

private const val JOB_ID: Int = 0

class MainActivity : AppCompatActivity() {

    private lateinit var mScheduler: JobScheduler
    private lateinit var mDeviceIdleSwitch: Switch
    private lateinit var mDeviceChargingSwitch: Switch
    private lateinit var mSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDeviceIdleSwitch = findViewById(R.id.idle_switch)
        mDeviceChargingSwitch = findViewById(R.id.charging_switch)
        mSeekBar = findViewById(R.id.seekBar)
        val seekBarProgress = findViewById<TextView>(R.id.seekBar_progress)

        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (i > 0) {
                    seekBarProgress.text = "$i s"
                } else {
                    seekBarProgress.text = "Not Set"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    fun scheduleJob(view: View) {
        val networkOptions = findViewById<RadioGroup>(R.id.network_options)
        val selectedNetworkID = networkOptions.checkedRadioButtonId
        var selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE

        var seekBarInteger = mSeekBar.progress
        val seekBarSet = seekBarInteger > 0

        when (selectedNetworkID) {
            R.id.no_network -> selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
            R.id.any_network -> selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY
            R.id.wifi_network -> selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED
        }
        val serviceName = ComponentName(packageName, NotificationJobService::class.java.name)
        val builder = JobInfo.Builder(JOB_ID, serviceName)
            .setRequiredNetworkType(selectedNetworkOption)
            .setRequiresDeviceIdle(mDeviceIdleSwitch.isChecked)
            .setRequiresCharging(mDeviceChargingSwitch.isChecked)
        if (seekBarSet) {
            builder.setOverrideDeadline((seekBarInteger * 1000).toLong())
        }
        val constraintSet = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE
                || mDeviceChargingSwitch.isChecked || mDeviceIdleSwitch.isChecked
                || seekBarSet

        mScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        if (constraintSet) {
            // Schedule the job and notify the user
            val myJobInfo = builder.build()
            mScheduler.schedule(myJobInfo)
            Toast.makeText(this, "Job Scheduled, job will run when " +
                    "the constraints are met.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please set at least one constraint",
                Toast.LENGTH_SHORT).show()
        }
    }

    fun cancelJob(view: View) {
        if (mScheduler != null) {
            mScheduler.cancelAll()
//            mScheduler = null
            Toast.makeText(this, "Jobs Canceled", Toast.LENGTH_SHORT).show()
        }
    }
}

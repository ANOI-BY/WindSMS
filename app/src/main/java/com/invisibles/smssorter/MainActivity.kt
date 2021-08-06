package com.invisibles.smssorter

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.invisibles.smssorter.Attributes.LogName

private const val REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var smsManager: SmsManager
    private var permissions = arrayOf(
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.READ_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setupPermissions()
    }

    private fun setupPermissions() {
        permissions.forEach { permission ->
            val permis = ContextCompat.checkSelfPermission(this, permission)
            if (permis != PackageManager.PERMISSION_GRANTED) makePermissionRequest()
        }
    }

    private fun makePermissionRequest() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
    }

    private fun init() {

        smsManager = SmsManager.getDefault()

        setFragmentView(MainPage(this))
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


    private fun setFragmentView(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_frame, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}
package com.invisibles.smssorter

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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

        val intent = Intent(this, MainChatsScreen::class.java)
        startActivity(intent)
        finish()

    }


}
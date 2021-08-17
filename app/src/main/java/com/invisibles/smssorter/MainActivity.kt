package com.invisibles.smssorter

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.invisibles.smssorter.Attributes.LogName

private const val REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private var permissions = arrayOf(
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.READ_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()

        if (checkPermissions()) init()

    }

    private fun checkPermissions(): Boolean {

        permissions.forEach { permission ->
            val permis = ContextCompat.checkSelfPermission(this, permission)
            if (permis != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true

    }

    private fun setupPermissions() {
        if (!checkPermissions()) makePermissionRequest()
    }

    private fun makePermissionRequest() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        if (checkPermissions()) init()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       //TODO
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun init() {

        val intent = Intent(this, MainChatsScreen::class.java)
        startActivity(intent)
        finish()

    }


}
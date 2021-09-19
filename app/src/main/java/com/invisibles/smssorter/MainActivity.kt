package com.invisibles.smssorter

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.invisibles.smssorter.Attributes.LogName
import com.invisibles.smssorter.Receiver.SmsReceiver

private const val REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var acceptButton: Button
    private lateinit var textPrivacy: TextView
    private lateinit var checkBox: CheckBox

    private var permissions = arrayOf(
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.READ_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkPermissions()) startNextActivity()

        init()

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
        if (checkPermissions()) startNextActivity()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (checkPermissions()) startNextActivity()
    }

    private fun startNextActivity(){
        val intent = Intent(this, MainChatsScreen::class.java)
        startActivity(intent)
        finish()
    }

    private fun setDefault(){

        val setSmsAppIntent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        setSmsAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
        startActivity(setSmsAppIntent)
    }

    private fun isDefaultApp(): Boolean {
        return packageName.equals(Telephony.Sms.getDefaultSmsPackage(this))
    }

    private fun init() {

        acceptButton = findViewById(R.id.accept_button)
        textPrivacy = findViewById(R.id.welcome_privacy)
        checkBox = findViewById(R.id.welcome_checkbox)
        registerReceiver(SmsReceiver(), IntentFilter("android.intent.action.TIME_TICK"))
        //Linkify.addLinks(textPrivacy, Linkify.WEB_URLS)

        textPrivacy.movementMethod = LinkMovementMethod.getInstance()
        textPrivacy.highlightColor = getColor(R.color.transparent)

        acceptButton.setOnClickListener {

            if (checkBox.isChecked) {

                //setDefault()

                if (checkPermissions()) startNextActivity()

                setupPermissions()
            }
            else{

                val anim = AnimationUtils.loadAnimation(this, R.anim.shake)
                checkBox.startAnimation(anim)

            }

        }

    }


}
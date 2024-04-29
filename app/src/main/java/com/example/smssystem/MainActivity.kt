package com.example.smssystem

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var phoneEdt: EditText
    private lateinit var messageEdt: EditText
    private lateinit var sendMsgBtn: Button
    private val REQUEST_CODE_SMS_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phoneEdt = findViewById(R.id.idEdtPhone)
        messageEdt = findViewById(R.id.idEdtMessage)
        sendMsgBtn = findViewById(R.id.idBtnSendMessage)

        sendMsgBtn.setOnClickListener {
            val phoneNumber = phoneEdt.text.toString()
            val message = messageEdt.text.toString()

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), REQUEST_CODE_SMS_PERMISSION)
            } else {
                sendSms(phoneNumber, message)
            }
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()

            // Sending SMS with a basic setup
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this, "Message Sent", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error sending message: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_SMS_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val phoneNumber = phoneEdt.text.toString()
                val message = messageEdt.text.toString()
                sendSms(phoneNumber, message)
            } else {
                Toast.makeText(this, "Permission denied for sending SMS.", Toast.LENGTH_LONG).show()
            }
        }
    }
}


package com.example.smssystem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.ContentResolver
import android.net.Uri
import android.provider.Telephony
class MainActivity : AppCompatActivity() {
    // Define constants for SMS and READ_SMS permission requests
    private val REQUEST_CODE_SMS_PERMISSION = 1
    private val REQUEST_CODE_READ_SMS = 2

    // Initialization logic for the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing UI elements and setting up event handlers
        val phoneEdt = findViewById<EditText>(R.id.idEdtPhone)
        val messageEdt = findViewById<EditText>(R.id.idEdtMessage)
        val sendMsgBtn = findViewById<Button>(R.id.idBtnSendMessage)

        sendMsgBtn.setOnClickListener {
            val phoneNumber = phoneEdt.text.toString()
            val message = messageEdt.text.toString()

            // Request SMS permission if not already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), REQUEST_CODE_SMS_PERMISSION)
            } else {
                sendSms(phoneNumber, message) // Permission already granted
            }
        }
    }

    // Merged onRequestPermissionsResult method
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_SMS_PERMISSION -> {
                // Handle the SEND_SMS permission request result
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val phoneEdt = findViewById<EditText>(R.id.idEdtPhone)
                    val messageEdt = findViewById<EditText>(R.id.idEdtMessage)
                    sendSms(phoneEdt.text.toString(), messageEdt.text.toString())
                } else {
                    Toast.makeText(this, "Permission denied for sending SMS.", Toast.LENGTH_LONG).show()
                }
            }

            REQUEST_CODE_READ_SMS -> {
                // Handle the READ_SMS permission request result
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readSmsMessages()
                } else {
                    Toast.makeText(this, "Permission denied to read SMS.", Toast.LENGTH_LONG).show()
                }
            }

            else -> {
            }
        }
    }

    // SMS sending method
    private fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this, "Message Sent", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error sending message: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkReadSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), REQUEST_CODE_READ_SMS)
        } else {
            readSmsMessages()  // Permission granted
        }
    }

    // Method to read SMS messages
    private fun readSmsMessages() {
        val contentResolver: ContentResolver = contentResolver
        val uri: Uri = Telephony.Sms.CONTENT_URI
        val projection = arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE)
        val sortOrder = "${Telephony.Sms.DATE} DESC"
        val cursor = contentResolver.query(uri, projection, null, null, sortOrder)

        cursor?.use {
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)

            var count = 0
            while (it.moveToNext() && count < 10) {
                val address = it.getString(addressIndex)
                val body = it.getString(bodyIndex)
                val date = it.getLong(dateIndex)
                println("SMS from: $address, Message: $body, Date: $date")
                count++
            }
        }
    }
}



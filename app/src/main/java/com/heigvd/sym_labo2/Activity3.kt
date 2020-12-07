package com.heigvd.sym_labo2

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.util.*

class Activity3 : AppCompatActivity() {
    /* NFC login */
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var connectButton: Button

    // Private NFC-Secured Data
    private lateinit var securityContainer: LinearLayout
    private lateinit var highSecurityButton: Button
    private lateinit var medSecurityButton: Button
    private lateinit var lowSecurityButton: Button
    lateinit var countDownTimer: CountDownTimer

    // Account Data
    private var myUsername = "User"
    private var verySecretPassword = "s3cret"

    // NFC First Login
    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        connectButton = findViewById(R.id.connect_button)
        securityContainer = findViewById(R.id.buttonsContainer)
        highSecurityButton = findViewById(R.id.high_security)
        medSecurityButton = findViewById(R.id.medium_security)
        lowSecurityButton = findViewById(R.id.low_security)
        countDownTimer = object : CountDownTimer(AUTHENTICATE_LOW * 1000L, 1000) {
            override fun onFinish() {
                logout()
            }

            override fun onTick(p0: Long) {
                val seconds = p0 / 1000
                // Update UI every seconds to inform the user of the TTL of each "data set"
                checkDatasetButton(highSecurityButton, AUTHENTICATE_MAX, seconds)
                checkDatasetButton(medSecurityButton, AUTHENTICATE_MEDIUM, seconds)
                checkDatasetButton(lowSecurityButton, AUTHENTICATE_LOW, seconds)
            }
        }

        // Connect button will be enabled only once the NFC tag is showed near for connection
        connectButton.isEnabled = false

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disabled", Toast.LENGTH_LONG).show();
        }

        connectButton.setOnClickListener {
            // Credentials test (ugly but not the focus for this lab)
            if(usernameInput.text.isEmpty() || usernameInput.text.trim().toString() != myUsername) {
                usernameInput.error = "Invalid username"
            } else if(passwordInput.text.isEmpty() || passwordInput.text.trim().toString() != verySecretPassword) {
                passwordInput.error = "Invalid password"
            } else {
                // User connected : show private content
                securityContainer.visibility = View.VISIBLE
                countDownTimer.start()
            }
        }
    }

    private fun checkDatasetButton(button: Button, time: Int, currentSeconds: Long) {
        if(currentSeconds % time < 1L) {
            button.isEnabled = false
        } else {
            if(button.isEnabled) {
                button.setText("(" + currentSeconds % time + ")")
            } else {
                button.setText("[LOCKED]")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, nfcAdapter);
    }

    override fun onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, nfcAdapter);

        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if(intent != null)
            handleIntent(intent)
    }


    /**
     * @param activity The corresponding [Activity] requesting the foreground dispatch.
     * @param adapter The [NfcAdapter] used for the foreground dispatch.
     */
    fun setupForegroundDispatch(activity: Activity, adapter: NfcAdapter) {
        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, 0)
        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()
        // Notice that this is the same filter as in our manifest.

        // Notice that this is the same filter as in our manifest.
        filters[0] = IntentFilter()
        filters[0]!!.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
        filters[0]!!.addCategory(Intent.CATEGORY_DEFAULT)
        try {
            filters[0]!!.addDataType(Activity3.MIME_TEXT_PLAIN)
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            throw RuntimeException("Check your mime type.")
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList)
    }

    /**
     * @param activity The corresponding [BaseActivity] requesting to stop the foreground dispatch.
     * @param adapter The [NfcAdapter] used for the foreground dispatch.
     */
    fun stopForegroundDispatch(activity: Activity?, adapter: NfcAdapter) {
        adapter.disableForegroundDispatch(activity)
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val type = intent.type
            Log.d(Activity3.TAG, "handleIntent: intent type : " + type)
            if (Activity3.MIME_TEXT_PLAIN.equals(type)) {
                val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                Log.d(Activity3.TAG, "handleIntent: tag : " + tag)
                if(tag != null) {
                    // Launch kotlin coroutine
                    runOnUiThread {
                        if(connectButton.isEnabled) {
                            countDownTimer.start()
                        } else {
                            connectButton.isEnabled = true
                            connectButton.setText("Connect to: " + handleNfcTag(tag))
                            usernameInput.setText(myUsername)
                            passwordInput.setText(verySecretPassword)
                        }
                    }
                } else {
                    Log.d(Activity3.TAG, "handleIntent: Couldn't retrieve NFC tag")
                }
            } else {
                Log.d(Activity3.TAG, "Wrong mime type: $type")
            }
        }
    }

    private fun handleNfcTag(tag: Tag) : String {
        val ndef = Ndef.get(tag)

        val ndefMessage = ndef!!.cachedNdefMessage

        val records = ndefMessage.records
        for (ndefRecord in records) {
            if (ndefRecord.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(
                    ndefRecord.type,
                    NdefRecord.RTD_TEXT
                )
            ) {
                try {
                    return readText(ndefRecord)
                } catch (e: UnsupportedEncodingException) {
                    Log.e(Activity3.TAG, "Unsupported Encoding", e)
                }
            }
        }

        return "Error"
    }

    @Throws(UnsupportedEncodingException::class)
    private fun readText(record: NdefRecord): String {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */
        val payload = record.payload

        // Get the Text Encoding
        val textEncoding = if ((payload[0].toInt() and 128) == 0) "UTF-8" else "UTF-16"

        // Get the Language Code
        val languageCodeLength: Int = payload[0].toInt() and 51

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            charset(textEncoding)
        )
    }

    private fun logout() {
        connectButton.isEnabled = false
        usernameInput.setText("")
        passwordInput.setText("")
        securityContainer.visibility = View.GONE
        countDownTimer.start()
    }

    companion object {
        private const val TAG: String = "Activity3"
        private const val MIME_TEXT_PLAIN = "text/plain"
        private const val AUTHENTICATE_MAX = 10
        private const val AUTHENTICATE_MEDIUM = 15
        private const val AUTHENTICATE_LOW = 20
    }
}
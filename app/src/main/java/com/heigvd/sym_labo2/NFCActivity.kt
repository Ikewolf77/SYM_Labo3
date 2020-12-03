package com.heigvd.sym_labo2

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentFilter.MalformedMimeTypeException
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.util.*


class NFCActivity : AppCompatActivity() {
    /* NFC */

    private lateinit var highSecurityButton: Button
    private lateinit var mediumSecurityButton: Button
    private lateinit var lowSecurityButton: Button
    private lateinit var resultMsg: TextView

    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        highSecurityButton = findViewById(R.id.high_security)
        mediumSecurityButton = findViewById(R.id.medium_security)
        lowSecurityButton = findViewById(R.id.low_security)
        resultMsg = findViewById(R.id.text_result)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        handleIntent(getIntent());

        highSecurityButton.setOnClickListener {
            // todo
        }
        mediumSecurityButton.setOnClickListener {
            // todo
        }
        lowSecurityButton.setOnClickListener {
            // todo
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

        Log.d(TAG, "onNewIntent: new intent detected")
        
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
            filters[0]!!.addDataType(MIME_TEXT_PLAIN)
        } catch (e: MalformedMimeTypeException) {
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
            if (MIME_TEXT_PLAIN.equals(type)) {
                val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                if(tag != null) {
                    // Launch kotlin coroutine
                    GlobalScope.launch { // launch a new coroutine in background and continue
                        resultMsg.text = "NFC MSG: " + handleNfcTag(tag)
                    }
                } else {
                    Log.d(TAG, "handleIntent: Couldn't retrieve NFC tag")
                }
            } else {
                Log.d(TAG, "Wrong mime type: $type")
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
                    Log.e(TAG, "Unsupported Encoding", e)
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

    companion object {
        private const val TAG: String = "NFCActivity"
        private const val MIME_TEXT_PLAIN = "text/plain"
    }
}
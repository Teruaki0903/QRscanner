package cm.test.qrcode

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.wifi.WifiManager
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.net.InetAddress

import android.content.Intent
import android.widget.Button
import android.widget.TextView
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {
    private lateinit var ipAddressTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ip = getIPAddress() // IPアドレスの取得

        generateQRCode(ip)

        val scanButton = findViewById<Button>(R.id.scanButton)
        ipAddressTextView = findViewById<TextView>(R.id.ipAddressTextView)

        scanButton.setOnClickListener {
            startQRScanner()
        }
    }
    private fun getIPAddress(): String {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress
        return InetAddress.getByAddress(
            byteArrayOf(
                (ipAddress and 0xFF).toByte(),
                (ipAddress shr 8 and 0xFF).toByte(),
                (ipAddress shr 16 and 0xFF).toByte(),
                (ipAddress shr 24 and 0xFF).toByte()
            )
        ).hostAddress
    }

    private fun generateQRCode(ip: String) {
        val barcodeEncoder = BarcodeEncoder()
        try {
            val bitmap = barcodeEncoder.encodeBitmap(
                ip,
                BarcodeFormat.QR_CODE,
                400,
                400
            )
            val imageViewQrCode = findViewById<ImageView>(R.id.qrCodeImageView)
            imageViewQrCode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun startQRScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val ipAddress = result.contents
                ipAddressTextView.text = "IP Address: $ipAddress"
            }
        }
    }
}
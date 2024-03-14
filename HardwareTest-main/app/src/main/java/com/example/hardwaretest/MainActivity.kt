package com.example.hardwaretest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity

class MainActivity : AppCompatActivity() {

    private lateinit var lm: LocationManager;
    private lateinit var ll: LocationListener;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnCamera : Button = findViewById(R.id.btnQr)
        val btnLocation : Button = findViewById(R.id.btnLocation)
        val txtLocation : TextView = findViewById(R.id.txtLocation)

        //Initializes the location manager
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ll = LocationListener { p0 -> txtLocation.text = "Latitude: ${String.format("%.4f", p0.latitude)}\nLongitude: ${String.format("%.2f", p0.longitude)}" }


        btnLocation.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, ll)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
        btnCamera.setOnClickListener{scanQR()} //llama  a la funcion que abre el scanner de qr
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, ll)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lm.removeUpdates(ll)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data) //guarda el valor obtenido del qr
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "El QR es: " + result.contents, Toast.LENGTH_LONG).show()
                //muestra la url a la que apunta el qr escaneado
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun scanQR() {
        //inicializa el scanner de qr
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

}
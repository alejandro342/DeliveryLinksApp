package com.alejandro.deliverylinks.modules.client.ui.views.address.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientAddressMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

//clase para la integración del mapa de google
/* obtener los datos de la localizacion del mapa */
class ClientAddressMapActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private var mBinding: ActivityClientAddressMapBinding? = null

    val TAG = "AddressMapClient"

    //para la integración del mapa de google
    var mGoogleMap: GoogleMap? = null

    /* obtener la ubicación actual */
    private val PERMISSION_ID = 42
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /* obtener la ubicación actual */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            Log.d("LOCALIZACION", "Callback: $lastLocation")
        }
    }

    /* obtener los datos de la localizacion del mapa */
    var mCity = ""
    var mCountry = ""
    var mAddress = ""
    var mAddressLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientAddressMapBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        //para la integración de la vista del mapa de google
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        /* obtener la ubicación actual */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        mBinding!!.btnAcceptAddress.setOnClickListener(this)
    }

    /* obtener los datos de la localizacion del mapa */
    @SuppressLint("SetTextI18n")
    private fun onCameraMove() {
        //para cuando la camra se mueva
        mGoogleMap?.setOnCameraIdleListener {
            try {
                val mGeocoder = Geocoder(this)
                //obtenemos datos del marcador
                mAddressLatLng = mGoogleMap?.cameraPosition?.target
                val mAddressList = mGeocoder.getFromLocation(
                    mAddressLatLng?.latitude!!,
                    mAddressLatLng?.longitude!!,
                    1
                )
                //obtener la ciudad
                mCity = mAddressList[0].locality
                mCountry = mAddressList[0].countryName
                mAddress = mAddressList[0].getAddressLine(0)

                //mostralo en el texto
                mBinding!!.txtGetAddressClient.text = "$mAddress $mCity"

            } catch (e: Exception) {
                Log.d(TAG, "Error : ${e.message}")
            }
        }
    }

    //para la integración del mapa de google para el mapa cargado
    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map
        //llamamos al metodo para mostrar los datos
        onCameraMove()
    }

    /* obtener la ubicación actual */
    private fun getLastLocation() {
        //verificar los permisos
        if (checkPermission()) {
            //validar localizacion
            if (isLocationEnable()) {
                mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    //obtener localizacion
                    val location = task.result
                    //validamos que no venga null
                    if (location == null) {
                        //si viene null llamamos
                        requestNewLocationData()
                    } else {
                        //ni no viene vacia movemos la camara
                        mGoogleMap?.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder().target(
                                    LatLng(location.latitude, location.longitude)
                                ).zoom(15f).build()
                            )
                        )
                    }
                }
            } else {
                //mandar al usuario ah que habilite el gps
                Toast.makeText(this, "Habilite la localizacion", Toast.LENGTH_LONG).show()
                val mInten = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(mInten)
            }
        } else {
            //si los permisos no estan habilitados
            requetsPermmission()
        }
    }

    /* obtener la ubicación actual obtener nueva localizacion */
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            //actualizar los datos de la localizasion modificar valores para ahorro de energia
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        //escuchador de la localizacion en tiempo real
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        mFusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )//creamos antes del create el objeto locationCallback
    }

    /* obtener la ubicación actual saber si la localizacion esta habilitada */
    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /* obtener la ubicación actual Validar Permisos */
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    /* obtener la ubicación actual obtener permisos */
    private fun requetsPermmission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    /* obtener la ubicación actual */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    override fun onClick(mIntemView: View?) {
        when (mIntemView) {
            mBinding!!.btnAcceptAddress -> goToCreateAddress()
        }
    }

    //para mandar los datos a la vista crear direecion obtener los datos de la localizacion del mapa
    private fun goToCreateAddress() {
        val mIntent = Intent()
        mIntent.putExtra("city", mCity)
        mIntent.putExtra("address", mAddress)
        mIntent.putExtra("country", mCountry)
        mIntent.putExtra("lat", mAddressLatLng?.latitude)
        mIntent.putExtra("lng", mAddressLatLng?.longitude)
        setResult(RESULT_OK,mIntent)
        finish() //volver hacia atras

    }
}
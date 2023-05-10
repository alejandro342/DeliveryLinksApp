package com.alejandro.deliverylinks.modules.client.ui.views.map


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientOrdersMapBinding
import com.alejandro.deliverylinks.models.SocketEmit
import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.providers.OrdersProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.alejandro.deliverylinks.utils.SocketHandler
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.maps.route.extensions.drawRouteOnMap
import com.squareup.picasso.Picasso


class ClientOrdersMapActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    private var mBinding: ActivityClientOrdersMapBinding? = null
    val TAG = "DeliveryOrdersMap"

    //para la integración del mapa de google
    private var mGoogleMap: GoogleMap? = null

    /* obtener la ubicación actual */
    private val PERMISSION_ID = 42
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var mAddressLatLng: LatLng? = null

    //agregar el marcador al mapa
    private var mMarkerDelivery: Marker? = null

    //agregar el marcador a la direccion del cliente al mapa
    private var mMarkerAddress: Marker? = null

    //guardar la ubicación
    private var myLocationLatLng: LatLng? = null

    //actualizar posición en tiempo real y mostrar marcador
    //obtener los datos de la orden
    private var mOrder: Order? = null
    private var mGson = Gson()

    //para realizar llamada
    private val REQUEST_PHONE_CALL = 27

    //actualizar estado de orden
    private var mOrdersProvider: OrdersProvider? = null
    private var mUser: User? = null
    private var mSharedPref: SharedPref? = null

    //trazar ruta del delivery al cliente
    private var deliveryLatLng: LatLng? = null

    //recibir position a socket IO en tiempo real
    var mSocket: Socket? = null


    /* obtener la ubicación actual */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            //obtener localizacion en tiempo real
            myLocationLatLng = LatLng(lastLocation?.latitude!!, lastLocation.longitude)

         //   removeDelivery()
        //    addDeliveryMarker()
            Log.d("LOCALIZACION", "Callback: $lastLocation")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientOrdersMapBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        //actualizar estado de orden
        mSharedPref = SharedPref(this)
        getUserFromSession()

        //obtener los datos de la orden de ClientDetailOrder
        mOrder = mGson.fromJson(intent.getStringExtra("order"), Order::class.java)
        //trazar ruta del delivery al cliente
        if (mOrder?.lat != null && mOrder?.lng != null) {
            deliveryLatLng = LatLng(mOrder?.lat!!, mOrder?.lng!!)
        }

        //obtener la distancia entre el repartidor y el cliente
        mAddressLatLng = LatLng(mOrder?.address?.lat!!, mOrder?.address?.lng!!)
        //para la integración de la vista del mapa de google
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        mOrdersProvider = OrdersProvider(mUser?.sessionToken!!)
        /* obtener la ubicación actual */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        connectSocket()

        mBinding!!.imgNumberPhoneDeliveryInformationClient.setOnClickListener(this)
        viewDataClient()
    }

    //para la integración del mapa de google para el mapa cargado
    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map
        //poner los controles para alejar y acercar mapa
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = true

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
                    if (location != null) {
                        //guardar la ubicación actual en la qeu me encuentro
                        myLocationLatLng = LatLng(location.latitude, location.longitude)
                        //llamamos al metodo una ves ue obtengamos la localización
                        removeDelivery()
                        addDeliveryMarker(deliveryLatLng?.latitude!!,deliveryLatLng?.longitude!!)
                        addAddressMarker()
                        drawRoute()
                        //trazar la ruta del celivery al cliente
                        if (deliveryLatLng != null) {
                            //comentar para que la camara siga al delivery
                            mGoogleMap?.moveCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.builder().target(
                                        LatLng(
                                            deliveryLatLng?.latitude!!,
                                            deliveryLatLng?.longitude!!
                                        )
                                    ).zoom(15f).build()
                                )
                            )
                        }
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

        //realizar llamadas
        if (requestCode == REQUEST_PHONE_CALL) {
            callClient()
        }
    }

    private fun permissionCall() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_PHONE_CALL
            )
        } else {
            callClient()
        }
    }

    //agregar el marcador al mapa modificamos para recibir position a socket IO en tiempo real
    //agregamos dos parametros
    private fun addDeliveryMarker(lat: Double, lng: Double) {
        val location = LatLng(lat, lng)
        //trazar ruta del delivery al cliente
        mMarkerDelivery = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .title("Repartidor")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.deivery))
        )

    }

    //eliminar el marcador del delivery evitar que se dibuje varias veces
    private fun removeDelivery() {
        mMarkerDelivery?.remove()
    }

    //trazar ruta de punto A punto B
    private fun drawRoute() {
        if (deliveryLatLng != null) {
            val addressLocation = LatLng(mOrder?.address?.lat!!, mOrder?.address?.lng!!)

            mGoogleMap?.drawRouteOnMap(
                getString(R.string.google_map_api_key),//pasamos la key
                source = deliveryLatLng!!, //trazar ruta del delivery al cliente A
                destination = addressLocation, //trazar ruta del delivery al cliente B
                context = this, //contexto
                color = Color.BLUE, //Color de ruta
                polygonWidth = 10, //ancho
                boundMarkers = false,
                markers = false
            )
        }
    }

    //agregar la direcion del cliente al mapa
    private fun addAddressMarker() {
        val addressLocation = LatLng(mOrder?.address?.lat!!, mOrder?.address?.lng!!)
        mMarkerAddress = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(addressLocation)
                .title("Entregar aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.homeclient))

        )
    }

    //mostrar la informacion del cliente
    @SuppressLint("SetTextI18n")
    private fun viewDataClient() {
        if (!mOrder?.delivery?.image.isNullOrBlank()) {
            Picasso.get().load(mOrder?.delivery?.image)
                .into(mBinding!!.imgDeliveryInformationClient)
        }

        mBinding!!.txtColonyInformationClient.text = mOrder?.address?.neighborhood
        mBinding!!.txtDirectionDeliveryInformationClient.text = mOrder?.address?.address
        mBinding!!.txtNameDeliveryInformationClient.text =
            "${mOrder?.delivery?.name} ${mOrder?.delivery?.lastname}"
        mBinding!!.txtPhoneClientInformationDelivery.text = "${mOrder?.delivery?.phone}"
        Log.d(TAG, "${mOrder?.delivery?.phone}")
    }

    //realizar llamada al cliente directamente
    private fun callClient() {
        val mIntentCall = Intent(Intent.ACTION_CALL)
        mIntentCall.data = Uri.parse("tel: ${mOrder?.delivery?.phone}")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        )
            Toast.makeText(this, "Permiso para realizar llamada denegado", Toast.LENGTH_SHORT)
                .show()
        return startActivity(mIntentCall)
    }

    //obterner rol Delivery datos del usuario sesion
    fun getUserFromSession() {
        val gson = Gson()

        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)
        }
    }

    override fun onClick(mIntemView: View?) {
        when (mIntemView) {
            mBinding!!.imgNumberPhoneDeliveryInformationClient -> permissionCall()
        }
    }

    //recibir position a socket IO en tiempo real
    private fun connectSocket() {
        SocketHandler.setSocket() // conectar
        mSocket = SocketHandler.getSocket()
        mSocket?.connect()
        mSocket?.on("position/${mOrder?.id}") { args ->
            if (args[0] != null) {
                runOnUiThread {
                    val data = mGson.fromJson(args[0].toString(), SocketEmit::class.java)
                    removeDelivery()
                    addDeliveryMarker(data.lat, data.lng)
                }
            }
        }
    }

    //detener la localizacion fuera de la vista del mapa
    override fun onDestroy() {
        super.onDestroy()
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(locationCallback)
        }
        mSocket?.disconnect()//desconectar el socket
    }
}
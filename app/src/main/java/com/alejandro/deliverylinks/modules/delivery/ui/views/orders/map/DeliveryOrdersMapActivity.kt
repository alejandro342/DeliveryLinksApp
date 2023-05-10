package com.alejandro.deliverylinks.modules.delivery.ui.views.orders.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityDeliveryOrdersMapBinding
import com.alejandro.deliverylinks.models.SocketEmit
import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.delivery.ui.views.DeliveryHomeActivityView
import com.alejandro.deliverylinks.providers.OrdersProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.alejandro.deliverylinks.utils.SocketHandler
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.maps.route.extensions.drawRouteOnMap
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//clase para la integración del mapa de google
/* obtener los datos de la localizacion del mapa */
//actualizar posición en tiempo real y mostrar marcador
class DeliveryOrdersMapActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private var mBinding: ActivityDeliveryOrdersMapBinding? = null

    val TAG = "DeliveryOrdersMap"

    //para la integración del mapa de google
    var mGoogleMap: GoogleMap? = null

    /* obtener la ubicación actual */
    private val PERMISSION_ID = 42
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /* obtener los datos de la localizacion del mapa */
    var mCity = ""
    var mCountry = ""
    var mAddress = ""
    var mAddressLatLng: LatLng? = null

    //agregar el marcador al mapa
    var mMarkerDelivery: Marker? = null

    //agregar el marcador a la direccion del cliente al mapa
    var mMarkerAddress: Marker? = null

    //guardar la ubicación
    var myLocationLatLng: LatLng? = null

    //actualizar posición en tiempo real y mostrar marcador
    //obtener los datos de la orden
    var mOrder: Order? = null
    var mGson = Gson()

    //para realizar llamada
    val REQUEST_PHONE_CALL = 27

    //actualizar estado de orden
    var mOrdersProvider: OrdersProvider? = null
    var mUser: User? = null
    var mSharedPref: SharedPref? = null

    //medir la distancia entre el repartido y la casa del cliente
    var mDistanceBetween = 0.0f

    //el manejo de los sockets tiempo real
    var mSocket: Socket? = null

    /* obtener la ubicación actual */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            //obtener localizacion en tiempo real
            myLocationLatLng = LatLng(lastLocation?.latitude!!, lastLocation.longitude)
            //emitir positon a socket IO en tiempo real
            emitPosition()
            /*  mGoogleMap?.moveCamera(
                  CameraUpdateFactory.newCameraPosition(
                      CameraPosition.builder().target(
                          LatLng( myLocationLatLng?.latitude!!,  myLocationLatLng?.longitude!!)
                      ).zoom(15f).build()
                  )
              )*///descomantar si quieres que la camara siga al delivery
//medir la distancia entre el repartido y la casa del cliente
            mDistanceBetween = getDistanceBetween(myLocationLatLng!!, mAddressLatLng!!)
            Log.d(TAG, "Distancia: $mDistanceBetween ")
            removeDelivery()
            addDeliveryMarker()
            Log.d("LOCALIZACION", "Callback: $lastLocation")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDeliveryOrdersMapBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        //obtener los datos de la orden de DeliveryDetailOrder
        mOrder = mGson.fromJson(intent.getStringExtra("order"), Order::class.java)
        //obtener la distancia entre el repartidor y el cliente
        mAddressLatLng = LatLng(mOrder?.address?.lat!!, mOrder?.address?.lng!!)
        //actualizar estado de orden
        mSharedPref = SharedPref(this)
        getUserFromSession()


        //para la integración de la vista del mapa de google
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        mOrdersProvider = OrdersProvider(mUser?.sessionToken!!)
        /* obtener la ubicación actual */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        mBinding!!.btnDeliverOrderClient.setOnClickListener(this)
        mBinding!!.imgNumberPhoneClientInformationDelivery.setOnClickListener(this)
        viewDataClient()

        connectSocket()
    }


    //para la integración del mapa de google para el mapa cargado
    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map
        //poner los controles para alejar y acercar mapa
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = true

    }

    //trazar la ruta del delivery al cliente
    private fun updateLatLng(lat: Double, lng: Double) {
        mOrder?.lat = lat
        mOrder?.lng = lng

        mOrdersProvider?.updateLagLng(mOrder!!)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                if (response.body() != null) {
                    Toast.makeText(
                        this@DeliveryOrdersMapActivity,
                        "${response.body()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(
                    this@DeliveryOrdersMapActivity,
                    "Error ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /* obtener la ubicación actual */
    private fun getLastLocation() {
        //verificar los permisos
        if (checkPermission()) {
            //validar localizacion
            if (isLocationEnable()) {
                requestNewLocationData()//iniciamos la posicion en tiempo real
                mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    //obtener localizacion una sola vez
                    val location = task.result
                    if (location != null) {
                        //guardar la ubicación actual en la que me encuentro
                        myLocationLatLng = LatLng(location.latitude, location.longitude)
                        //trazar la ruta del delivery al cliente
                        updateLatLng(location.latitude, location.longitude)
                        //llamamos al metodo una ves ue obtengamos la localización
                        removeDelivery()
                        addDeliveryMarker()
                        addAddressMarker()
                        drawRoute()
                        //comentar para que la camara siga al delivery
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
        mFusedLocationClient?.requestLocationUpdates(//inicializa la posision en tiempo real
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
                arrayOf(android.Manifest.permission.CALL_PHONE),
                REQUEST_PHONE_CALL
            )
        } else {
            callClient()
        }
    }

    //agregar el marcador al mapa
    private fun addDeliveryMarker() {
        mMarkerDelivery = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(myLocationLatLng!!)
                .title("Mi posición")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.deivery))

        )
    }

    //eliminar el marcador del delivery evitar que se dibuje varias veces
    private fun removeDelivery() {
        mMarkerDelivery?.remove()
    }

    //trazar ruta de punto A punto B
    private fun drawRoute() {
        val addressLocation = LatLng(mOrder?.address?.lat!!, mOrder?.address?.lng!!)
        mGoogleMap?.drawRouteOnMap(
            getString(R.string.google_map_api_key),//pasamos la key
            source = myLocationLatLng!!, //trazar ruta desde el punto A
            destination = addressLocation, //punto B
            context = this, //contexto
            color = Color.BLUE, //Color de ruta
            polygonWidth = 10, //ancho
            boundMarkers = false,
            markers = false
        )
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
        if (!mOrder?.client?.image.isNullOrBlank()) {
            Picasso.get()
                .load(mOrder?.client?.image)
                .into(mBinding!!.imgClientInformationDelivery)
        }
        mBinding!!.txtColonyClientInformationDelivery.text = mOrder?.address?.neighborhood
        mBinding!!.txtDirectionClientInformationDelivery.text = mOrder?.address?.address
        mBinding!!.txtNameClientInformationDelivery.text =
            "${mOrder?.client?.name} ${mOrder?.client?.lastname}"
        mBinding!!.txtPhoneClientInformationDelivery.text = "${mOrder?.client?.phone}"
        Log.d(TAG, "${mOrder?.client?.phone}")
    }

    //realizar llamada al cliente directamente
    private fun callClient() {
        val mIntentCall = Intent(Intent.ACTION_CALL)
        mIntentCall.data = Uri.parse("tel: ${mOrder?.client?.phone}")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        )
            Toast.makeText(this, "Permiso para realizar llamada denegado", Toast.LENGTH_SHORT)
                .show()
        return startActivity(mIntentCall)
    }

    //para cambiar estado de la orden a entregado solo si el repartidor esta cernca del domicilio
    private fun getDistanceBetween(fromLatLng: LatLng, toLatLng: LatLng): Float {
        var mDistan = 0.0f
        val from = Location("")
        val to = Location("")

        from.latitude = fromLatLng.latitude
        from.longitude = fromLatLng.longitude

        to.latitude = toLatLng.latitude
        to.longitude = toLatLng.longitude

        mDistan = from.distanceTo(to)
        return mDistan
    }

    //para cambiar estado de la orden a entregado
    private fun updateOrder() {
        mOrdersProvider?.updateToDelivery(mOrder!!)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                if (response.body() != null) {
                    Toast.makeText(
                        this@DeliveryOrdersMapActivity,
                        "${response.body()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (response.body()?.isSuccess == true) {

                        goToHome()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(
                    this@DeliveryOrdersMapActivity,
                    "Error ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun goToHome() {
        val mIntent = Intent(this, DeliveryHomeActivityView::class.java)
        //detener la localizacion fuera de la vista del mapa
        mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mIntent)
    }

    //obterner rol Delivery datos del usuario sesion
    fun getUserFromSession() {
        val gson = Gson()

        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)
        }
    }

    private fun validateDistance() {
        if (mDistanceBetween <= 60) {
            updateOrder()
        } else {
            Toast.makeText(this, "Debe acercarse mas a su destino", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(mIntemView: View?) {
        when (mIntemView) {
            mBinding!!.btnDeliverOrderClient -> validateDistance()
            mBinding!!.imgNumberPhoneClientInformationDelivery -> permissionCall()
        }
    }

    //emitir positon a socket IO en tiempo real
    private  fun emitPosition(){
        val data = SocketEmit(
            id_order = mOrder?.id!!,
            lat = myLocationLatLng?.latitude!!,
            lng = myLocationLatLng?.longitude!!
        )
        mSocket?.emit("position",data.toJson())
    }

    //el manejo de los sockets tiempo real
    private fun connectSocket(){
        SocketHandler.setSocket() // conectar
        mSocket=SocketHandler.getSocket()
        mSocket?.connect()
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
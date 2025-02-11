package com.example.torneo_participante

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar


class MapaTorneo : AppCompatActivity() {
    private lateinit var mapa: GoogleMap
    var permisos = false
    val nombrePermisoFine = android.Manifest.permission.ACCESS_FINE_LOCATION
    val nombrePermisoCoarse = android.Manifest.permission.ACCESS_COARSE_LOCATION
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var torneo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mapa_torneo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Obtener datos del Intent
        latitud = intent?.getDoubleExtra("latitud", Double.NaN) ?: Double.NaN
        longitud = intent?.getDoubleExtra("longitud", Double.NaN) ?: Double.NaN

        if (latitud.isNaN() || longitud.isNaN()) {
            mostrarSnackbar("Error: Coordenadas inválidas")
            return
        }

        torneo = intent.getStringExtra("nombre") ?: ""
        solicitarPermisos()
        inicializarLogicaMapa(latitud,longitud,torneo)
    }

    fun tengoPermisos(): Boolean {
        val contexto = applicationContext
        val permisoFine = ContextCompat.checkSelfPermission(contexto, nombrePermisoFine)
        val permisoCoarse = ContextCompat.checkSelfPermission(contexto, nombrePermisoCoarse)
        val tienePermisos = permisoFine == PackageManager.PERMISSION_GRANTED &&
                permisoCoarse == PackageManager.PERMISSION_GRANTED
        permisos = tienePermisos
        return permisos
    }

    fun solicitarPermisos() {
        if (!tengoPermisos()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(nombrePermisoFine, nombrePermisoCoarse), 1
            )
        }
    }

    fun inicializarLogicaMapa(lat: Double, lng: Double, torneo:String) {
        val fragmentoMapa = supportFragmentManager.findFragmentById(
            R.id.map
        ) as SupportMapFragment
        fragmentoMapa.getMapAsync { googleMap ->
            with(googleMap) {
                mapa = googleMap
                establecerConfiguracionMapa()
                moverUbicacion(lat, lng, torneo)
                anadirPolilinea(lat, lng)
                anadirPoligono(lat, lng)
                escucharListeners()
            }
        }
    }

    fun moverUbicacion(lat: Double, lng: Double, torneo:String) {
        val ubicacion = LatLng(lat, lng)
        val titulo = torneo
        val marcador = anadirMarcador(ubicacion, titulo)
        marcador.tag = titulo
        moverCamaraConZoom(ubicacion)
    }

    fun anadirPolilinea(lat: Double, lng: Double) {
        with(mapa) {
            val polilinea = mapa.addPolyline(
                PolylineOptions()
                    .clickable(true)
                    .add(
                        LatLng(lat + 0.001, lng - 0.001),
                        LatLng(lat - 0.001, lng + 0.001),
                        LatLng(lat + 0.002, lng + 0.002)
                    )
            )
            polilinea.tag = "polilinea-uno"
        }
    }

    fun anadirPoligono(lat: Double, lng: Double) {
        with(mapa) {
            val poligono = mapa.addPolygon(
                PolygonOptions()
                    .clickable(true)
                    .add(
                        LatLng(lat + 0.0015, lng - 0.0015),
                        LatLng(lat - 0.0015, lng - 0.0015),
                        LatLng(lat - 0.0015, lng + 0.0015),
                        LatLng(lat + 0.0015, lng + 0.0015)
                    )
            )
            poligono.tag = "poligono-uno"
        }
    }


    fun escucharListeners() {
        mapa.setOnPolygonClickListener {
            mostrarSnackbar("setOnPolygonClickListener ${it.tag}")
        }
        mapa.setOnPolylineClickListener {
            mostrarSnackbar("setOnPolylineClickListener ${it.tag}")
        }
        mapa.setOnMarkerClickListener {
            mostrarSnackbar("setOnMarkerClickListener ${it.tag}")
            return@setOnMarkerClickListener true
        }
        mapa.setOnCameraIdleListener { mostrarSnackbar("setOnCameraIdleListener") }
        mapa.setOnCameraMoveListener { mostrarSnackbar("setOnCameraMoveListener") }
        mapa.setOnCameraMoveStartedListener { mostrarSnackbar("setOnCameraMoveStartedListener") }
    }


    @SuppressLint("MissingPermission")
    fun establecerConfiguracionMapa() {
        with(mapa) {
            if (tengoPermisos()) {
                mapa.isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = true
            }
            uiSettings.isZoomControlsEnabled = true
        }
    }

    fun moverCamaraConZoom(latLang: LatLng, zoom: Float = 17f) {
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, zoom))
    }

    fun anadirMarcador(latLang: LatLng, title: String): Marker {
        return mapa.addMarker(MarkerOptions().position(latLang).title(title))!!
    }

    fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.show()
    }
}
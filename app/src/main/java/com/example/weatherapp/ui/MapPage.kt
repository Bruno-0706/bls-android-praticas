package com.example.weatherapp.ui

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.MainViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapPage(modifier: Modifier = Modifier,
            viewModel: MainViewModel) {
    val recife = remember { MarkerState(LatLng(-8.05, -34.9)) }
    val caruaru = remember { MarkerState( LatLng(-8.27, -35.98)) }
    val joaopessoa = remember { MarkerState( LatLng(-7.12, -34.84)) }
    val camPosState = rememberCameraPositionState ()
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        onMapClick = {
            val lat = "%.2f".format(it.latitude)
            val lng = "%.2f".format(it.longitude)
            viewModel.add("Cidade@$lat:$lng", location = it)
        },
        cameraPositionState = camPosState
    ) {
          viewModel.cities.forEach {
              if (it.location != null) {
                  val markerState = remember { MarkerState(position = it.location) }
                  Marker(state = markerState, title = it.name, snippet = "${it.location}")
              }
          }

          Marker(
              state = recife,
              title = "Recife",
              snippet = "Marcador em Recife",
              icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
          )

          Marker(
              state = caruaru,
              title = "Caruaru",
              snippet = "Marcaador em Caruaru",
              icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
          )

          Marker(
              state = joaopessoa,
              title = "João Pessoa",
              snippet = "Marcador em João Pessoa",
              icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
          )
//        Text(
//            text = "Map",
//            fontWeight = FontWeight.Bold,
//            color = Color.White,
//            modifier = modifier.align(CenterHorizontally),
//            textAlign = TextAlign.Center,
//            fontSize = 20.sp
//        )
    }
}
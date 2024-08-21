package com.ali.examplefirst

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mapbox.common.MapboxOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
//            MapboxMapView()
        }
    }
}

@Composable
fun MapboxMapView() {
    MapboxOptions.accessToken = "sk.eyJ1IjoiYW9icmVnb24xMiIsImEiOiJjbTAzZ3prYWkwNW8xMmpwc2o2NnZtbTBjIn0.j4yrFsG0oeX543Tf3FrdvA"
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = MapViewportState().apply {
            setCameraOptions {
                zoom(2.0)
                center(com.mapbox.geojson.Point.fromLngLat(2.5696927, -72.6344168))
                pitch(0.0)
                bearing(0.0)
            }
        }

    )
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
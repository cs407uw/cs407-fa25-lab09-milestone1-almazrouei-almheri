package com.cs407.lab09

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.lab09.ui.theme.Lab09Theme
import kotlin.math.roundToInt
import android.content.pm.ActivityInfo

class MainActivity : ComponentActivity() {

    private val viewModel: BallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContent {
            Lab09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: BallViewModel) {
    val context = LocalContext.current

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val gravitySensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    DisposableEffect(sensorManager, gravitySensor) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    viewModel.onSensorChanged(it)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (gravitySensor != null) {
            sensorManager.registerListener(
                listener,
                gravitySensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val density = LocalDensity.current
    val ballSizeDp = 48.dp
    val ballSizePx = with(density) { ballSizeDp.toPx() }

    val ballState by viewModel.ballState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8E8E8))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { viewModel.resetBall() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4)
                )
            ) {
                Text("Reset", color = Color.White)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    viewModel.initializeBall(
                        backgroundWidth = size.width.toFloat(),
                        backgroundHeight = size.height.toFloat(),
                        ballSize = ballSizePx
                    )
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.field),
                contentDescription = "Field",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Image(
                painter = painterResource(id = R.drawable.soccer),
                contentDescription = "Ball",
                modifier = Modifier
                    .size(ballSizeDp)
                    .offset {
                        IntOffset(
                            ballState.x.roundToInt(),
                            ballState.y.roundToInt()
                        )
                    }
            )
        }
    }
}
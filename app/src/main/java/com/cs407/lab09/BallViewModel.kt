package com.cs407.lab09

import android.hardware.SensorEvent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BallState(
    val x: Float = 0f,
    val y: Float = 0f
)

class BallViewModel : ViewModel() {

    private val _ballState = MutableStateFlow(BallState())
    val ballState: StateFlow<BallState> = _ballState.asStateFlow()

    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L

    fun initializeBall(backgroundWidth: Float, backgroundHeight: Float, ballSize: Float) {
        if (ball == null) {
            ball = Ball(backgroundWidth, backgroundHeight, ballSize)
            updateBallState()
        }
    }

    fun onSensorChanged(event: SensorEvent) {
        val currentTime = event.timestamp

        if (lastTimestamp == 0L) {
            lastTimestamp = currentTime
            return
        }

        val deltaTime = (currentTime - lastTimestamp) / 1_000_000_000f
        lastTimestamp = currentTime

        val rawX = -event.values[0]
        val rawY = event.values[1]

        val gScreenX = -rawX
        val gScreenY = rawY


        val scale = 40f
        val accelX = gScreenX * scale
        val accelY = gScreenY * scale

        ball?.let {
            val scale = 40f
            val accelX = gScreenX * scale
            val accelY = gScreenY * scale
            it.updatePositionAndVelocity(accelX, accelY, deltaTime)
            it.checkBoundaries()
            updateBallState()
        }
    }

    fun resetBall() {
        ball?.reset()
        lastTimestamp = 0L
        updateBallState()
    }

    private fun updateBallState() {
        ball?.let {
            _ballState.value = BallState(it.posX, it.posY)
        }
    }
}
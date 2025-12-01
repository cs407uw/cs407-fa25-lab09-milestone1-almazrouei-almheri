package com.cs407.lab09

/**
 * Represents a ball that can move. (No Android UI imports!)
 */
class Ball(
    private val backgroundWidth: Float,
    private val backgroundHeight: Float,
    private val ballSize: Float
) {
    var posX = 0f
    var posY = 0f
    var velocityX = 0f
    var velocityY = 0f
    private var accX = 0f
    private var accY = 0f

    private var isFirstUpdate = true

    init {
        reset()
    }

    /**
     * Updates the ball's position and velocity based on the given acceleration and time step.
     */
    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {
        if (isFirstUpdate) {
            isFirstUpdate = false
            accX = xAcc
            accY = yAcc
            return
        }

        // Equation 1: v1 = v0 + 0.5 * (a0 + a1) * deltaTime
        val newVelocityX = velocityX + 0.5f * (accX + xAcc) * dT
        val newVelocityY = velocityY + 0.5f * (accY + yAcc) * dT

        // Equation 2: distance = v0 * dT + (1/6) * dT^2 * (3*a0 + a1)
        val distanceX = velocityX * dT + (1f / 6f) * dT * dT * (3f * accX + xAcc)
        val distanceY = velocityY * dT + (1f / 6f) * dT * dT * (3f * accY + yAcc)

        // Update position
        posX += distanceX
        posY += distanceY

        // Update velocity
        velocityX = newVelocityX
        velocityY = newVelocityY

        // Update acceleration for next iteration
        accX = xAcc
        accY = yAcc
    }

    /**
     * Ensures the ball does not move outside the boundaries.
     */
    fun checkBoundaries() {
        // Left boundary
        if (posX < 0f) {
            posX = 0f
            velocityX = 0f
            accX = 0f
        }

        // Right boundary
        if (posX > backgroundWidth - ballSize) {
            posX = backgroundWidth - ballSize
            velocityX = 0f
            accX = 0f
        }

        // Top boundary
        if (posY < 0f) {
            posY = 0f
            velocityY = 0f
            accY = 0f
        }

        // Bottom boundary
        if (posY > backgroundHeight - ballSize) {
            posY = backgroundHeight - ballSize
            velocityY = 0f
            accY = 0f
        }
    }

    /**
     * Resets the ball to the center of the screen.
     */
    fun reset() {
        posX = (backgroundWidth - ballSize) / 2f
        posY = (backgroundHeight - ballSize) / 2f
        velocityX = 0f
        velocityY = 0f
        accX = 0f
        accY = 0f
        isFirstUpdate = true
    }
}

package dev.fluttercommunity.plus.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink

internal class StreamHandlerImpl(
        private val sensorManager: SensorManager,
        sensorType: Int
) : EventChannel.StreamHandler {
    private var sensorEventListener: SensorEventListener? = null
    private var samplingPeriodUs: Int = SensorManager.SENSOR_DELAY_NORMAL
    private var isListening: Boolean = false
    private var currentEventSink: EventSink? = null

    private val sensor: Sensor by lazy {
        sensorManager.getDefaultSensor(sensorType)
    }

    override fun onListen(arguments: Any?, events: EventSink) {
        currentEventSink = events
        sensorEventListener = createSensorEventListener(events)
        registerListener()
        isListening = true
    }

    override fun onCancel(arguments: Any?) {
        unregisterListener()
        sensorEventListener = null
        currentEventSink = null
        isListening = false
    }

    fun setSamplingPeriod(microseconds: Int) {
        samplingPeriodUs = microseconds
        // If already listening, re-register with new sampling period
        if (isListening && sensorEventListener != null) {
            unregisterListener()
            registerListener()
        }
    }

    private fun registerListener() {
        sensorEventListener?.let { listener ->
            sensorManager.registerListener(listener, sensor, samplingPeriodUs)
        }
    }

    private fun unregisterListener() {
        sensorEventListener?.let { listener ->
            sensorManager.unregisterListener(listener)
        }
    }

    private fun createSensorEventListener(events: EventSink): SensorEventListener {
        return object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                val sensorValues = DoubleArray(event.values.size)
                event.values.forEachIndexed { index, value ->
                    sensorValues[index] = value.toDouble()
                }
                events.success(sensorValues)
            }
        }
    }
}

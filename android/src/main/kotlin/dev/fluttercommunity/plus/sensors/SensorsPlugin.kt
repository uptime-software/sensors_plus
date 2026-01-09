package dev.fluttercommunity.plus.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/** SensorsPlugin  */
class SensorsPlugin : FlutterPlugin, MethodChannel.MethodCallHandler {
    private lateinit var methodChannel: MethodChannel
    private lateinit var accelerometerChannel: EventChannel
    private lateinit var userAccelChannel: EventChannel
    private lateinit var gyroscopeChannel: EventChannel
    private lateinit var magnetometerChannel: EventChannel
    private lateinit var accelerometerStreamHandler: StreamHandlerImpl
    private lateinit var userAccelStreamHandler: StreamHandlerImpl
    private lateinit var gyroscopeStreamHandler: StreamHandlerImpl
    private lateinit var magnetometerStreamHandler: StreamHandlerImpl

    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        setupEventChannels(binding.applicationContext, binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        teardownEventChannels()
    }

    private fun setupEventChannels(context: Context, messenger: BinaryMessenger) {
        val sensorsManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Setup method channel for sampling period configuration
        methodChannel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
        methodChannel.setMethodCallHandler(this)

        accelerometerChannel = EventChannel(messenger, ACCELEROMETER_CHANNEL_NAME)
        accelerometerStreamHandler = StreamHandlerImpl(
                sensorsManager,
                Sensor.TYPE_ACCELEROMETER
        )
        accelerometerChannel.setStreamHandler(accelerometerStreamHandler)

        userAccelChannel = EventChannel(messenger, USER_ACCELEROMETER_CHANNEL_NAME)
        userAccelStreamHandler = StreamHandlerImpl(
                sensorsManager,
                Sensor.TYPE_LINEAR_ACCELERATION
        )
        userAccelChannel.setStreamHandler(userAccelStreamHandler)

        gyroscopeChannel = EventChannel(messenger, GYROSCOPE_CHANNEL_NAME)
        gyroscopeStreamHandler = StreamHandlerImpl(
                sensorsManager,
                Sensor.TYPE_GYROSCOPE
        )
        gyroscopeChannel.setStreamHandler(gyroscopeStreamHandler)

        magnetometerChannel = EventChannel(messenger, MAGNETOMETER_CHANNEL_NAME)
        magnetometerStreamHandler = StreamHandlerImpl(
                sensorsManager,
                Sensor.TYPE_MAGNETIC_FIELD
        )
        magnetometerChannel.setStreamHandler(magnetometerStreamHandler)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "setAccelerationSamplingPeriod" -> {
                val microseconds = call.arguments as? Int
                if (microseconds != null) {
                    accelerometerStreamHandler.setSamplingPeriod(microseconds)
                    result.success(null)
                } else {
                    result.error("INVALID_ARGUMENT", "Sampling period must be an integer", null)
                }
            }
            "setGyroscopeSamplingPeriod" -> {
                val microseconds = call.arguments as? Int
                if (microseconds != null) {
                    gyroscopeStreamHandler.setSamplingPeriod(microseconds)
                    result.success(null)
                } else {
                    result.error("INVALID_ARGUMENT", "Sampling period must be an integer", null)
                }
            }
            "setUserAccelerometerSamplingPeriod" -> {
                val microseconds = call.arguments as? Int
                if (microseconds != null) {
                    userAccelStreamHandler.setSamplingPeriod(microseconds)
                    result.success(null)
                } else {
                    result.error("INVALID_ARGUMENT", "Sampling period must be an integer", null)
                }
            }
            "setMagnetometerSamplingPeriod" -> {
                val microseconds = call.arguments as? Int
                if (microseconds != null) {
                    magnetometerStreamHandler.setSamplingPeriod(microseconds)
                    result.success(null)
                } else {
                    result.error("INVALID_ARGUMENT", "Sampling period must be an integer", null)
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun teardownEventChannels() {
        methodChannel.setMethodCallHandler(null)
        accelerometerChannel.setStreamHandler(null)
        userAccelChannel.setStreamHandler(null)
        gyroscopeChannel.setStreamHandler(null)
        magnetometerChannel.setStreamHandler(null)
    }

    companion object {
        private const val METHOD_CHANNEL_NAME = "dev.fluttercommunity.plus/sensors/method"
        private const val ACCELEROMETER_CHANNEL_NAME = "dev.fluttercommunity.plus/sensors/accelerometer"
        private const val GYROSCOPE_CHANNEL_NAME = "dev.fluttercommunity.plus/sensors/gyroscope"
        private const val USER_ACCELEROMETER_CHANNEL_NAME = "dev.fluttercommunity.plus/sensors/user_accel"
        private const val MAGNETOMETER_CHANNEL_NAME = "dev.fluttercommunity.plus/sensors/magnetometer"
    }
}

package com.example.opticalcharacterrecognition

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {

    private  lateinit  var tvResult: TextView
    private  lateinit  var  btnStartReading:Button
    private lateinit  var surfaceView: SurfaceView

    private lateinit var cameraSource: CameraSource
    private lateinit var textRecognizer: TextRecognizer

    private var textToSpeech: TextToSpeech? = null
    private var stringResult: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PackageManager.PERMISSION_GRANTED)
        textToSpeech = TextToSpeech(this) { }
        tvResult = findViewById(R.id.tv_result)


    }

    override fun onResume() {
        super.onResume()
        btnStartReading = findViewById(R.id.btn_start_reading)
        btnStartReading.setOnClickListener {
            btnStart()
        }
    }

    private fun btnStart() {
        setContentView(R.layout.surface_view)
        textRecognizer()
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraSource.release()
    }

   private fun textRecognizer() {
       textRecognizer = TextRecognizer.Builder(applicationContext).build()
       cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
           .setRequestedPreviewSize(1280, 1024)
           .build()

       surfaceView = findViewById(R.id.surfaceView)

       surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
           override fun surfaceCreated(holder: SurfaceHolder) {
               try {
                   if (ActivityCompat.checkSelfPermission(this@MainActivity, CAMERA) != PackageManager.PERMISSION_GRANTED
                   ) {

                       Toast.makeText(this@MainActivity, "camera permission not granted", Toast.LENGTH_SHORT).show()
                       // TODO: Consider calling
                       //    ActivityCompat#requestPermissions
                       // here to request the missing permissions, and then overriding
                       //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                       //                                          int[] grantResults)
                       // to handle the case where the user grants the permission. See the documentation
                       // for ActivityCompat#requestPermissions for more details.
                       return
                   }else {
                       cameraSource.start(surfaceView.holder)
                   }
               } catch (e: IOException) {
                   e.printStackTrace()
               }
           }

           override fun surfaceChanged(
               holder: SurfaceHolder,
               format: Int,
               width: Int,
               height: Int
           ) {
           }

           override fun surfaceDestroyed(holder: SurfaceHolder) {
               cameraSource.stop()
           }
       })


       a()



   }

    fun a(){



       textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {

           override fun release() {

           }

           override fun receiveDetections(detections: Detector.Detections<TextBlock>?) {

               val sparseArr = detections?.detectedItems

               if (sparseArr != null) {
                   if (sparseArr.size() <= 0) {
                       return
                   }
               }

           //    Thread {
               runOnUiThread {

                   val stringBuilder = StringBuilder()
                   Log.d("test", "sparseArr $sparseArr")

                   if (sparseArr != null) {
                       for (i in 0 until sparseArr.size()) {
                           val textBlock = sparseArr.valueAt(i)
                           Log.d("test", "textBlock.Val ->  ${textBlock.value}")

                           stringBuilder.append(textBlock.value)
                           stringBuilder.append("\n")

                           Log.d("test", "stringBuilder $stringBuilder")
                       }
                   }
                    stringResult = stringBuilder.toString()
                   Log.d("test", "text===== $stringResult")
                   // tvResult.text = stringResult
                   resultObtained(stringResult!!)
               }
                 //  return@Thread
                  }
       })



    }

    private fun resultObtained(stringResultN: String) {
        setContentView(R.layout.activity_main)
        var tvResult: TextView = findViewById(R.id.tv_result)
        tvResult.text = stringResultN
        textToSpeech?.speak(this.stringResult, TextToSpeech.QUEUE_FLUSH, null, null)


        btnStartReading = findViewById(R.id.btn_start_reading)
        btnStartReading.setOnClickListener {
            btnStart()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("test","TTS The Language not supported!")
            } else {
                btnStartReading.isEnabled = true
                Log.d("test"," btnSpeak!!.isEnabled = true")
            }
        }
    }


}
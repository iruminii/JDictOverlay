package com.example.jdictoverlay.recognition
//package com.google.mlkit.samples.vision.digitalink.kotlin

import android.content.Context
import android.graphics.ColorSpace
import android.os.Handler
import android.os.Message
import androidx.annotation.VisibleForTesting
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.google.android.gms.tasks.SuccessContinuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.MlKitException
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.*
import com.google.mlkit.vision.digitalink.Ink.Stroke
import java.util.ArrayList

/** Manages the recognition logic and the content that has been added to the current page.  */
class StrokeManager {

    private var LANGUAGE_TAG = "ja"
    // recognition model
    private lateinit var model : DigitalInkRecognitionModel
    // Managing ink currently drawn.
    private var strokeBuilder = Stroke.builder()
    private var inkBuilder = Ink.builder()
    private var triggerRecognitionAfterInput = true
    private var clearCurrentInkAfterRecognition = true
    //
    private var content = listOf("")

    fun setTriggerRecognitionAfterInput(shouldTrigger: Boolean) {
        triggerRecognitionAfterInput = shouldTrigger
    }

    fun setClearCurrentInkAfterRecognition(shouldClear: Boolean) {
        clearCurrentInkAfterRecognition = shouldClear
    }

    fun reset() {
        inkBuilder = Ink.builder()
        strokeBuilder = Stroke.builder()
    }

    val currentInk: Ink
        get() = inkBuilder.build()

    /**
     * This method is called when a new touch event happens on the drawing client and notifies the
     * StrokeManager of new content being added.
     *
     *
     * This method takes care of triggering the UI timeout and scheduling recognitions on the
     * background thread.
     *
     * @return whether the touch event was handled.
     */
    fun addNewTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val x = event.x
        val y = event.y
        val t = System.currentTimeMillis()

        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> strokeBuilder.addPoint(
                Ink.Point.create(
                    x,
                    y,
                    t
                )
            )
            MotionEvent.ACTION_UP -> {
                strokeBuilder.addPoint(Ink.Point.create(x, y, t))
                inkBuilder.addStroke(strokeBuilder.build())
                strokeBuilder = Stroke.builder()
            }
            else -> // Indicate touch event wasn't handled.
                return false
        }
        return true
    }

    fun getContent(): List<String> {
        return content
    }

    fun download() {
        try {
            val modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag(LANGUAGE_TAG)
            model = DigitalInkRecognitionModel.builder(modelIdentifier!!).build()

            val remoteModelManager = RemoteModelManager.getInstance()
            remoteModelManager.download(model, DownloadConditions.Builder().build())
                .addOnSuccessListener {
                    Log.i("TAG", "Model downloaded.")
                }
                .addOnFailureListener { e: Exception ->
                    Log.i(TAG, "Model download failed. $e")
                }
        } catch (e: MlKitException) {
            // couldnt download
        }
    }

    // Recognition-related.
    fun recognize(callback:(List<String>) -> Unit) {
        val candidates = mutableListOf<String>()
        val recognizer: DigitalInkRecognizer =
            DigitalInkRecognition.getClient(
                DigitalInkRecognizerOptions.builder(model).build()
            )
        val ink = inkBuilder.build()
        recognizer.recognize(ink)
            .addOnSuccessListener { result : RecognitionResult ->
                for(candidate in result.candidates.take(4)) {
                    candidates.add(candidate.text)
                    Log.d("STROKEMANAGER", "candidates = $candidates")
                }
                content = candidates
                callback(content)
                Log.d("STROKEMANAGER", "INSIDEONSUCCESS content = $content")
            }
            .addOnFailureListener { e: Exception ->
                Log.e(TAG, "Recognition Error: $e")
            }
    }

    companion object {
        private const val TAG = "MLKD.StrokeManager"
    }
}

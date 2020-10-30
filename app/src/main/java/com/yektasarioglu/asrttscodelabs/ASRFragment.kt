package com.yektasarioglu.asrttscodelabs

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.huawei.hms.mlsdk.asr.MLAsrListener
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer

import com.yektasarioglu.asrttscodelabs.databinding.FragmentAsrBinding

import kotlinx.android.synthetic.main.fragment_asr.*

import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

private const val ASR_RESULT_CODE = 300

@RuntimePermissions
class ASRFragment : Fragment() {

    private var binding: FragmentAsrBinding? = null

    private lateinit var asrRecognizer: MLAsrRecognizer

    private var isPickUpUI: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentAsrBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeASRWithPermissionCheck()
        initializeUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyASR()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ASR_RESULT_CODE) {
            if (resultCode == MLAsrCaptureConstants.ASR_SUCCESS) {
                val result = data?.extras?.getString(MLAsrCaptureConstants.ASR_RESULT) ?: ""
                showResult(result)
            }
        }
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    fun initializeASR() {
        initializeASRRecognizer()
        initializeCallback()
    }

    private fun initializeASRRecognizer() {
        asrRecognizer = MLAsrRecognizer.createAsrRecognizer(activity)
    }

    private fun initializeCallback() {
        asrRecognizer.setAsrListener(object : MLAsrListener {
            override fun onResults(result: Bundle?) {
                MediaPlayer.create(activity, R.raw.stop_record).start()
                Log.i("ASRFragment", "result is ${result?.getString(MLAsrRecognizer.RESULTS_RECOGNIZED)}")
                showResult(result?.getString(MLAsrRecognizer.RESULTS_RECOGNIZED) ?: "")
            }

            override fun onRecognizingResults(partialResult: Bundle?) {
                Log.i("ASRFragment", "partialResult is ${partialResult?.getString(MLAsrRecognizer.RESULTS_RECOGNIZED)}")
                showResult(partialResult?.getString(MLAsrRecognizer.RESULTS_RECOGNIZED) ?: "")
            }

            override fun onError(errorCode: Int, errorMessage: String?) {
                Log.e("ASRFragment", "errorCode is $errorCode")
                Log.e("ASRFragment", "errorMessage is $errorMessage")
            }

            override fun onStartListening() {
                Log.i("ASRFragment", "onStartListening()")
                MediaPlayer.create(activity, R.raw.start_record).start()
            }

            override fun onStartingOfSpeech() {
                Log.i("ASRFragment", "onStartingOfSpeech()")
            }

            override fun onVoiceDataReceived(p0: ByteArray?, p1: Float, p2: Bundle?) {

            }

            override fun onState(p0: Int, p1: Bundle?) {

            }
        })
    }

    private fun destroyASR() = asrRecognizer.destroy()

    private fun startRecognizeWithSpeechPickupUI() {
        val intent = Intent(activity, MLAsrCaptureActivity::class.java)
            .putExtra(MLAsrCaptureConstants.LANGUAGE, ENGLISH_LANGUAGE_CODE)
            .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
        startActivityForResult(intent, ASR_RESULT_CODE)
    }

    private fun startRecognizeWithoutSpeechPickupUI() {
        val intent = Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH)
            .putExtra(MLAsrCaptureConstants.LANGUAGE, ENGLISH_LANGUAGE_CODE)
            .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
        asrRecognizer.startRecognizing(intent)
    }

    private fun initializeUI() {
        binding.apply {
            withPickUpUIRadioButton.setOnClickListener { isPickUpUI = true }
            withoutPickUpUIRadioButton.setOnClickListener { isPickUpUI = false }
            asrButton.setOnClickListener {
                if (isPickUpUI) startRecognizeWithSpeechPickupUI()
                else startRecognizeWithoutSpeechPickupUI()
            }
        }
    }

    private fun showResult(text: String) {
        binding?.resultTextView?.text =  text
    }

}
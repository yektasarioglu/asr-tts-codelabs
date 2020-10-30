package com.yektasarioglu.asrttscodelabs

import android.os.Bundle
import android.util.Pair
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment

import com.huawei.hms.mlsdk.tts.*

import com.yektasarioglu.asrttscodelabs.databinding.FragmentTtsBinding

private const val FEMALE_SPEAKER_CODE = "-st-1"
private const val MALE_SPEAKER_CODE = "-st-2"

private const val NO_MALE_INFO = "Male version is not available !!"

class TTSFragment : Fragment() {

    private var binding: FragmentTtsBinding? = null

    private lateinit var ttsConfig: MLTtsConfig
    private lateinit var ttsEngine: MLTtsEngine
    private lateinit var ttsCallback: MLTtsCallback

    private var isMale: Boolean = false
    private var isDefaultSpeedValueExceeded = false
    private var voiceSpeed: Float = 1f
    private var languageCode: String = ENGLISH_LANGUAGE_CODE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentTtsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureEnginePreferences()
        initializeEngine()
        initializeCallback()

        initializeUI()
    }

    private fun initializeUI() {
        binding?.apply {
            voiceSpeedSlider.addOnChangeListener { _, value, _ ->
                voiceSpeed = value
                isDefaultSpeedValueExceeded = value > 1f
            }

            maleRadioButton.setOnClickListener {
                isMale = true
                languageRadioGroup.clearCheck()
            }
            femaleRadioButton.setOnClickListener {
                isMale = false
                languageRadioGroup.clearCheck()
            }

            chRadioButton.setOnClickListener {
                languageCode = CHINESE_LANGUAGE_CODE
                showMale()
            }
            enRadioButton.setOnClickListener {
                languageCode = ENGLISH_LANGUAGE_CODE
                showMale()
            }
            frRadioButton.setOnClickListener {
                languageCode = FRENCH_LANGUAGE_CODE
                hideMale()
                toast(NO_MALE_INFO)
            }
            itRadioButton.setOnClickListener {
                languageCode = ITALIAN_LANGUAGE_CODE
                hideMale()
                toast(NO_MALE_INFO)
            }
            gerRadioButton.setOnClickListener {
                languageCode = GERMAN_LANGUAGE_CODE
                hideMale()
                toast(NO_MALE_INFO)
            }
            spaRadioButton.setOnClickListener {
                languageCode = SPANISH_LANGUAGE_CODE
                hideMale()
                toast(NO_MALE_INFO)
            }

            ttsButton.setOnClickListener {
                if (binding?.ttsFieldEditText?.text?.toString()!!.isEmpty()) toast("Empty TTS text field !!")

                if (languageRadioGroup.checkedRadioButtonId == -1)
                    toast("Select language first")
                else {
                    changeTTSConfiguration(isMale, languageCode, voiceSpeed)

                    if (isDefaultSpeedValueExceeded)
                        toast("You've exceeded default voice speed value(1.0)")

                    speak(binding?.ttsFieldEditText?.text?.toString()!!)
                }
            }
        }
    }

    private fun configureEnginePreferences() {
        ttsConfig = MLTtsConfig()
            .setLanguage(ENGLISH_LANGUAGE_CODE)
            .setPerson(ENGLISH_LANGUAGE_CODE + FEMALE_SPEAKER_CODE)
            .setSpeed(1f)
            .setVolume(1f)
    }

    private fun initializeEngine() {
        ttsEngine = MLTtsEngine(ttsConfig)
    }

    private fun initializeCallback() {
        ttsCallback = object : MLTtsCallback {
            override fun onError(taskId: String?, error: MLTtsError?) {
                // Processing logic for TTS failure.
            }

            override fun onWarn(taskId: String?, warn: MLTtsWarn?) {
                // Alarm handling without affecting service logic.
            }

            // Return the mapping between the currently played segment and text.
            // start: start position of the audio segment in the input text;
            // end (excluded): end position of the audio segment in the input text.
            override fun onRangeStart(taskId: String?, start: Int, end: Int) {

            }

            // taskId: ID of an audio synthesis task corresponding to the audio.
            // audioFragment: audio data.
            // offset: offset of the audio segment to be transmitted in the queue. One audio synthesis task corresponds to an audio synthesis queue.
            // range: text area where the audio segment to be transmitted is located; range.first (included): start position; range.second (excluded): end position.
            override fun onAudioAvailable(
                taskId: String?,
                audioFragment: MLTtsAudioFragment?,
                offset: Int,
                range: Pair<Int, Int>?,
                bundle: Bundle?
            ) {

            }

            override fun onEvent(taskId: String?, eventId: Int, bundle: Bundle?) {
                when (eventId) {
                    MLTtsConstants.EVENT_PLAY_START -> {
                    }
                    MLTtsConstants.EVENT_PLAY_PAUSE -> {
                    }
                    MLTtsConstants.EVENT_PLAY_RESUME -> {
                    }
                    MLTtsConstants.EVENT_PLAY_STOP -> {
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_START -> {
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_END -> {
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_COMPLETE -> {
                    }
                }
            }
        }

        ttsEngine.setTtsCallback(ttsCallback)
    }

    private fun changeTTSConfiguration(isMale: Boolean, languageCode: String, voiceSpeed: Float = 1f, volume: Float = 1f) {
        val newConfig = MLTtsConfig()
            .setLanguage(languageCode)
            .setPerson(getSpeakerCode(isMale, languageCode))
            .setSpeed(voiceSpeed)
            .setVolume(volume)

        changeEnginePreferences(newConfig)
    }

    private fun getSpeakerCode(isMale: Boolean, languageCode: String): String {
        var speakerCode = languageCode

        speakerCode += if (isMale && (languageCode == ENGLISH_LANGUAGE_CODE || languageCode == CHINESE_LANGUAGE_CODE))
            MALE_SPEAKER_CODE
        else
            FEMALE_SPEAKER_CODE

        return speakerCode
    }

    private fun changeEnginePreferences(config: MLTtsConfig) = ttsEngine.updateConfig(config)

    private fun speak(text: String) = ttsEngine.speak(text, MLTtsEngine.QUEUE_APPEND)

    private fun showMale() {
        binding?.maleRadioButton?.visibility = View.VISIBLE
    }

    private fun hideMale() {
        binding?.maleRadioButton?.visibility = View.INVISIBLE
    }

    private fun toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).apply {
        setGravity(Gravity.TOP, 0, 75)
        show()
    }

}
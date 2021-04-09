package mchou.apps.ssendu.ui.translate

import androidx.lifecycle.ViewModel

class TranslateViewModel : ViewModel() {
    private var translate_case :Int = 0
    private var translate_word :String = ""

    fun getCase() = translate_case
    fun setCase(value: Int) {
        translate_case = value
    }
    fun getword() = translate_word
    fun setword(value: String) {
        translate_word = value
    }

}
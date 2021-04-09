package mchou.apps.ssendu

import android.animation.*
import android.content.Intent
import android.transition.TransitionManager

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.start_activity.*
import mchou.apps.ssendu.web.JSoupHttpRequest
import mchou.apps.ssendu.web.SimpleHttpRequest

class StartActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)
        supportActionBar!!.hide()

        startAnimation()

    }

    private fun startAnimation() {

        btnStart.alpha = 0f

        val animatorSet = AnimatorSet()
        val fadeAnim: ValueAnimator = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f)
        fadeAnim.duration = 2500
        fadeAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animateLogo()
                //JSoupHttpRequest(applicationContext, "https://amawal.net/tama").execute()
            }
        })
        animatorSet.play(fadeAnim)
        animatorSet.start()
    }

    private fun animateLogo() {
        val finishingConstraintSet = ConstraintSet()
        finishingConstraintSet.clone(applicationContext, R.layout.start_activity_finish)
        TransitionManager.beginDelayedTransition(root)
        finishingConstraintSet.applyTo(root)

        logo!!.setImageDrawable(resources.getDrawable(R.drawable.logo_zw_finish, null))

        val fade1Anim: ValueAnimator =
            ObjectAnimator.ofFloat(btnStart, "alpha", 0f, 1f).setDuration(1500)

        val animatorSet = AnimatorSet()
        animatorSet.play(fade1Anim) //.before(fade2Anim).before(fade3Anim)
        animatorSet.start()
    }

    fun open(view: View) {

        val activityClass : Class<*> = TranslateActivity::class.java
        val intent = Intent( this,  activityClass)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_down, R.anim.slide_up);

    }


}
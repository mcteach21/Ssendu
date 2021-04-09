package mchou.apps.ssendu

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text


class TestsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests)
    }

    object ViewAnimationUtils {
        fun expand(v: View) {
            v.measure(LinearLayout.LayoutParams.MATCH_PARENT,400) // LinearLayout.LayoutParams.WRAP_CONTENT)

            val targtetHeight: Int = 400 //v.getMeasuredHeight()

            v.getLayoutParams().height = 0
            v.setVisibility(View.VISIBLE)

            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    v.getLayoutParams().height =
                        if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targtetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.setDuration(500
                //((targtetHeight / v.getContext().getResources().getDisplayMetrics().density) as Float).toLong()*3
            )
            v.startAnimation(a)
        }

        fun collapse(v: View) {
            val initialHeight: Int = v.getMeasuredHeight()
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    if (interpolatedTime == 1f) {
                        v.setVisibility(View.GONE)
                    } else {
                        v.getLayoutParams().height =
                            initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.setDuration(500
               // ((initialHeight / v.getContext().getResources().getDisplayMetrics().density) as Float).toLong()*2
            )
            v.startAnimation(a)
        }
    }

    var opened = false

    fun onclick(view: View) {
        var tv = findViewById<TextView>(R.id.result)

        var txt = "Lorem, ipsum dolor sit amet consectetur adipisicing elit. Optio odio similique dolor dolore delectus fuga facilis mollitia, enim necessitatibus cumque nostrum sint hic esse nisi dolorem reiciendis atque totam eius.Lorem, ipsum dolor sit amet consectetur adipisicing elit. Optio odio similique dolor dolore delectus fuga facilis mollitia, enim necessitatibus cumque nostrum sint hic esse nisi dolorem reiciendis atque totam eius."
        tv.setText(txt)

        if(!opened)
            ViewAnimationUtils.expand(tv)
        else
            ViewAnimationUtils.collapse(tv)

        opened = !opened
    }
}
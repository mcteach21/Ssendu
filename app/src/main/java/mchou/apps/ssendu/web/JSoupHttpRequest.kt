package mchou.apps.ssendu.web

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import org.jsoup.Jsoup
import java.io.IOException


class JSoupHttpRequest(var context: Context, var tvResult: TextView?) : AsyncTask<String, Void, List<String>>() {
    val TAG = "tests"
    override fun doInBackground(vararg urls: String): List<String> {
        var result = arrayListOf<String>()

        try {
            for (url in urls) {
                var document = Jsoup.connect(url).get()
                var htmlContent = document.select(".content_fields li span").html()
                val txtRemove = document.select("#page_title_single p")[1].html()

                var content = clean(htmlContent, txtRemove)
                var contents = content.split("<br>")

                Log.i(TAG, "=============================================")
                for (line in contents) {
                    Log.i(TAG, line)
                    result.add(line)
                }
                Log.i(TAG, "=============================================")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun clean(html: String, txtRemove: String): String {
        val tags = arrayOf("\n", "<p></p>", "<span>","</span>", "<p>","</p>", "<ul>","</ul>", "<li>","</li>", "<strong>","</strong>", "<span class=\"tz\">"
        , "<p dir=\"rtl\" class=\"rtl\">", "<span class=\"translation flag_ar_MA\">", "<span class=\"translation flag_en_US\">" , "<span style=\"text-decoration:line-through\">"
        )

        var cleanHtml = html.replace(txtRemove, "")
        for (tag in tags)
            cleanHtml = cleanHtml.replace(tag, "")


/*        cleanHtml = cleanHtml.replace(" Agdawal:","Agdawal:\n")
        cleanHtml = cleanHtml.replace(" Addad amaruz:","Addad amaruz:\n")

        cleanHtml = cleanHtml.replace("Addad amaruz:","Addad amaruz:\n")*/

        cleanHtml = cleanHtml.replace(" Imedyaten:","\nImedyaten (Exemples):\n")
        return cleanHtml
    }

    override fun onPostExecute(result: List<String>) {
        //Toast.makeText(context, "result : $result", Toast.LENGTH_SHORT).show()
        var txt = StringBuilder()
        for (s in result)
            txt.append(s+"\n")

        tvResult!!.text = txt.toString()
    }

}

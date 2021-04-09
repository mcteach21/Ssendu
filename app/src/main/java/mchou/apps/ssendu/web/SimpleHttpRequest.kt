package mchou.apps.ssendu.web

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class SimpleHttpRequest {
    public fun request(context: Context) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://amawal.net/amdan"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.i("tests", "**********************************")
                Log.i("tests", "$response")
                Log.i("tests", "**********************************")
            },
            { Log.i("tests", "That didn't work!") })

        queue.add(stringRequest)
    }
}
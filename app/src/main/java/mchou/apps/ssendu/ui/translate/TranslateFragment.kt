package mchou.apps.ssendu.ui.translate

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import mchou.apps.ssendu.R
import mchou.apps.ssendu.TestsActivity
import mchou.apps.ssendu.web.JSoupHttpRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class TranslateFragment : Fragment() {


    private var search_more_layout: LinearLayout? = null
    private val TAG = "tests"
    private var noitem: TextView? = null
    private var search_more: Button? = null
    private var search_more_result: TextView? = null
    private var spinner: Spinner? = null
    private var edtSearch: EditText? = null
    private var keyboard: GridLayout? = null
    private var recyclerView: RecyclerView? = null
    private var recyclerView2: RecyclerView? = null
    private var data: List<String> = mutableListOf()
    private var go: Button? = null
    private var displayKeyboard: Button? = null
//    private var progress: ProgressBar? = null
    private val API_BASE_URL = "https://fr.glosbe.com/"

    companion object {
        fun newInstance() = TranslateFragment()
    }

    private lateinit var viewModel: TranslateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.translate_fragment_2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TranslateViewModel::class.java)

        initContent()
        initResultList()
    }

    private fun initContent() {
        val options = resources.getStringArray(R.array.options)
        spinner  = view?.findViewById<Spinner>(R.id.filter_options)
        edtSearch = view?.findViewById<EditText>(R.id.edt_search)

        keyboard = view?.findViewById<androidx.gridlayout.widget.GridLayout>(R.id.keyboard)

        noitem = view?.findViewById<TextView>(R.id.txt_noitem)
        go = view?.findViewById<Button>(R.id.go)
        displayKeyboard = view?.findViewById<Button>(R.id.btnKeybord)

//        progress = view?.findViewById<ProgressBar>(R.id.progress)
//        progress!!.visibility = View.GONE

        go!!.setOnClickListener {
            startSearch()
        }

        search_more_layout= view?.findViewById<LinearLayout>(R.id.search_more_layout)
        search_more_result= view?.findViewById<TextView>(R.id.search_more_result)

        search_more = view?.findViewById<Button>(R.id.search_more)
        search_more!!.setOnClickListener{
            var search = edtSearch!!.text.toString()

            if(!search.trim().isEmpty()) {
                JSoupHttpRequest(
                    activity!!.applicationContext,
                    search_more_result
                ).execute("https://amawal.net/$search")
                animate()
                hideKeyboard()
                search_more_result?.let { TestsActivity.ViewAnimationUtils.expand(it) }
            }
        }


        spinner!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected( parent: AdapterView<*>, view: View, position: Int, id: Long ) {
                createCustomKeyboard()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        displayKeyboard?.setOnClickListener(View.OnClickListener {
            createCustomKeyboard()
            opened = !opened
            slideKeybord(opened)

            if (opened)
                hideKeyboard()

        })
    }

    private fun startSearch() {
        var search = edtSearch!!.text.toString()
        var id = spinner!!.selectedItemId
        var url = API_BASE_URL+"fr/kab/"
        if(id>0)
            url = API_BASE_URL + "kab/fr/"

        RetrieveSiteData().execute(url + search)

        go?.isEnabled = false

        hideKeyboard()
        animate()
    }

    private fun searchFinihed(result: ResultInfo) {
        go?.isEnabled = true

        search_more_layout?.visibility = if(result.words.size>0) View.GONE else View.VISIBLE
        search_more_result?.visibility = if(result.words.size>0) View.GONE else View.VISIBLE
        // search_more?.visibility = if(result.words.size>0) View.GONE else View.VISIBLE

        if(opened) {
            opened = false
            slideKeybord(opened)
        }

        updateList(result)
    }
    /**
     * Result List (RecyclerView+Adapter+ItemListener)
     */
    //*************************************************************************//
    //*************************************************************************//
    private fun initResultList() {
        recyclerView = view!!.findViewById<RecyclerView>(R.id.list1)
        recyclerView!!.setHasFixedSize(true)

        //val layoutManager = LinearLayoutManager(context)
        var viewLayoutManager = GridLayoutManager(context, 2)

        recyclerView!!.layoutManager = viewLayoutManager!!       //layoutManager
        //recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        data  = mutableListOf<String>() //Dico.relatedWords()
        var adapter = WordsRecyclerViewAdapter(
            data.toTypedArray(),
            1,
            object : OnItemClickListener {
                override fun onItemClick(postion: Int, item: String) {
                    edtSearch!!.setText(item, TextView.BufferType.EDITABLE)

                    var id = if (spinner!!.selectedItemId > 0) 0 else 1
                    spinner!!.setSelection(id, true)
                    go?.performClick()
                }
            }
        )
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            context, R.anim.layout_fall_down_animation
        )

        recyclerView2 = view!!.findViewById<RecyclerView>(R.id.list2)
        recyclerView2!!.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        //var viewLayoutManager = GridLayoutManager(context, 2)

        recyclerView2!!.layoutManager = layoutManager
        //recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        var adapter2 = WordsRecyclerViewAdapter(
            mutableListOf<String>().toTypedArray(),
            2,
            object : OnItemClickListener {
                override fun onItemClick(postion: Int, item: String) {
                    Toast.makeText(context, "Click on $item", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView2!!.adapter = adapter2
        recyclerView2!!.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            context, R.anim.grid_layout_animation_from_bottom
        )
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int, item: String)
    }
    inner class WordsRecyclerViewAdapter(
        public var dataset: Array<String>,
        private var case: Int,
        private val listener: OnItemClickListener
    ) :
        RecyclerView.Adapter<WordsRecyclerViewAdapter.ViewHolder>() {

        public fun updateDataSet(data: Array<String>){
            this.dataset=data
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var item_layout : Int
            if(case==1)
                item_layout = R.layout.recyclerview_item_layout
            else
                item_layout = R.layout.recyclerview_item_2_layout

            val itemView = LayoutInflater.from(parent.context).inflate(item_layout, parent, false)

            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = dataset[position]
            holder.itemView.setOnClickListener { listener.onItemClick(position, dataset[position]) }

            //holder.bind(dataset[position], listener);
        }

        override fun getItemCount(): Int {
            return dataset.size
        }
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var title: TextView = itemView.findViewById(R.id.item_title)

            /* public void bind(final String item, final OnItemClickListener listener) {
                title.setText(item);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(item);
                    }
                });
            }*/
        }
    }

    private fun updateList(result: ResultInfo) {
        var data : MutableList<String>
        (context as Activity).runOnUiThread {

            data = mutableListOf()
            for (word in result.words)
                data.add(word!!)

            (recyclerView!!.adapter as WordsRecyclerViewAdapter).updateDataSet(data.toTypedArray())
            recyclerView!!.adapter!!.notifyDataSetChanged()

            data.clear()
            for (example in result.examples)
                data.add(example!!)
            (recyclerView2!!.adapter as WordsRecyclerViewAdapter).updateDataSet(data.toTypedArray())
            recyclerView2!!.adapter!!.notifyDataSetChanged()

        }
    }

    //*************************************************************************//
    //*************************************************************************//

    /**
     * Translate WebSite Request!
     */
    //*************************************************************************//
    //*************************************************************************//

    class ResultInfo{
       var words :  MutableList<String?> = mutableListOf()
       var examples : MutableList<String?> = mutableListOf()

    }
    inner class RetrieveSiteData() : AsyncTask<String?, Void?, ResultInfo>() {
        override fun doInBackground(vararg urls: String?): ResultInfo {

            var result = ResultInfo()
            for (url in urls) {
                try {

                    val doc: Document = Jsoup.connect(url).get()
                    val translatedWords: Elements = doc.select(".ng-star-inserted h4")

                    for (headline in translatedWords) {
                        var word : String? = format(headline.text())
                        if (!result.words.contains(word))
                            result.words.add(word)
                    }

                    val translatedExamples: Elements = doc.select(".translate-entry-translation-example-text")
                    for (headline in translatedExamples) {
                        var example : String? = headline.text()
                        if (!result.examples.contains(example))
                            result.examples.add(example)
                    }
                }catch (e: Exception){
                    Log.i("tests", "No Result! $e")
                }

            }
            return result
        }

        override fun onPostExecute(result: ResultInfo) {
            searchFinihed(result)
        }
    }
    //*************************************************************************//
    //*************************************************************************//

    /**
     * Tools
     */
    //*************************************************************************//
    //*************************************************************************//
    private fun animate() {
        val root: ConstraintLayout = view!!.findViewById(R.id.root)
        val finishingConstraintSet = ConstraintSet()
        finishingConstraintSet.clone(this.activity, R.layout.translate_fragment_final)
        TransitionManager.beginDelayedTransition(root)
        finishingConstraintSet.applyTo(root)
    }
    private fun format(text: String): String? {
        return text.substring(0, 1).toUpperCase()+text.substring(1).toLowerCase()
    }

    private fun hideKeyboard(){
        val view = activity?.currentFocus
        view?.let { v ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
    private fun getAlphabet() : Array<String>{
        //var current_lang =  if(spinner!!.selectedItemId>0) "kab" else "fr"
        currentKeyboard = spinner!!.selectedItemId.toInt()+1
        Log.i(TAG, "currentKeyboard: $currentKeyboard")
        return if(currentKeyboard==2)
            arrayOf(
                "a", "b", "g", "d", "Ḍ", "e", "f", "k", "h", "Ḥ", "ɛ", "kh", "q", "i", "j", "l",
                "m", "n", "u", "r", "Ṛ", "ɣ", "s", "Ṣ", "sh", "t", "Ṭ", "w", "y", "z", "Ẓ", "w",
                "bh", "gh", "dj", "dj", "d", "Ḍh", "kh", "h", "p", "th", "č", "v"
            )
        else
            arrayOf(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h",
                "i",
                "j",
                "k",
                "l",
                "m",
                "n",
                "o",
                "p",
                "q",
                "r",
                "s",
                "t",
                "u",
                "v",
                "w",
                "x",
                "y",
                "z",
                "<="
            )
    }

    private var opened : Boolean = false
    private val maxIndice = 26
    private var currentKeyboard = 1 //fr 2:ber(latin) 3:tifinaɣ

    private fun createCustomKeyboard(next:Boolean=false) {
        keyboard!!.removeAllViews()

        var keys = getAlphabet()
        if(next){
           for (i in maxIndice+1..keys.indices.last){
               addKeytoKeyboard(i, keys[i])
           }
           addKeytoKeyboard(keys.indices.last + 1, "<=")
           opened=false
        }else {
            for (i in 0 until maxIndice)
                addKeytoKeyboard(i, keys[i])

            addKeytoKeyboard(maxIndice, "<=")
            if(keys.indices.last>maxIndice)
                addKeytoKeyboard(maxIndice+1, "..")
        }
        keyboard!!.useDefaultMargins = true

       /* opened = !opened
        slideKeybord(opened)*/
    }
    private fun addKeytoKeyboard(i: Int, txt: String) {
        val margin = 6
        val scale = resources.displayMetrics.density

        var btn: Button = Button(context)
        btn.id = i

        btn.text = txt
        btn.isAllCaps=false

        val color = if (txt == "..") R.color.material_red_a200 else R.color.primaryTextColor
        btn.setTextColor(activity!!.getColor(color))
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

        val params = LinearLayout.LayoutParams(
            (45 * scale + 0.5f).toInt(), (45 * scale + 0.5f).toInt()
        )
        params.setMargins(margin, margin, margin, margin)
        btn.layoutParams = params

        btn.setBackgroundResource(R.drawable.button_key_shape)
        btn.setOnClickListener(View.OnClickListener {
            //Toast.makeText(context, (it as Button).text, Toast.LENGTH_SHORT).show()
            writeWord((it as Button).text)
        })
        keyboard!!.addView(btn)
    }

    private fun writeWord(text: CharSequence?) {
        when (text) {
            "<=" -> {
                edtSearch!!.setText("")
            }
            ".." -> {
                createCustomKeyboard(true)
            }
            else -> {
                val resultText: String = edtSearch!!.text.toString() + text
                Log.i(TAG, "writeWord: "+resultText)
                edtSearch!!.setText(resultText);
            }
        }
        edtSearch!!.setSelection(edtSearch!!.text.length);
    }

    private fun slideKeybord(open:Boolean=true){
        val currentHeight = if(open) 0 else 800
        val newHeight = if(open) 800 else 0

        val slideAnimator: ValueAnimator = ValueAnimator
            .ofInt(currentHeight, newHeight)
            .setDuration(500)

        slideAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            keyboard!!.layoutParams.height = value
            keyboard!!.requestLayout()
        }

        val set = AnimatorSet()
        set.play(slideAnimator)

        set.interpolator = AccelerateDecelerateInterpolator()
        set.start()
    }
    //*************************************************************************//
    //*************************************************************************//

}

package com.apps.android.list

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.apps.android.list.databinding.ActivityEditlistBinding
import kotlinx.android.synthetic.main.activity_editlist.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "DEPRECATION"
)
class ListEditor : AppCompatActivity() {

    private lateinit var binding: ActivityEditlistBinding
    private lateinit var dir: String
    private lateinit var title: String
    private var cards = mutableListOf<CardView>()
    private var textvs = mutableListOf<TextView>()
    private var tasks = mutableListOf<String>()
    private var taskCount = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = intent.extras.getString("title")
        val ab = supportActionBar
        ab?.title = Html.fromHtml("<font color='#ff1dce'>" + title.removeSuffix(".txt") + "</font>")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_editlist)
        dir = applicationContext.filesDir.path
        loadPrev()

        binding.addBtn.setOnClickListener {
            val txt = binding.newTask.text.toString()
            if (txt == "")
                Toast.makeText(this, "Please enter something.", Toast.LENGTH_LONG).show()
            else {
                val cv = CardView(this)
                cv.alpha = 0.5.toFloat()
                val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                p.bottomMargin = 10
                cv.setContentPadding(20, 20, 20, 20)
                cv.layoutParams = p
                task_contain.addView(cv)
                val tv = TextView(this)
                taskCount++
                tv.text = "$taskCount.\t$txt"
                tv.textSize = 22.toFloat()
                tv.setTextColor(resources.getColor(R.color.barca_pink))
                tv.gravity = Gravity.CENTER_VERTICAL
                cv.addView(tv)
                cards.add(cv)
                textvs.add(tv)
                tasks.add(txt)
                scroll_view.fullScroll(ScrollView.FOCUS_DOWN)
                binding.newTask.setText("")
                addToFile(txt)
                cv.setOnClickListener {
                    //removing task
                    /*task_contain.removeView(cv)
                    cards.remove(cv)
                    textvs.remove(tv)
                    tasks.remove(txt)
                    reNumberize()
                    taskCount--
                    refreshFile()*/
                    removeCard(cv, tv)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadPrev() {
        if (!File("$dir/$title").exists())
            return
        val tfile = FileReader("$dir/$title")
        val lines = tfile.readLines()
        tfile.close()
        for (i in lines) {
            val cv = CardView(this)
            cv.alpha = 0.5.toFloat()
            task_contain.addView(cv)
            val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            p.bottomMargin = 10
            cv.setContentPadding(20, 20, 20, 20)
            cv.layoutParams = p
            val tv = TextView(this)
            taskCount++
            tv.text = "$taskCount.\t$i"
            tv.setTextColor(resources.getColor(R.color.barca_pink))
            tv.textSize = 22.toFloat()
            tv.gravity = Gravity.CENTER_VERTICAL
            cv.addView(tv)
            cards.add(cv)
            textvs.add(tv)
            tasks.add(i)
            cv.setOnClickListener {
                //deletion of task

                /*task_contain.removeView(cv)
                cards.remove(cv)
                textvs.remove(tv)
                tasks.remove(i)
                reNumberize()
                taskCount--
                refreshFile()*/
                removeCard(cv, tv)
            }
        }
    }

    private fun addToFile(txt: String) {
        val fo = FileWriter("$dir/$title", true)
        fo.write(txt + "\n")
        fo.close()
    }

    private fun refreshFile() {
        val fo = FileWriter("$dir/$title")
        for (i in tasks) {
            fo.write(i + "\n")
        }
        fo.close()
    }

    @SuppressLint("SetTextI18n")
    private fun reNumberize() {
        for (i in 0 until tasks.size)
            textvs[i].text = "${(i + 1)}.\t${tasks[i]}"
    }

    @SuppressLint("SetTextI18n")
    private fun removeCard(cv: CardView, tv: TextView) {
        val txt = tv.text.toString()
        val delDialog = Dialog(this)
        delDialog.setContentView(R.layout.delete_popup)
        val yes = delDialog.findViewById<Button>(R.id.yes_btn)
        val no = delDialog.findViewById<Button>(R.id.no_btn)
        val textv = delDialog.findViewById<TextView>(R.id.del_title)
        textv.text = "Delete \"$txt\" ?"
        delDialog.show()

        no.setOnClickListener { delDialog.dismiss() }

        yes.setOnClickListener {
            try {
                delDialog.dismiss()
                task_contain.removeView(cv)
                cards.remove(cv)
                tasks.remove(txt.split("\t")[1])
                textvs.remove(tv)
                reNumberize()
                taskCount--
                refreshFile()
            }catch (ex: Exception) {
                Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}
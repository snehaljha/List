package com.apps.android.list

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileReader

@Suppress("NAME_SHADOWING", "DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: com.apps.android.list.databinding.ActivityMainBinding
    private lateinit var dir: String
    private lateinit var filenames: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ab = this.supportActionBar
        ab?.title = Html.fromHtml("<font color='#ff1dce'>List It</font>")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dir = applicationContext.filesDir.path
        showFiles()
        binding.createList.setOnClickListener {
            val dialog  = Dialog(this)
            dialog.setContentView(R.layout.new_popup)
            val save = dialog.findViewById<Button>(R.id.save_btn)
            val cancel = dialog.findViewById<Button>(R.id.cancel_btn)
            val title = dialog.findViewById<EditText>(R.id.list_title)
            dialog.show()
            cancel.setOnClickListener { dialog.dismiss() }
            save.setOnClickListener {
                when {
                    title.text.toString() == "" -> Toast.makeText(this, "List name can't be empty!", Toast.LENGTH_LONG).show()
                    title.text.toString().contains(".") -> Toast.makeText(this, "List name can't contain '.'", Toast.LENGTH_LONG).show()
                    filenames.contains(title.text.toString() + ".txt") -> Toast.makeText(this, "${title.text} already exists!", Toast.LENGTH_LONG).show()
                    else -> {
                        filenames.add(title.text.toString() + ".txt")
                        addCard(title.text.toString() + ".txt")
                        dialog.dismiss()
                        openFile(title.text.toString() + ".txt")
                    }
                }
            }
        }

    }

    private fun showFiles() {
        val files = File(dir).listFiles()
        filenames = mutableListOf()
        for (i in files) {
            if(i.name.toString().endsWith(".txt"))
                filenames.add(i.name.toString())
        }
        filenames.sort()
        for (i in filenames)
            addCard(i)
    }

    private fun openFile(title: String) {
        val intent = Intent(this, ListEditor::class.java)
        intent.putExtra("title", title)
        startActivity(intent)
    }


    private fun delFile(name: String) {
        File("$dir/$name").delete()
    }


    @SuppressLint("SetTextI18n")
    private fun addCard(namewtxt: String) {
        val name = namewtxt.removeSuffix(".txt")
        val cv = CardView(this)
        cv.alpha = 0.5.toFloat()
        linear_layout.addView(cv)
        val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        p.topMargin = 10
        val cpad = binding.createList.contentPaddingBottom
        cv.setContentPadding(cpad,cpad,cpad,cpad)
        cv.layoutParams = p
        //cv.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        //cv.layoutParams.height = 116
        val tv = TextView(this)
        tv.text = name
        tv.textSize = 22.toFloat()
        tv.setTextColor(resources.getColor(R.color.barca_pink))
        tv.gravity = Gravity.CENTER_VERTICAL
        cv.addView(tv)
        cv.setOnLongClickListener {
            /*val dialog = Dialog(this)
            dialog.setContentView(R.layout.delete_popup)
            val tv = dialog.findViewById<TextView>(R.id.del_title)
            val yes = dialog.findViewById<Button>(R.id.yes_btn)
            val no = dialog.findViewById<Button>(R.id.no_btn)
            val s = "Delete $name ?"
            dialog.show()
            tv.text = s
            no.setOnClickListener { dialog.dismiss() }
            yes.setOnClickListener { delFile(namewtxt)
                linear_layout.removeView(cv)
                dialog.dismiss()
            }*/

            val menu = Dialog(this)
            menu.setContentView(R.layout.list_menu)
            val delCard = menu.findViewById<CardView>(R.id.del_card)
            val shareCard = menu.findViewById<CardView>(R.id.share_card)
            val renameCard = menu.findViewById<CardView>(R.id.rename_card)
            menu.show()

            delCard.setOnClickListener {
                menu.dismiss()
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.delete_popup)
                val tv = dialog.findViewById<TextView>(R.id.del_title)
                val yes = dialog.findViewById<Button>(R.id.yes_btn)
                val no = dialog.findViewById<Button>(R.id.no_btn)
                val s = "Delete $name ?"
                dialog.show()
                tv.text = s
                no.setOnClickListener { dialog.dismiss() }
                yes.setOnClickListener { delFile(namewtxt)
                    linear_layout.removeView(cv)
                    filenames.remove(namewtxt)
                    dialog.dismiss()
                }
            }

            shareCard.setOnClickListener {
                menu.dismiss()
                share(name)
            }

            renameCard.setOnClickListener {
                menu.dismiss()
                val renameDialog = Dialog(this)
                renameDialog.setContentView(R.layout.new_popup)
                val cancel = renameDialog.findViewById<Button>(R.id.cancel_btn)
                val save = renameDialog.findViewById<Button>(R.id.save_btn)
                val et = renameDialog.findViewById<EditText>(R.id.list_title)
                renameDialog.show()
                cancel.setOnClickListener { renameDialog.dismiss() }
                save.setOnClickListener {
                    when {
                        filenames.contains(et.text.toString() + ".txt") -> Toast.makeText(this, "${et.text} already exists !", Toast.LENGTH_LONG).show()
                        et.text.toString() == "" -> Toast.makeText(this, "Title can't be empty !", Toast.LENGTH_LONG).show()
                        et.text.toString().contains(".") -> Toast.makeText(this, "Title can't contain '.' !", Toast.LENGTH_LONG).show()
                        else -> {
                            renameDialog.dismiss()
                            renameFile(name, et.text.toString(), tv)
                            filenames.add(et.text.toString() + ".txt")
                        }
                    }
                }
            }

            true

        }
        cv.setOnClickListener {
            openFile(tv.text.toString() + ".txt")
        }
    }

    private fun share(name: String) {
        val fin = FileReader("$dir/$name.txt")
        var no = 1
        val lines = fin.readLines()
        var msg = ""
        for(i in lines) {
            msg += "$no.  $i\n"
            no++
        }

        val shareIntent = Intent(android.content.Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, name)
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg)
        startActivity(Intent.createChooser(shareIntent, "Share using"))
    }

    private fun renameFile(prev: String, after: String, tv: TextView) {
        tv.text = after

        val file = File("$dir/$prev.txt")
        file.renameTo(File("$dir/$after.txt"))
    }
}


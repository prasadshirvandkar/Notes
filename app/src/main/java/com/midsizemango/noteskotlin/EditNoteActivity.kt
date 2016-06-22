package com.midsizemango.noteskotlin

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast

import java.text.DateFormat
import java.util.*

/**
 * Created by Prasad on 21/06/16.
 */

class EditNoteActivity : AppCompatActivity() {

    var note: Note? = null

    private var databaseHelper: DatabaseHelper? = null
    private var saveButton: FloatingActionButton? = null

    internal var editTitle: EditText? = null
    internal var editContent: EditText? = null
    internal var inputlayoutTitle: TextInputLayout? = null
    internal var inputlayoutContent: TextInputLayout? = null

    val REQUEST_CODE_ADD_NOTE = 4

    private val DATETIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val toolbar = findViewById(R.id.toolbar_edit) as Toolbar?
        setSupportActionBar(toolbar)

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        databaseHelper = DatabaseHelper(applicationContext)

        inputlayoutTitle = findViewById(R.id.inputlayoutTitle) as TextInputLayout?
        inputlayoutContent = findViewById(R.id.inputlayoutContent) as TextInputLayout?
        editTitle = findViewById(R.id.note_title) as EditText
        editContent = findViewById(R.id.note_content) as EditText?

        val bundle = intent.extras
        val s = bundle.getString("edit")

        if (s == "add") {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        } else if (s == "editv") {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }

        note = intent.getSerializableExtra(EXTRA_NOTE) as? Note
        if (note != null) {
            editTitle?.setText(note!!.getTitle())
            editContent?.setText(note!!.getContent())
        } else {
            note = Note()
            note!!.setUpdatedAt(DATETIME_FORMAT.format(Date()));
        }

        saveButton = findViewById(R.id.add_edit_button) as? FloatingActionButton?
        saveButton!!.setOnClickListener {
            if (isNoteFormOk) {
                setNoteResult()
                finish()
            } else
                validateNoteForm()
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu_edit_note, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                onBack()
                return true
            }

            R.id.speech -> {
                try {
                    displaySpeechRecognizer();
                } catch (e: ActivityNotFoundException) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://market.android.com/details?id=com.google.android.googlequicksearchbox"));
                    startActivity(browserIntent);
                }
                return true;
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results[0]
            editContent?.setText(spokenText)
        }
        if (requestCode == REQUEST_CODE_ADD_NOTE) {
            if (resultCode == Activity.RESULT_OK) {
                addNote(data)
            }
        }
    }


    private val isNoteFormOk: Boolean
        get() {
            val title = editTitle?.text.toString()
            return title.trim { it <= ' ' }.length != 0
        }

    private fun validateNoteForm() {
        var msg: String? = null
        if (isNullOrBlank(editTitle?.text.toString())) {
            msg = "Title Required"
            inputlayoutTitle?.error = "Title is Missing"
        }
        if (msg != null) {
            Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
        }
    }

    private fun setNoteResult() {
        note!!.setTitle(editTitle?.text.toString().trim { it <= ' ' })
        note!!.setContent(editContent?.text.toString().trim { it <= ' ' })
        note!!.setUpdatedAt(DATETIME_FORMAT.format(Date()));
        val intent = Intent()
        intent.putExtra(EXTRA_NOTE, note)
        setResult(Activity.RESULT_OK, intent)
        Toast.makeText(this@EditNoteActivity, "Note Saved.", Toast.LENGTH_LONG).show()
    }

    private fun onBack() {
        if (isNoteFormOk) {
            if (editTitle?.text.toString() == note!!.getTitle() && editContent?.text.toString() == note!!.getContent()) {
                setResult(Activity.RESULT_CANCELED, Intent())
                finish()
            } else {
                AlertDialog.Builder(this@EditNoteActivity)
                        .setTitle("Save")
                        .setMessage("Do You Want to Save Note")
                        .setPositiveButton("SAVE") { dialog, which ->
                            setNoteResult()
                            finish()
                }.setNegativeButton("CANCEL") { dialog, which ->
                    setResult(Activity.RESULT_CANCELED, Intent())
                    finish()
                }.show()
            }
        } else {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }
    }

    private fun addNote(data: Intent) {
        val note = data.getSerializableExtra(EXTRA_NOTE) as Note
        val noteId = databaseHelper!!.createNote(note)
        note.setId(noteId)
    }

    override fun onBackPressed() {
        onBack()
        val intentHome = Intent(this@EditNoteActivity, MainActivity::class.java)
        intentHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intentHome.putExtra(EXTRA_NOTE, note)
        setResult(Activity.RESULT_OK, intentHome)
    }

    companion object {

        private val EXTRA_NOTE = "EXTRA_NOTE"
        private val SPEECH_REQUEST_CODE = 0

        fun isNullOrBlank(str: String?): Boolean {
            return str == null || str.trim { it <= ' ' }.length == 0
        }
    }
}
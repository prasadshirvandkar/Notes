package com.midsizemango.noteskotlin

import android.app.Activity
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast

import java.util.Locale
import java.util.regex.Pattern

/**
 * Created by Prasad on 21/06/16.
 */

class ViewNoteActivity : AppCompatActivity() {

    private var textUpdated: TextView? = null
    private var textContent: TextView? = null
    private var editButton: FloatingActionButton? = null
    private var cardView: CardView? = null
    private var note: Note? = null
    private var notesData: List<Note>? = null
    private var databaseHelper: DatabaseHelper? = null

    internal var tts: TextToSpeech? = null
    internal var ttsContent: String? = null
    internal var titlen: String? = null
    internal var collapsingToolbar: CollapsingToolbarLayout? = null
    internal var toolbar: Toolbar? = null
    internal var coordinatorLayout: CoordinatorLayout? = null
    internal var edit = "editv"

    var EXTRA_NOTE: String = "EXTRA_NOTE"
    val REQUEST_CODE_EDIT_NOTE = 2
    val RESULT_CODE_DELETE_NOTE = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        databaseHelper = DatabaseHelper(applicationContext)
        notesData = databaseHelper!!.getAllNotes()

        note = intent.getSerializableExtra(EXTRA_NOTE) as Note

        collapsingToolbar = findViewById(R.id.collapsingToolbar) as CollapsingToolbarLayout?
        collapsingToolbar?.title = note!!.getTitle()
        collapsingToolbar?.setExpandedTitleColor(Color.WHITE)

        toolbar = findViewById(R.id.toolbar_collapse) as Toolbar?
        setSupportActionBar(toolbar)
        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        coordinatorLayout = findViewById(R.id.clayoutcl) as CoordinatorLayout?

        databaseHelper = DatabaseHelper(applicationContext)

        textContent = findViewById(R.id.note_content) as TextView?
        textUpdated = findViewById(R.id.textUpdated) as TextView?
        textUpdated!!.text = note!!.getUpdatedAt()

        textContent!!.movementMethod = LinkMovementMethod.getInstance()

        val hashText = SpannableString(note!!.getContent())
        val matcher = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(hashText)
        while (matcher.find()) {
            hashText.setSpan(ForegroundColorSpan(Color.parseColor("#FF5722")), matcher.start(), matcher.end(), 0)
        }
        textContent!!.text = hashText
        ttsContent = textContent!!.text.toString()
        titlen = collapsingToolbar?.title!!.toString()

        cardView = findViewById(R.id.content) as CardView?

        editButton = findViewById(R.id.edit_note_button) as FloatingActionButton?
        editButton!!.setOnClickListener { editNote() }

        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.language = Locale.UK
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d("ViewNoteActivity", "onActivityResult")
        if (requestCode == REQUEST_CODE_EDIT_NOTE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("ViewNoteActivity", "RESULT_OK")
                note = data.getSerializableExtra(EXTRA_NOTE) as Note
                collapsingToolbar?.title = note!!.getTitle()
                textContent!!.text = note!!.getContent()
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            updateNote(data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.menu_view_note, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.delete_note -> {
                deleteNote();
                return true;
            }

            R.id.share_note -> {
                shareNote();
                return true;
            }

            R.id.copy_note -> {
                val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = textContent!!.text;
                Toast.makeText(applicationContext, "Content Copied", Toast.LENGTH_SHORT).show();
                return true;
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateNote(data: Intent) {
        val updatedNote = data.getSerializableExtra(EXTRA_NOTE) as Note
        databaseHelper!!.updateNote(updatedNote)
        for (note in notesData!!) {
            if (note.getId() == updatedNote.getId()) {
                note.setTitle(updatedNote.getTitle())
                note.setContent(updatedNote.getContent())
                note.setUpdatedAt(updatedNote.getUpdatedAt()!!)
            }
        }
    }

    public override fun onPause() {
        super.onPause()
        val intentHome = Intent(this, ViewNoteActivity::class.java)
        intentHome.flags = Intent.FLAG_ACTIVITY_TASK_ON_HOME
        intentHome.putExtra(EXTRA_NOTE, note)
        setResult(Activity.RESULT_OK, intentHome)
    }

    override fun onBackPressed() {
        val intentHome = Intent(this, MainActivity::class.java)
        intentHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intentHome.putExtra(EXTRA_NOTE, note)
        setResult(Activity.RESULT_OK, intentHome)
        finish()
    }

    private fun editNote() {
        val intent = Intent(this@ViewNoteActivity, EditNoteActivity::class.java)
        intent.putExtra(EXTRA_NOTE, note)
        intent.putExtra("edit", edit)
        startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE)
    }

    fun deleteNote() {

        AlertDialog.Builder(this@ViewNoteActivity).setTitle("Delete").setMessage("Do You Want to Delete the Note?").setPositiveButton("delete") { dialog, which ->
            val intentHome = Intent(this@ViewNoteActivity, MainActivity::class.java)
            intentHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intentHome.putExtra(EXTRA_NOTE, note)
            setResult(RESULT_CODE_DELETE_NOTE, intentHome)
            finish()
        }.setNegativeButton("cancel") { dialog, which -> }.show()

    }

    private fun shareNote() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Title: " + note!!.getTitle() + "\nContent: " + note!!.getContent())
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, "Share Via"))
    }

}

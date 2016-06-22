package com.midsizemango.noteskotlin


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

/**
 * Created by Prasad on 21/06/16.
 */

class MainActivity : AppCompatActivity() {

    var notesData: MutableList<Note>? = null

    private var textEmpty: TextView? = null
    private var textEmpty1: TextView? = null
    private var notesAdapter: NotesAdapter? = null
    private var databaseHelper: DatabaseHelper? = null
    private val selected = -1
    private var recyclerView: RecyclerView? = null

    internal var STAGGER_CONTENT = "EXTRA CONTENT"
    internal var add = "add"

    val REQUEST_CODE_VIEW_NOTE = 1
    val REQUEST_CODE_ADD_NOTE = 4
    val RESULT_CODE_DELETE_NOTE = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = applicationContext

        val toolbar = findViewById(R.id.toolbar_main) as Toolbar?
        setSupportActionBar(toolbar)

        databaseHelper = DatabaseHelper(applicationContext)
        textEmpty = findViewById(R.id.textEmpty) as TextView?
        textEmpty1 = findViewById(R.id.textEmpty1) as TextView?

        val fab = findViewById(R.id.fab) as FloatingActionButton?

        recyclerView = findViewById(R.id.listNotes) as RecyclerView?
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        setupNotesAdapter()
        updateView()

        fab?.setOnClickListener {
            val intent = Intent(this@MainActivity, EditNoteActivity::class.java)
            intent.putExtra("edit", add);
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
        }
        setListeners()
    }

    private fun setListeners() {

        RecyclerListener(recyclerView!!).setOnItemClickListener(
                object: RecyclerListener.OnItemClickListener {
                    override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View?) {
                        val note: Note = notesData!!.get(position);
                        val intentn = Intent(this@MainActivity, ViewNoteActivity::class.java)
                        intentn.putExtra(EXTRA_NOTE, note);
                        startActivityForResult(intentn, REQUEST_CODE_VIEW_NOTE);
                        intentn.putExtra(STAGGER_CONTENT, note);
                    }
                })
                .setOnItemLongClickListener(
                        object: RecyclerListener.OnItemLongClickListener {
                            override fun onItemLongClicked(recyclerView: RecyclerView, position: Int, v: View?): Boolean {
                                val note1: Note = notesData!!.get(position);
                                AlertDialog.Builder(this@MainActivity).setTitle("Delete").setMessage("Do You Want to Delete the Note?").setPositiveButton("delete") { dialog, which ->
                                    databaseHelper!!.deleteNote(note1)
                                    notesData!!.remove(note1)
                                    updateView()
                                    notesAdapter!!.notifyDataSetChanged()
                                }.setNegativeButton("cancel") { dialog, which -> }.show()
                                return true
                        }
         })
    }

    override fun onResume() {
        super.onResume()
        notesAdapter!!.notifyDataSetChanged()
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt(MENU_SELECTED, selected)
        super.onSaveInstanceState(savedInstanceState)
    }

    private fun setupNotesAdapter() {
        notesData = databaseHelper?.getAllNotes()
        notesAdapter = NotesAdapter(notesData!!, context!!, this@MainActivity)
        recyclerView!!.adapter = notesAdapter
    }

    private fun updateView() {
        if (notesData!!.isEmpty()) {
            recyclerView!!.visibility = View.GONE
            textEmpty!!.visibility = View.VISIBLE
            textEmpty1!!.visibility = View.VISIBLE
        } else {
            recyclerView!!.visibility = View.VISIBLE
            textEmpty!!.visibility = View.GONE
            textEmpty1!!.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_CODE_VIEW_NOTE) {
            if (resultCode == Activity.RESULT_OK) {
                updateNote(data)
                notesAdapter!!.notifyDataSetChanged()
            } else if (resultCode == RESULT_CODE_DELETE_NOTE) {
                deleteNote(data)
            }
        }
        if (requestCode == REQUEST_CODE_ADD_NOTE) {
            if (resultCode == Activity.RESULT_OK) {
                val note = data.getSerializableExtra(EXTRA_NOTE) as Note
                val noteId = databaseHelper!!.createNote(note)
                note.setId(noteId)
                notesData!!.add(0, note)
                updateView()
                notesAdapter!!.notifyDataSetChanged()
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                addNote(data)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun addNote(data: Intent) {
        val note = data.getSerializableExtra(EXTRA_NOTE) as Note
        notesData!!.add(0, note)
        updateView()
        notesAdapter!!.notifyDataSetChanged()
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
        notesAdapter!!.notifyDataSetChanged()
    }

    private fun deleteNote(data: Intent) {
        val deletedNote = data.getSerializableExtra(EXTRA_NOTE) as Note
        databaseHelper!!.deleteNote(deletedNote)
        notesData?.remove(deletedNote)
        updateView()
        notesAdapter?.notifyDataSetChanged()
        Toast.makeText(this@MainActivity, "Note Deleted.", Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.about -> {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(R.string.about)
                    .setMessage(R.string.desc)
                    .setPositiveButton("OKAY"){dialog,which ->
                        dialog.dismiss()
                    }.setNeutralButton("My Apps"){dialog, which ->
                        val intentp = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=7907638907779209421"))
                        startActivity(intentp)
                    }.show()

            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    companion object {
        var context: Context? = null
        private val MENU_SELECTED = "selected"
        private val EXTRA_NOTE = "EXTRA_NOTE"
    }

}
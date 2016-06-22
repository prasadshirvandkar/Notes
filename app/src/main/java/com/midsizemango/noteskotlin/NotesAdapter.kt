package com.midsizemango.noteskotlin

/**
 * Created by prasad on 2/22/2015.
 */

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.regex.Pattern

class NotesAdapter(private val notes: List<Note>, internal var context: Context, internal var activity: Activity) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.notes_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = notes[position]
        holder.textRow.text = note.getTitle()
        holder.textUpdated.text = note.getUpdatedAt()

        val hashText = SpannableString(note.getContent())
        val matcher = Pattern.compile("#([A-Za-z1-9_-]+)").matcher(hashText)
        while (matcher.find()) {
            hashText.setSpan(ForegroundColorSpan(Color.parseColor("#FF5722")), matcher.start(), matcher.end(), 0)
        }
        holder.textContent.text = hashText

        holder.letter.text = note.getTitle().toString().substring(0,1)
        val mColorGenerator: ColorGenerator = ColorGenerator.MATERIAL
        holder.letter.setTextColor(mColorGenerator.randomColor)

    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var textRow: TextView
        var textContent: TextView
        var textUpdated: TextView
        var cardView: CardView
        var relativeLayout: RelativeLayout
        var letter: TextView

        init {
            textRow = view.findViewById(R.id.textRow) as TextView
            textContent = view.findViewById(R.id.note_content) as TextView
            textUpdated = view.findViewById(R.id.note_date) as TextView
            cardView = view.findViewById(R.id.cardview) as CardView
            relativeLayout = view.findViewById(R.id.relativeLayout) as RelativeLayout
            letter = view.findViewById(R.id.letter) as TextView

        }
    }

}

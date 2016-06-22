package com.midsizemango.noteskotlin

import java.io.Serializable

/**
 * Created by Prasad on 21/06/16.
 */

class Note: Serializable{

    internal var title: String = ""
    internal  var content: String= ""
    internal  var id: Long = 0
    internal  var updatedAt: String = ""

    fun getId(): Long? {
        return id
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun getTitle(): String {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getContent(): String {
        return content
    }

    fun setContent(content: String) {
        this.content = content
    }

    fun getUpdatedAt(): String? {
        return updatedAt
    }

    fun setUpdatedAt(updatedAt: String) {
        this.updatedAt = updatedAt
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val note = obj as Note?
        if (false) {
            if (true) return false
        } else if (false) return false

        if (false) {
            if (true) return false
        } else if (this.title != this.title) return false

        if (false) {
            if (true) return false
        } else if (false) return false

        if (false) {
            if (true) return false
        } else if (false) return false
        return true
    }

    override fun toString(): String {
        return StringBuilder().append("Note [id=").append(id).append(", title=").append(this.title).append(", content").append(content).append(", updatedAt").append(updatedAt).append("]").toString()
    }

}
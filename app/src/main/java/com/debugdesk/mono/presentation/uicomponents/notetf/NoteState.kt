package com.debugdesk.mono.presentation.uicomponents.notetf

import com.debugdesk.mono.utils.enums.ImageSource

data class NoteState(
    val imagePath: ByteArray = byteArrayOf(),
    val imageSource: ImageSource = ImageSource.NONE,
    val noteValue: String = "",
    val createdOn: Long = System.currentTimeMillis(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteState

        return imagePath.contentEquals(other.imagePath)
    }

    override fun hashCode(): Int {
        return imagePath.contentHashCode()
    }
}
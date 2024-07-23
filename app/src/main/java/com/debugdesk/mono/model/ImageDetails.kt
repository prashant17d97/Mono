package com.debugdesk.mono.model

import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.formatFileSize
import com.google.gson.annotations.SerializedName

data class ImageDetails(
    @SerializedName("absolutePath")
    val absolutePath: String,
    @SerializedName("fileName")
    val fileName: String,
    @SerializedName("fileSize")
    val fileSize: Long,
    @SerializedName("isEmpty")
    val isEmpty: Boolean,
    @SerializedName("lastModified")
    val lastModified: String,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int,
    @SerializedName("orientation")
    val orientation: String,
) {
    val size: String get() = fileSize.formatFileSize()

    override fun toString(): String {
        return "absolutePath = $absolutePath ," +
            "fileName = $fileName ," +
            "fileSize = $size ," +
            "lastModified = $lastModified ," +
            "width = $width ," +
            "height = $height ," +
            "orientation = $orientation ," +
            "isEmpty = $isEmpty"
    }
}

val emptyImageDetails =
    ImageDetails(
        absolutePath = "",
        fileName = "",
        fileSize = 0,
        lastModified = "",
        width = 0,
        height = 0,
        orientation = "",
        isEmpty = true,
    )

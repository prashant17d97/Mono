package com.debugdesk.mono.domain.data.local.localdatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.formatFileSize
import com.debugdesk.mono.utils.enums.ImageFrom
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "transaction_images",
)
data class TransactionImage(
    @PrimaryKey(autoGenerate = true)
    val imageId: Int = 0,
    @SerializedName("transactionId")
    val transactionId: Int,
    @SerializedName("absolutePath")
    val absolutePath: String,
    @SerializedName("transactionUniqueId")
    val transactionUniqueId: String,
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
    @SerializedName("from")
    val from: String
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

val emptyTransactionImageDetail = TransactionImage(
    imageId = 0,
    transactionId = 0,
    absolutePath = "",
    fileName = "",
    fileSize = 0,
    lastModified = "",
    width = 0,
    height = 0,
    orientation = "",
    transactionUniqueId = "",
    isEmpty = true,
    from = ImageFrom.CAMERA.name
)
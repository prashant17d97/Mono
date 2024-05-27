package com.debugdesk.mono.domain.data.local.localdatabase.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.debugdesk.mono.R


@Entity(
    tableName = "categoryModel",
)
data class CategoryModel(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val category: String = "",
    val categoryIcon: Int? = null,
    val categoryType: String? = null,
    var isSelected: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(categoryId)
        parcel.writeString(category)
        parcel.writeInt(categoryIcon ?: R.drawable.mono)
        parcel.writeString(categoryType)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryModel> {
        override fun createFromParcel(parcel: Parcel): CategoryModel {
            return CategoryModel(parcel)
        }

        override fun newArray(size: Int): Array<CategoryModel?> {
            return arrayOfNulls(size)
        }
    }
}
package com.debugdesk.mono.domain.data.local.localdatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey


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
)
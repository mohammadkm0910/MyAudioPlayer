package com.mohammadkk.myaudioplayer.extension

import android.database.Cursor


internal fun Cursor.getIntVal(columnName: String): Int {
    return getInt(getColumnIndexOrThrow(columnName))
}
internal fun Cursor.getLongVal(columnName: String): Long {
    return getLong(getColumnIndexOrThrow(columnName))
}
internal fun Cursor.getStringVal(columnName: String): String {
    return getString(getColumnIndexOrThrow(columnName))
}
internal fun Cursor.getStringOrNullVal(columnName: String): String? {
    return getString(getColumnIndexOrThrow(columnName))
}
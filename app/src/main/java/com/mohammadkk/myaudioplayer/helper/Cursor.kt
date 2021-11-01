package com.mohammadkk.myaudioplayer.helper

import android.database.Cursor


@Suppress("HasPlatformType")
fun Cursor.getStringValue(key: String) = getString(getColumnIndexOrThrow(key))
fun Cursor.getLongValue(key: String) = getLong(getColumnIndexOrThrow(key))
fun Cursor.getIntValue(key: String) = getInt(getColumnIndexOrThrow(key))
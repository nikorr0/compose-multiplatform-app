package org.company.app.session

import android.content.Context
import java.io.File

actual class FileOperator(private val context: Context) {
    actual fun writeText(fileName: String, text: String) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE)
            .use { it.write(text.toByteArray()) }
    }
    actual fun readText(fileName: String): String? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) file.readText() else null
    }
}
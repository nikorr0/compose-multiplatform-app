package org.company.app.session
import java.io.File

actual class FileOperator {
    private val baseDir: File = File(System.getProperty("user.home"), ".myapp").apply {
        if (!exists()) mkdirs()
    }

    actual fun writeText(fileName: String, text: String) {
        val file = File(baseDir, fileName)
        file.writeText(text)
    }

    actual fun readText(fileName: String): String? {
        val file = File(baseDir, fileName)
        return if (file.exists() && file.length() > 0) file.readText() else null
    }
}

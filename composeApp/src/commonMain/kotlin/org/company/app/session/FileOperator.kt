package org.company.app.session

expect class FileOperator {
    fun writeText(fileName: String, text: String)
    fun readText(fileName: String): String?
}

const val SESSION_FILE_NAME = "session.json"
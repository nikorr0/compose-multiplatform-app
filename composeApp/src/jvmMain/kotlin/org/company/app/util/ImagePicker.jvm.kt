package org.company.app.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.coroutines.resume

/**
 * Desktop-реализация выбора картинки.
 * Показывает JFileChooser и возвращает Pair(bytes, mime) либо null.
 */
actual suspend fun CoroutineScope.pickImage(): Pair<ByteArray, String>? =
    suspendCancellableCoroutine { cont ->

        // Весь Swing-UI должен выполняться на EDT
        SwingUtilities.invokeLater {
            val chooser = JFileChooser().apply {
                isMultiSelectionEnabled = false
                fileFilter = FileNameExtensionFilter(
                    "Images (*.png, *.jpg, *.jpeg)",
                    "png", "jpg", "jpeg"
                )
            }

            val result = chooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                val bytes = file.readBytes()
                val mime  = when (file.extension.lowercase()) {
                    "png"       -> "image/png"
                    "jpg", "jpeg" -> "image/jpeg"
                    else          -> "application/octet-stream"
                }
                cont.resume(bytes to mime)          // успех
            } else {
                cont.resume(null)                   // отмена
            }
        }
    }
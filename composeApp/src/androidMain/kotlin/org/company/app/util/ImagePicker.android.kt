package org.company.app.util

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Opens the platform image picker and returns Pair(bytes, mime) or null if cancelled.
 *
 * Call from any coroutine, e.g.
 *   val img = rememberCoroutineScope().pickImage()
 */
actual suspend fun CoroutineScope.pickImage(): Pair<ByteArray, String>? =
    suspendCancellableCoroutine { cont ->

        /* 1️⃣  Grab the visible activity as ComponentActivity */
        val activity = ActivityProvider.currentActivity as? ComponentActivity
        if (activity == null) {                // no window in foreground
            cont.resume(null); return@suspendCancellableCoroutine
        }

        /* 2️⃣  Register once-off launcher */
        lateinit var launcher: androidx.activity.result.ActivityResultLauncher<String>
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri == null)                       // user cancelled
                cont.resume(null)
            else                                   // user picked a file
                activity.contentResolver.openInputStream(uri)?.use { stream ->
                    val bytes = stream.readBytes()
                    val mime  = activity.contentResolver.getType(uri) ?: "image/*"
                    cont.resume(bytes to mime)
                } ?: cont.resume(null)

            launcher.unregister()                  // cleanup
        }

        /* 3️⃣  Launch the picker */
        launcher.launch("image/*")

        /* 4️⃣  If the coroutine is canceled, also unregister */
        cont.invokeOnCancellation { launcher.unregister() }
    }
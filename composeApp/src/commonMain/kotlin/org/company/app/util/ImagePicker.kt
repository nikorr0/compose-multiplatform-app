package org.company.app.util

import kotlinx.coroutines.CoroutineScope

expect suspend fun CoroutineScope.pickImage(): Pair<ByteArray, String>?

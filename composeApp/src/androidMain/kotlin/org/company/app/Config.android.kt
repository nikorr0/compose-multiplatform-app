package org.company.app

actual object Config {
    actual val host: String
        get() = "http://10.0.2.2:8000"
}
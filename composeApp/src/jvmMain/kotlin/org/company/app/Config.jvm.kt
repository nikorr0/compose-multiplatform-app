package org.company.app

actual object Config {
    actual val host: String
        get() = "http://localhost:8000"
}
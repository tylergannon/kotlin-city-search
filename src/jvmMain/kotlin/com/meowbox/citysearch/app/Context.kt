package com.meowbox.citysearch.app


actual abstract class Context

internal class JvmContext private constructor(): Context() {
    companion object {
        val singleton = JvmContext()
    }
}

package com.meowbox.citysearch.util

fun interface Reader {
    fun readLine(): String?
}

fun<T> tsvReader(
    reader: Reader,
    transform: (List<String>) -> T
) = object : Iterable<T> {
    override fun iterator() = object : Iterator<T> {

        private var nextLine: List<String>?
        init {
            nextLine = readNext()
        }

        override fun hasNext() = nextLine != null

        override fun next() = transform(nextLine!!).also { nextLine = readNext() }

        private fun readNext() = reader.readLine()?.split('\t')
    }
}

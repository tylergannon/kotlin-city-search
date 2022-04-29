package com.meowbox.citysearch.util

fun interface Reader {
    fun readLine(): String?
}

fun<T> tsvReader(
    reader: Reader,
    transform: (List<String>) -> T
) = object : Iterable<T> {
    override fun iterator() = object : Iterator<T> {
        private var nextLine = readNext()
        override fun hasNext() = nextLine != null
        private fun readNext() = reader.readLine()?.split('\t')
        override fun next() = transform(nextLine!!).also { nextLine = readNext() }
    }
}

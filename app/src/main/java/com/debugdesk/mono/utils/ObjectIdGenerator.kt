package com.debugdesk.mono.utils

class ObjectIdGenerator(private val length: Int = 16) {
    private val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_"

    companion object {
        private val instance = ObjectIdGenerator()

        fun generate(): String {
            return (1..instance.length)
                .map { instance.allowedChars.random() }
                .joinToString("")
        }
    }
}

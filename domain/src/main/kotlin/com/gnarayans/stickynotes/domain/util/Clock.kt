package com.gnarayans.stickynotes.domain.util

/**
 * Abstraction over the system clock so time-dependent logic stays testable.
 */
fun interface Clock {
    fun now(): Long
}

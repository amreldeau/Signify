package com.example.signify

interface Command {
    fun execute()
    fun undo()
}

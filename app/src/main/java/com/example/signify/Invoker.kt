package com.example.signify

class Invoker {
    private val commands = mutableListOf<Command>()

    fun executeCommand(command: Command) {
        command.execute()
        commands.add(command)
    }

    fun undoLastCommand() {
        if (commands.isNotEmpty()) {
            val command = commands.removeLast()
            command.undo()
        }
    }
}
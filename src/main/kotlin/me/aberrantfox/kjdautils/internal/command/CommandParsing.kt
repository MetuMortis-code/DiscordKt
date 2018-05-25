package me.aberrantfox.kjdautils.internal.command


import me.aberrantfox.kjdautils.api.dsl.KJDAConfiguration
import me.aberrantfox.kjdautils.api.dsl.Command
import me.aberrantfox.kjdautils.api.dsl.arg

data class CommandStruct(val commandName: String, val commandArgs: List<String> = listOf())

fun cleanCommandMessage(message: String, config: KJDAConfiguration): CommandStruct {
    var trimmedMessage = message.substring(config.prefix.length)

    if (trimmedMessage.startsWith(config.prefix)) trimmedMessage = trimmedMessage.substring(config.prefix.length)

    if (!message.contains(" ")) {
        return CommandStruct(trimmedMessage.toLowerCase())
    }

    val commandName = trimmedMessage.substring(0, trimmedMessage.indexOf(" ")).toLowerCase()
    val commandArgs = trimmedMessage.substring(trimmedMessage.indexOf(" ") + 1).split(" ")

    return CommandStruct(commandName, commandArgs)
}

fun getArgCountError(actual: List<String>, cmd: Command): String? {
    val optionalCount = cmd.expectedArgs.count { it.optional }
    val validRange = (cmd.parameterCount - optionalCount) .. cmd.parameterCount

    val manual = cmd.expectedArgs
            .map { it.type }
            .any { it == Manual }

    if (manual) return null

    val hasMultipleArg = cmd.expectedArgs
            .map { it.type.consumptionType }
            .any { it in listOf(ConsumptionType.Multiple, ConsumptionType.All) }

    if (hasMultipleArg) {
        if (actual.size < validRange.start) {
            return "This command requires at least ${validRange.start} argument(s)"
        }
    } else if (actual.size !in validRange) {
        return if (validRange.start == validRange.endInclusive) {
            "This command requires ${validRange.start} argument(s)."
        } else {
            "This command requires between ${validRange.start} and ${validRange.endInclusive} arguments."
        }
    }

    return null
}
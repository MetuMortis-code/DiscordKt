package me.jakejmattson.discordkt.api.dsl.command

import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.internal.utils.Responder

/**
 * Data class containing the raw information from the command execution.
 *
 * @property rawMessageContent The message as it was sent from the user - no modifications.
 * @property commandName The command name parses from the raw content. This is not necessarily a valid command.
 * @property commandArgs The arguments provided to the command execution.
 * @property prefixCount The number of prefixes used to invoke this command.
 */
data class RawInputs(
    val rawMessageContent: String,
    val commandName: String,
    val commandArgs: List<String> = listOf(),
    val prefixCount: Int
)

/**
 * The discord context of the command execution.
 *
 * @property discord The [Discord] instance.
 * @property message The Message that invoked this command.
 * @property guild The Guild this command was invoked in.
 * @property author The User who invoked this command.
 * @property channel The MessageChannel this command was invoked in.
 * @property relevantPrefix The prefix used to invoke this command.
 */
data class DiscordContext(override val discord: Discord,
                          val message: Message,
                          val guild: Guild?,
                          val author: User = message.author!!,
                          override val channel: MessageChannel = message.channel as MessageChannel) : Responder {
    val relevantPrefix: String = discord.configuration.prefix.invoke(this)
}

/**
 * A command execution event containing the [RawInputs] and the relevant [DiscordContext].
 *
 * @param rawInputs The [RawInputs] of the command.
 *
 * @property discord The [Discord] instance.
 * @property author The User who invoked this command.
 * @property message The Message that invoked this command.
 * @property channel The MessageChannel this command was invoked in.
 * @property guild The Guild this command was invoked in.
 * @property command The [Command] that is resolved from the invocation.
 * @property relevantPrefix The prefix used to invoke this command.
 * @property args The [GenericContainer] containing the converted input.
 */
data class CommandEvent<T : GenericContainer>(val rawInputs: RawInputs,
                                              private val discordContext: DiscordContext) : Responder {
    override val discord = discordContext.discord
    val author = discordContext.author
    val message = discordContext.message
    override val channel = discordContext.channel
    val guild = discordContext.guild
    val command = discord.commands[rawInputs.commandName]
    val relevantPrefix = discordContext.relevantPrefix

    lateinit var args: T

    /**
     * Clone this event with optional modifications.
     */
    fun cloneToGeneric(input: RawInputs = rawInputs, context: DiscordContext = discordContext) = CommandEvent<GenericContainer>(input, context)
}
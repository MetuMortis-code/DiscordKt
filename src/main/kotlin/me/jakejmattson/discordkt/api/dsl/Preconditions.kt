@file:Suppress("unused")

package me.jakejmattson.discordkt.api.dsl

/**
 * A class that represents some condition that must Pass before a command can be executed.
 *
 * @param priority The relative priority of this precondition being run.
 */
abstract class Precondition(val priority: Int = 5) {
    /**
     * A function that will either [Pass] or [Fail].
     */
    abstract suspend fun evaluate(event: CommandEvent<*>): PreconditionResult
}

/** @suppress Redundant doc */
interface PreconditionResult

/**
 * Object indicating that this precondition has passed.
 */
object Pass : PreconditionResult
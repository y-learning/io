import result.Result
import list.List

class ScriptReader : Input {
    private val commands: List<String>

    constructor(commands: List<String>) : super() {
        this.commands = commands
    }

    constructor(vararg commands: String) : super() {
        this.commands = List(*commands)
    }

    override fun readString(): Result<Pair<String, Input>> = when {
        commands.isEmpty() -> Result.failure("Not enough entries in script")
        else -> Result(Pair(commands.first(), ScriptReader(commands.rest())))
    }

    override fun readInt(): Result<Pair<Int, Input>> = try {
        when {
            commands.isEmpty() -> Result.failure("Not enough entries in script")
            parse(commands.first()) >= 0 ->
                Result(Pair(parse(commands.first()),
                            ScriptReader(commands.rest())))
            else -> Result()
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    companion object {
        private fun parse(str: String): Int = Integer.parseInt(str)
    }

    override fun close() {}
}

import result.Result

import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.lang.System.`in`


interface Input : Closeable {
    fun readString(): Result<Pair<String, Input>>

    fun readInt(): Result<Pair<Int, Input>>

    fun readString(message: String): Result<Pair<String, Input>> = readString()

    fun readInt(message: String): Result<Pair<Int, Input>> = readInt()
}

abstract class Reader(private val reader: BufferedReader) : Input {
    private fun <T> read(parse: (String) -> T): Result<Pair<T, Input>> =
            reader.readLine().let {
                when {
                    it.isEmpty() -> Result()
                    else -> Result(Pair(parse(it), this))
                }
            }

    override fun readString(): Result<Pair<String, Input>> = try {
        read { it }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun readInt(): Result<Pair<Int, Input>> = try {
        read { it.toInt() }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun close() = reader.close()
}

class ConsoleReader(reader: BufferedReader) : Reader(reader) {
    override fun readString(message: String): Result<Pair<String, Input>> {
        print("$message ")
        return readString()
    }

    override fun readInt(message: String): Result<Pair<Int, Input>> {
        print("$message ")
        return readInt()
    }

    companion object {
        operator fun invoke(): ConsoleReader =
                ConsoleReader(BufferedReader(InputStreamReader(`in`)))
    }
}
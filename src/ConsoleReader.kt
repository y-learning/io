import result.Result
import java.io.BufferedReader
import java.io.InputStreamReader

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
                ConsoleReader(BufferedReader(InputStreamReader(System.`in`)))
    }
}
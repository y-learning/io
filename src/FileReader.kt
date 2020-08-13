import result.Result

import java.io.BufferedReader
import java.io.File

class FileReader private constructor(private val reader: BufferedReader) :
        Reader(reader), AutoCloseable {

    override fun close() {
        reader.close()
    }

    companion object {
        operator fun invoke(path: String): Result<FileReader> = try {
            Result(FileReader(File(path).bufferedReader()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
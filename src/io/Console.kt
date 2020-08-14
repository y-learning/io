package io

import result.Result
import java.io.BufferedReader
import java.io.InputStreamReader

object Console {
    private val br = BufferedReader(InputStreamReader(System.`in`))

    fun readln(): Input<Result<String>> = Input {
        try {
            Result(br.readLine())
        } catch (e: Exception) {
            Result.failure<String>(e)
        }
    }

    fun println(o: Any): Input<Unit> = Input { kotlin.io.println(o.toString()) }

    fun print(o: Any): Input<Unit> = Input { kotlin.io.print(o.toString()) }
}
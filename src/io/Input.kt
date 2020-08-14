package io

import result.Result
import java.io.BufferedReader
import java.io.InputStreamReader

class Input<out A>(private val f: () -> A) {

    operator fun invoke() = f()

    fun <B> map(g: (A) -> B): Input<B> = Input { g(f()) }

    companion object {
        val empty: Input<Unit> = Input {}

        operator fun <A> invoke(a: A): Input<A> = Input { a }
    }
}

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
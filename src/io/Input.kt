package io

class Input<out A>(private val f: () -> A) {

    operator fun invoke() = f()

    fun <B> map(g: (A) -> B): Input<B> = Input { g(f()) }

    companion object {
        val empty: Input<Unit> = Input {}

        operator fun <A> invoke(a: A): Input<A> = Input { a }
    }
}
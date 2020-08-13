package io

class Input<out A>(private val f: () -> A) {

    operator fun invoke() = f()

    companion object {
        val empty: Input<Unit> = Input {}

        operator fun <A> invoke(a: A): Input<A> = Input { a }
    }
}
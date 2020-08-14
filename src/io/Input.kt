package io

import list.List
import stream.Lazy
import stream.Stream

class Input<out A>(private val f: () -> A) {

    operator fun invoke() = f()

    fun <B> map(g: (A) -> B): Input<B> = Input { g(f()) }

    fun <B> flatMap(g: (A) -> Input<B>): Input<B> = g(f())

    companion object {

        val empty: Input<Unit> = Input {}

        operator fun <A> invoke(a: A): Input<A> = Input { a }

        fun <A> repeat(n: Int, input: Input<A>): Input<List<A>> =
                Stream.fill(n, Lazy { input })
                        .foldRight(Lazy { Input { List<A>() } })
                        { inA: Input<A> ->
                            { acc: Lazy<Input<List<A>>> ->
                                map2(inA, acc()) { a ->
                                    { list: List<A> -> list.cons(a) }
                                }
                            }
                        }

        fun <A, B, C> map2(inputA: Input<A>,
                           inputB: Input<B>,
                           f: (A) -> (B) -> C): Input<C> =
                inputA.flatMap { a -> inputB.map { b: B -> f(a)(b) } }

        fun <A> doWhile(input: Input<A>, f: (A) -> Input<Boolean>):
                Input<Unit> =
                input.flatMap { f(it) }
                        .flatMap { b: Boolean ->
                            when {
                                b -> doWhile(input, f)
                                else -> empty
                            }
                        }
    }

}

package stream

import result.Result
import list.List

sealed class Stream<out E> {

    abstract fun first(): Result<E>

    abstract fun rest(): Result<Stream<E>>

    abstract fun isEmpty(): Boolean

    abstract fun takeAtMost(n: Int): Stream<E>

    abstract fun takeWhile(p: (E) -> Boolean): Stream<E>

    abstract fun <U> foldRight(acc: Lazy<U>, f: (E) -> (Lazy<U>) -> U): U

    fun dropAtMost(n: Int): Stream<E> = dropAtMost(n, this)

    fun dropWhile(p: (E) -> Boolean): Stream<E> = dropWhile(this, p)

    fun <T> repeat(f: () -> T): Stream<T> =
            Cons(Lazy { f() }, Lazy { repeat(f) })

    fun toList(): List<E> = toList(this)

    fun exists(p: (E) -> Boolean): Boolean = exists(this, p)

    fun takeWhileViaFoldRight(p: (E) -> Boolean): Stream<E> =
            foldRight(Lazy<Stream<E>> { Empty }) { item: E ->
                { lStream: Lazy<Stream<E>> ->
                    if (p(item))
                        Cons(Lazy { item }, lStream)
                    else
                        lStream()
                }
            }

    fun firstSafe(): Result<E> =
            foldRight(Lazy { Result<E>() }) { t: E -> { Result(t) } }

    fun <U> map(f: (E) -> U): Stream<U> = foldRight(Lazy<Stream<U>> { Empty })
    { e: E ->
        { acc: Lazy<Stream<U>> ->
            Cons(Lazy { f(e) }, acc)
        }
    }

    fun filter(p: (E) -> Boolean): Stream<E> = dropWhile { x ->
        !p(x)
    }.let { stream: Stream<E> ->
        when (stream) {
            Empty -> stream
            is Cons -> cons(stream.head, Lazy { stream.tail().filter(p) })
        }
    }

    fun append(stream2: Lazy<Stream<@UnsafeVariance E>>): Stream<E> =
            foldRight(stream2) { e: E ->
                { acc: Lazy<Stream<@UnsafeVariance E>> ->
                    Cons(Lazy { e }, acc)
                }
            }

    fun <U> flatMap(f: (E) -> Stream<U>): Stream<U> =
            foldRight<Stream<U>>(Lazy { Empty }) { e: E ->
                { acc: Lazy<Stream<U>> ->
                    f(e).append(acc)
                }
            }

    fun find(p: (E) -> Boolean): Result<E> = filter(p).first()

    private object Empty : Stream<Nothing>() {
        override fun first(): Result<Nothing> = Result()

        override fun rest(): Result<Nothing> = Result()

        override fun isEmpty(): Boolean = true

        override fun takeAtMost(n: Int): Stream<Nothing> = this

        override fun takeWhile(p: (Nothing) -> Boolean): Stream<Nothing> = this

        override fun <U> foldRight(acc: Lazy<U>,
                                   f: (Nothing) -> (Lazy<U>) -> U): U = acc()
    }

    private data class Cons<E>(val head: Lazy<E>, val tail: Lazy<Stream<E>>)
        : Stream<E>() {

        override fun first(): Result<E> = Result(head())

        override fun rest(): Result<Stream<E>> = Result(tail())

        override fun isEmpty(): Boolean = false

        override fun takeAtMost(n: Int): Stream<E> = when {
            n > 0 -> Cons(head, Lazy { tail().takeAtMost(n - 1) })
            else -> Empty
        }

        override fun takeWhile(p: (E) -> Boolean): Stream<E> = when {
            p(head()) -> Cons(head, Lazy { tail().takeWhile(p) })
            else -> Empty
        }

        override fun <U> foldRight(acc: Lazy<U>, f: (E) -> (Lazy<U>) -> U): U =
                f(head())(Lazy { tail().foldRight(acc, f) })
    }

    companion object {
        operator
        fun <E> invoke(): Stream<E> = Empty

        fun <E> cons(head: Lazy<E>, tail: Lazy<Stream<E>>): Stream<E> =
                Cons(head, tail)

        tailrec fun <E> dropAtMost(n: Int, stream: Stream<E>): Stream<E> =
                if (stream is Cons && n > 0)
                    dropAtMost(n - 1, stream.tail())
                else stream

        tailrec fun <E> dropWhile(stream: Stream<E>,
                                  p: (E) -> Boolean): Stream<E> =
                if (stream is Cons && p(stream.head()))
                    dropWhile(stream.tail(), p)
                else stream

        fun <E> toList(stream: Stream<E>): List<E> {
            tailrec fun <T> toList(stream: Stream<T>, list: List<T>): List<T> =
                    when (stream) {
                        Empty -> list
                        is Cons -> toList(stream.tail(), list.cons(stream.head()))
                    }

            return toList(stream, List()).reverse()
        }

        fun <E> iterate(seed: E, f: (E) -> E): Stream<E> =
                Cons(Lazy { seed }, Lazy { iterate(f(seed), f) })

        fun <E> iterate(seed: Lazy<E>, f: (E) -> E): Stream<E> =
                Cons(seed, Lazy { iterate(f(seed()), f) })

        tailrec fun <E> exists(stream: Stream<E>, p: (E) -> Boolean): Boolean =
                when (stream) {
                    Empty -> false
                    is Cons -> when {
                        p(stream.head()) -> true
                        else -> exists(stream.tail(), p)
                    }
                }

        fun <E, S> unfold(start: S, f: (S) -> Result<Pair<E, S>>): Stream<E> =
                f(start).map { (s, t) ->
                    cons(Lazy { s }, Lazy { unfold(t, f) })
                }.getOrElse(Empty)

        fun from(i: Int): Stream<Int> = unfold(i) { Result(Pair(it, it + 1)) }

        fun <E> fill(n: Int, element: Lazy<E>): Stream<E> {
            tailrec
            fun fill(acc: Stream<E>, n: Int, elem: Lazy<E>): Stream<E> =
                    when {
                        n <= 0 -> acc
                        else -> fill(Cons(elem, Lazy { acc }), n - 1, elem)
                    }

            return fill(Empty, n, element)
        }
    }
}

fun fib(): Stream<Int> = Stream.unfold(Pair(1, 1)) { (x, y) ->
    Result(Pair(x, Pair(y, x + y)))
}
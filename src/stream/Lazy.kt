package stream

import result.Result
import result.map2
import list.List
import list.traverse

import java.lang.RuntimeException

class Lazy<A>(function: () -> A) : () -> A {
    private val memoizedValue: A by lazy(function)

    override fun invoke(): A = memoizedValue

    fun <B> map(f: (A) -> B): Lazy<B> = Lazy { f(memoizedValue) }

    fun <B> flatMap(f: (A) -> Lazy<B>): Lazy<B> = Lazy { f(memoizedValue)() }

    fun forEach(condition: Boolean,
                ifTrue: (A) -> Unit,
                ifFalse: () -> Unit = {}) =
            if (condition) ifTrue(memoizedValue)
            else ifFalse()

    fun forEach(condition: Boolean,
                ifTrue: () -> Unit = {},
                ifFalse: (A) -> Unit) =
            if (condition) ifTrue()
            else ifFalse(memoizedValue)

    fun forEach(condition: Boolean, ifTrue: (A) -> Unit, ifFalse: (A) -> Unit) =
            if (condition) ifTrue(memoizedValue)
            else ifFalse(memoizedValue)

    companion object {
        fun <A, B, C> lift(f: (A) -> (B) -> C):
                (Lazy<A>) -> (Lazy<B>) -> Lazy<C> = { a ->
            { b ->
                Lazy { f(a())(b()) }
            }
        }
    }
}

fun <A> sequence(list: List<Lazy<A>>): Lazy<List<A>> =
        Lazy { list.map { it() } }

fun <A> toResult(lazyA: Lazy<A>): Result<A> = try {
    Result(lazyA())
} catch (e: Exception) {
    Result.failure(e)
} catch (e: RuntimeException) {
    Result.failure(e)
}

fun <A> sequenceResult1(l: List<Lazy<A>>): Lazy<Result<List<A>>> =
        Lazy { list.sequence(l.map { toResult(it) }) }

fun <A> sequenceResult2(list: List<Lazy<A>>): Lazy<Result<List<A>>> =
        Lazy { traverse(list) { toResult(it) } }

fun <A> sequenceResult3(list: List<Lazy<A>>): Lazy<Result<List<A>>> {
    val p = { acc: Result<List<A>> ->
        acc.map { false }.getOrElse(true)
    }
    return Lazy {
        list.foldLeft(Result(List()), p) { result: Result<List<A>> ->
            { lazyA: Lazy<A> ->
                map2(toResult(lazyA), result) { a: A ->
                    { list: List<A> ->
                        list.cons(a)
                    }
                }
            }
        }
    }
}
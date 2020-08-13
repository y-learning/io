class IO(private val f: () -> Unit) {
    operator fun invoke() = f()
}
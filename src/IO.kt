class IO(private val f: () -> Unit) {
    operator fun invoke() = f()

    operator fun plus(io: IO): IO = IO {
        f()
        io.f()
    }
}
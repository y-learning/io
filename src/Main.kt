fun main() {
    val input = ConsoleReader()

    val rStr = input.readString("Enter your name:").map { it.first }

    val greetingName = rStr.map { "Hello, $it" }

    greetingName.forEach(::println, onFailure = { println(it.message) })

    val rInt = input.readInt("Enter your age:").map { it.first }

    val commentAge = rInt.map { "You look younger than $it : )" }

    commentAge.forEach(::println, onFailure = {
        println("Invalid age. Please enter an integer.")
    })
}
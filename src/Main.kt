import io.Console
import io.IO
import list.List
import result.Result
import stream.Stream

data class Person(val id: Int, val firstName: String, val lastName: String)

fun person(input: Input): Result<Pair<Person, Input>> =
        input.readInt("Enter your ID:").flatMap { id ->
            id.second.readString("Enter your first name:")
                    .flatMap { fName ->
                        fName.second.readString("Enter your last name:")
                                .map { lName ->
                                    Pair(Person(id.first,
                                                fName.first,
                                                lName.first), lName.second)
                                }
                    }
        }


private fun readPeopleFromConsole(): List<Person> =
        Stream.unfold(ConsoleReader(), ::person).toList()

private fun readPeopleFromFile(filename: String): Result<List<Person>> =
        FileReader(filename).map { fileReader ->
            fileReader.use {
                Stream.unfold(it, ::person).toList()
            }
        }

private fun readPeopleFromScript(vararg commands: String): List<Person> =
        Stream.unfold(ScriptReader(*commands), ::person).toList()

fun getName() = "Mickey"

fun sayHello(): io.Input<Unit> = Console.print("Enter your name:")
        .map { Console.readln()() }
        .map { result -> result.map { name -> "Hello, $name!" }.getOrElse("") }
        .map { s -> Console.println(s)() }

fun main() {
//    readPeopleFromConsole().forEach(::println)

    readPeopleFromFile("ppl.txt")
            .forEach({ list ->
                         list.forEach(::println)
                     }, onFailure = ::println)

    println("\n")

    readPeopleFromScript("1", "Mickey", "Mouse",
                         "2", "Minnie", "Mouse").forEach(::println)

    val instruction1 = IO { print("Hello, ") }
    val instruction2 = IO { print(getName()) }
    val instruction3 = IO { print("!\n") }

    val instructions = List(instruction1, instruction2, instruction3)

    val script1: IO = instructions.foldRight(IO.empty) { io -> { io + it } }

    val script2: IO = instructions.foldLeft(IO.empty) { acc -> { acc + it } }
    script2()

    val script3 = sayHello()
    script3()
}
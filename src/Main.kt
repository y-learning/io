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

fun main() {
//    readPeopleFromConsole().forEach(::println)

    readPeopleFromFile("ppl.txt")
            .forEach({ list ->
                         list.forEach(::println)
                     }, onFailure = ::println)

    println("\n")

    readPeopleFromScript("1", "Mickey", "Mouse",
                         "2", "Minnie", "Mouse").forEach(::println)
}
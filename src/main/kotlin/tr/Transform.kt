package tr

val f: Int.(Int, Int) -> Boolean = { a, b ->
	println("Context = $this")
	println("Receiver a = $a")
	println("Receiver b = $b")
	this % 2 == 0
}
val g: (Int, Int) -> Unit = { a, b ->
	println("Receiver a = $a")
	println("Receiver b = $b")
}

fun main() {
	println(f(1, 2, 3))  // parameters: (context, receiver1, ... , receiverN)

	println(10.f(20, 30))
	g(3, 4)
}
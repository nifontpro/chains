package workers

import chains.ChainBuilder

fun ChainBuilder<Context>.w1() = worker {

	on { true }

	exec {
		i += 3
//		throw Exception("EXC")
	}

	except {
		println("Перехват исключения ${it.message}")
	}
}
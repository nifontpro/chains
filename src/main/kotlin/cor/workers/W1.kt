package cor.workers

import cor.ChainBuilder

fun ChainBuilder<Context>.w1() = worker {
	runIf { true }
	exec {
		i += 3
		throw Exception("EXC")
	}

	except {
		println("Перехват исключения ${it.message}")
	}
}
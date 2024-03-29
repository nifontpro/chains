package cor.workers

import cor.ChainBuilder

fun ChainBuilder<Context>.w1() = worker {
	on { true }
	exec {
		i += 3
	}
}
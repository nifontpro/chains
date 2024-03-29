package cor.workers

import cor.ChainBuilder

fun ChainBuilder<Context>.w2() = worker {
	exec {
		i *= 10
	}
}
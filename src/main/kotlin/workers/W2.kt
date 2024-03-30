package workers

import chains.ChainBuilder

fun ChainBuilder<Context>.w2() = worker {
	exec {
		i *= 10
	}
}
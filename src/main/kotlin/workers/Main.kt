package workers

import chains.rootChain
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
	val ctx = Context()

	rootChain<Context> {
		chain {
			on { true }
			w1()
			w2()
			exec { }
			repeatIf { i < 1000 }
		}

	}.execute(context = ctx)

	println(ctx.i)
}

package cor

import cor.workers.Context
import cor.workers.w1
import cor.workers.w2
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
	val ctx = Context()

	rootChain<Context> {
		chain {
			w1()
			w2()
			repeatIf { i < 1000 }
		}

//		parallel {
//			on { true }
//			w2()
//			w1()
//			exec {
//				i += 7
//			}
//		}

	}.execute(context = ctx)

	println(ctx.i)

//	coroutineScope {
//		var i = 0
//		val j1 = launch {
//			repeat(1000000) {
//				i++
//			}
//		}
//		val j2 = launch {
//			repeat(1000000) {
//				i++
//			}
//		}
//		joinAll(j1, j2)
//		println(i)
//	}
}

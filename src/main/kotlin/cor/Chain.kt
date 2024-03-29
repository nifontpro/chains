package cor

import cor.workers.Context
import cor.workers.w1
import cor.workers.w2
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChainBuilder<T>(
	private val isParallel: Boolean = false
) {
	private var blockOn: T.() -> Boolean = { true }
	private val executors: MutableList<IBaseExecutor<T>> = mutableListOf()

	private var blockAgain: T.() -> Boolean = { false }

	private fun add(executor: IBaseExecutor<T>) {
		executors.add(executor)
	}

	fun on(block: T.() -> Boolean) {
		blockOn = block
	}

	fun again(block: T.() -> Boolean) {
		blockAgain = block
	}

	fun worker(function: WorkerBuilder<T>.() -> Unit) {
		add(WorkerBuilder<T>().apply(function).build())
	}

	fun exec(function: T.() -> Unit) {
		add(WorkerBuilder<T>().also {
			it.blockRun = function
		}.build())
	}

	fun chain(function: ChainBuilder<T>.() -> Unit) {
		add(ChainBuilder<T>().apply(function).build())
	}

	fun parallel(function: ChainBuilder<T>.() -> Unit) {
		add(ChainBuilder<T>(isParallel = true).apply(function).build())
	}

	fun build(): Chain<T> {
		return Chain(
			on = blockOn,
			workers = executors.toList(),
			isParallel = isParallel,
			again = blockAgain
		)
	}
}

fun <T> rootChain(block: ChainBuilder<T>.() -> Unit): Chain<T> {
	return ChainBuilder<T>().apply(block).build()
}

class Chain<T>(
	val on: T.() -> Boolean = { true },
	val workers: List<IBaseExecutor<T>> = emptyList(),
	val isParallel: Boolean = false,
	val again: T.() -> Boolean = { true },
) : IBaseExecutor<T> {

	override suspend fun execute(context: T) {
		if (on(context)) {
			if (!isParallel) {
				val startTime = getSysSec()
				do {
					workers.forEach {
						it.execute(context)
					}
				} while (again(context) && (getSysSec() - startTime < 3))
			} else {
				coroutineScope {
					val jobs = workers.map {
						launch { it.execute(context) }
					}
					jobs.joinAll()
				}
			}
		}
	}

	private fun getSysSec() = System.currentTimeMillis() / 1000

}

fun main(): Unit = runBlocking {
	val ctx = Context()

	rootChain<Context> {
		chain {
			w1()
			w2()
			again { i < 3 }
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

package cor

import cor.workers.Context
import cor.workers.w1
import cor.workers.w2
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChainBuilder<T>(
	private var isParallel: Boolean = false
) {
	private var blockOn: T.() -> Boolean = { true }
	private val _workers: MutableList<ICorExec<T>> = mutableListOf()


	private fun add(worker: ICorExec<T>) {
		_workers.add(worker)
	}

	fun on(block: T.() -> Boolean) {
		blockOn = block
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
			workers = _workers.toList(),
			isParallel = isParallel
		)
	}
}

fun <T> rootChain(block: ChainBuilder<T>.() -> Unit): Chain<T> {
	return ChainBuilder<T>().apply(block).build()
}

class Chain<T>(
	val on: T.() -> Boolean = { true },
	val workers: List<ICorExec<T>> = emptyList(),
	val isParallel: Boolean = false
) : ICorExec<T> {

	override suspend fun execute(context: T) {
		if (on(context)) {
			if (!isParallel) {
				workers.forEach {
					it.execute(context)
				}
			} else {
				coroutineScope {
					workers.map {
						launch { it.execute(context) }
					}.joinAll()
				}

			}
		}
	}

}

fun main(): Unit = runBlocking {
	val ctx = Context()

	rootChain<Context> {
		chain {
			w1()
			w2()
		}

		parallel {
			on { true }
			w2()
			w1()
			exec {
				i += 7
			}
		}

	}.execute(context = ctx)

	println(ctx.i)
}

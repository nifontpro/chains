package cor

class ChainBuilder<T>(
	private val isParallel: Boolean = false
) {
	private var blockIf: T.() -> Boolean = { true }
	private val executors: MutableList<IBaseExecutor<T>> = mutableListOf()

	private var blockRepeat: T.() -> Boolean = { false }

	private fun add(executor: IBaseExecutor<T>) {
		executors.add(executor)
	}

	fun runIf(block: T.() -> Boolean) {
		blockIf = block
	}

	fun repeatIf(block: T.() -> Boolean) {
		blockRepeat = block
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
			on = blockIf,
			executors = executors.toList(),
			isParallel = isParallel,
			repeatIf = blockRepeat
		)
	}
}

fun <T> rootChain(block: ChainBuilder<T>.() -> Unit): Chain<T> {
	return ChainBuilder<T>().apply(block).build()
}

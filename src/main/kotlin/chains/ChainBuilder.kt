package chains

class ChainBuilder<T>(
	private val isParallel: Boolean = false
) {
	private var blockIf: suspend T.() -> Boolean = { true }
	private val executors: MutableList<IBaseExecutor<T>> = mutableListOf()

	private var blockRepeat: suspend T.() -> Boolean = { false }

	private fun add(executor: IBaseExecutor<T>) {
		executors.add(executor)
	}

	fun on(block: suspend T.() -> Boolean) {
		blockIf = block
	}

	fun repeatIf(block: suspend T.() -> Boolean) {
		blockRepeat = block
	}

	fun worker(function: WorkerBuilder<T>.() -> Unit) {
		add(WorkerBuilder<T>().apply(function).build())
	}

	@RunDsl
	fun exec(function: T.() -> Unit) {
		add(WorkerBuilder<T>().also {
			it.blockExec = function
		}.build())
	}

	@ChainsDsl
	fun chain(function: ChainBuilder<T>.() -> Unit) {
		add(ChainBuilder<T>().apply(function).build())
	}

	@ChainsDsl
	fun parallel(function: ChainBuilder<T>.() -> Unit) {
		add(ChainBuilder<T>(isParallel = true).apply(function).build())
	}

	fun build(): Chain<T> {
		return Chain(
			blockOn = blockIf,
			executors = executors.toList(),
			isParallel = isParallel,
			repeatIf = blockRepeat
		)
	}
}

@ChainsDsl
fun <T> rootChain(block: ChainBuilder<T>.() -> Unit): Chain<T> {
	return ChainBuilder<T>().apply(block).build()
}

@DslMarker
annotation class ChainsDsl


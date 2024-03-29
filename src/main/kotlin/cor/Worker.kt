package cor

class WorkerBuilder<T> {
	private var blockOn: T.() -> Boolean = { true }
	var blockRun: T.() -> Unit = {}

	fun on(block: T.() -> Boolean) {
		blockOn = block
	}

	fun exec(function: T.() -> Unit) {
		blockRun = function
	}

	fun build(): Worker<T> {
		return Worker(on = blockOn, runBlock = blockRun)
	}
}

class Worker<T>(
	val on: T.() -> Boolean,
	val runBlock: T.() -> Unit,
) : IBaseExecutor<T> {

	override suspend fun execute(context: T) {
		if (on(context)) {
			runBlock(context)
		}
	}
}
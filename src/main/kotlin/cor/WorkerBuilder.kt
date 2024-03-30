package cor

class WorkerBuilder<T> {
	private var blockIf: T.() -> Boolean = { true }
	var blockRun: T.() -> Unit = {}
	private var blockExcept: T.(Exception) -> Unit = {}

	fun runIf(block: T.() -> Boolean) {
		blockIf = block
	}

	fun exec(function: T.() -> Unit) {
		blockRun = function
	}

	fun except(function: T.(Exception) -> Unit) {
		blockExcept = function
	}

	fun build(): Worker<T> {
		return Worker(on = blockIf, runBlock = blockRun, blockExcept = blockExcept)
	}
}
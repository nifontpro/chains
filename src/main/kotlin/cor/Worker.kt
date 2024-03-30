package cor

class Worker<T>(
	val on: T.() -> Boolean,
	val runBlock: T.() -> Unit,
	val blockExcept: T.(Exception) -> Unit,
) : IBaseExecutor<T> {

	override suspend fun execute(context: T) {
		if (context.on()) {
			try {
				runBlock(context)
			} catch (e: Exception) {
				context.blockExcept(e)
			}
		}
	}
}
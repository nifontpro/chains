package cor

interface IBaseExecutor<T> {
	suspend fun execute(context: T)
}
package cor

interface ICorExec<T> {
	suspend fun execute(context: T)
}